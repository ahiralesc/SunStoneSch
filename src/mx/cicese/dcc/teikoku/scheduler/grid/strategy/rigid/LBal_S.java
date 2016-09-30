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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.GridEPState;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.collections.SortedQueue;



public class LBal_S extends RigidStgy {
	
	
	/**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	

	/**
	 * The Grid size
	 */
	private int gridSize;
	
	
	/**
	 * Class constructor
	 */
	public LBal_S() {
		super();
		this.initialized = false;
		this.gridSize = 0;
	} 
	
	
	/**
	 * 	Schedules a rigid job to a site based on the min load balancing criteria
	 *  (Heuristic #13)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		UUID targetMachine = null; 
		double minSTD = Double.MAX_VALUE;		
		
		// GET THE ADMISSIBLE SET OF MACHINES
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));
	
		// GET GRID LAYER CHARACTERISTICS
		Map<UUID,EndPointData> stateData = gridInfBroker.poll(admissibleSet, EntityType.STATUS, null, null);
		if(!initialized) {
			gridSize = ((GridEPState)gridInfBroker.getPublicInformation(EntityType.STATUS,null,null)).size;
			initialized = true;
		}
		
		
		// GET JOB PROPERTIES	
		int size = ((SWFJob)job).getRequestedNumberOfProcessors();
		
												
		// SELECT A TARGET MACHINE
		Hashtable<UUID, Number> sumLoad = new Hashtable<UUID,Number>();
		for(UUID sId : admissibleSet) {
			Site s = gridInfBroker.getKnownMachine(sId);
					
			// Get the current scheduled and waiting jobs			
			Set<Job> sj = s.getScheduler().getSchedule().getScheduledJobs();
			SortedQueue<Job> qj = s.getScheduler().getQueue();
			double sum = 0 ;

			// Estimate PL by adding scheduled jobs
			for(Job j : sj) {
				int s_j = ((SWFJob)j).getRequestedNumberOfProcessors();
				sum += s_j;
			}
			
			// Estimate PL by adding queued jobs
			for(Job j : qj) {
				int q_j = ((SWFJob)j).getRequestedNumberOfProcessors();
				sum += q_j;
			}
					
			// Add the job to the calculation
			sumLoad.put(sId, sum);
		} // End known sites
		

		Hashtable<UUID, Number> PL = new Hashtable<UUID,Number>();
		for(UUID machine : admissibleSet) {
			double meanPL = 0;
				
			// Add the new job to this machine and compute each machine PL
			for(UUID oMachine : admissibleSet) {
				double pl = 0;
				if (machine == oMachine) 
					pl = (sumLoad.get(machine).doubleValue() + size) / 
						((ComputeEPState)stateData.get(machine)).size;
				else
					pl = sumLoad.get(oMachine).doubleValue() /
						((ComputeEPState)stateData.get(machine)).size;
				
				PL.put(oMachine, pl);
				meanPL += pl;
			}
			
			// Compute the mean PL
			meanPL = meanPL / PL.size();
			
			// Compute the std deviation
			double sumDiff = 0;
			for(UUID s : PL.keySet())
				sumDiff += Math.pow((PL.get(s).doubleValue() - meanPL),2);
			double std = Math.sqrt((1.0/(double)gridSize)* sumDiff);
			
			if(std < minSTD) {
				minSTD = std;
				targetMachine = machine;
			}
		} //End for
		
	
		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	} // End schedule	
}
