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

package mx.cicese.dcc.teikoku.information.broker;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.sim.kuiga.Clock;


/**
 * The computing endpoint is a specialization of the Endpoint class for a service that provides 
 * processing capabilities. In teikoku, the computing endpoint is referred to as ComputeSite.
 * <p>
 * 
 * The GridInformationBroker interfaces provides methods to access GridEPState and GridEPStatistics data.
 * <p>
 *   
 * This implementation is based on GLUE Specification V. 2.0. The GLUE specification is an information 
 * model for Grid entities described using the natural language and UML Class Diagrams.
 * <p>
 *  
 *  @author <a href="mailto:ahiralesc@gmail.com">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 *         
 * 	@version $Version$, $Date$
 * 
 *  @category Information system
 * 
 */
public final class GridInformationBroker extends AbstrInformationBroker {

	/**
	 * The list of known sites
	 * 
	 * The UUID models the unique site UUID
	 * The SiteInformationBroker models the site information broker
	 */	
	Map<UUID, SiteInformationBroker> knownSites = null;
	
	
	/**
	 * 	Grid state data
	 */
	GridEPState stateData = null;
	
	
	/**
	 * Class constructor
	 */
	public GridInformationBroker() {
		super();
		knownSites =  new LinkedHashMap<UUID,SiteInformationBroker>();
		type = InfBrokerType.GRID;
	}
	
	
	/**
	 * Registers the known sites
	 * 
	 */
	public void bind( ) {
		
		List<SiteInformationBroker> allSites = new LinkedList<SiteInformationBroker>();
		
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		
		// Register all sites in the temporary data structure, but exclude site 0
		for (Site s:sites.values()){
			if ( !(s == site) ){
				allSites.add((SiteInformationBroker)s.getInformationBroker());
			}
		} 
			
		// Sort the machines in increasing order of machine size
		Collections.sort(allSites,new SortSitesBySize());
		
		// Register the known sites
		for(SiteInformationBroker s:allSites){
			knownSites.put(s.getSite().getUUID(), s);
		}
	}
	
	
	
	/**
	 * Given an information broker UUID, gets the information broker 
	 * instance. 
	 * 
	 * @return 	the instance of the requested information broker 
	 * 			of type SiteInformationBroker
	 */	
	public SiteInformationBroker getInformationBroker(UUID id) {
		if(knownSites.size() == 0)
			bind();
		
		return this.knownSites.get(id);
	}


	/**
	 * Get a site specified by its unique site identifier 
	 * 
	 * @return 	a site        
	*/
	public Site getKnownMachine(UUID sId) {
		return knownSites.get(sId).getSite();
	}
	
	
	/**
	 * Gets all known sites UUID's 
	 * 
	 * @return 	a list containing the UUID of all known sites. 
	 */	
	public List<UUID> getKnownMachines() {
		if(knownSites.size() == 0)
			bind();
	
		return new LinkedList<UUID>(this.knownSites.keySet());
	}
	
	
	/**
	 * Polls all known machines
	 *  
	 * @param infoType
	 *		Two type of information data structures can be polled from
	 *		each site information broker: information that gives the 
	 *		status of the site (SiteStatusInformation) and information that
	 *		allows to get estimates. (see {@link EntityType})
	 * @param jobs
	 * 		If the requested information is a job runtime estimate. Then, for each job in 
	 * 		jobs a runtime estimate is estimate is computed
	 * @param jcb
	 * 		The job control block
	 *
	 *@return A map containing polled data from all site information brokers.
	 *        
	 */
	public Map<UUID,EndPointData> poll(EntityType infoType, Queue<Job> jobs, JobControlBlock jcb) {
		Map<UUID,EndPointData> polledData = new LinkedHashMap<UUID,EndPointData>();
		
		if(knownSites.size() == 0)
			bind();
		
		for (SiteInformationBroker s:knownSites.values()){
			EndPointData data = s.getPublicInformation(infoType, jobs, jcb);
			if(data != null)
				polledData.put(s.getSite().getUUID(), data);
		}
		
		return polledData;
	}
	
	
	/**
	 * Polls a given set of machines
	 * 
	 * @param sites, the set of machines to be polled
	 * @param infoType, the type of requested data
	 * @param jobs, the set of jobs from whom the estimate is requested
	 * @param jcb, the job control block
	 * @return
	 */
	public Map<UUID,EndPointData> poll(List<UUID> sites, EntityType infoType, Queue<Job> jobs, JobControlBlock jcb) {
		Map<UUID,EndPointData> polledData = new LinkedHashMap<UUID,EndPointData>();
		
		if( sites != null) {
			for (UUID s:sites){
				
				// Get the information system for the site
				SiteInformationBroker siteInfBroker = knownSites.get(s);
				EndPointData data = siteInfBroker.getPublicInformation(infoType, jobs, jcb);
				
				if(data != null)
					polledData.put(s, data);
			}
		} 
			
		return polledData;
	}
	
	
	
