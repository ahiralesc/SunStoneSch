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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;
import mx.cicese.dcc.teikoku.scheduler.SchedulerHelper;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.utilities.TopologicalSort;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.job.Precedence;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.teikoku.site.schedule.UtilizationChange;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Scheduling strategies may require accounting data or job execution estimates for 
 * decision-making purposes. In Teikoku, a grid information component provides services for 
 * consulting resource availability, state, and architectural characteristics. Its design is 
 * based on the Grid laboratory Uniform Environment (GLUE) schema 
 * <p>
 * 
 * The information broker can obtain either status information or job runtime estimates 
 * from computational resources. They are modeled by the ComputeEPSate and JRTEstimate 
 * objects correspondingly. Status information is easily acquired by consulting the state of 
 * queues, local schedules, etc. Obtaining job runtime estimates is more complex, since it 
 * requires knowledge of the real schedule and of past job runtime requests.
 * <p>
 * 
 * @author <a href="mailto:ahirales@cicese.edu.mx">Adan Hirales Carbajal</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 * @category Information system
 * 
 */
public final class SiteInformationBroker extends AbstrInformationBroker{

	/**
	 * The compute end-point public information
	 * 
	 */
	private Map<EntityType,EndPointData> publicInformation;

	
	/**
	 * Stores requested job runtime estimates of jobs that have not
	 * been allocated to a target machine. 
	 * 
	 */
	private List<Job> pastJREQueue;
	
	/**
	 *	 The refresh rate for status information
	 *
	 */
	private long refresRate;
		
	
	/**
	 * Holds the temporal schedule used for estimates.
	 */
	private TemporalSchedule cloneSchedule;

	
	public SiteInformationBroker(){
		super();
		this.refresRate = -1;
		publicInformation = new HashMap<EntityType,EndPointData>();
		pastJREQueue = new LinkedList<Job>();
		cloneSchedule = null;
		type = InfBrokerType.SITE;
	}
	
	
	
	
	/**
	 * Gets longest processing time of a currently executing job
	 * 
	 * TODO: delete
	 */
	public long getMaxlengthJob( ) {
		long maxRunningTimelenght = 0;
		
		Instant now = Clock.instance().now();
		long minTime = Long.MAX_VALUE;
		List<UtilizationChange> utilizations = site.getScheduler().getSchedule().getManagedUtilizationChanges();
		for(UtilizationChange u : utilizations){
			long currentUtilizationTime = u.getTimestamp().timestamp();
			if( currentUtilizationTime < minTime)
				minTime = currentUtilizationTime; 
		}
		
		
		if(utilizations.size() != 0) {
			long diff = now.timestamp() - minTime;
			Distance length = new Distance(diff);
			maxRunningTimelenght  = TimeHelper.toSeconds(length);	
		}
		
		return maxRunningTimelenght;
	}
	
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see mx.cicese.dcc.teikoku.information.broker.SiteInformationBrokerOld#notifyGridInfBroker()
	 */
	public void notifyGridInfBroker() {
		//TODO: Implement
	} 


	@Override
	protected EndPointData getJREstimate(Queue<Job> jobs, JobControlBlock jcb) {
		EndPointData msg =  null;
		Hypergraph<SWFJob,Precedence> g = null;
		
		if( jcb != null )
			g = ((CompositeJob)jcb.getJob()).getStructure();
		
		
		Job job = jobs.peek();
		if(((SWFJob)job).getJobType().equals(JobType.INDEPENDENT) && jobs.size() == 1) {
			int numResources = ((SWFJob) job).getDescription().getNumberOfRequestedResources();
			int numAvailResources = site.getSiteInformation().getNumberOfAvailableResources(); /// checar
			
			if(numResources > numAvailResources)
				return null;
		}
		
		try {
			updateTemporalSchedule(jobs,g);
		} // try
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		List<Job> E = new LinkedList<Job>();
		msg = new JRTEstimate();
		for(Job v : this.pastJREQueue) {
			Slot nextFreeSlot = null;
			if( v.getLifecycle().getLastCondition().equals(State.RELEASED)) {
				nextFreeSlot = insertInTemporalSchedule(v,jcb);
				// Fill in the estimates
				if( nextFreeSlot != null) {	
					if(jobs.contains(v)){
						((JRTEstimate) msg).earliestStartTime.put((SWFJob)v, nextFreeSlot.getAdvent()); 
						((JRTEstimate) msg).earliestFinishingTime.put((SWFJob)v,nextFreeSlot.getCessation());
						((JRTEstimate) msg).validity = null; 			//Does not apply, it must always be sampled.
						((JRTEstimate) msg).earliestAvailTime.put((SWFJob)v,nextFreeSlot.getAdvent());
					}
				} 
			} else {
				E.add(v);
			}//End if 
		}
		
		// Clean data structures
		this.pastJREQueue.removeAll(E);
				
		return msg;
	}


