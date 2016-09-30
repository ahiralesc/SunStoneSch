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
 * Copyright (c) 2007 by the
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

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;

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
public class StrategyHelper {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(StrategyHelper.class);

	/**
	 * Finds a slot for starting the given job, if possible.
	 * 
	 * In detail, this method tries to find a slot in the schedule allowing the
	 * job provided in the parameter to be started now or as soon as possible.
	 * 
	 * The requested start time for the given job is determined by calling
	 * {@link #determineRequestedStartTime(OldJob)}, which has to be
	 * overwritten by subclasses for different than default behaviour.
	 * 
	 * In the former case, the behaviour of this method directly corresponds to
	 * the <em>First-Come-First-Serve</em> (FCFS) scheduling algorithm: if the
	 * search is successful, a possible slot is returned with the determined
	 * start time and (estimated) end time and the set of resources that could
	 * be occupied by the job. Otherwise, nothing is returned.
	 * 
	 * In the latter case, the behaviour of this method is characterized by an
	 * <em>As-Soon-As-Possible</em> (ASAP) policy: searching will
	 * <strong>always</strong> yield a result; however, such a slot is not
	 * anymore guaranteed to start now, but may be delayed until the end of the
	 * schedule.
	 * 
	 * Which of the two cases is applied depends on the setting of the
	 * "allowTardiness" parameter.
	 * 
	 * @param schedule
	 *            The schedule to which the given job is to be added.
	 * @param job
	 *            The job to be scheduled immediately.
	 * @param allowTardiness
	 *            Denotes which behaviour is to be used for the next search.
	 *            <code>false</code> entails in the first (FCFS) case,
	 *            <code>true</code> in the second (ASAP).
	 * @return A slot with start time, end time and occupied resources
	 *         parameters matching the job's requirements, iif the job could be
	 *         scheduled; <code>null</code>, otherwise.
	 * 
	 * @see Schedule#findNextFreeSlot(long, int,
	 *      de.irf.it.rmg.core.teikoku.common.Period)
	 * @see #determineRequestedStartTime(OldJob)
	 * @see Job#durationFrom(Instant)
	 * @see Job#getNumberOfRequestedResources()
	 * @see Slot
	 */
	public static Slot determineSoonestSlot(Schedule schedule, Job job,
			Instant startTime, boolean allowTardiness) {
		Slot result = null;

		/*
		 * determine the allowed search area for the given job
		 */
		Period searchArea = null;
		if (allowTardiness) {
			/*
			 * tardiness : search within now and forever
			 */
			Instant endTime = TimeFactory.newEternity();
			searchArea = new Period(startTime, endTime);
		} // if
		else {
			/*
			 * no tardiness: search within now and runtime estimation
			 */
			 searchArea = new Period(startTime, TimeHelper.add(startTime, job.getDescription().getEstimatedRuntime()));
			//long rt = TimeHelper.toMilliseconds(distance)((SWFJob)job).getRunTime();
			//Distance strt = job.getDescription().getEstimatedRuntime();
			//searchArea = new Period(startTime, TimeHelper.add(startTime, strt ));
		} // else

		try {
			/*
			 * try to find a fitting slot for the given job
			 */
			result = schedule.findNextFreeSlot(job.getDescription().getEstimatedRuntime(), job.getDescription()
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

		return result;
	}
	
	
	
	/**
	 * Finds a slot for starting the given job, if possible.
	 * 
	 * In detail, this method tries to find a slot in the schedule allowing the
	 * job provided in the parameter to be started now or as soon as possible.
	 * 
	 * The requested start time for the given job is determined by calling
	 * {@link #determineRequestedStartTime(OldJob)}, which has to be
	 * overwritten by subclasses for different than default behaviour.
	 * 
	 * In the former case, the behaviour of this method directly corresponds to
	 * the <em>First-Come-First-Serve</em> (FCFS) scheduling algorithm: if the
	 * search is successful, a possible slot is returned with the determined
	 * start time and (estimated) end time and the set of resources that could
	 * be occupied by the job. Otherwise, nothing is returned.
	 * 
	 * In the latter case, the behaviour of this method is characterized by an
	 * <em>As-Soon-As-Possible</em> (ASAP) policy: searching will
	 * <strong>always</strong> yield a result; however, such a slot is not
	 * anymore guaranteed to start now, but may be delayed until the end of the
	 * schedule.
	 * 
	 * Which of the two cases is applied depends on the setting of the
	 * "allowTardiness" parameter.
	 * 
	 * @param schedule
	 *            The schedule to which the given job is to be added.
	 * @param job
	 *            The job to be scheduled immediately.
	 * @param allowTardiness
	 *            Denotes which behaviour is to be used for the next search.
	 *            <code>false</code> entails in the first (FCFS) case,
	 *            <code>true</code> in the second (ASAP).
	 * @return A slot with start time, end time and occupied resources
	 *         parameters matching the job's requirements, iif the job could be
	 *         scheduled; <code>null</code>, otherwise.
	 * 
	 * @see Schedule#findNextFreeSlot(long, int,
	 *      de.irf.it.rmg.core.teikoku.common.Period)
	 * @see #determineRequestedStartTime(OldJob)
	 * @see Job#durationFrom(Instant)
	 * @see Job#getNumberOfRequestedResources()
	 * @see Slot
	 */
	public static Slot determineSoonestSlot(TemporalSchedule schedule, Job job,
			Instant startTime, boolean allowTardiness) {
		Slot result = null;

		/*
		 * determine the allowed search area for the given job
		 */
		Period searchArea = null;
		if (allowTardiness) {
			/*
			 * tardiness : search within now and forever
			 */
			Instant endTime = TimeFactory.newEternity();
			searchArea = new Period(startTime, endTime);
		} // if
		else {
			/*
			 * no tardiness: search within now and runtime estimation
			 */
			searchArea = new Period(startTime, TimeHelper
					.add(startTime, job.getDescription().getEstimatedRuntime()));
		} // else

		try {
			/*
			 * try to find a fitting slot for the given job
			 */
			result = schedule.findNextFreeSlot(job.getDescription().getEstimatedRuntime(), job.getDescription()
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

		return result;
	}

	
	
	
}
