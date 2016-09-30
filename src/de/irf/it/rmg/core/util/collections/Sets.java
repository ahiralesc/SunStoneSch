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
package de.irf.it.rmg.core.util.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * Sets implements typical set operations using java.util.HashSet.
 * 
 * @author alexp
 * 
 */
public class Sets {

	/**
	 * TODO: not yet commented
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	private Sets() {
		// do nothing here.
	}

	/**
	 * Implements a subset test.
	 * 
	 * @param set
	 * @param candidate
	 * @return boolean Returns true iff the given set contains every element
	 *         from the candidate.
	 */
	public static boolean isSubsetOf(Set<?> set, Set<?> candidate) {
		return set.containsAll(candidate);
	}

	/**
	 * Implements a proper subset test.
	 * 
	 * @param set
	 * @param candidate
	 * @return boolean Returns true iff the given set contains every element
	 *         from the candidate set and iff the cardinality of set is greater
	 *         than the cardinality of the candidate.
	 */
	public static boolean isProperSubsetOf(Set<?> set, Set<?> candidate) {
		return set.containsAll(candidate) && set.size() > candidate.size();
	}

	/**
	 * Implements a union method for both sets given as parameter.
	 * 
	 * @param <T>
	 * @param one
	 * @param another
	 * @return union
	 * 
	 */
	public static <T> Set<T> union(Set<T> one, Set<T> another) {
		Set<T> union = null;
		if (one.size() < another.size()) {
			union = new HashSet<T>(another);
			union.addAll(one);
		} // if
		else {
			union = new HashSet<T>(one);
			union.addAll(another);
		} // else
		return union;
	}

	/**
	 * Implements a intersection method for both sets given as parameter.
	 * 
	 * @param <T>
	 * @param one
	 * @param another
	 * @return intersec
	 */
	public static <T> Set<T> intersect(Set<T> one, Set<T> another) {
		Set<T> intersect = null;

		if (one.size() < another.size()) {
			intersect = new HashSet<T>(one);
			intersect.retainAll(another);
		} // if
		else {
			intersect = new HashSet<T>(another);
			intersect.retainAll(one);
		} // else

		return intersect;
	}

	/**
	 * TODO: not yet commented
	 *
	 * @param <T>
	 * @param one
	 * @param another
	 * @return
	 */
	public static <T> Set<T> complement(Set<T> one, Set<T> another) {
		Set<T> complement = union(one, another);
		Set<T> intersect = intersect(one, another);

		complement.removeAll(intersect);

		return complement;
	}

	/**
	 * Implements a difference method for both sets given as parameter. The
	 * result is a new set which contains all the elements from set "one", that
	 * are not contained in set "another".
	 * 
	 * @param <T>
	 * @param one
	 * @param another
	 * @return complement
	 * 
	 */
	public static <T> Set<T> difference(Set<T> one, Set<T> another) {
		Set<T> difference = new HashSet<T>(one);
		difference.removeAll(another);
		return difference;
	}

	/**
	 * This method implements a copy function for sets.
	 * 
	 * @param <T>
	 * @param source
	 *            Source to copy.
	 * @param destination
	 *            Destination for the new copy
	 */
	public static <T> void copy(Set<T> source, Set<T> destination) {
		destination.addAll(source);
		//		for (T t : source) {
		//			destination.add(t);
		//		} // for
	}

	/**
	 * This method checks if two sets have elements in common or not.
	 * 
	 * @param set1
	 *            the first Set
	 * @param set2
	 *            the second set
	 * @return boolean returns true if there is at least one element which is
	 *         part of both sets, returns false otherwise and if one of the sets
	 *         is null
	 */
	public static boolean hasIntersection(Set<?> set1, Set<?> set2) {
		if (set1 == null || set2 == null) {
			return false;
		} // if
		for (Object t : set1) {
			if (set2.contains(t)) {
				return true;
			} // if
		} // for
		return false;
	}

}
