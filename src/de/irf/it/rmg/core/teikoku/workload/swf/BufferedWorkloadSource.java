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
package de.irf.it.rmg.core.teikoku.workload.swf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.WorkloadException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.WorkloadFilter;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSource;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;

public class BufferedWorkloadSource
        implements WorkloadSource {

    /**
     * The default log facility for this class, using the <a
     * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
     *
     * @see org.apache.commons.logging.Log
     */
    final private static Log log = LogFactory.getLog(BufferedWorkloadSource.class);

    /**
     * TODO: not yet commented
     */
    private WorkloadSource bufferedSource;

    /**
     * TODO: not yet commented
     */
    private Job bufferedJob = null;

    /**
     * TODO: not yet commented
     * Creates a new instance of this class, using the given parameters.
     *
     * @param ws
     */
    public BufferedWorkloadSource(WorkloadSource ws) {
        this.bufferedSource = ws;
    }

    /*
      * (non-Javadoc)
      * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSource#inspectNextJob()
      */
    public Job inspectNextJob()
            throws WorkloadException {
        return this.bufferedSource.inspectNextJob();
    }

    /*
      * (non-Javadoc)
      * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSource#getNextJob()
      */
    public Job fetchNextJob()
            throws WorkloadException {

        Job returnJob = null;
        if (bufferedJob == null) {

            this.bufferedJob = this.bufferedSource.fetchNextJob();

        } // if
        if (this.bufferedJob != null) {
            Instant initialStarttime = Clock.instance().advent();
            Distance relativeReleasetime = TimeHelper.distance(TimeFactory.newMoment(0), this.bufferedJob.getReleaseTime());
            Instant realReleasetime = TimeHelper.add(initialStarttime, relativeReleasetime);

            if (!Clock.instance().now().before(realReleasetime)) {
                returnJob = this.bufferedJob;
                this.bufferedJob = null;
            } // if

        } // if

        return returnJob;
    }

    /*
      * (non-Javadoc)
      * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSource#initialize(de.irf.it.rmg.core.teikoku.workload.WorkloadFilter[])
      */
    public void initialize(WorkloadFilter[] filters)
            throws InitializationException {
        this.bufferedSource.initialize(filters);
    }
}
