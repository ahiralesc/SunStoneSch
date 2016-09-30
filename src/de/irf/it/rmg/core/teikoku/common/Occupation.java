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
package de.irf.it.rmg.core.teikoku.common;

import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.util.time.Period;

/**
 * An <code>Occupation</code> denotes the exclusive usage of a set of
 * resources for a certain period of time.
 * 
 * Two <code>Occupation</code>s may not overlap regarding a certain system
 * (which in turn consists of a certain resource set) in terms of time and
 * space. That is, for any given period of time, no two <code>Occupation</code>s
 * referring to the same system must an other than empty intersect of their
 * corresponding resources.
 * 
 * @see Period
 * @see ResourceBundle
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class Occupation {

	/**
	 * Holds this occupation's duration as a period of time.
	 * 
	 */
	private Period duration;

	/**
	 * Holds this occupation's resource set as a bundle of resources.
	 * 
	 */
	private ResourceBundle resources;

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	public Occupation() {
		// do nothing here
	}
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param duration
	 *            The stipulated duration of the new occupation.
	 * @param resources
	 *            The stipulated resource set of the new occupation.
	 */
	public Occupation(Period duration, ResourceBundle resources) {
		this.duration = duration;
		this.resources = resources;
	}

	/**
	 * Returns the duration of this occupation. That is, it denotes the period
	 * of time during which a given set of resources is exclusively in use.
	 * 
	 * @return The duration of this occupation as a period of time.
	 * 
	 * @see #getResources()
	 */
	public Period getDuration() {
		return this.duration;
	}

	/**
	 * Sets the duration of this occupation to the provided parameter. That is,
	 * it manipulates the period of time during which a given set of resources
	 * is exclusively in use.
	 * 
	 * @param duration
	 *            The new duration of this occupation as a period of time.
	 */
	public void setDuration(Period duration) {
		this.duration = duration;
	}

	/**
	 * Returns the set of resources affected by this occupation. That is, it
	 * denotes the resource bundle exclusively in use during a give period of
	 * time.
	 * 
	 * @return the set of resources affected by this occupation as a bundle of
	 *         resources.
	 */
	public ResourceBundle getResources() {
		return this.resources;
	}

	/**
	 * Sets the set of resources affected by this occupation to the provided
	 * parameter. That is, it manipulates the resource bundle exclusively in use
	 * during a give period of time.
	 * 
	 * @param resources
	 *            The new set of resources affected by this occupation as a
	 *            bundle of resources.
	 */
	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}

}
