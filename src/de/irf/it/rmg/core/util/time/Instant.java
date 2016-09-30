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
package de.irf.it.rmg.core.util.time;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
 *         Papaspyrou</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
final public class Instant
		implements Comparable<Instant> {

	/**
	 * TODO: not yet commented
	 */
	final public static long ETERNITY = -1;

	/**
	 * TODO: not yet commented
	 */
	private long timestamp;

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 */
	Instant(final long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public Instant(Instant another) {
		this.timestamp = another.timestamp;
	}
	
	/**
	 * Clones this object.
	 */
	public Instant clone() {
		return new Instant(this);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public long timestamp() {
		return this.timestamp;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public boolean isEternity() {
		return this.timestamp == ETERNITY ? true : false;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param other
	 * @return
	 */
	public boolean before(final Instant other) {
		return this.compareTo(other) < 0;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param other
	 * @return
	 */
	public boolean after(final Instant other) {
		return this.compareTo(other) > 0;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	public int compareTo(final Instant other) {
		int result = 0;

		/*
		 * check for eternity comparison
		 */
		if (this.isEternity()) {
			result = 1;
		} // if
		else if (other.isEternity()) {
			result = -1;
		} // else if
		else {
			/*
			 * no: do a "real" comparison
			 */
			if (this.timestamp < other.timestamp) {
				result = -1;
			} // if
			else if (this.timestamp > other.timestamp) {
				result = 1;
			} // else if
			else {
				result = 0;
			} // else
		} // else

		return result;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */

	@Override
	public boolean equals(final Object candidate) {
		boolean result;

		if (candidate == null) {
			result = false;
		} // if
		else if (this == candidate) {
			result = true;
		} // else if
		else if (candidate instanceof Instant) {
			final Instant other = ( Instant )candidate;
			result = new EqualsBuilder()
					.append(this.timestamp, other.timestamp).isEquals();
		} // else if
		else {
			result = false;
		} // else

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO: not yet implemented.
		return new HashCodeBuilder().append(this.timestamp).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append(
						this.timestamp == ETERNITY ? "eternity (" + ETERNITY
								+ ")" : Long.toString(this.timestamp))
				.toString();
	}
}
