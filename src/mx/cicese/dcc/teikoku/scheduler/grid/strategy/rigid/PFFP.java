/*
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2013 by the
 *   CICESE Research Center and  
 *   CETYS University, Mexico
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */

package mx.cicese.dcc.teikoku.scheduler.grid.strategy.rigid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;

import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.ConfigurationHelper;

/**
 * See article: 
 * 	Load-Balancing Long-Tailed Self-Similar Jobs in Hierarchical Computational Grids 
 * 
 * Given PI and XI, the heuristic allocates a job to the machine with smallest 'Aggregated Execution Time of Queued Jobs (AET_QJ)' 
 * and 'Execution Time Tendency (ETT)'.
 * 
 * PHI, is used to compute the probability that the execution time of a sequence of jobs in a queue exceeds the given value PI.
 * 		The Queued Job Probability (QJP)
 * PSI, the Execution Extension Probability (EEP)
 *   	
 * The strategy does not use information from the received job to guide the allocation process. Instead, it requires state information, namely
 * the number of queued jobs and the mean job execution time at each machine. 
 * 
 * 
 * Reference: Load-Balancing Long-Tailed Self-Similar Jobs in Hierarchical Computational Grids
 * 
 * @see mx.cicese.dcc.teikoku.scheduler.grid.strategy.rigid.RigidStgy
 *
 * @author <a href="mailto:ahirales@cicese.edu.mx">Adan Hirales Carbajal</a>,
 * 		   <a href="mailto:chernykh@cicese.edu.mx">Andrey Chernykh</a>, and
 * 		   <a href="mailto:rramirez@itesm.mx">Raul Ramirez-Velarde</a>, and 
 * 		   <a href="mailto:@">Carlos Barba</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 * @category Rigid Scheduling Strategy
 */


public class PFFP extends RigidStgy {
	
	/**
	 * The scale parameter
	 */
	private double Sm;
	
	
	/**
	 * The shape parameter
	 */
	private double beta;
	
	
	/**
	 * The Hurst factor
	 */
	private double hurst;
	
	
	/**
	 * The phi (queue) input parameter, equation (5)
	 */
	private double phi;
	
	
	/**
	 * The psi (execution) input parameter, equation (6)
	 */
	private double psi;
	
	
	/**
	 * The Mu parameter
	 */
	private double mu;
	
	
	
	/**
	 * Class constructor
	 */
	public PFFP() {
		super();
		
		try{
			this.loadConfiguration();
		}catch(InstantiationException e){
			e.printStackTrace();
			System.exit(0);
		}
		//this.hurst = 0.784; 		// H, hurst 
		//this.beta = 0.524;		// B, beta
		//this.Sm = 1320; 		// Sm
		//this.mu = 5248;			// Mu
		//this.phi = 0.01;		// Phi, queue
		//this.psi = 0.001;		// Psi, execution
	} // End PFFP
	
	

	/**
	 * 	Schedules a rigid job to a site based on the MinProcessorLoad criteria
	 *  (Heuristic #2)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		double minValue = Double.MAX_VALUE;
		UUID targetSite = null; 
		double n=0.0, tau=0.0, exp=0.0, rho=0.0, delta=0.0;
		double m = 0;
				

		// GET THE ADMISSIBLE SET OF MACHINES
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));
		
		
		// GET GRID OR SITE LAYER CHARACTERISTICS
		Map<UUID,EndPointData> avail = super.gridInfBroker
				.poll(admissibleSet, EntityType.STATUS, null, null);
		
		
		
		// SELECT A TARGET MACHINE
		for(UUID s : admissibleSet) {
			n = ((ComputeEPState)avail.get(s)).waitingJobs; 	// Number of waiting jobs
			tau = ((ComputeEPState)avail.get(s)).maxLengthExecJob; 		// Maximum execution time of a currently executing job (single processor model)
			m = ((ComputeEPState)avail.get(s)).size;
			
			// Aggregated execution time of queued jobs in seconds (Eq. 5)
			exp = (1 / beta);
			rho = ( Math.floor((n/m)) * this.mu) + ((Sm * Math.pow(Math.floor((n/m)), hurst)) / Math.pow(this.phi, exp));
			//rho = ( n * this.mu) + ((Sm * Math.pow(n, hurst)) / Math.pow(this.phi, exp));
			// Execution time extension in seconds (Eq. 6) 
			exp = (-1 / this.beta);
			delta = tau * (Math.pow(psi, exp) - 1);
			
			// Select the site with smallest value only if the site fits in the site
			if( (rho + delta) < minValue ){
				minValue = (rho + delta);
				targetSite = s;
			} //End if
		} //End for
		
		
		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetSite, -1);
		
		return entry;
	} // End schedule
	
	
	
	private void loadConfiguration() throws InstantiationException{
		
		// Loads the first activity broker (node level)
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(Site.CONFIGURATION_SECTION);

		String str_hurst = ConfigurationHelper.retrieveRelevantKey(c, "site0", Constants.PFFP_HURST);
		String str_beta = ConfigurationHelper.retrieveRelevantKey(c, "site0", Constants.PFFP_BETA);
		String str_Sm = ConfigurationHelper.retrieveRelevantKey(c, "site0", Constants.PFFP_SM);
		String str_Mu = ConfigurationHelper.retrieveRelevantKey(c, "site0", Constants.PFFP_MU);
		String str_phi = ConfigurationHelper.retrieveRelevantKey(c, "site0", Constants.PFFP_PHI);
		String str_psi = ConfigurationHelper.retrieveRelevantKey(c, "site0", Constants.PFFP_PSI);
		
		if( str_hurst == null || str_beta == null || str_Sm == null || str_Mu == null || str_phi == null || str_psi == null)
			throw new InstantiationException("PFFP Exception: check all parameters are set for the strategy in configuration file");
		else {
			this.hurst = c.getDouble(str_hurst); 
			this.beta = c.getDouble(str_beta);
			this.Sm = c.getDouble(str_Sm); 
			this.mu = c.getDouble(str_Mu);
			this.phi = c.getDouble(str_phi);
			this.psi = c.getDouble(str_psi); 
		}
	}
	
}