	@Override
	protected EndPointData getStatus() {
		EndPointData msg = null;
		
		/* Refresh rate not define. Then always sample data and set its validity to null. */
		if( this.refresRate == -1 ) {
			msg = this.instMachineStatus();
			((ComputeEPState)msg).validity = null;
		} else {
			/* Refresh rate was set */
			/* Perhaps, there is previously sample status data */
			ComputeEPState bufferedData = (ComputeEPState) publicInformation.get(EntityType.STATUS);
			if( bufferedData != null ) {
					/* Must check if this data is still usable, validity might have expired */
					if(Clock.instance().now().after(bufferedData.getValidity())) {
						/* information is no longer valid, must re-sample and store copy*/
						msg = this.instMachineStatus();
						publicInformation.put(EntityType.STATUS, msg);
					} else {
						/* information still valid */
						msg = bufferedData;
					} // End if validity check
			} else {
			/* No information was previously sampled, just sample it, validity is automatically set */
				msg = this.instMachineStatus();	
				publicInformation.put(EntityType.STATUS, msg);
			}// End if check is previous data was stored
		}// End refresh rate check	
		
		return msg;
	}


	@Override
	protected EndPointData getStatistic() {
		long size = 0, totalJobs = 0, sumUsedSlots = 0;
		
		SiteEPStatistics statData = new SiteEPStatistics();
		ComputeEPState data = this.instMachineStatus();
		
		size = data.size;
		totalJobs = data.totalNumberExecutedJobs;
		sumUsedSlots = data.usedSlots;
				
		statData.estAvgFlowTime = data.sumFlowTimes / totalJobs;
		statData.estAvgProcTime = data.sumProcTimes / totalJobs;
		statData.estAvgWaitingTime = data.sumWaitingTimes / totalJobs;
		statData.utilization = sumUsedSlots / size;
		
		return statData;
	}
	
	
	/**
	 * Given one job, it gets the job earliest start time. To compute the estimate, 
	 * state information from the real schedule is used. 
	 * 
	 * @param jobs, a single job
	 * @param jcb, MAY BE DELETED
	 * 
	 * @return the job earliest start time
	 */

	@Override
	protected EndPointData getEST(Queue<Job> jobs, JobControlBlock jcb) {
		Schedule realSchedule = site.getScheduler().getSchedule();
		JRTEstimate msg =  null;
		Job job = jobs.peek();
		Slot nextFreeSlot = null;
		long r = -1;
					
		/*
		 * Sets up the exact interval of time required for execution of a job
		 */
		r = ((SWFJob)job).getSubmitTime();
		Instant jSTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(r));
		Instant jETime = TimeFactory.newEternity();
		Period jSearchArea = new Period(jSTime, jETime);
		Distance ocupation = new Distance(DateHelper.convertToMilliseconds(((SWFJob)job).getRequestedTime()));
		int numResources = ((SWFJob) job).getDescription().getNumberOfRequestedResources();
	
		/* Before trying to schedule, 
		 * 	A)The site must verify if it has sufficient resources to schedule the job.
		 *    That is, if the size of the machine is not large enough to process the job, then
		 *    - ((Estimate) msg).earliestStartTime = -1
		 *    - ((Estimate) msg).earliestFinishingTime = -1
		 *    - And ((Estimate) msg).earliestAvailTime = -1
		 *   
		 *   Otherwise, estimates are estimated by building the temporal schedule.
		 */
		// Validate if request can be satisfied
		int numAvailResources = site.getSiteInformation().getNumberOfAvailableResources();
		if( numResources <=  numAvailResources) {
			try {
				nextFreeSlot = realSchedule.findNextFreeSlot(ocupation, numResources, jSearchArea);		
			} // try
			catch (InvalidTimestampException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
		} //End if
	
		// Fill in the estimates
		msg = new JRTEstimate();
		if( nextFreeSlot != null) {			
			msg.earliestStartTime.put((SWFJob)job, nextFreeSlot.getAdvent()); 
			msg.earliestFinishingTime.put((SWFJob)job,nextFreeSlot.getCessation());
			msg.validity = null; 			//Does not apply, it must always be sampled.
		} 
		
		return msg;
	}


