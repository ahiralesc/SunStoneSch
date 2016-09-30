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
package de.irf.it.rmg.core.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */
public class PreviewableElementListDecorator<E> implements List<E> {

	/**
	 * TODO: not yet commented
	 * 
	 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
	 *         Papaspyrou</a> (last modified by: $Author$)
	 * @version $Version$, $Date$
	 * 
	 */
	final private class PreviewableListIteratorImpl implements
			PreviewableListIterator<E> {

		/**
		 * TODO: not yet commented
		 * 
		 */
		private ListIterator<E> listIterator;

		/**
		 * TODO: not yet commented
		 * 
		 */
		PreviewableListIteratorImpl() {
			this.listIterator = PreviewableElementListDecorator.this.decoratedList
					.listIterator();
		}

		/**
		 * TODO: not yet commented
		 * 
		 * @param index
		 */
		PreviewableListIteratorImpl(int index) {
			this.listIterator = PreviewableElementListDecorator.this.decoratedList
					.listIterator(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#hasNext()
		 */
		public boolean hasNext() {
			return this.listIterator.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.util.PreviewableListIterator#viewNext()
		 */
		public E viewNext() {
			E element = this.listIterator.next();
			this.listIterator.previous();
			return element;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#next()
		 */
		public E next() {
			return this.listIterator.next();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex() {
			return this.listIterator.nextIndex();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious() {
			return this.listIterator.hasPrevious();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.util.PreviewableListIterator#viewPrevious()
		 */
		public E viewPrevious() {
			E element = this.listIterator.previous();
			this.listIterator.next();
			return element;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#previous()
		 */
		public E previous() {
			return this.listIterator.previous();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex() {
			return this.listIterator.previousIndex();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(E o) {
			this.listIterator.add(o);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#remove()
		 */
		public void remove() {
			this.listIterator.remove();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(E o) {
			this.listIterator.set(o);
		}
	}

	/**
	 * TODO: not yet commented
	 * 
	 */
	private List<E> decoratedList;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private PreviewableElementListDecorator() {
		// do nothing here
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param listToDecorate
	 */
	protected PreviewableElementListDecorator(List<E> listToDecorate) {
		this.decoratedList = listToDecorate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(E o) {
		return this.decoratedList.add(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, E element) {
		this.decoratedList.add(index, element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		return this.decoratedList.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		return this.decoratedList.addAll(index, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		this.decoratedList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.decoratedList.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.decoratedList.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	public E get(int index) {
		return this.decoratedList.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return this.decoratedList.indexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return this.decoratedList.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#iterator()
	 */
	public Iterator<E> iterator() {
		return this.decoratedList.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return this.decoratedList.lastIndexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator() {
		ListIterator<E> listIterator = new PreviewableListIteratorImpl();
		return listIterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(int index) {
		ListIterator<E> listIterator = new PreviewableListIteratorImpl(index);
		return listIterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.decoratedList.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	public E remove(int index) {
		return this.decoratedList.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return this.decoratedList.removeAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return this.decoratedList.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public E set(int index, E element) {
		return this.decoratedList.set(index, element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#size()
	 */
	public int size() {
		return this.decoratedList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		return this.decoratedList.subList(fromIndex, toIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return this.decoratedList.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return this.decoratedList.toArray(a);
	}

	public static <E> List<E> decorate(List<E> listToDecorate) {
		return new PreviewableElementListDecorator<E>(listToDecorate);
	}

}
