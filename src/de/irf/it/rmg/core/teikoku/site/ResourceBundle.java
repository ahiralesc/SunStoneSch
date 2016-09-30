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

import java.util.List;


/**
 * Represents a set of {@link Resource}s aggregated as a bundle with respect to
 * certain properties. For example, sets of free, occupied or otherwise similar
 * resources can be grouped this way.
 * 
 * Furthermore, it allows various manipulative operations on such a set. This
 * includes addition, removal, and conversion to an array of {@link Resource}
 * objects as well as various set-based operations such as cardinality,
 * intersect and difference building, and subset test and creation.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public interface ResourceBundle extends List<Resource>{

	/**
	 * Adds the given set of resources to this bundle. Note that this operation
	 * works set-based: that is, {@link Resource} objects that are already part
	 * of the bundle are <em>not</em>
	 * 
	 * @param resources
	 */
	void add(ResourceBundle resources);

	/**
	 * TODO: not yet commented
	 * 
	 * @param numberOfResources
	 * @return
	 */
	ResourceBundle createSubSetWith(int numberOfResources);

	/**
	 * TODO: not yet commented
	 * 
	 * @param numberOfResources
	 * @param startIndex
	 * @return
	 */
	ResourceBundle createSubSetWith(int numberOfResources, int startIndex);

	/**
	 * TODO: not yet commented
	 * 
	 * @param resources
	 * @return
	 */
	ResourceBundle difference(ResourceBundle resources);

	/**
	 * TODO: not yet commented
	 * 
	 * @param resources
	 * @return
	 */
	ResourceBundle intersect(ResourceBundle resources);

	/**
	 * TODO: not yet commented
	 * 
	 * @param resources
	 * @param proper
	 * @return
	 */
	boolean isSubSetOf(ResourceBundle resources, boolean proper);

	/**
	 * TODO: not yet commented
	 * 
	 * @param resources
	 */
	void remove(ResourceBundle resources);

	/**
	 * 
	 * @param rb
	 */
	void shiftRemove(ResourceBundle rb);

	/**
	 * Calculates the size of this bundle, denoting how many {@link Resource}
	 * object are represented by it.
	 * 
	 * @return The size of this bundle.
	 */
	int size();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	Resource[] toArray();

}