	/**
	 * Inverts a given topological sorted list
	 * 
	 * @param EntityType	a request type of information
	 * @param Job				a job to schedule
	 * @return					the requested data 
	 */
	private Slot insertInTemporalSchedule(Job job, JobControlBlock jcb) {
		
		Hypergraph<SWFJob,Precedence> g = null;
			
		if( jcb != null )
			g = ((CompositeJob)jcb.getJob()).getStructure();
		/*
		 * Sets up the exact interval of time required for execution of a job
		 */
		Slot nextFreeSlot = null;
		int numResources = -1;
		int numAvailResources = -1;
		Distance ocupation = null;
		Period jSearchArea = null;
		Instant jETime, jSTime;
		long r = -1;
		
		/*
		 * Determine the release time of the job. 
		 */
		if(job.getJobType().equals(JobType.INDEPENDENT)) {
			r = ((SWFJob)job).getSubmitTime();
		} else {
			/* The jobs belonging to the current workflow is analyzed */
			if(g != null && g.containsVertex((SWFJob)job))
				if(g.inDegree((SWFJob)job) != 0) {
					for(SWFJob p :g.getPredecessors((SWFJob)job)) {
						long cur_aest = jcb.getAFT(p); 
						if(cur_aest > r)
								r = cur_aest;
					}//End predecessor
					r = DateHelper.convertToSeconds(r);
				}// End degree 
			else 
				r = ((SWFJob)job).getSubmitTime();
		} //End else
		
		/* Determine if time to submit job occurs in the past. If so, 
		 * release job in this moment
		 */
		long time = DateHelper.convertToSeconds(Clock.instance().now().timestamp());
		if( time > r)
			r = time;
		
		/*
		 * Set occupation variables
		 */
		jSTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(r));
		jETime = TimeFactory.newEternity();
		jSearchArea = new Period(jSTime, jETime);
		//occupation = job.getDescription().getEstimatedRuntime();
		ocupation = new Distance(DateHelper.convertToMilliseconds(((SWFJob)job).getRequestedTime()));
		numResources = ((SWFJob) job).getDescription().getNumberOfRequestedResources();
		numAvailResources = site.getSiteInformation().getNumberOfAvailableResources();
		
		/*
		 * Execution time estimates can be estimated if machine can accommodate the job
		 */
		if( numResources <=  numAvailResources) {
			try {
				nextFreeSlot = this.cloneSchedule.findNextFreeSlot(ocupation, numResources, jSearchArea);
			
			} // try
			catch (InvalidTimestampException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String errorMsg = "scheduling failed: " + e.getMessage();
				//log.error(errorMsg, e);
			} // catch	
		}
		
		/* After finding when the job can be scheduled in the temporal schedule
		 * such, must be inserted in the schedule. Otherwise, the resources that such
		 * job would have occupy will be free.
		 */
		SortedQueue<Job> queuedJobs = new SortedQueueBinarySearchImpl<Job>();
		queuedJobs.add(job);
			
