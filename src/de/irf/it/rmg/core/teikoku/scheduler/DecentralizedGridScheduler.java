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
package de.irf.it.rmg.core.teikoku.scheduler;

import java.util.Map;

import org.apache.log4j.Logger;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.distribution.DistributionTarget;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
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
public class DecentralizedGridScheduler extends AbstractScheduler
		implements Scheduler, StrategyListener, ActivityDelegationSource,
		DistributionTarget {
	
		
	/**
	 * The default log facility for this class, using the <a
	 * href="http://logging.apache.org/log4j">Apache "log4j" API</a>.
	 * 
	 * @see org.apache.log4j.Logger
	 */
	final private static Logger log = Logger
			.getLogger(DecentralizedGridScheduler.class);

	/**
	 * TODO: not yet commented Creates a new instance of this class, using the
	 * given parameters.
	 * 
	 */
	public DecentralizedGridScheduler()
			throws InstantiationException {
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public DecentralizedGridScheduler(DecentralizedGridScheduler another) {
		super(another);
	}
	
	/**
	 * Creates a clone copy 
	 */
	public Scheduler clone() {
		return new DecentralizedGridScheduler(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#activate()
	 */
	public void activate() {
		super.getLocalStrategy().decide(super.getQueue(), super.getSchedule());
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener#advise(de.irf.it.rmg.core.teikoku.common.Job,
	 *      de.irf.it.rmg.core.teikoku.common.Job)
	 */
	public void advise(Job jobToSchedule, Slot possibleSlot)
			throws DecisionVetoException {
		/*
		 * check whether the job could be scheduled
		 */
		if (possibleSlot == null) {
//			try {
//				super.getSite().getDecisionMaker().offer(this, jobToSchedule);				
//			}
//			catch (OfferingVetoException e) {
//				/*
//				 * if job has not been accepted the decision can also not be
//				 * made.
//				 */
//				throw new DecisionVetoException();
//			}
//			super.getQueue().remove(jobToSchedule);
		} // if
		else {
			/*
			 * yes: remove from corresponding queue and add to schedule
			 */		
			try {
				
				Instant startTime = possibleSlot.getAdvent();
				Period duration = new Period(startTime, TimeHelper.add(
						startTime, jobToSchedule.getRuntimeInformation()
								.getRemainingRunTime()));
				jobToSchedule.setDuration(duration);
				jobToSchedule.setResources(possibleSlot.getResources()
						.createSubSetWith(
								jobToSchedule.getDescription()
										.getNumberOfRequestedResources()));
				
				super.getSchedule().addJob(jobToSchedule);	
				
				super.getQueue().remove(jobToSchedule);
				
				jobToSchedule.getLifecycle().addEpisode(State.SCHEDULED);
				
			}
			catch (IllegalScheduleException e) {
				e.printStackTrace();
			}
			catch (InvalidTimestampException e) {
				e.printStackTrace();
			}
			catch (IllegalOccupationException e) {
				e.printStackTrace();
			}
			/*
			 * create runtime system events only if no exception occured
			 */
			super.createEvents(jobToSchedule,State.STARTED);
		} // else
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.AbstractScheduler
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.AbstractScheduler#initialize()
	 */
	@Override
	public void initialize()
			throws InitializationException {
		super.setInitialized(false);
		super.initialize();
		super.setInitialized(true);
	}

	public void delegate(Job job) throws AcceptanceVetoException {
		// TODO Automatisch erstellter Methoden-Stub
		
	}

	public Map<String, Object> getPublicInformation() {
		// There is no publicInformation
		return null;
	}

}
