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
 * Copyright (c) 2007 by the
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
package de.irf.it.rmg.core.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
final public class SortedQueueBinarySearchImpl<E>
		implements SortedQueue<E> {

	/**
	 * TODO: not yet commented
	 * 
	 */
	final private List<E> elementQueue;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Comparator<E> ordering;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean keepOrdered;

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public SortedQueueBinarySearchImpl() {
		this.elementQueue = new ArrayList<E>();
		this.keepOrdered = false;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param elementQueue
	 */
	public SortedQueueBinarySearchImpl(List<E> elementQueue) {
		this.elementQueue = elementQueue;
		this.keepOrdered = false;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param ordering
	 */
	public SortedQueueBinarySearchImpl(Comparator<E> ordering) {
		this();
		this.ordering = ordering;
		this.keepOrdered = true;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param ordering
	 */
	public SortedQueueBinarySearchImpl(Comparator<E> ordering,
			boolean keepOrdered) {
		this();
		this.ordering = ordering;
		this.keepOrdered = keepOrdered;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param elementQueue
	 * @param ordering
	 */
	public SortedQueueBinarySearchImpl(List<E> elementQueue,
			Comparator<E> ordering) {
		this(elementQueue);
		this.ordering = ordering;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param element
	 * @return
	 */
	private int determineInsertionIndex(E element) {
		int index = Collections.binarySearch(this.elementQueue, element,
				this.ordering);
		return (index < 0 ? Math.abs(index) - 1 : index);
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.utils.collections.SortedQueue
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.collections.SortedQueue#getOrdering()
	 */
	public Comparator<E> getOrdering() {
		return this.ordering;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.collections.SortedQueue#setOrdering(java.util.Comparator)
	 */
	public void setOrdering(Comparator<E> ordering) {
		this.ordering = ordering;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.collections.SortedQueue#isKeepOrdered()
	 */
	public boolean isKeepOrdered() {
		return this.keepOrdered;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.collections.SortedQueue#setKeepOrdered(boolean)
	 */
	public void setKeepOrdered(boolean keepOrdered) {
		this.keepOrdered = keepOrdered;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.collections.SortedQueue#reorder()
	 */
	public void reorder() {
		Collections.sort(this.elementQueue, this.ordering);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.collections.SortedQueue#reorder(java.util.Comparator)
	 */
	public void reorder(Comparator<E> ordering) {
		Collections.sort(this.elementQueue, ordering);
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.util.Queue
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#offer(java.lang.Object)
	 */
	public boolean offer(E element) {
		boolean result;

		if (this.keepOrdered) {
			int index = this.determineInsertionIndex(element);
			this.elementQueue.add(index, element);
			result = true;
		} // if
		else {
			result = this.elementQueue.add(element);
		} // else

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#element()
	 */
	public E element() {
		E result = null;
		if (this.elementQueue.isEmpty()) {
			String msg = "head element not found: queue is empty";
			throw new NoSuchElementException(msg);
		} // if
		else {
			result = this.elementQueue.get(0);
		} // else
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#remove()
	 */
	public E remove()
			throws NoSuchElementException {
		E result = null;

		if (this.elementQueue.isEmpty()) {
			String msg = "removal failed: queue empty";
			throw new NoSuchElementException(msg);
		} // if
		else {
			result = this.elementQueue.remove(0);
		} // else

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#peek()
	 */
	public E peek() {
		return (this.elementQueue.isEmpty() ? null : this.elementQueue.get(0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#poll()
	 */
	public E poll() {
		return (this.elementQueue.isEmpty() ? null : this.elementQueue
				.remove(0));
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.util.Collection
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(E element) {
		boolean result;

		if (this.keepOrdered) {
			int index = this.determineInsertionIndex(element);
			this.elementQueue.add(index, element);
			result = true;
		} // if
		else {
			result = this.elementQueue.add(element);
		} // else

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> elements) {
		boolean result = false;
		for (E element : elements) {
			result |= this.add(element);
		} // for
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		this.elementQueue.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object element) {
		return this.elementQueue.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> elements) {
		return this.elementQueue.containsAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return this.elementQueue.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<E> iterator() {
		return this.elementQueue.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object element)  {
		if(!this.elementQueue.contains(element)){
			String msg = "Cannot reemove job " + element + " from queue.";
			System.err.println(msg);
		}
		return this.elementQueue.remove(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> elements) {
		return this.elementQueue.removeAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> elements) {
		return this.elementQueue.retainAll(elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return this.elementQueue.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return this.elementQueue.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray(T[] array) {
		return this.elementQueue.toArray(array);
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
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} // if
		if (this == other) {
			return true;
		} // if
		if (this instanceof SortedQueueBinarySearchImpl) {
			SortedQueueBinarySearchImpl candidate = ( SortedQueueBinarySearchImpl ) other;
			return new EqualsBuilder().append(this.elementQueue,
					candidate.elementQueue).append(this.ordering,
					candidate.ordering).append(this.keepOrdered,
					candidate.keepOrdered).isEquals();
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
		return new HashCodeBuilder().append(this.elementQueue).append(
				this.ordering).append(this.keepOrdered).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("elementQueue",
				this.elementQueue).append("ordering", this.ordering).append(
				"keepOrdered", this.keepOrdered).toString();
	}
}
