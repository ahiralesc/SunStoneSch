/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2009 by the
 *   Robotics Research Institute (Section Information Technology)
 *   at TU Dortmund University, Germany
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
package de.irf.it.rmg.core.teikoku.site;

import java.util.List;

import de.irf.it.rmg.core.teikoku.Bootstrap;
import de.irf.it.rmg.core.teikoku.common.Reservation;
import de.irf.it.rmg.core.teikoku.exceptions.AbortionNotHandledException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.exceptions.SubmissionException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobAbortedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedOnForeignSiteEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.ReservationEndEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.ReturnOfLentResourcesEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.AllocQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.VoltageFrequencyRegulationEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.WaitTimeTimeoutEvent;
import de.irf.it.rmg.core.teikoku.scheduler.AbstractScheduler;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import de.irf.it.rmg.sim.kuiga.annotations.EventSink;
import de.irf.it.rmg.sim.kuiga.annotations.InvalidAnnotationException;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.sim.kuiga.annotations.NotificationTime;
import de.irf.it.rmg.sim.kuiga.listeners.TimeChangeListener;
import de.irf.it.rmg.sim.kuiga.listeners.TypeChangeListener;
import mx.cicese.dcc.teikoku.energy.EnergyBroker;
import mx.cicese.dcc.teikoku.information.broker.InstrHelper;
import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.cicese.dcc.teikoku.scheduler.IndpJobScheduler;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;


/**
 * 
 * @author Adan Hirales Carbajal
 *
 */
@EventSink
public class ComputeSiteEventHandler implements TimeChangeListener,TypeChangeListener {

	/*
	 * Event Handler Site
	 */
	private Site site;
	
	
	private boolean completedEventPresent=false;
	
	
	/* The machine instrumentation mechanism */
	private InstrHelper instrSystem;
	
		
	public ComputeSiteEventHandler(Site site) throws InvalidAnnotationException {
		this.site=site;
		/*
		 * Register the compute site event handler as eventsink to be notified for handling events in the
		 * main handling phase and for changes in time or eventtype
		 */
		Kernel.getInstance().registerEventSink(this);
		Kernel.getInstance().registerTypeChangeListener(this);
		Kernel.getInstance().registerTimeChangeListener(this);
		
		/* The machine instrumentation helper */
		this.instrSystem = new InstrHelper();
	} //ComputeSiteEventHandler
	
	
	
	/**
	 * Overwritten realization of delivering the released event
	 */
	@AcceptedEventType(value=JobReleasedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverReleasedEvent(JobReleasedEvent event) {
		//Check the Tag
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			this.site.getLocalSubmissionComponent().deliverReleaseEvent(event);
		}
	}
	
	

	/**
	 * Overwritten realization of delivering the Queued event
	 */
	@AcceptedEventType(value=JobQueuedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverQueuedEvent(JobQueuedEvent event){
		Job job=event.getQueuedJob();
		
		/* First check tag-existence */
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			if (job.getProvenance().getLastCondition()==this.site){
				
				
				/* INSTRUMENTATION COMPONENT: START */
				instrSystem.instrCSIQueuedEv(site, event, (SWFJob)job);
				/* INSTRUMENTATION COMPONENT: END */
				
				/* 
				 * The energy management component is not activated.
				 * Therefore, queue the job locally. 
				 */ 
				((AbstractScheduler) this.site.getScheduler()).activate();
				
				
				
			}
		}
	}
				
				
						
	
	@AcceptedEventType(value=JobStartedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverStartedEvent(JobStartedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			try {
				
				/* INSTRUMENTATION: START */
				Job job = event.getStartedJob();
				instrSystem.instrCSIStartedEv(site, event, (SWFJob)job);
				/* INSTRUMENTATION: END */
				
				this.site.getExecutor().submit(event.getStartedJob());
				
				/* ENERGY Management start */
				if(this.site.getComponents().get("EnergyManager"))
					site.getEnergyManager().handlePowerUpEvent((SWFJob)job);
				/* ENERGY Management end */
				
				
			} // try
			catch (SubmissionException e) {
				e.printStackTrace();
			}
			
			
			this.site.getScheduler().activate();
		}
	}
	
	
	@AcceptedEventType(value=WaitTimeTimeoutEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverCompletedEvent(WaitTimeTimeoutEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			if (event.getSite() == this.site){
	//			EnergyBroker eb = site.getEnergyManager();
	//			eb.handleTimeOutEvent(event);
			}
		}
	}
	
	
	
