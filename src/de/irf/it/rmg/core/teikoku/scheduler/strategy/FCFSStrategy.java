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
// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2006 by the
 *   Robotics Research Institute (Information Technology Section)
 *   Dortmund University, Germany
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
package de.irf.it.rmg.core.teikoku.scheduler.strategy;

import java.util.Queue;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class FCFSStrategy extends AbstractStrategy {

	public FCFSStrategy() {
		super();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	*/
	public FCFSStrategy(FCFSStrategy another) { 
		this();	
	} 
	
	/**
	 * Clone 
	*/
	@Override
	public Strategy clone() {
		return new FCFSStrategy(this);
	} 
	
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(FCFSStrategy.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Strategy#decide(java.util.Queue,
	 *      de.irf.it.rmg.core.teikoku.site.schedule.Schedule)
	 */
	public void decide(Queue<Job> queue, Schedule schedule) {
				
		if (!queue.isEmpty()) {
			Job jobToSchedule = queue.peek();
			
			/*
			 * try to schedule the given job
			 */
			Slot possibleSlot = null;

			/*
			 * determine start (now) and end (eternity) time and use as search
			 * area for schedule
			 */
			Instant startTime = this.determineRequestedStartTime(jobToSchedule);
			Instant endTime = TimeFactory.newEternity();
			Period searchArea = new Period(startTime, endTime);

			try {
				//Experimental for the findNextFreePeriod-Method
				searchArea=schedule.findNextFreePeriod(jobToSchedule.getDescription()
								.getNumberOfRequestedResources(), searchArea);
				
				if (searchArea.getAdvent().equals(startTime)){
					/*
					 * try to find a fitting slot for the given job
					 */
					possibleSlot = schedule.findNextFreeSlot(jobToSchedule
							.getDescription().getEstimatedRuntime(),
							jobToSchedule.getDescription()
									.getNumberOfRequestedResources(), searchArea);
				}
			} // try
			catch (InvalidTimestampException e) {
				String msg = "scheduling failed: " + e.getMessage();
				log.error(msg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String msg = "scheduling failed: " + e.getMessage();
				log.error(msg, e);
			} // catch

			/*
			 * check whether the slot provides the requested start time (now)
			 */
			
			
			
			
		//	if (!possibleSlot.getAdvent().equals(startTime)) {
				/*
				 * no: the job cannot be started immediately, thus return null
				 */
			//	possibleSlot = null;
			//} // if
			
			System.out.println("Time : " + Clock.instance().now().timestamp() + " queue size " + queue.size() + "jobs " + queuedJobs(queue));
			
			/*
			 * notify (advise) the listener
			 */
			try {
				super.getStrategyListener().advise(jobToSchedule, possibleSlot);
			}
			catch (DecisionVetoException e) {
				String msg = "decision vetoed: properties in slot \""
						+ possibleSlot + "\" not accepted for job \""
						+ jobToSchedule + "\"; skipping current.";
				log.info(msg, e);
			}
		} // if
		
	}
	
	
	private String queuedJobs(Queue<Job> queue){
		String list = new String();
		for(Job j :queue){
			list += j.getName() + ",";
		}
		
		return list;
	}

	
	/**
	 * 
	 * @param queue			the jobs to insert
	 * @param schedule		the schedule were the job is to be inserted
	 * @return				The job that the strategy selected 
	 */
	@Override
	public JobControlBlock getNextDecision(final Queue<Job> queue, final Schedule schedule){
		Slot possibleSlot = null;
		JobControlBlock jcb = null;
		Job jobToSchedule = null;
		
		if (!queue.isEmpty()) {
			jobToSchedule = queue.peek();
			Instant startTime = this.determineRequestedStartTime(jobToSchedule);
			Instant endTime = TimeFactory.newEternity();
			Period searchArea = new Period(startTime, endTime);

			try {
				/*
				 * try to find a fitting slot for the given job
				 */
				possibleSlot = schedule.findNextFreeSlot(jobToSchedule
						.getDescription().getEstimatedRuntime(),
						jobToSchedule.getDescription()
								.getNumberOfRequestedResources(), searchArea);
			} // try
			catch (InvalidTimestampException e) {
				String msg = "scheduling failed: " + e.getMessage();
				log.error(msg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String msg = "scheduling failed: " + e.getMessage();
				log.error(msg, e);
			} // catch
		} // if
		
		jcb = new JobControlBlock(possibleSlot,jobToSchedule);
		return jcb;
	}

	
	/**
	 * 
	 * @param queue			the jobs to insert
	 * @param schedule		the schedule were the job is to be inserted
	 * @return				The job that the strategy selected 
	 */
	@Override
	public JobControlBlock getNextDecision(final Queue<Job> queue, final TemporalSchedule schedule){
		Slot possibleSlot = null;
		JobControlBlock jcb = null;
		Job jobToSchedule = null;
		
		if (!queue.isEmpty()) {
			jobToSchedule = queue.peek();
			Instant startTime = this.determineRequestedStartTime(jobToSchedule);
			Instant endTime = TimeFactory.newEternity();
			Period searchArea = new Period(startTime, endTime);

			try {
				/*
				 * try to find a fitting slot for the given job
				 */
				possibleSlot = schedule.findNextFreeSlot(jobToSchedule
						.getDescription().getEstimatedRuntime(),
						jobToSchedule.getDescription()
								.getNumberOfRequestedResources(), searchArea);
			} // try
			catch (InvalidTimestampException e) {
				String msg = "scheduling failed: " + e.getMessage();
				log.error(msg, e);
			} // catch
			catch (IllegalOccupationException e) {
				String msg = "scheduling failed: " + e.getMessage();
				log.error(msg, e);
			} // catch
		} // if
		
		jcb = new JobControlBlock(possibleSlot,jobToSchedule);
		return jcb;
	}
	
	
	/**
	 * Determines the requested start time for a given job.
	 * 
	 * By default, the current system time is returned here.
	 * 
	 * @param job
	 *            The job the requested start time is to be determined for.
	 * @return The requested start time for a given job.
	 */
	protected Instant determineRequestedStartTime(Job job) {
		return Clock.instance().now();
	}
}
