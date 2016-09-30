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
package de.irf.it.rmg.core.teikoku.kernel.events;


import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
//import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
 *         Papaspyrou</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public class JobReleasedEvent extends Event {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Job releasedJob;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private ActivityDelegationSource delegationSource;

	/**
	 * TODO: not yet commented
	 * 
	 * @param localSubmissionComponent
	 * @param timestamp
	 * @param releasedJob
	 * @param location
	 * 
	 */
	public JobReleasedEvent(
			final ActivityDelegationSource delegationSource,
			final Instant timestamp, final Job releasedJob, final Site location) {
		super(timestamp, location.getUUID().toString(), location);
		this.releasedJob = releasedJob;
		this.delegationSource = delegationSource;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Job getReleasedJob() {
		return this.releasedJob;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param releasedJob
	 */
	public void setReleasedJob(final Job releasedJob) {
		this.releasedJob = releasedJob;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public ActivityDelegationSource getdelegationSource() {
		return this.delegationSource;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param localSubmissionComponent
	 */
	public void setDelegationSource(
			final ActivityDelegationSource delegationSource) {
		this.delegationSource = delegationSource;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.sim.kuiga.Event
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.kernel.events.Event#getType()
	 */
	@Override
	public int getOrdinal() {
		return EventType.JOB_RELEASED.ordinal();
	}
}
