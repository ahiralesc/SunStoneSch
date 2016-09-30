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

import java.util.UUID;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class LBal_E extends RigidStgy {

	/*
	 * The consumed energy
	 */
	private double coreEnergy;
	
	
	/**
	 * Used to initialize the Grid size
	 */
	private boolean initialized;
	
	
	/**
	 * The Grid size
	 */
	private int gridSize;
	
	
	/**
	 * Class constructor
	 */
	public LBal_E() {
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
		
		/*
		
		
		List<UUID> admissibleSet = getAdmissableSet(((SWFJob)job));
		
		
		
		Map<UUID,EndPointData> data = informationBroker.poll(admissibleSet, EntityType.STATUS, null, null);
		if(!initialized) {
			gridSize = ((GridEPState)informationBroker.getPublicInformation(EntityType.STATUS)).size;
			initialized = true;
		}
		
					
			
		int size = ((SWFJob)job).getRequestedNumberOfProcessors();

															
		
		Hashtable<UUID, Number> ParLoad = new Hashtable<UUID,Number>();
		Hashtable<UUID, Number> mSize = new Hashtable<UUID,Number>();
		for(UUID sId : admissibleSet) {
			int m = ((ComputeEPState)data.get(sId)).size;
			
			if( size <= m ) {
				double sum = 0 ;

				/* ENERGY: Computes the energy consumption 
			
				sum = site.getSiteEnergyManager().getEnergyConsumption();
				coreEnergy = s.getSiteInformation().getCoreEnergyConsumption();
				
				// Add the job to the calculation
				UUID id = s.getUUID();
				ParLoad.put(id, sum);
				mSize.put(id, m);
			}
		}

		Hashtable<UUID, Number> PL = new Hashtable<UUID,Number>();
		for(UUID targetSite : ParLoad.keySet()) {
			double meanPL = 0;
			
			// Compute the PL values
			for(UUID gSite : ParLoad.keySet()) {
				
				
				double pl = 0;
				if (targetSite == gSite){

					//Macadamia
					Site s = gInfoBroker.getKnownSite(gSite);
					coreEnergy = s.getSiteInformation().getCoreEnergyConsumption();
					
					pl = (ParLoad.get(gSite).doubleValue() + size*coreEnergy) / mSize.get(gSite).doubleValue();
				}
				else
					pl = ParLoad.get(gSite).doubleValue() / mSize.get(gSite).doubleValue();
				PL.put(gSite, pl);
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
				minSite = targetSite;
			}
		}
		*/
		
		//Create the allocation entry to schedule
		AllocationEntry entry = new AllocationEntry((SWFJob)job, targetMachine, -1);
		
		return entry;
	}
	
}