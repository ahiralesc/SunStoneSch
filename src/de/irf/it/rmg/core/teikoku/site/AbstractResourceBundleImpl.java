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

import java.util.LinkedList;
import java.util.List;


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
public abstract class AbstractResourceBundleImpl implements ResourceBundle {

	/**
	 * Holds a cached array representation of this {@link ResourceBundle}.
	 * Note, however, that this field might be inaccurate at any given time,
	 * containing a "dirty" copy of the represented {@link Resource} objects.
	 * 
	 * @see #arrayRepresentationIsDirty
	 */
	private Resource[] arrayRepresentation;

	/**
	 * Denotes whether the cached array representation of this
	 * {@link ResourceBundle} is valid.
	 * 
	 * A <code>true</code> value indicates that the cached values have been
	 * invalidated and, as such, that the array must be rebuilt before use.
	 * 
	 * A <code>false</code> value indicates that the cached values are still
	 * correctly representing this {@link ResourceBundle}.
	 * 
	 * Note that this field must be set after every manipulating operation on
	 * this {@link ResourceBundle}.
	 * 
	 */
	private boolean arrayRepresentationIsDirty = true;

	/**
	 * Creates an array representation of this {@link ResourceBundle}
	 * implementation, containing all resources held in this class, in ascending
	 * order.
	 * 
	 */
	abstract protected Resource[] createArrayRepresentation();

	public Resource set(int index, Resource element) {
		return null;
	}

	/**
	 * Invalidates the cached array representation of this resource bundle.
	 * 
	 */
	protected void setDirty() {
		this.arrayRepresentationIsDirty = true;
	}

	public List<Resource> subList(int fromIndex, int toIndex) {
		List<Resource> tmp = new LinkedList<Resource>();
		Resource[] arrayRep = this.toArray();
		for(int j=fromIndex; j<toIndex; j++){
			tmp.add(arrayRep[j]); 
		}
		return tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.site.ResourceBundle#toArray()
	 */
	public Resource[] toArray() {
		if (arrayRepresentationIsDirty) {
			this.arrayRepresentation = this.createArrayRepresentation();
			this.arrayRepresentationIsDirty = false;
		} // if
		return this.arrayRepresentation;
	}

	public <T> T[] toArray(T[] a) {
		return null;
	}
}
