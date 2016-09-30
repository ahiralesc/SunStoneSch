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


import java.util.HashSet;
import java.util.Set;

import de.irf.it.rmg.core.util.Pair;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 * @param <T>
 */
public class Episode<T> extends Pair<Instant, Set<T>>
		implements Comparable<Episode<T>> {

	/**
	 * TODO: not yet commented
	 * 
	 */
	public Episode() {
		super();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param instant
	 */
	public Episode(final Instant instant) {
		this();
		super.setLeft(instant);
		super.setRight(new HashSet<T>());
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param instant
	 * @param condition
	 */
	public Episode(final Instant instant, final T condition) {
		this();
		super.setLeft(instant);
		final Set<T> s = new HashSet<T>();
		s.add(condition);
		super.setRight(s);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param instant
	 * @param conditions
	 */
	public Episode(final Instant instant, final T[] conditions) {
		this();
		super.setLeft(instant);
		final Set<T> s = new HashSet<T>();
		for (final T t : conditions) {
			s.add(t);
		} // for
		super.setRight(s);
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Episode<T> other) {
		return this.getLeft().compareTo(other.getLeft());
	}

}
