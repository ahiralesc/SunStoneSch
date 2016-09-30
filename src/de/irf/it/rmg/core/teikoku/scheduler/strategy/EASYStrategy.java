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
/**
 * 
 */
package de.irf.it.rmg.core.teikoku.scheduler.strategy;

import java.util.Iterator;
import java.util.Queue;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;


/**
 * @author Administrator
 *
 */
public class EASYStrategy extends AbstractStrategy {

	public EASYStrategy() {
		super();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	*/
	public EASYStrategy(EASYStrategy another) { 
		this();
	} 
	
	/**
	 * Clone 
	*/
	@Override
	public Strategy clone() {
		return new EASYStrategy(this);
	}  
	
	
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(EASYStrategy.class);
	
	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Strategy#decide(java.util.Queue, de.irf.it.rmg.core.teikoku.site.schedule.Schedule)
	 */
	@Override
	public void decide(Queue<Job> queue, Schedule schedule) {
		
		if (!queue.isEmpty()) {
			Job job = queue.peek();
			
			Instant now = this.determineRequestedStartTime(job);
			//System.out.println("Job number : " + job.getName());

			/*
			 * try to schedule the first job
			 */
			Slot slot = StrategyHelper.determineSoonestSlot(schedule, job, now,	true);
			//Slot slot = StrategyHelper.determineSoonestSlot(schedule, job, now,	false);

			boolean scheduledFirst = false;
			
			if (slot.getAdvent().equals(now)) {
				/*
				 * the first job in the queue can be started immediately, thus
				 * schedule it in an FCFS manner
				 */
				scheduledFirst = this.scheduleFirst(job, slot);
				
				
			} // if
			if (!scheduledFirst) {
				/*
				 * the first job in the queue cannot be started immediately,
				 * thus delay it and try to backfill another one
				 */
				this.delayFirstAndBackfill(queue, schedule, slot);
			} // if
		} // if
		
	}

	/**
	 * Determines the requested start time for a given job.
	 * 
	 * By default, the current system time is returned here.
	 * 
	 * @param job
	 *            The job the requested start time is to be determined for.
	 * @return The requested start time for a given job.
	 * 
	 * @see Clock#getCurrentTimestamp()
	 */
	protected Instant determineRequestedStartTime(Job job) {
		return Clock.instance().now();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param job
	 * @param slot
	 * @return
	 */
	protected boolean scheduleFirst(Job job, Slot slot) {
		boolean result = false;
		/*
		 * notify (advise) the listener
		 */
		try {
			super.getStrategyListener().advise(job, slot);
			result = true;
		} // try
		catch (DecisionVetoException e) {
			String msg = "decision vetoed: properties in slot \"" + slot
					+ "\" not accepted for job \"" + job
					+ "\"; skipping current and trying backfill";
			log.info(msg, e);
		} // catch
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param schedule
	 * @param queue
	 * @param slot
	 */
	protected void delayFirstAndBackfill(Queue<Job> queue, Schedule schedule, Slot slot) {
		Instant now = Clock.instance().now();
		if (schedule.getActualFreeness() > 0) {
			if (queue.size() > 1) {
				/*
				 * determine the "shadow time" of the first job, i.e. the time
				 * in the future when the first job in the queue could be
				 * started at earliest
				 */
				Instant shadowTime = slot.getAdvent();

				/*
				 * loop over all jobs in the queue, except for the shadowed one
				 * and try to backfill the first which is matching with respect
				 * to one of the two backfiling conditions
				 */
				Iterator<Job> iter = queue.iterator();
				iter.next();

				Period backfillingArea = new Period(now, shadowTime);

				Job jobToSchedule = null;
				Slot possibleSlot = null;

				while (iter.hasNext()) {
					jobToSchedule = iter.next();

					/*
					 * try condition 1: backfilling of short jobs
					 */
					possibleSlot = this.backfillWithinShadowTime(schedule,
							jobToSchedule, backfillingArea);
					if (possibleSlot != null) {
						break;
					} // if
					else {
						/*
						 * try condition 2: backfilling of narrow jobs
						 */
						int delayedJobNumberOfRequestedResources = queue.peek()
								.getDescription()
								.getNumberOfRequestedResources();
						possibleSlot = this.backfillWithinExtraNodes(schedule,
								jobToSchedule, slot,
								delayedJobNumberOfRequestedResources);
						if (possibleSlot != null) {
							break;
						} // if
					} // else
				} // while
				try {
					super.getStrategyListener().advise(jobToSchedule,
							possibleSlot);
				} // try
				catch (DecisionVetoException e) {
					String msg = "decision vetoed: properties in slot \""
							+ possibleSlot + "\" not accepted for job \""
							+ jobToSchedule + "\"; skipping backfilling";
					log.info(msg, e);
				} // catch
			} // if
		} // if
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param schedule
	 * @param job
	 * @param backfillingArea
	 * @return
	 */
	private Slot backfillWithinShadowTime(Schedule schedule, Job job,
			Period backfillingArea) {
		Slot result = null;
		Instant now = this.determineRequestedStartTime(job);

		/*
		 * determine the job's duration, starting at the backfilling area
		 */
		Instant jobStartTime = backfillingArea.getAdvent();
		Instant jobEndTime = TimeHelper.add(jobStartTime, job
				.getDescription().getEstimatedRuntime());
		Period jobDuration = new Period(jobStartTime, jobEndTime);

		/*
		 * if the job fits at all, try to find a valid slot
		 */
		if (jobDuration.isWithin(backfillingArea)) {
			result = StrategyHelper.determineSoonestSlot(schedule, job, now,
					false);
		} // if

		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param schedule
	 * @param job
	 * @param slot
	 * @return
	 */
	private Slot backfillWithinExtraNodes(Schedule schedule, Job job,
			Slot slot, int nonExtraNodes) {
		Slot result = null;
		Instant now = this.determineRequestedStartTime(job);

		/*
		 * determine the number of "extra nodes", i.e. those resources that are
		 * not used by the first job in the queue.
		 */
		int extraNodes = slot.getResources().size() - nonExtraNodes;

		/*
		 * if the job fits at all, try to find a valid slot
		 */
		if (job.getDescription().getNumberOfRequestedResources() <= extraNodes) {
			result = StrategyHelper.determineSoonestSlot(schedule, job, now,
					false);
		} // if

		return result;
	}

	@Override
	public JobControlBlock getNextDecision(Queue<Job> queue, Schedule schedule) {

		JobControlBlock jcb = null;
		Slot possibleSlot = null;
		Job job = null;
		
		if (!queue.isEmpty()) {
			job = queue.peek();
			
			Instant now = this.determineRequestedStartTime(job);

			/*
			 * try to schedule the first job, equal to determineSoonestSlot
			 */
			possibleSlot = StrategyHelper.determineSoonestSlot(schedule, job, now,	true);
		}
		
		jcb = new JobControlBlock(possibleSlot,job);
		
		return jcb;
	}
	
	
	
	@Override
	public JobControlBlock getNextDecision(Queue<Job> queue, TemporalSchedule schedule) {
		JobControlBlock jcb = null;
		Slot possibleSlot = null;
		Job job = null;
		
		if (!queue.isEmpty()) {
			job = queue.peek();
			
			Instant now = this.determineRequestedStartTime(job);

			/*
			 * try to schedule the first job, equal to determineSoonestSlot
			 */
			possibleSlot = StrategyHelper.determineSoonestSlot(schedule, job, now,	true);
		}
		
		jcb = new JobControlBlock(possibleSlot,job);
		
		return jcb;
		
	}

}
