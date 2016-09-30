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

import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Allocates a job to the site with maximum available resources
 * 
 * @author Adan Hirales Carbajal
 *
 */
public class MaxAR extends RigidStgy {
	
	
	/**
	 * Class constructor
	 */
	public MaxAR() {
		super();
	} 

	
	/**
	 * No (javadoc)
	 * 
	 * @see schedule
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		Map<UUID,EndPointData> stateData = null;	//Site status information
		double maxRatio = Double.MIN_VALUE;
		UUID targetMachine = null;
		
		
		// Get the admissible set of machines
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));
		
		// Poll for status and estimates
		stateData = gridInfBroker.poll(admissibleSet, EntityType.STATUS, null, null);
		
		/* (ni/mi) 
		 * avail	- machine i number of available resources 
		 * mi		- machine i the size 
		 */
		for(UUID machine : admissibleSet) {
			double avail = (double) ((ComputeEPState)stateData.get(machine)).freeSlots;
			double mi = (double) ((ComputeEPState)stateData.get(machine)).size;
			double ratio = (avail/ mi);
			if( ratio > maxRatio ){
				maxRatio = ratio;
				targetMachine = machine;
			} //End if
		} //End for
		
		// If all machines are occupied, then chose one randomly
		if(targetMachine == null) {
			int numMachines = admissibleSet.size();
			int i = (int) Math.round((Math.random()*(numMachines-1)));
			targetMachine = admissibleSet.get(i);
		}
		
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	} // End schedule

}
