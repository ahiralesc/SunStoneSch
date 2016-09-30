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
package de.irf.it.rmg.sim.kuiga;


import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
 *         Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
final public class Clock {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private static final long PROGRESSION = 20000;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private static Clock instance;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Instant advent;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Instant cessation;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Instant now;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Distance progression;

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public static Clock instance() {
		if (instance == null) {
			instance = new Clock(TimeFactory.newMoment(0), TimeFactory
					.newFinite(PROGRESSION));
		} // if
		return instance;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param timestamp
	 * @param progression
	 */
	private Clock(final Instant timestamp, final Distance progression) {
		this.now = timestamp;
		this.progression = progression;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Instant now() {
		return this.now;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Instant advent() {
		return this.advent;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Instant cessation() {
		return this.cessation;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Distance progression() {
		return this.progression;
	}

	/**
	 * TODO: not yet commented
	 * 
	 */
	public void advance() {
		this.advance(TimeHelper.add(this.now, this.progression));
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 */
	public void advance(final Instant timestamp) {
		/*
		 * Check whether the advance request is valid, i.e. verify that the
		 * timestamp requested (a) is not null and (b) lies in the future.
		 */
		if (timestamp == null || timestamp.before(this.now)) {
			final String msg = "advance invalid : current instant (" + this.now
					+ ") after requested instant (" + timestamp + ")";
			throw new IllegalArgumentException(msg);
		} // if
		else {
			/*
			 * yes: Advance time.
			 */
			this.now = timestamp;
		} // if
	}
}
