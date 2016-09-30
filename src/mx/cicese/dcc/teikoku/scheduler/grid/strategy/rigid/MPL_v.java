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
import java.util.HashMap;


public class MPL_v extends RigidStgy {

	/**
	 * The machine parallel loads
	 */
	private Map<UUID,Number> pl;
	
	
	/**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	
	
	/**
	 * Class constructor
	 */
	public MPL_v() {
		super();
		this.pl = new HashMap<UUID,Number>();
		this.initialized = false;
	}

	/**
	 * 	Schedules a rigid job to a site based on the MinParallelLoad criteria
	 *  taking into acount site speed
	 *  (Heuristic #3)For further details on all supported rigid job scheduling 
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
			for(UUID s : gridInfBroker.getKnownMachines())
				pl.put(s, 0.0);
			this.initialized = true;
		}
		
		
		// GET THE ADMISSIBLE SET OF MACHINES
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));
		
		
		// GET GRID OR SITE LAYER CHARACTERISTICS
		Map<UUID,EndPointData> stateData = gridInfBroker.poll(admissibleSet,EntityType.STATUS, null, null);
		
		
		// GET JOB PROPERTIES
		long size = ((SWFJob)job).getRequestedNumberOfProcessors();
		
		
		// SELECT A TARGET MACHINE
		for(UUID machine : admissibleSet ) {
			double mi = (double) ((ComputeEPState)stateData.get(machine)).size;
			double vi = ((ComputeEPState)stateData.get(machine)).clockRate;
			double ratio = (size / (vi*mi)) + pl.get(machine).doubleValue();
			if(ratio < minRatio && size <= mi){
				minRatio = ratio;
				targetMachine = machine;
			}
		}
		pl.put(targetMachine, minRatio);
		
		
		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	}	
	
}