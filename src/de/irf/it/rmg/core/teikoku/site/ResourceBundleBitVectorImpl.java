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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import mx.cicese.dcc.teikoku.information.broker.SiteInformation;

/**
 * <p>
 * An implementation of the {@link ResourceBundle} interface for the
 * representation of sets of {@link Resource}s using bit vectors. This allows
 * efficient support for operations on sets of resources such as intersects,
 * differences and complements, which are implemented as logical operations on
 * bit vectors, iif the other bundle also is of this type.
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
final public class ResourceBundleBitVectorImpl extends
		AbstractResourceBundleImpl {

	/**
	 * Holds the bit vector representation of this {@link ResourceBundle}.
	 * 
	 * @see BitSet
	 */
	private BitSet resources;

	/**
	 * Holds the site this {@link ResourceBundle} implementation represents
	 * resources of.
	 * 
	 * @see ComputeSite
	 */
	private SiteInformation siteInformation;


	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param siteInformation
	 *            The site this {@link ResourceBundle} implementation represents
	 *            resources of.
	 */
	public ResourceBundleBitVectorImpl(SiteInformation siteInformation) {
		this.resources = new BitSet();
		this.siteInformation = siteInformation;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param siteInformation
	 *            The site this {@link ResourceBundle} implementation represents
	 *            resources of.
	 * @param resources
	 *            The set of resources this {@link ResourceBundle}
	 *            implementation is equivalent to, as an array of
	 *            {@link Resource}s.
	 */
	public ResourceBundleBitVectorImpl(SiteInformation siteInformation,	Resource[] resources) {
		this(siteInformation);
		for (int i = 0; i < resources.length; i++) {
			this.resources.set(resources[i].getOrdinal() - 1);
		} // for
	}

	
	public ResourceBundleBitVectorImpl(SiteInformation siteInformation,	Resource[] resources, int numberOfResources) {
		this(siteInformation);
		for (int i = 0; i < numberOfResources; i++) {
			this.resources.set(resources[i].getOrdinal() - 1);
		} // for
	}

	public ResourceBundleBitVectorImpl(SiteInformation siteInformation,	BitSet resources, int numberOfResources) {
		this(siteInformation);
		this.resources.or(resources);
	}
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param siteInformation
	 *            The site this {@link ResourceBundle} implementation represents
	 *            resources of.
	 * @param resources
	 *            The set of resources this {@link ResourceBundle}
	 *            implementation is equivalent to, as a {@link BitSet}.
	 */
	private ResourceBundleBitVectorImpl(SiteInformation siteInformation,
			BitSet resources) {
		this(siteInformation);
		this.resources = resources;
	}

	// ---------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.site.ResourceBundle
	// ---------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#add(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public void add(ResourceBundle resources) {
		if (resources instanceof ResourceBundleBitVectorImpl) {
			ResourceBundleBitVectorImpl other = ( ResourceBundleBitVectorImpl ) resources;
			this.resources.or(other.resources);
		} // if
		else {
			Resource[] other = resources.toArray();
			for (int i = 0; i < other.length; i++) {
				this.resources.set(other[i].getOrdinal() - 1);
			} // for
		} // else
		super.setDirty();
	}

	public boolean add(Resource r) {
		this.resources.set(r.getOrdinal()-1);
		super.setDirty();
		return true;
	}

	public void add(int index, Resource r) {
		this.resources.set(r.getOrdinal()-1);
		super.setDirty();
	}

	public boolean addAll(Collection<? extends Resource> c) {
		Iterator<? extends Resource> it=c.iterator();
		while (it.hasNext()){
			this.resources.set(it.next().getOrdinal() - 1);
		}
		return true;
	}

	public boolean addAll(int index, Collection<? extends Resource> c) {
		Iterator<? extends Resource> it=c.iterator();
		while (it.hasNext()){
			this.resources.set(it.next().getOrdinal() - 1);
		}
		return true;
	}

	public void clear() {
		this.resources.clear();
	}

	public boolean contains(Object o) {
		return (resources.get(((Resource) o).getOrdinal() - 1));
	}

	public boolean containsAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		while (it.hasNext()){
			if (!this.contains(it.next())){
				return false;
			}
		}
		return true;
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
		BitSet bs = null;
	
		/*
		 * check whether building a subset is possible at all
		 */
		if (startIndex <= (this.resources.size() - numberOfResources)) {
			/*
			 * initialize the result bit set and find the first set bit,
			 * beginning at the requested start index
			 */
			bs = new BitSet(this.resources.size());
			int nextSetBit = this.resources.nextSetBit(startIndex);
	
			/*
			 * try to add as many set bits from this.resources _as requested by
			 * numberOfResources_ to the result bit set
			 */
			for (int i = 0; i < numberOfResources; i++) {
				/*
				 * in case that no next set bit could be found (and therefore no
				 * more set bits after the last set bit exist in this bit set),
				 * break the loop
				 */
				if (nextSetBit == -1) {
					break;
				} // if
				bs.set(nextSetBit);
				nextSetBit = this.resources.nextSetBit(nextSetBit + 1);
			} // for
		} // if
	
		/*
		 * check whether the bit set satisfies the requested numberOfResources
		 */
		if (bs != null && bs.cardinality() == numberOfResources) {
			result = new ResourceBundleBitVectorImpl(this.siteInformation, bs);
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
		Resource[] result = new Resource[this.resources.cardinality()];
		Resource[] all = this.siteInformation.getProvidedResources().toArray();
		Resource[] lent = this.siteInformation.getLentResources().toArray();
		int before=all.length;
		int index = 0;
		for (int i = this.resources.nextSetBit(0); i >= 0; i = this.resources
				.nextSetBit(i + 1)) {
			if(i<before){
				result[index++] = all[i];
			} else{
				result[index++] = lent[i];
			}
		} // for
		return result;
	}

	private List<Resource> createVectorRepresentation() {
		List<Resource> result = new ArrayList<Resource>();
		Resource[] allProvided = this.siteInformation.getProvidedResources().toArray();
		Resource[] allLent = this.siteInformation.getLentResources().toArray();
		
		for (int i = this.resources.nextSetBit(0); i >= 0; i = this.resources
				.nextSetBit(i + 1)) {
			if (i<allProvided.length){
				result.add(allProvided[i]);
			} else{
				result.add(allLent[i]);
			}
		} // for
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#difference(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public ResourceBundle difference(ResourceBundle resources) {
		// TODO: define interface contract and implement accordingly
		ResourceBundleBitVectorImpl result = new ResourceBundleBitVectorImpl(
				this.siteInformation);
		ResourceBundleBitVectorImpl other = null;
		if (resources instanceof ResourceBundleBitVectorImpl) {
			other = ( ResourceBundleBitVectorImpl ) resources;
		} // if
		else {
			other = new ResourceBundleBitVectorImpl(this.siteInformation,
					resources.toArray());
		} // else
		if (this.resources.cardinality() > other.resources.cardinality()) {
			result.resources = ( BitSet ) this.resources.clone();
			result.resources.andNot(other.resources);
		} // if
		else {
			result.resources = ( BitSet ) other.resources.clone();
			result.resources.andNot(this.resources);
		} // else
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
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} // if
		if (this == other) {
			return true;
		} // if
		if (other instanceof ResourceBundleBitVectorImpl) {
			ResourceBundleBitVectorImpl candidate = ( ResourceBundleBitVectorImpl ) other;
			return new EqualsBuilder().append(this.resources,
					candidate.resources).append(this.siteInformation,
					candidate.siteInformation).isEquals();
		} // if
		else {
			return false;
		} // else
	}

	public Resource get(int index) {
		return this.createArrayRepresentation()[index];
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.resources).append(
				this.siteInformation).toHashCode();
	}

	public int indexOf(Object o) {
		return ((Resource) o).getOrdinal()-1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#intersect(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public ResourceBundle intersect(ResourceBundle resources) {
		ResourceBundleBitVectorImpl result = new ResourceBundleBitVectorImpl(
				this.siteInformation);
		ResourceBundleBitVectorImpl other = null;
		result.resources = ( BitSet ) this.resources.clone();
		if (resources instanceof ResourceBundleBitVectorImpl) {
			other = ( ResourceBundleBitVectorImpl ) resources;
		} // if
		else {
			other = new ResourceBundleBitVectorImpl(this.siteInformation,
					resources.toArray());
		} // else
		result.resources.and(other.resources);
		return result;
	}

	public boolean isEmpty() {
		return this.resources.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#isSubSetOf(de.irf.it.rmg.core.teikoku.site.ResourceBundle,
	 *      boolean)
	 */
	public boolean isSubSetOf(ResourceBundle resources, boolean proper) {
		// XXX: defective implementation
		ResourceBundle subSet = resources.intersect(this);
		return (proper ? resources.size() > subSet.size() : subSet.size() > 0);
	}

	public Iterator<Resource> iterator() {
		return this.createVectorRepresentation().iterator();
	}

	public int lastIndexOf(Object o) {
		return ((Resource) o).getOrdinal()-1;
	}

	public ListIterator<Resource> listIterator() {
		return null;
	}

	public ListIterator<Resource> listIterator(int index) {
		return null;
	}

	public boolean remove(Object o) {
		this.resources.clear(((Resource) o).getOrdinal() - 1);
		return true;
	}

	public Resource remove(int index) {
		Resource r=this.createVectorRepresentation().get(index);
		this.resources.clear(index);
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#remove(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	public void remove(ResourceBundle resources) {
		if (resources instanceof ResourceBundleBitVectorImpl) {
			ResourceBundleBitVectorImpl other = ( ResourceBundleBitVectorImpl ) resources;
			this.resources.andNot(other.resources);
		} // if
		else {
			Resource[] other = resources.toArray();
			for (int i = 0; i < other.length; i++) {
				this.resources.clear(other[i].getOrdinal()-1);
			} // for
		} // else
		super.setDirty();
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

	@Override
	public void shiftRemove(ResourceBundle rb) {
		int leftIndex=rb.get(0).getOrdinal()-1;
		int endIndex=leftIndex+rb.size();
		int rightIndex=endIndex;
		while (leftIndex<=endIndex){
			this.resources.set(leftIndex,this.resources.get(rightIndex));
			leftIndex++;
			rightIndex++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#size()
	 */
	public int size() {
		return this.resources.cardinality();
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.resources).toString();
	}


}
