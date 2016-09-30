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
package de.irf.it.rmg.core.teikoku.common;

import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleSortedSetImpl;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;

/**
 * This class models, similar to the class job, a reservation as occupation
 * within the schedule. Compared to a OldJob, a Reservation can expire after a Time
 * 
 * @author Grimme, Lepping, Papaspyrou
 * 
 */
public class Reservation extends Occupation {

	/**
	 * The expiration time of this reservation.
	 */
	private Instant expirationTime;

	
	public Reservation(Instant startTime, Instant endTime, ResourceBundle resourceBundle) {
		super(new Period(startTime, endTime), resourceBundle);

		expirationTime = endTime; // by default
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public Reservation(Reservation another) {
		// Clone the super class duration
		this.setDuration(super.getDuration().clone());
		ResourceBundle resources = new ResourceBundleSortedSetImpl();
		
		//Clone the super class resources
		for(Resource r :super.getResources())
			resources.add(r.clone());
		super.setResources(resources);
		
		//Clone the expiration time
		this.expirationTime = another.expirationTime.clone();
	}
	
	
	/**
	 * Creates a clone copy 
	 */
	public Reservation clone() {
		return new Reservation(this);
	}
	
	/**
	 * Gest the expiration time of this reservation
	 * 
	 * @return the expiration time
	 */
	public Instant getExpirationTime() {
		return this.expirationTime;
	}

	/**
	 * Sets the expiration time of this reservation
	 * 
	 * @param expirationTime
	 *            the expiration time
	 */
	public void setExpirationTime(Instant expirationTime) {
		this.expirationTime = expirationTime;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.Occupation#toString()
	 */
	@Override
	public String toString() {
		// TODO: not yet implemented.
		return super.toString();
	}

}
