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


import de.irf.it.rmg.core.teikoku.common.Reservation;
import de.irf.it.rmg.core.teikoku.site.Site;
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
public class ReturnOfLentResourcesEvent extends Event {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Reservation reservation;

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @param reservation
	 * @param location
	 */
	public ReturnOfLentResourcesEvent(final Instant timestamp,
			final Reservation reservation, final Site location) {
		super(timestamp, location.getUUID().toString(), location);
		this.reservation = reservation;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Reservation getReservation() {
		return this.reservation;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param reservation
	 */
	public void setReservation(final Reservation reservation) {
		this.reservation = reservation;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.sim.kuiga.Event
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.sim.kuiga.Event#getOrdinal()
	 */
	@Override
	protected int getOrdinal() {
		return EventType.RETURN_OF_LENT_RESOURCES.ordinal();
	}

}
