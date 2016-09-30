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
import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class MaxAR_v extends RigidStgy {
	
	
	/**
	 * Class constructor
	 */
	public MaxAR_v() {
		super();
	}

	
	/**
	 * 	Schedules a rigid job to a site based on the MaxAvailableProcessors criteria
	 *  taking into account site speed
	 * 
	 * 	@param		job	the job to schedule (rigid) of type Job
	 * 	@return		an allocation entry
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		Map<UUID,EndPointData> stateData = null;	//Site status information
		double maxRatio = Double.MIN_VALUE;
		UUID targetMachine = null;
		
		// GET THE ADMISSIBLE SET OF MACHINES
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));
		
		
		// GET GRID OR SITE LAYER CHARACTERISTICS
		stateData = gridInfBroker.poll(admissibleSet, EntityType.STATUS, null, null);
	
		// SELECT A TARGET MACHINE
		for(UUID machine : admissibleSet) {
			double ratio = 0.0;
			
			//Total number of processors in site s
			long mi = ((ComputeEPState)stateData.get(machine)).size;

			//Available processors in site s
			long avail = ((ComputeEPState)stateData.get(machine)).freeSlots; 

			//Speed of site s
			double vi = ((ComputeEPState)stateData.get(machine)).clockRate; 
								
			ratio = avail*vi/mi;
			
			if (ratio > maxRatio) {
				maxRatio = ratio;
				targetMachine = machine;
			}
		}
		
		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	}
	
	
}
