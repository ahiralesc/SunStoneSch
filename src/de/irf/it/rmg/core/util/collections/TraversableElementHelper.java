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

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */
public class TraversableElementHelper {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private TraversableElement previous;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private TraversableElement next;

	/**
	 * TODO: not yet commented
	 * 
	 */
	public TraversableElementHelper() {
		// do nothing here
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param previous
	 * @param next
	 */
	public TraversableElementHelper(TraversableElement previous,
			TraversableElement next) {
		this.previous = previous;
		this.next = next;
	}

	/**
	 * Getter method for the "next" field of this type.
	 * 
	 * @return Returns the current contents of "this.next".
	 */
	public final TraversableElement getNext() {
		return this.next;
	}

	/**
	 * Setter method for the "next" field of this type.
	 * 
	 * @param next
	 *            Sets the contens of "this.next" to the given value.
	 */
	public final void setNext(TraversableElement next) {
		this.next = next;
	}

	/**
	 * Getter method for the "previous" field of this type.
	 * 
	 * @return Returns the current contents of "this.previous".
	 */
	public final TraversableElement getPrevious() {
		return this.previous;
	}

	/**
	 * Setter method for the "previous" field of this type.
	 * 
	 * @param previous
	 *            Sets the contens of "this.previous" to the given value.
	 */
	public final void setPrevious(TraversableElement previous) {
		this.previous = previous;
	}

}
