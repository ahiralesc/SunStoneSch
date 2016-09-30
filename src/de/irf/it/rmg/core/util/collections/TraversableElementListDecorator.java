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
public class TraversableElementListDecorator<E extends TraversableElement>
		implements List<E> {

	/**
	 * TODO: not yet commented
	 * 
	 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
	 *         Papaspyrou</a> (last modified by: $Author$)
	 * @version $Version$, $Date$
	 * 
	 * @param <E>
	 */
	final private class TraversableListIterator implements ListIterator<E> {

		/**
		 * TODO: not yet commented
		 * 
		 */
		private ListIterator<E> listIterator;

		/**
		 * TODO: not yet commented
		 * 
		 */
		private int indexOflastElement = -1;

		/**
		 * TODO: not yet commented
		 * 
		 */
		public TraversableListIterator() {
			this.listIterator = TraversableElementListDecorator.this.decoratedList
					.listIterator();
		}

		/**
		 * TODO: not yet commented
		 * 
		 * @param index
		 */
		public TraversableListIterator(int index) {
			this.listIterator = TraversableElementListDecorator.this.decoratedList
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
		 * @see java.util.ListIterator#next()
		 */
		public E next() {
			this.indexOflastElement = this.listIterator.nextIndex();
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
		 * @see java.util.ListIterator#previous()
		 */
		public E previous() {
			this.indexOflastElement = this.listIterator.previousIndex();
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
		@SuppressWarnings("unchecked")
		public void add(E o) {
			/*
			 * remove element from list
			 */
			this.listIterator.add(o);
			/*
			 * update [ante|suc]cessors
			 */
			TraversableElementListDecorator.this
					.updateElement(this.listIterator.previousIndex());
			/*
			 * reset last good element
			 */
			this.indexOflastElement = -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#remove()
		 */
		@SuppressWarnings("unchecked")
		public void remove() {
			if (this.indexOflastElement == -1) {
				String msg = "no preceeding call of previous() or next()";
				throw new IllegalStateException(msg);
			} // if
			else {
				/*
				 * remove element from list and update traversables
				 */
				E element = TraversableElementListDecorator.this
						.remove(indexOflastElement);
				element.setAntecessor(null);
				element.setSuccessor(null);
				/*
				 * update [ante|suc]cessors
				 */
				TraversableElementListDecorator.this
						.updateElement(indexOflastElement);
				/*
				 * reset last good element
				 */
				this.indexOflastElement = -1;
			} // else
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		public void set(E o) {
			if (this.indexOflastElement == -1) {
				String msg = "no preceeding call of previous() or next()";
				throw new IllegalStateException(msg);
			} // if
			else {
				/*
				 * update element
				 */
				TraversableElementListDecorator.this.set(indexOflastElement, o);
				/*
				 * update [ante|suc]cessors
				 */
				TraversableElementListDecorator.this
						.updateElement(indexOflastElement);
				/*
				 * reset last good element
				 */
				this.indexOflastElement = -1;
			} // else
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
	private TraversableElementListDecorator() {
		// do nothing here
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param listToDecorate
	 */
	protected TraversableElementListDecorator(List<E> listToDecorate) {
		this.decoratedList = listToDecorate;
		this.updateElements(0, this.decoratedList.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean add(E element) {
		int insertionIndex = this.decoratedList.size();
		/*
		 * add the given element to the list
		 */
		boolean result = this.decoratedList.add(element);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		if (result) {
			this.updateElement(insertionIndex);
		} // if
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void add(int index, E element) {
		/*
		 * add the given element to the list
		 */
		this.decoratedList.add(index, element);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements
		 */
		this.updateElement(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		int insertionIndex = this.decoratedList.size();
		int numberOfElements = c.size();
		/*
		 * add the given elements to the list
		 */
		boolean result = this.decoratedList.addAll(c);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		if (result) {
			this.updateElements(insertionIndex, numberOfElements);
		} // if
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		int numberOfElements = c.size();
		/*
		 * add the given elements to the list
		 */
		boolean result = this.decoratedList.addAll(index, c);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		if (result) {
			this.updateElements(index, numberOfElements);
		} // if
		return result;
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
		return new TraversableListIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(int index) {
		return new TraversableListIterator(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		int removalIndex = this.decoratedList.indexOf(o);
		E element = this.decoratedList.get(removalIndex);
		/*
		 * remove the given element from the list and delete its
		 * [ante|suc]cessors
		 */
		boolean result = this.decoratedList.remove(o);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		if (result) {
			element.setAntecessor(null);
			element.setSuccessor(null);
			this.updateElement(removalIndex);
		} // if
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		/*
		 * remove the given element from the list
		 */
		E element = this.decoratedList.remove(index);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		element.setAntecessor(null);
		element.setSuccessor(null);
		this.updateElement(index);
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		/*
		 * remove the given elements from the list
		 */
		boolean result = this.decoratedList.removeAll(c);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		if (result) {
			this.updateElements(0, this.decoratedList.size());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		/*
		 * retain the given elements in the list
		 */
		boolean result = this.decoratedList.retainAll(c);
		/*
		 * modify the [ante|suc]cessor relationships for all concerned elements,
		 * if appropriate
		 */
		if (result) {
			this.updateElements(0, this.decoratedList.size());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public E set(int index, E element) {
		E oldElement = this.decoratedList.set(index, element);
		oldElement.setAntecessor(null);
		oldElement.setSuccessor(null);
		this.updateElement(index);
		return oldElement;
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

	private void updateElement(int index) {
		ListIterator<E> li = this.decoratedList.listIterator(index);
		this.updateRelationshipsAtIndex(li);
	}

	private void updateElements(int index, int numberOfElements) {
		ListIterator<E> li = this.decoratedList.listIterator(index);
		for (int i = 0; i < numberOfElements; i++) {
			this.updateRelationshipsAtIndex(li);
		} // for
	}

	@SuppressWarnings("unchecked")
	private void updateRelationshipsAtIndex(ListIterator<E> li) {
		TraversableElement element = null;
		TraversableElement antecessor = null;
		TraversableElement successor = null;
		/*
		 * fetch the antecessor, if existent
		 */
		if (li.hasPrevious()) {
			antecessor = li.previous(); // iterator points on "antecessor"
			li.next(); // iterator points on "element"
		} // if
		/*
		 * keep "current", if applicable
		 */
		if (li.hasNext()) {
			element = li.next(); // iterator points on "successor"
		} // if
		/*
		 * fetch the successor, if existent
		 */
		if (li.hasNext()) {
			successor = li.next(); // iterator points behind "successor"
			li.previous(); // iterator points on "successor"
		} // if
		/*
		 * update the element, if applicable
		 */
		if (element != null) {
			element.setAntecessor(antecessor);
			element.setSuccessor(successor);
		} // if
		/*
		 * update the antecessor, if applicable
		 */
		if (antecessor != null) {
			antecessor.setSuccessor(element);
		} // if
		/*
		 * update the successor, if applicable
		 */
		if (successor != null) {
			successor.setAntecessor(element);
		} // if
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.decoratedList.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return decoratedList.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.decoratedList.toString();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param <E>
	 * @param listToDecorate
	 * @return
	 */
	public static <E extends TraversableElement> List<E> decorate(
			List<E> listToDecorate) {
		return new TraversableElementListDecorator<E>(listToDecorate);
	}

}