		SchedulerHelper.scheduleQueuedJobs(
				cloneSchedule,
				queuedJobs,
				site.getScheduler().getStrategy());
		
		
		return nextFreeSlot;
	} // insertInTemporalSchedule
	
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see mx.cicese.dcc.teikoku.information.broker.SiteInformationBrokerOld#setRefreshRate()
	 */
	public void setRefreshRate(long refreshRate) {
		this.refresRate = refreshRate;
	}
	
	/**
	 * Updates the temporal schedule
	 * 
	 * @param J
	 * @param g
	 * @throws InstantiationException
	 * @throws InitializationException
	 */
	private void updateTemporalSchedule(Queue<Job> J, Hypergraph<SWFJob,Precedence> g) throws InstantiationException, InitializationException{
		/**
		 * Create a clone schedule if it does not exists, otherwise update the
		 * clone schedule to the real schedule.  
		 */
		if( cloneSchedule == null ) {
			/**
			 *  Information is cloned in the following order
			 *  - Site information
			 *  - Update utilization changes 
			 */
			ComputeSiteInformation siteInformation = ((ComputeSiteInformation)site.getSiteInformation()).clone();
			cloneSchedule = new TemporalSchedule(siteInformation);
			cloneSchedule.update(this.getSite().getScheduler().getSchedule());
		} else {
			/**
			 * Update the clone schedule (queue, utilization changes, etc)
			 */
			cloneSchedule.update(this.getSite().getScheduler().getSchedule());
						
			/**
			 *  Update the compute site information
			 */
			ComputeSiteInformation siteInformation = ((ComputeSiteInformation)site.getSiteInformation()).clone();
			cloneSchedule.updateSiteInformation(siteInformation);
		}
		
	
		/**
		 * Schedule any job that is pending execution, that is, that have state Queued.
		 * Such set of jobs are to be inserted in the clone schedule. But their state 
		 * must remain unchanged.
		 */
		if(this.getSite().getScheduler().getQueue().size() > 0){
			// Create a copy by value of queued jobs. 	
			SortedQueue<Job> queuedJobs = new SortedQueueBinarySearchImpl<Job>();
			queuedJobs.addAll(this.getSite().getScheduler().getQueue());
			
			/* Schedule queued jobs, such jobs have been sent to this site but
			 * are pending scheduling and execution
			 */
			SchedulerHelper.scheduleQueuedJobs(
					cloneSchedule, queuedJobs,
					site.getScheduler().getStrategy());	
		} // End if
		
		Job job = J.peek();
		if(((SWFJob)job).getJobType().equals(JobType.INDEPENDENT) && J.size() == 1)
			pastJREQueue.add(job);
		else 
			updatePastJREQueue(J, g);
	} // End cloneSite
	
	
	
	/**
	 * Update the queue 
	 * 
	 * @param J
	 * @param g
	 */
	private void updatePastJREQueue(Queue<Job> J, Hypergraph<SWFJob,Precedence> g) {
		boolean C;
		LinkedList<Job> pi = new LinkedList<Job>();
		List<SWFJob> T = new LinkedList<SWFJob>();
		TopologicalSort<SWFJob,Precedence> algTS = new TopologicalSort<SWFJob,Precedence>();
		
		int G = ((SWFJob)J.peek()).getCompositeJobId();
		
		for(Job v : this.pastJREQueue)
			if(((SWFJob)v).getCompositeJobId() == G)
				T.add((SWFJob)v);
		
		if(T.size() != 0){
			for(Job x : J)
				T.add((SWFJob)x);
						
			// Creacion del grafo temporal inicio
			DirectedGraph<SWFJob,Precedence> tmp = new DirectedSparseMultigraph<SWFJob,Precedence>();
			for(Job p : T) 
				tmp.addVertex((SWFJob)p);
		
			for(Job p : T)		
				for(SWFJob s : g.getSuccessors((SWFJob)p))
					if(T.contains(s))
						tmp.addEdge(new Precedence(), (SWFJob)p, s, EdgeType.DIRECTED);
			// Creacion del grafo temporal fin
			
			algTS.sort(tmp);
			T = algTS.getVerticesOrdByFinishTime(); /*algTS.getTopologicalSort();*/
		}//End if
			
		C = true;
		for(Job v : this.pastJREQueue){
			if(((SWFJob)v).getCompositeJobId() != G)
				pi.add(v);
			else{ if(C != false){
					pi.addAll(T);
					C=false;
				}//End if
			}//End if
		} //End for
		
		if (C != false){
			this.pastJREQueue.clear();
			this.pastJREQueue.addAll(pi);
			this.pastJREQueue.addAll(J);
		} else {
			this.pastJREQueue.clear();
			this.pastJREQueue.addAll(pi);
		} //End if
	}//End method
	
	
	
	/**
	 *	Samples state information from this compute site
	 *
	 * 	@return	state information
	 */
	private ComputeEPState instMachineStatus(){
		site.updateSiteInformation();
		
		SiteInformation siteInfo = this.site.getSiteInformation();
		ComputeEPState msg = new ComputeEPState();
		
		
		int totalNumExecJobs = ((ComputeSiteInformation)siteInfo).totalNumExecJobs; 	// Total number of executed jobs
		Schedule schedule = site.getScheduler().getSchedule();							// Number of available processors (slot)
		int freeSlots = schedule.getActualFreeness();
		int totalSlots = site.getSiteInformation().getNumberOfAvailableResources();		// Total number of processors (slots) in the machine
		Instant downtimeEnd = ((ComputeSiteInformation)siteInfo).downtimeEnd;				// The state of the machine
		Instant downtimeStart = ((ComputeSiteInformation)siteInfo).downtimeStart;
		Instant now = Clock.instance().now();
		boolean state = true;
		if( (downtimeStart == null) && (downtimeEnd == null) )
			state = true;
		else {
			// The machine is program to be shutdown
			if( now.before(downtimeStart) )
				state = true;
			if( now.after(downtimeEnd) && now.before(downtimeEnd) )
				state = false;
			else
				state = true;
		}
		
		msg.clockRate = ((ComputeSiteInformation)siteInfo).getClockRate();
		msg.estAvgFlowTime = (totalNumExecJobs == 0)? 0 : ((ComputeSiteInformation)siteInfo).sumFlowTimes / totalNumExecJobs;
		msg.estAvgProcTime = (totalNumExecJobs == 0)? 0 : ((ComputeSiteInformation)siteInfo).sumProcTimes / totalNumExecJobs;
		msg.estAvgWaitingTime = (totalNumExecJobs == 0)? 0 : ((ComputeSiteInformation)siteInfo).sumWaitingTimes / totalNumExecJobs;
		msg.freeSlots = freeSlots;
		msg.healthState = state;
		msg.maxPreLRMSWaitingJobs = ((ComputeSiteInformation)siteInfo).maxPreLRMSWaitingJobs;
		msg.maxRunningJobs = ((ComputeSiteInformation)siteInfo).maxRunningJobs;
		msg.maxTotalJobs = ((ComputeSiteInformation)siteInfo).maxTotalJobs;
		msg.maxWaitingJobs = ((ComputeSiteInformation)siteInfo).maxWaitingJobs;
		msg.localRunningJobs = ((ComputeSiteInformation)siteInfo).localRunningJobs;
		msg.localSuspendenJobs = ((ComputeSiteInformation)siteInfo).localSuspendedJobs;
		msg.localWaitingJobs = ((ComputeSiteInformation)siteInfo).localWaitingJobs;
		msg.p_max = ((ComputeSiteInformation)siteInfo).p_max;
		msg.preLRMSWaitingJobs = ((ComputeSiteInformation)siteInfo).preLRMSWaitingJobs;
		msg.runningJobs = ((ComputeSiteInformation)siteInfo).runningJobs;
		msg.size = totalSlots;
		msg.stagingJobs = ((ComputeSiteInformation)siteInfo).stagingJobs;
		msg.sumFlowTimes = ((ComputeSiteInformation)siteInfo).sumFlowTimes;
		msg.sumProcTimes = ((ComputeSiteInformation)siteInfo).sumProcTimes;
		msg.sumWaitingTimes = ((ComputeSiteInformation)siteInfo).sumWaitingTimes;
		msg.suspendedJobs = ((ComputeSiteInformation)siteInfo).suspendedJobs;
		msg.totalJobs = ((ComputeSiteInformation)siteInfo).totalJobs;
		msg.totalNumberExecutedJobs = totalNumExecJobs;
		msg.usedSlots = totalSlots - freeSlots;
		msg.waitingJobs = ((ComputeSiteInformation)siteInfo).waitingJobs;
		
		/* ENERGY Management start */
		if(this.site.getComponents().get("EnergyManager")) 
			msg.energyConsumpion = this.site.getEnergyManager().getHierarchy().getRoot().getEnergyConsumption();
		/* ENERGY Management end */
		
		/* Determine the current longest executing job  */
		Set<Job> schdSet = schedule.getScheduledJobs();
		long max = 0;
		if(schdSet.size() > 0)
			for(Job job : schdSet){
				long st = ((SWFJob)job).getSubmitTime();
				long jobRunningTime = (now.timestamp()/1000) - st;
				if( jobRunningTime > max)
					max = jobRunningTime;
			}
		msg.maxLengthExecJob = max;		
		
	
		// msg.validity = TimeHelper.add(Clock.instance().now(), new Distance(this.refresRate));
		
		return msg;
	} // instrumentStatusInformation

}
