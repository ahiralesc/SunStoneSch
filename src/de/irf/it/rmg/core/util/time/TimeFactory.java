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


import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Provides a factory for {@link de.irf.it.rmg.core.util.time.Instant} and
 * {@link de.irf.it.rmg.core.util.time.Period} instances and ensures the correct
 * handling of eternity and perpetuality. It also ensures object pooling for
 * both types.
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
 *         Papaspyrou</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 * @see de.irf.it.rmg.core.util.time.Instant
 * @see de.irf.it.rmg.core.util.time.Period
 * 
 */
public class TimeFactory {

	/**
	 * Keeps an object pool of <code>Instant</code>s that have been created
	 * already.
	 */
	static private Map<Long, Instant> poolOfInstants;

	/**
	 * Keeps a cached copy of the "eternity" <code>Instant</code>
	 */
	static private Instant cachedEternity;

	/**
	 * Keeps an object pool of <code>Distance</code>s that have been created
	 * already.
	 */
	static private Map<Long, Distance> poolOfDistances;

	/**
	 * Keeps a cached copy of the "perpetual" <code>Distance</code>
	 */
	static private Distance cachedPerpetual;

	/**
	 * Initializes the class on creation.
	 */
	static {
		poolOfInstants = new HashMap<Long, Instant>();
		poolOfDistances = new HashMap<Long, Distance>();
	}

	/**
	 * Creates a new instance of a given time instant, based on a timestamp in
	 * <code>Long</code> format.
	 * 
	 * In most cases, the parameter depicts the value of a certain moment in
	 * time as understood by the {@link java.util.Date#getTime()} method;
	 * however, this is not required unless -- at a later time -- the produced
	 * <code>Instant</code> is to be converted back to a <code>Date</code>,
	 * using the {@link TimeHelper#toDate(Instant, java.util.Date)} method
	 * without an externally stored reference date.
	 * 
	 * @param timestamp
	 *            A value representing time. If supposed to be compatible with
	 *            {@link Date#getTime()}, the value semantically represents
	 *            milliseconds.
	 * @return A corresponding <code>Instant</code> for the given parameter.
	 * 
	 * @see de.irf.it.rmg.core.util.time.Instant
	 */
	static public Instant newMoment(final long timestamp) {
		Instant result = null;

		if (timestamp < 0) {
			final String msg = "creation error: timestamp (" + timestamp
					+ ") must not be smaller than zero";
			throw new IllegalArgumentException(msg);
		} // if
		else {
			if (!poolOfInstants.containsKey(timestamp)) {
				final Instant i = new Instant(timestamp);
				poolOfInstants.put(timestamp, i);
				result = i;
			} // if
			else {
				result = poolOfInstants.get(timestamp);
			} // else
		} // else

		return result;
	}

	/**
	 * Returns an instance representing eternity within the time system.
	 * 
	 * @return An <code>Instant</code> representing eternity.
	 */
	static public Instant newEternity() {
		if (cachedEternity == null) {
			cachedEternity = new Instant(Instant.ETERNITY);
		} // if
		return cachedEternity;
	}

	/**
	 * Creates a new instance of a given distance, based on its length in
	 * <code>Long</code> format.
	 * 
	 * In most cases, the parameter depicts the value of a certain time span as
	 * understood by the {@link java.util.Date#getTime()} method; however, this
	 * is not required unless -- at a later time -- the produced
	 * <code>Distance</code> is to be used with <code>Instant</code>s that are
	 * converted back to a <code>Date</code>.
	 * 
	 * @param length
	 *            A value representing span. If supposed to be compatible with
	 *            {@link Date#getTime()}, the value semantically represents
	 *            milliseconds.
	 * @return A corresponding <code>Distance</code> for the given parameter.
	 * 
	 * @see #newMoment(long)
	 */
	public static Distance newFinite(final long length) {
		Distance result = null;

		if (length < 0) {
			final String msg = "creation error: length (" + length
					+ ") must not be smaller than zero";
			throw new IllegalArgumentException(msg);
		} // if
		else {
			if (!poolOfDistances.containsKey(length)) {
				final Distance d = new Distance(length);
				poolOfDistances.put(length, d);
				result = d;
			} // if
			else {
				result = poolOfDistances.get(length);
			} // else
		} // else

		return result;
	}

	/**
	 * Returns an instance representing perpetuity within the time system.
	 * 
	 * @return A <code>Distance</code> representing perpetuity.
	 */
	public static Distance newPerpetual() {
		if (cachedPerpetual == null) {
			cachedPerpetual = new Distance(Distance.PERPETUAL);
		} // if
		return cachedPerpetual;
	}
}
