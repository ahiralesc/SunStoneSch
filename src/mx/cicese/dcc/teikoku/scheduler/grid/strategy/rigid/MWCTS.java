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

import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.JRTEstimate;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MWCTS extends RigidStgy {
		
	/**
	 * Min completion time values
	 */
	private Map<UUID,Double> minCT;
	
	/**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	
	
	/**
	 * Class constructor
	 */
	public MWCTS() {
		super();
		this.minCT = new HashMap<UUID,Double>();
		this.initialized = false;
	} // End MinWeightedCompleteTime

	
	/**
	 * 	Schedules a rigid job to a site based on the MinProcessorLoad criteria
	 *  (Heuristic #2)For further details on all supported rigid job scheduling 
	 *  strategies @see mx.cicese.dcc.teikoku.scheduler.strategy.rigid.Strategy#schedule(de.irf.it.rmg.teikoku.job.Job)
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		double minCompletion = Double.MAX_VALUE;
		UUID targetMachine = null; 

		// INITIALIZE
		if(!this.initialized) {
			for(UUID s: gridInfBroker.getKnownMachines())
				minCT.put(s, 0.0);
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
		Map<UUID,EndPointData> rteData = gridInfBroker.poll(admissibleSet,EntityType.STATUS, null, null);
		
		
		// GET JOB PROPERTIES
		double size = ((SWFJob)job).getRequestedNumberOfProcessors();
		
		
		// SELECT A TARGET MACHINE
		for(UUID machine : admissibleSet ) {
			double c = ((JRTEstimate)rteData.get(machine)).earliestFinishingTime.get(job).timestamp();

			// Weight is added here!
			c = (c * size) + minCT.get(machine).doubleValue();
			
			if( c < minCompletion ) {
				minCompletion = c;
				targetMachine = machine; 
			} //End if
		} //End for
		minCT.put(targetMachine, minCompletion);

		
		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	} // End schedule
	
}