	@Override
	protected EndPointData getJREstimate(Queue<Job> jobs, JobControlBlock jcb) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Gets the Grid status data
	 * 
	 *  @see ComputeEPState 
	 */
	@Override
	protected EndPointData getStatus() {
		// Query machines state data
		Map<UUID,EndPointData> dataSet = 
				poll(new LinkedList<UUID>(knownSites.keySet()), EntityType.STATUS, null, null);
				
				
		if( stateData == null ) {
			stateData = new GridEPState(EntityType.STATUS);
			
			// Static data is only queried once
			for(EndPointData  data: dataSet.values()){
				stateData.maxPreLRMSWaitingJobs += ((ComputeEPState)data).maxPreLRMSWaitingJobs;
				stateData.maxRunningJobs += ((ComputeEPState)data).maxRunningJobs;
				stateData.maxTotalJobs += ((ComputeEPState)data).maxTotalJobs;
				stateData.maxWaitingJobs += ((ComputeEPState)data).maxWaitingJobs;
				stateData.preLRMSWaitingJobs += ((ComputeEPState)data).preLRMSWaitingJobs;
				stateData.runningJobs += ((ComputeEPState)data).runningJobs;
				stateData.size += ((ComputeEPState)data).size;
				stateData.stagingJobs += ((ComputeEPState)data).stagingJobs;
				stateData.suspendedJobs += ((ComputeEPState)data).suspendedJobs;
				stateData.totalJobs += ((ComputeEPState)data).totalJobs;
				stateData.waitingJobs += ((ComputeEPState)data).waitingJobs;
			}
					
			stateData.validity = Clock.instance().now();
		}
				
		// Machine state is always updated
		if(stateData.validity.timestamp() < Clock.instance().now().timestamp()) {
					
			for(UUID  s: dataSet.keySet()){
				ComputeEPState data = (ComputeEPState) dataSet.get(s);
				stateData.state.put(s, data.healthState);
			}
					
			stateData.validity = Clock.instance().now();
		}
						
		return stateData;
	}

	

	/**
	 * Gets Grid statistical data, which include:
	 * - The current state of the machines
	 * - The estimated average flow time 
	 * - The estimated average processing time
	 * - The estimates average waiting time
	 * - The grid utilization
	 * 
	 */
	@Override
	protected EndPointData getStatistic() {
		GridEPStatistics statData = new GridEPStatistics();
		long size = 0, sumUsedSlots = 0, sumJobs = 0;
		double sumFlowTime = 0, sumProcTime = 0, sumWaitingTime = 0;
		
		// Query machines state data
		Map<UUID,EndPointData> dataSet = 
						poll(new LinkedList<UUID>(knownSites.keySet()), EntityType.STATUS, null, null);
				
		for(EndPointData  data: dataSet.values()){
			size += ((ComputeEPState) data).size;
			sumFlowTime += ((ComputeEPState) data).sumFlowTimes;
			sumProcTime += ((ComputeEPState) data).sumProcTimes;
			sumWaitingTime += ((ComputeEPState) data).sumWaitingTimes;
			sumUsedSlots += ((ComputeEPState) data).usedSlots;
			sumJobs += ((ComputeEPState) data).totalNumberExecutedJobs;
		}
		
		
		statData.estAvgFlowTime = sumFlowTime / sumJobs;
		statData.estAvgProcTime = sumProcTime / sumJobs;
		statData.estAvgWaitingTime = sumWaitingTime / sumJobs;
		statData.utilization = sumUsedSlots / size;
		
		return statData;
	}

}
