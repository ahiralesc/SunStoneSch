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

import org.apache.commons.lang.builder.ToStringBuilder;

import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeHelper;

/**
 * The class slot is a special time interval that can be used to model free
 * segments within a schedule. It includes additionally the list of free
 * resources during the specifies time interval.
 * 
 * @author Grimme, Lepping, Papaspyrou
 * 
 */
public class Slot extends Period {

	private ResourceBundle resources;

	/**
	 * TODO: not yet commented
	 * 
	 */
	public Slot() {
		super();
	}
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * @param startTime
	 * @param endTime
	 * @throws IllegalArgumentException
	 */
	public Slot(Instant startTime, Instant endTime)
			throws IllegalArgumentException {
		super(startTime, endTime);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param advent
	 * @param length
	 * @throws IllegalArgumentException
	 */
	public Slot(Instant advent, Distance length)
			throws IllegalArgumentException {
		super(advent, TimeHelper.add(advent, length));
	}
	
	
	/**
	 * Clone
	 
	public Slot clone() {
		return new Slot(this);
	} */
	
	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public ResourceBundle getResources() {
		return this.resources;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param resources
	 */
	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.TimeInterval
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.common.TimeInterval#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		// if (other == null) {
		// return false;
		// } // if
		// if (this == other) {
		// return true;
		// } // if
		// if (other instanceof Slot) {
		// Slot candidate = (Slot) other;
		// return new EqualsBuilder().appendSuper(super.equals(candidate))
		// .append(this.resources,
		// candidate.resources).isEquals();
		// } // if
		// else {
		// return false;
		// } // else
		return super.equals(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.TimeInterval#hashCode()
	 */
	@Override
	public int hashCode() {
		// return new HashCodeBuilder().appendSuper(super.hashCode()).append(
		// this.resources).toHashCode();
		return super.hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).append(
				this.resources).toString();
	}

}
