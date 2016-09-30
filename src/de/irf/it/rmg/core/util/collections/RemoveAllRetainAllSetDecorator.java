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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * TODO: not yet commented
 * 
 * @author <a href="alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$ as of $Date$
 * 
 * @param <E>
 */
public class RemoveAllRetainAllSetDecorator<E> implements Set<E> {

	/**
	 * TODO: not yet commented
	 */
	private Set<E> decoratedSet;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private RemoveAllRetainAllSetDecorator() {
		// do nothing here
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param setToDecorate
	 */
	protected RemoveAllRetainAllSetDecorator(Set<E> setToDecorate) {
		this.decoratedSet = setToDecorate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#size()
	 */
	public int size() {
		return this.decoratedSet.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return this.decoratedSet.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.decoratedSet.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#iterator()
	 */
	public Iterator<E> iterator() {
		return this.decoratedSet.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return this.decoratedSet.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return this.decoratedSet.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(E o) {
		return this.decoratedSet.add(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.decoratedSet.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.decoratedSet.contains(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		return this.decoratedSet.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		SortedSet<Object> elementsToRemove = new TreeSet<Object>();
		for (Object o : this.decoratedSet) {
			if (!c.contains(o)) {
				/*
				 * Doesn't work (concurrent modification):
				 * this.decoratedSet.remove(o);
				 */
				elementsToRemove.add(o);
				changed = true;
			} // if
		} // for
		for (Object o : elementsToRemove) {
			this.decoratedSet.remove(o);
		}
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			if (this.decoratedSet.contains(o)) {
				this.decoratedSet.remove(o);
				changed = true;
			} // if
		} // for
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		this.decoratedSet.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.decoratedSet.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.decoratedSet.hashCode();
	}

	/**
	 * @param <E>
	 * @param setToDecorate
	 * @return
	 */
	public static <E> Set<E> decorate(Set<E> setToDecorate) {
		return new RemoveAllRetainAllSetDecorator<E>(setToDecorate);
	}
}
