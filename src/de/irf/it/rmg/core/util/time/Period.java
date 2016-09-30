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
public class Period {

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
	private Distance distance;

	/**
	 * TODO: not yet commented
	 * 
	 * @throws IllegalArgumentException
	 */
	public Period()
			throws IllegalArgumentException {
		this(TimeFactory.newMoment(0), TimeFactory.newEternity());
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public Period(Period another) {
		this.advent = another.advent.clone();
		this.cessation = another.cessation.clone();
		this.distance = another.distance.clone();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param advent
	 * @throws IllegalArgumentException
	 */
	public Period(final Instant advent)
			throws IllegalArgumentException {
		this(advent, TimeFactory.newEternity());
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param advent
	 * @param cessation
	 * @throws IllegalArgumentException
	 */
	public Period(final Instant advent, final Instant cessation)
			throws IllegalArgumentException {
		if (this.checkValidity(advent, cessation)) {
			this.advent = advent;
			this.cessation = cessation;
			this.distance = TimeHelper.distance(this.advent, this.cessation);
		} // if
		else {
			final String msg = "period illegal: advent must be before cessation";
			throw new IllegalArgumentException(msg);
		} // else
	}
	
	
	/**
	 * Creates a clone copy
	 */
	public Period clone() {
		return new Period(this);
	}
	

	/**
	 * TODO: not yet commented
	 * 
	 * @param advent
	 * @param cessation
	 * @return
	 */
	private boolean checkValidity(final Instant advent, final Instant cessation) {
		return advent.before(cessation) || advent.equals(cessation);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Instant getAdvent() {
		return this.advent;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param advent
	 */
	public void setAdvent(final Instant advent)
			throws IllegalArgumentException {
		if (this.checkValidity(advent, this.cessation)) {
			this.advent = advent;
			this.distance = TimeHelper.distance(this.advent, this.cessation);
		} // if
		else {
			final String msg = "period illegal: advent must be before cessation";
			throw new IllegalArgumentException(msg);
		} // else
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Instant getCessation() {
		return this.cessation;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param cessation
	 */
	public void setCessation(final Instant cessation)
			throws IllegalArgumentException {
		if (this.checkValidity(this.advent, cessation)) {
			this.cessation = cessation;
			this.distance = TimeHelper.distance(this.advent, this.cessation);
		} // if
		else {
			final String msg = "period illegal: advent must be before cessation";
			throw new IllegalArgumentException(msg);
		} // else
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Distance distance() {
		return this.distance;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param other
	 * @return
	 */
	public boolean isWithin(final Period other) {
		boolean result = false;
		result = !this.advent.before(other.advent)
				&& !this.cessation.after(other.cessation);
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
		if (candidate == null) {
			return false;
		} // if
		if (this == candidate) {
			return true;
		} // if
		if (candidate instanceof Period) {
			final Period other = ( Period )candidate;
			return new EqualsBuilder().append(this.advent, other.advent)
					.append(this.cessation, other.cessation).isEquals();
		} // if
		else {
			return false;
		} // else
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.advent).append(this.cessation)
				.toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("advent", this.advent).append(
				"cessation", this.cessation).toString();
	}
}
