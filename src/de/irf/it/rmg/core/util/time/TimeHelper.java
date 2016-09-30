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

import de.irf.it.rmg.core.util.DateHelper;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public class TimeHelper {

	/**
	 * This method adds a Distance to an Instant and returns a new instance of
	 * Instant representing the result of the summation. The summation adheres
	 * to the following rules: <li>If the Instant is eternal or the length of
	 * the Distance is infinite, the result is always an eternal Instant. <li>
	 * Otherwise, a new Instant is created as result. In case of a roll over in
	 * the internal representation (>MAX_LONG), the result is eternal.
	 * 
	 * @param instant
	 * @param distance
	 * @return
	 */
	static public Instant add(final Instant instant, final Distance distance) {
		Instant result = null;
		if (instant.isEternity() || distance.length() == Distance.PERPETUAL) {
			result = TimeFactory.newEternity();
		}// if
		else {
			final long t = instant.timestamp() + distance.length();
			if (t < 0) {
				result = TimeFactory.newEternity();
			}// if
			else {
				result = TimeFactory.newMoment(t);
			}
		}// else

		return result;
	}

	/**
	 * This method adds to Distances and returns a new Distance instance
	 * representing the summation. If at least one distance is infinite, the
	 * result is infinite as well. In case the internal representation of a
	 * distance rolls over (>MAX_LONG) after adding up both distances, the
	 * resulting distance is assumed to be infinite.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	static public Distance add(final Distance left, final Distance right) {
		Distance result = null;

		if (left.length() == Distance.PERPETUAL
				|| right.length() == Distance.PERPETUAL) {
			result = TimeFactory.newPerpetual();
		}// if
		else {
			final long t = left.length() + right.length();
			if (t < 0) {
				/*
				 * roll over case, assume resulting distance as infinite.
				 */
				result = TimeFactory.newPerpetual();
			}// if
			else {
				/*
				 * the new distance is a positive, not eternal value.
				 */
				result = TimeFactory.newFinite(t);
			}// else
		}// else
		return result;
	}

	/**
	 * This method subtracts a distance value from an instant in time. In case
	 * the distance is infinite, the new instant results in the first instant
	 * possible in this system (here zero). If the instant is the substraction
	 * has no influence on the result being eternal. In case if roll over at the
	 * minimal timestamp the result is set to the minimal timestamp (zero).
	 * 
	 * @param instant
	 * @param distance
	 * @return
	 */
	static public Instant subtract(final Instant instant,
			final Distance distance) {
		Instant result = null;
		if (instant.isEternity()) {
			/*
			 * eternity minus some value results still in eternity.
			 */
			result = TimeFactory.newEternity();
		}// if
		else {
			if (distance.length() == Distance.PERPETUAL
					|| distance.length() < -1) {
				/*
				 * if the distance to be subtracted is infinite, the new instant
				 * is zero.
				 */
				result = TimeFactory.newMoment(0);
			}// if
			else {
				final long t = instant.timestamp() - distance.length();
				if (t < 0) {
					/*
					 * the subtraction yielded a timestemp that exceeded the
					 * initial timestamp (< 0). Thus, the result is set to this
					 * initial timestamp.
					 */
					result = TimeFactory.newMoment(0);
				}// if
				else {
					/*
					 * create an instant containing the calculated timestamp
					 */
					result = TimeFactory.newMoment(t);
				}
			}
		}// else

		return result;
	}

	/**
	 * This method calculates the difference of two distances resulting in a
	 * distance. In case of an infinite distance on the left side of the
	 * subtraction the result is always infinite. Otherwise, in case of an
	 * infinite subtrahend, the result is always zero. In case both conditions
	 * hold, the result is infinite.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	static public Distance subtract(final Distance left, final Distance right) {
		Distance result = null;

		if (left.isPerpetual()) {
			/*
			 * left is an infinite distance, thus the resulting distance is
			 * infinite as well.
			 */
			result = TimeFactory.newPerpetual();
		} // if
		else {
			if (right.isPerpetual() || right.length() < -1) {
				/*
				 * the distance to be subtracted is infinite, thus the resulting
				 * distance corresponds to zero.
				 */
				result = TimeFactory.newFinite(0);
			} // if
			else {
				final long t = left.length() - right.length();
				if (t < 0) {
					/*
					 * roll over case, set result to zero.
					 */
					result = TimeFactory.newFinite(0);
				} // if
				else {
					result = TimeFactory.newFinite(t);
				} // else
			} // else
		} // else

		return result;
	}

	/**
	 * This method calculates the distance of two instants. In case both
	 * distances are infinite zero is returned as new distance. If only one
	 * distance is infinite, an infinite distance is returned.
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	static public Distance distance(final Instant one, final Instant two) {
		Distance result;
		Instant left;
		Instant right;

		/*
		 * swap the instants if necessary
		 */
		if (one.before(two)) {
			left = one;
			right = two;
		}// if
		else {
			left = two;
			right = one;
		}// else

		if (right.isEternity() && !left.isEternity() || !right.isEternity()
				&& left.isEternity()) {
			result = TimeFactory.newPerpetual();
		}// if
		else {
			if (right.isEternity() && left.isEternity()) {
				result = TimeFactory.newFinite(0);
			}// if
			else {
				final long t = right.timestamp() - left.timestamp();
				result = TimeFactory.newFinite(t);
			}// else
		}// else

		return result;
	}

	/**
	 * This method extends the given period by the diven distance value.
	 * 
	 * @param period
	 * @param distance
	 * @return
	 */
	static public Period extend(final Period period, final Distance distance) {
		Period result = null;

		if (period.getCessation().isEternity()
				|| distance.length() == Distance.PERPETUAL) {
			result = new Period(TimeFactory.newMoment(period.getAdvent()
					.timestamp()), TimeFactory.newEternity());
		}// if
		else {
			final long t = period.getCessation().timestamp()
					+ distance.length();
			if (t < 0) {
				result = new Period(period.getAdvent(), TimeFactory
						.newEternity());
			}// if
			else {
				result = new Period(period.getAdvent(), TimeFactory
						.newMoment(t));
			}// else
		}// else
		return result;
	}

	/**
	 * This method reduces the given period's dimension by the given distance.
	 * 
	 * @param period
	 * @param distance
	 * @return
	 */
	static public Period shrink(final Period period, final Distance distance) {
		Period result = null;
		// if period goes to eternity, shrinking does not work
		if (period.getCessation().isEternity()) {
			result = new Period(TimeFactory.newMoment(period.getAdvent()
					.timestamp()), TimeFactory.newEternity());
		}// if
		else {
			/*
			 * if the distance by which the period is to be shortened is
			 * infinite, return a period of length 0
			 */
			if (distance.length() == Distance.PERPETUAL) {
				result = new Period(TimeFactory.newMoment(period.getAdvent()
						.timestamp()), TimeFactory.newMoment(period.getAdvent()
						.timestamp()));
			}// if
			else {
				final long t = period.getCessation().timestamp()
						- distance.length();

				if (t < 0) {
					result = new Period(TimeFactory.newMoment(0), TimeFactory
							.newMoment(0));
				}// if
				else {
					if (t < period.getAdvent().timestamp()) {
						result = new Period(TimeFactory.newMoment(t),
								TimeFactory.newMoment(t));
					}// if
					else {
						result = new Period(TimeFactory.newMoment(period
								.getAdvent().timestamp()), TimeFactory
								.newMoment(t));
					}// else
				}// else
			}// else
		}// else
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param distance
	 * @return
	 */
	static public long toSeconds(final Distance distance) {
		return DateHelper.convertToSeconds(distance.length());
	}

	static public long toMilliseconds(final Distance distance) {
		return distance.length();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param instant
	 * @return
	 */
	static public long toLongValue(final Instant instant) {
		return instant.timestamp();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 */
	public static Date toDate(final Instant timestamp, final Date reference) {
		return new Date(reference.getTime() + timestamp.timestamp());
	}

}
