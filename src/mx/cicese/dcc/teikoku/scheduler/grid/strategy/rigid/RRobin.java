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
import java.util.UUID;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
 * (non-Javadoc)
 * 
 * @see mx.cicese.dcc.teikoku.scheduler.grid.strategy.rigid.RigidStgy
 *
 * @author <a href="mailto:ahirales@cicese.edu.mx">Adan Hirales Carbajal</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 * @category Rigid Scheduling Strategy
 */
public class RRobin extends RigidStgy{
	
	/**
	 * Next site
	 */
	private int nextSite;
	
	
	/**
	 * Class constructor
	 */
	public RRobin() { 
		super();
	}
	
		
	/**
	 * (non-Javadoc)
	 * 
	 * @see mx.cicese.dcc.teikoku.scheduler.grid.strategy.rigid.RigidStgy#schedule(Job job)
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		UUID targetMachine = null;
		

		// GET THE ADMISSIBLE SET OF MACHINES
		if(jobFilteringStgy.getKnownMachines().size() == 0){
			jobFilteringStgy.setKnownMachines(gridInfBroker.getKnownMachines(), gridInfBroker);
		}
		List<UUID> admissibleSet = jobFilteringStgy.getAdmissibleMachines(((SWFJob)job));
		
		
		// SELECT A TARGET MACHINE
		if (nextSite >= admissibleSet.size())
			nextSite = 0;
		targetMachine = admissibleSet.get(nextSite);
		nextSite++;


		// CREATE AN ALLOCATION ENTRY
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	} 
	
}