	@SuppressWarnings("deprecation")
	@AcceptedEventType(value=JobCompletedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverCompletedEvent(JobCompletedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Job completedJob = event.getCompletedJob();
			
			//Check whether the job is completed on this site or completed on another site
			if (completedJob.getProvenance().getLastCondition()==this.site){
				this.completedEventPresent=true;
				Period realDuration = new Period(completedJob.getDuration().getAdvent(), event.getTimestamp());
				
				try {
						/* INSTRUMENTATION: START */
						this.instrSystem.instrCSICompletedEv(site, event, (SWFJob)completedJob);
						/* INSTRUMENTATION: END */
						
							
						/* ENERGY Management start */
						if(this.site.getComponents().get("EnergyManager"))
							site.getEnergyManager().handlePowerDownEvent((SWFJob)completedJob); 
						/* ENERGY Management end */
						
						/* SCHEDULER */
						completedJob = this.site.getScheduler().getSchedule().shortenJobDuration(completedJob, realDuration);
						completedJob.getLifecycle().addEpisode(State.COMPLETED);
						this.site.getScheduler().getSchedule().removeJob(completedJob);
						this.site.getScheduler().handleCompletion(completedJob);
												
						
						
						
						/* Validate if a foreign site requires notification. A foreign site is notified if
							-	The job source is not the current site.
							-	The job is part of a workflow, its job type should not be independent
							-	The foreign site has a Grid broker (this is only important for workflow scheduling)
						*/
						Site foreignSite = completedJob.getReleasedSite();
						if( foreignSite != this.site &&
							!completedJob.getJobType().equals(JobType.INDEPENDENT) &&
							foreignSite.getGridActivityBroker() != null) {
								Event jce = new JobCompletedOnForeignSiteEvent(Clock.instance().now(), completedJob, foreignSite);
								jce.getTags().put(foreignSite.getUUID().toString(), foreignSite);
								Kernel.getInstance().dispatch(jce );
						}
					} // try
					catch (IllegalScheduleException e) {
						Bootstrap.getInstance().terminateUngracefully(e);
					} // catch
					catch (InvalidTimestampException e) {
						Bootstrap.getInstance().terminateUngracefully(e);
					} // catch
					catch (IllegalOccupationException e) {
						Bootstrap.getInstance().terminateUngracefully(e);
					} // catch
			}
		}
	}
	

	@AcceptedEventType(value=VoltageFrequencyRegulationEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverVoltageFrequencyRegulationEvent(VoltageFrequencyRegulationEvent event){
		System.out.println("Me invoco");
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			// Call the power management component of the machine and apply DVFS or timeouts.
			EnergyBroker eb = site.getEnergyManager();
			eb.scaleVoltageAndFrequency();
		}
	}
	
	
	
	@AcceptedEventType(value=JobAbortedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverAbortedEvent(JobAbortedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Job abortedJob = event.getAbortedJob();
			if (abortedJob.getProvenance().getLastCondition()==this.site){
				abortedJob.getLifecycle().addEpisode(State.ABORTED);
				try {
					this.site.getScheduler().getSchedule().removeJob(abortedJob);
					this.site.getScheduler().handleAbortion(abortedJob);
				} catch (IllegalOccupationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AbortionNotHandledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/* INSTRUMENTATION COMPONENT: START */
				instrSystem.instrCSIAbortEv(site, event, (SWFJob)abortedJob);
				/* INSTRUMENTATION COMPONENT: END */				
			}
		}
	}
	
	
	
	@AcceptedEventType(value=AllocQueuedEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverAllocQueuedEvent(AllocQueuedEvent event){
		Job job=event.getQueuedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			if (job.getProvenance().getLastCondition()==this.site){
				//job is queued locally
				IndpJobScheduler sch = (IndpJobScheduler)  
					site.getGridActivityBroker().getIndependentJobScheduler();
				sch.onAllocQueueEvent(event);
			}
		}
	}
	
	
	@AcceptedEventType(value=JobCompletedOnForeignSiteEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverJobCompletedEventOnForeignSite(JobCompletedOnForeignSiteEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Job completedJob = event.getCompletedJob();
			Site foreignSite = completedJob.getReleasedSite();
			if( foreignSite == this.site ) {
				this.site.getGridActivityBroker().hadleJobCompletionOnForeignSite(event);
			}
		}
	}

	
	@AcceptedEventType(value=ReservationEndEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverReservationEndEvent(ReservationEndEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			this.completedEventPresent=true;
		}
	}
	
	
	
	@AcceptedEventType(value=ReturnOfLentResourcesEvent.class)
	@MomentOfNotification(value=NotificationTime.HANDLE)
	public void deliverReturnOfLentResourcesEvent(ReturnOfLentResourcesEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.site.getUUID().toString())){
			Reservation r=event.getReservation();
			Schedule s=this.site.getScheduler().getSchedule();
			int size=r.getResources().size();
			
			//remove the reservation within the schedule-data structure
			try {
				s.removeReservation(r);
			} catch (IllegalOccupationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Remove the real lent Objects within the siteInformation
			SiteInformation info=this.site.getSiteInformation();
			info.getLentResources().remove(r.getResources());
			List<Resource> rl=info.getLentResources();
			for (Resource res:rl){
				res.setOrdinal(res.getOrdinal()-size);
			}
			
			//Adjust all involved utilization changes
			s.removeLentResources(r.getResources());
		}
	}
	

	public void notifyTypeChange(Event fromEvent, Event toEvent) {
		if ((fromEvent.getClass()==JobCompletedEvent.class) && 
		((toEvent!=null) && (fromEvent.getTimestamp().equals(toEvent.getTimestamp()))) ||
		(toEvent==null) ||
		((fromEvent.getClass()==ReservationEndEvent.class) && (toEvent!=null) && (toEvent.getClass()==JobCompletedEvent.class) &&
				(fromEvent.getTimestamp().equals(toEvent.getTimestamp())))){
			this.site.getScheduler().activate();
		}
	}
	
	
	public void notifyTimeChange(Instant fromTime, Instant toTime) {
		if (this.completedEventPresent){
			this.site.getScheduler().activate();
			this.completedEventPresent=false;
		}
	}
	
} //End ComputeSiteEventHandler 
