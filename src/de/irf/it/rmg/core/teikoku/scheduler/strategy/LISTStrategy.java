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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.Pair;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * TODO: not yet commented
 * <p/>
 * Note that this implementation does not support decision veto handling.
 *
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * @see StrategyListener#advise(Job, Job)
 */
public class LISTStrategy extends AbstractStrategy {

		
    /**
     * The default log facility for this class, using the <a
     * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
     *
     * @see org.apache.commons.logging.Log
     */
    final private static Log log = LogFactory.getLog(LISTStrategy.class);

    /**
     * TODO: not yet commented
     */
    private List<Pair<Job, Slot>> processedJobs;

    public LISTStrategy() {
        super();
        this.processedJobs = new ArrayList<Pair<Job, Slot>>();
    }

    /**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public LISTStrategy(LISTStrategy another) { 
		super(another);
		this.processedJobs = new ArrayList<Pair<Job, Slot>>();
		for(Iterator<Pair<Job, Slot>> it = another.processedJobs.iterator(); it.hasNext();) 
			this.processedJobs.add(it.next());
	} 
	
	/**
	 * Clone 
	*/
    @Override
	public Strategy clone() {
		return new LISTStrategy(this);
	} 
    
    /**
     * Determines the requested start time for a given job.
     * <p/>
     * By default, the current system time is returned here.
     *
     * @param job The job the requested start time is to be determined for.
     * @return The requested start time for a given job.
     */
    protected Instant determineRequestedStartTime(Job job) {
        return Clock.instance().now();
    }

    // -------------------------------------------------------------------------
    // Implementation/Overrides for de.irf.it.rmg.core.teikoku.scheduler.Strategy
    // -------------------------------------------------------------------------

    /*
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.scheduler.Strategy#decide(java.util.Queue,
      *      de.irf.it.rmg.core.teikoku.site.schedule.Schedule)
      */

    public void decide(Queue<Job> queue, Schedule schedule) {
    	    	
        this.processedJobs.clear();
        if (!queue.isEmpty()) {
            Iterator<Job> iter = queue.iterator();
            /*
                * repeat decision process for all jobs in the queue
                */
            boolean finished = false;
            while (!finished) {
                if (iter.hasNext()) {
                    Job jobToSchedule = iter.next();

                    Instant now = this.determineRequestedStartTime(jobToSchedule);

                    /*
                          * try to schedule the given job
                          */
                    Slot possibleSlot = StrategyHelper.determineSoonestSlot(schedule,
                            jobToSchedule, now, false);

                    if (possibleSlot != null) {
                        /*
                               * break the loop: a fitting job has been found
                               */
                        finished = true;
        
                    } // if
                    this.processedJobs.add(new Pair<Job, Slot>(jobToSchedule,
                            possibleSlot));
                } // if
                else {
                    finished = true;
                } // else
            } // while

            /*
                * send collected decisions for this iteration to strategy listener
                */
            for (Pair<Job, Slot> p : this.processedJobs) {
                try {
                    super.getStrategyListener().advise(p.getLeft(),
                            p.getRight());
                } // try
                catch (DecisionVetoException e) {
                    String msg = "decision vetoed: properties in slot \""
                            + p.getRight() + "\" not accepted for job \""
                            + p.getLeft()
                            + "\"; skipping current and trying backfill";
                    log.info(msg, e);
                } // catch
            } // for
        } // if
        
    }
    
    @Override
    public JobControlBlock getNextDecision(final Queue<Job> queue, final Schedule schedule){
		/* TODO: implement */
		return null;
	}
    @Override
    public JobControlBlock getNextDecision(final Queue<Job> queue, final TemporalSchedule schedule){
		/* TODO: implement */
		return null;
	}
}
