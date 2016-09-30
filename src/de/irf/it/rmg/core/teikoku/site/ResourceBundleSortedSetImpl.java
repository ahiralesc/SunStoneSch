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
package de.irf.it.rmg.core.teikoku.site;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>
 * An implementation of the {@link ResourceBundle} interface for the
 * representation of sets of {@link Resource}s using sorted sets. This allows
 * efficient support for operations on sets of resources such as addition,
 * removal and search, which are implemented on the basis of balanced tree sets,
 * iif the other bundle also is of this type.
 * </p>
 * <p>
 * This class also supports interaction with other implementations of the
 * {@link ResourceBundle} interface. However, since no assumptions on the
 * internal representation can be made in this case, all operations have then to
 * resort to fetching the corresponding set of resources via {@link #toArray()}
 * and then perform iterations over these.
 * </p>
 * <p>
 * In any case, the result of all non-manipulating methods ({@link #intersect(ResourceBundle)}
 * and {@link #difference(ResourceBundle)}) is of this type again.
 * </p>
 * 
 * @see ResourceBundle
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
final public class ResourceBundleSortedSetImpl extends
		AbstractResourceBundleImpl {

	/**
	 * Holds the sorted set representation of this {@link ResourceBundle}.
	 * 
	 * @see SortedSet
	 */
	private SortedSet<Resource> resources;

		
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public ResourceBundleSortedSetImpl() {
		this.resources = new TreeSet<Resource>();
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param resources
	 *            The set of resources this {@link ResourceBundle}
	 *            implementation is equivalent to.
	 */
	public ResourceBundleSortedSetImpl(Resource[] resources) {
		this();
		for (int i = 0; i < resources.length; i++) {
			this.resources.add(resources[i]);
		} // for
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#size()
	 */
	public int size() {
		return this.resources.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#add(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public void add(ResourceBundle resources) {
		if (resources instanceof ResourceBundleSortedSetImpl) {
			ResourceBundleSortedSetImpl other = (ResourceBundleSortedSetImpl) resources;
			this.resources.addAll(other.resources);
		} // if
		else {
			Resource[] other = resources.toArray();
			for (int i = 0; i < other.length; i++) {
				this.resources.add(other[i]);
			} // for
		} // else
		super.setDirty();
	}
	
	public boolean add(Resource r) {
		this.resources.add(r);
		super.setDirty();
		return true;
	}
	
	public void add(int index, Resource r) {
		this.resources.add(r);
		super.setDirty();
	}
	
	public boolean addAll(Collection<? extends Resource> c) {
		Iterator<? extends Resource> it=c.iterator();
		while (it.hasNext()){
			this.resources.add(it.next());
		}
		return true;
	}
	
	public boolean addAll(int index, Collection<? extends Resource> c) {
		Iterator<? extends Resource> it=c.iterator();
		while (it.hasNext()){
			this.resources.add(it.next());
		}
		return true;
	}
	
	public void clear() {
		this.resources.clear();
	}
	
	public boolean contains(Object o) {
		return this.resources.contains(o);
	}
	
	public boolean containsAll(Collection<?> c) {
		return this.resources.containsAll(c);
	}
	
	public Resource get(int index) {
		return this.createArrayRepresentation()[index];
	}
	
	public int indexOf(Object o) {
		return -1;
	}
	
	public boolean isEmpty() {
		return this.resources.isEmpty();
	}
	
	public Iterator<Resource> iterator() {
		return this.resources.iterator();
	}
	
	public int lastIndexOf(Object o) {
		return -1;
	}
	
	public ListIterator<Resource> listIterator() {
		return null;
	}
	
	public ListIterator<Resource> listIterator(int index) {
		return null;
	}
	
	public boolean remove(Object o) {
		return this.resources.remove(o);
	}
	
	public Resource remove(int index) {
		Resource r=this.createArrayRepresentation()[index];
		this.resources.remove(r);
		return r;
	}
	
	public boolean removeAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		while(it.hasNext()){
			this.remove(((Resource) it.next()));
		}
		return true;
	}
	
	public boolean retainAll(Collection<?> c) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#remove(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public void remove(ResourceBundle resources) {
		if (resources instanceof ResourceBundleSortedSetImpl) {
			ResourceBundleSortedSetImpl other = (ResourceBundleSortedSetImpl) resources;
			this.resources.removeAll(other.resources);
		} // if
		else {
			Resource[] other = resources.toArray();
			for (int i = 0; i < other.length; i++) {
				this.resources.remove(other[i]);
			} // for
		} // else
		super.setDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#intersect(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public ResourceBundle intersect(ResourceBundle resources) {
		ResourceBundleSortedSetImpl result = new ResourceBundleSortedSetImpl();
		ResourceBundleSortedSetImpl other = null;
		if (resources instanceof ResourceBundleSortedSetImpl) {
			other = (ResourceBundleSortedSetImpl) resources;
		} // if
		else {
			other = new ResourceBundleSortedSetImpl(resources.toArray());
		} // else
		if (this.resources.size() > other.resources.size()) {
			result.resources.addAll(other.resources);
			result.resources.retainAll(this.resources);
		} // if
		else {
			result.resources.addAll(this.resources);
			result.resources.retainAll(other.resources);
		} // else
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#difference(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public ResourceBundle difference(ResourceBundle resources) {
		ResourceBundleSortedSetImpl result = new ResourceBundleSortedSetImpl();
		ResourceBundleSortedSetImpl other = null;
		if (resources instanceof ResourceBundleSortedSetImpl) {
			other = (ResourceBundleSortedSetImpl) resources;
		} // if
		else {
			other = new ResourceBundleSortedSetImpl(resources.toArray());
		} // else
		if (this.resources.size() > other.resources.size()) {
			result.resources.addAll(this.resources);
			result.resources.removeAll(other.resources);
		} // if
		else {
			result.resources.addAll(other.resources);
			result.resources.removeAll(this.resources);
		} // else
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#isSubSetOf(de.irf.it.rmg.core.teikoku.site.ResourceBundle,
	 *      boolean)
	 */
	public boolean isSubSetOf(ResourceBundle resources, boolean proper) {
		boolean result = false;
		ResourceBundle subSet = resources.intersect(this);
		result = subSet.size() > 0;
		return (proper ? result && resources.size() > subSet.size() : result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#subSetOf(int)
	 */
	public ResourceBundle createSubSetWith(int numberOfResources) {
		return this.createSubSetWith(numberOfResources, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#createSubSetWith(int, int)
	 */
	public ResourceBundle createSubSetWith(int numberOfResources, int startIndex) {
		ResourceBundle result = null;

		Resource[] resources = this.toArray();

		/*
		 * check whether building a subset is possible at all
		 */
		if (startIndex <= (resources.length - numberOfResources)) {
			result = new ResourceBundleSortedSetImpl((Resource[]) ArrayUtils
					.subarray(resources, startIndex, startIndex
							+ numberOfResources));
		} // if

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.AbstractResourceBundleImpl#createArrayRepresentation()
	 */
	@Override
	protected Resource[] createArrayRepresentation() {
		Resource[] resources = new Resource[this.resources.size()];
		return this.resources.toArray(resources);
	}

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
		if (other instanceof ResourceBundleSortedSetImpl) {
			ResourceBundleSortedSetImpl candidate = (ResourceBundleSortedSetImpl) other;
			return new EqualsBuilder().append(this.resources,
					candidate.resources).isEquals();
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
		return new HashCodeBuilder().append(this.resources).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.resources).toString();
	}

	@Override
	public void shiftRemove(ResourceBundle rb) {
		System.out.println("Argh");
		//BitSet one=this.resources.get(0, rb.get(0).getOrdinal()-1);
		//BitSet two=this.resources.get(rb.get(0).getOrdinal()-1+rb.size(),this.resources.length()+1);
	}

}
