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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.JRTEstimate;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;


public class MWTAS extends RigidStgy {
	
	/**
	 * Min waiting time values
	 */
	private Map<UUID,Double> minTa;
	
	/**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	
	/**
	 * Class constructor
	 */
	public MWTAS() {
		super();
		this.minTa = new HashMap<UUID,Double>();
		this.initialized = false;
	} // End MinWeightedTurnAround

	
	/**
	 * 	Schedules a rigid job to a site based on the MinWeigtedTurnAround criteria
	 *  (Heuristic #13)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		double minRatio = Double.MAX_VALUE;
		UUID targetMachine = null; 

		// INITIALIZE
		if(!this.initialized) {
			for(UUID s: gridInfBroker.getKnownMachines())
				minTa.put(s, 0.0);
			initialized = true;
		}

		
		// GET THE ADMISSIBLE SET OF MACHINES
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));		
		
		
		// GET GRID OR SITE LAYER CHARACTERISTICS
		LinkedList<Job> jobs = new LinkedList<Job>();
		jobs.add(job);
		Map<UUID,EndPointData> stateData = gridInfBroker.poll(admissibleSet,EntityType.STATUS, null, null);
		Map<UUID,EndPointData> jreData = gridInfBroker.poll(admissibleSet,EntityType.ESTIMATE, jobs, null);
		
		
		// GET JOB PROPERTIES
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
		
		// SELECT A TARGET MACHINE
		for(UUID machine : admissibleSet ) {
			double ratio = 0.0;
			
			double ni = ((ComputeEPState)stateData.get(machine)).totalJobs;
			double c = ((JRTEstimate)jreData.get(machine)).earliestFinishingTime.get(job).timestamp();
			
			// The heuristic has an error if n_i zero, to avoid getting NAN, we do the following
			ratio = ( ni != 0 )? (((c - r)* size) / ni + minTa.get(machine).doubleValue())
					: minTa.get(machine).doubleValue();
			
			if( ratio < minRatio){
				minRatio = ratio;
				targetMachine = machine;
			} //End if
		} //End for
		minTa.put(targetMachine, minRatio);
		
		
		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	} // End schedule
	
} // End MinWeightedTurnAround
