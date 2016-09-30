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
final public class Distance
		implements Comparable<Distance> {

	/**
	 * TODO: not yet commented
	 * 
	 */
	final public static long PERPETUAL = -1;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private long length;

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param length
	 */
	public Distance(final long length) {
		this.length = length;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public Distance(Distance another) {
		this.length = another.length;
	}
	
	/**
	 * Creates a clone copy 
	 */
	public Distance clone() {
		return new Distance(this);
	}
	

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public long length() {
		return this.length;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public boolean isPerpetual() {
		return this.length == PERPETUAL ? true : false;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public boolean isZero() {
		return this.length == 0 ? true : false;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param than
	 * @return
	 */
	public boolean shorter(final Distance than) {
		return this.compareTo(than) < 0;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param than
	 * @return
	 */
	public boolean longer(final Distance than) {
		return this.compareTo(than) > 0;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Distance other) {
		int result = 0;

		/*
		 * check for perpetual comparison
		 */
		if (this.length == PERPETUAL) {
			result = 1;
		} // if
		else if (other.length == PERPETUAL) {
			result = -1;
		} // else if
		else {
			/*
			 * no: do a "real" comparison
			 */
			// PERF: CompareToBuilder too slow for trivial comparison
			if (this.length < other.length) {
				result = -1;
			} // if
			else if (this.length > other.length) {
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
		if (this == candidate) {
			result = true;
		} // if
		if (candidate instanceof Instant) {
			final Distance other = ( Distance )candidate;
			result = new EqualsBuilder().append(this.length, other.length)
					.isEquals();
		} // if
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
		return new HashCodeBuilder().append(this.length).toHashCode();
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
						this.length == PERPETUAL ? "perpetual (" + PERPETUAL
								+ ")" : Long.toString(this.length)).toString();
	}
}
