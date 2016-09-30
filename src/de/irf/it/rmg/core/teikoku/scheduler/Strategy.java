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
package de.irf.it.rmg.core.teikoku.scheduler;

import java.util.Queue;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;

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
public interface Strategy {

	/**
	 * Registers an interested listener at this strategy, which is notified
	 * whenever a decision has been made.
	 * 
	 * Supporting more than one listener at a time is optional.
	 * 
	 * @param strategyListener
	 *            The listener to be registered for notification at this
	 *            strategy. Note that, depending on the strategy implementation,
	 *            this may overwrite previously registered listeners without
	 *            notice.
	 * 
	 * @see StrategyListener
	 */
	void register(StrategyListener strategyListener);

	/**
	 * Decides upon the given queue and schedule which job(s) to schedule next.
	 * 
	 * The strategy is allowed to decide upon more than one job at a time, must
	 * however notify registered {@link StrategyListener}s upon <em>every</em>
	 * decision it makes.
	 * 
	 * It is mandatory that the given parameters are <strong>left unaltered</strong>,
	 * with the only exception that those jobs that a positive decision (i.e.,
	 * they could be scheduled) could be made for may be changed accordingly and
	 * thus affecting the queue content.
	 * 
	 * @param queue
	 *            The queue of {@link Job}s this strategy should decide upon.
	 * @param schedule
	 *            The schedule this strategy bases it's decision upon.
	 * 
	 * @see Schedule
	 * @see StrategyListener#advise(Job, Job)
	 */
	void decide(final Queue<Job> queue, final Schedule schedule);

	void handleCompletion(Job job);
	
	void setSite(Site site);
	
	Site getSite();
	
	/**
	 * 
	 * @param queue
	 * @param schedule
	 * @return
	 */
	JobControlBlock getNextDecision(final Queue<Job> queue, final Schedule schedule);
	
	/**
	 * 
	 * @param queue
	 * @param schedule
	 * @return
	 */
	JobControlBlock getNextDecision(final Queue<Job> queue, final TemporalSchedule schedule);
	
	/**
	 * Creates a clone copy of the scheduler
	 * @return
	 */
	public Strategy clone();

}
