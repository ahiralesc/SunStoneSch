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
package de.irf.it.rmg.core.teikoku.metrics;

import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.Ephemeral;
import de.irf.it.rmg.core.util.Initializable;
import de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>,
 * 		   <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public interface Metrics extends Initializable, Ephemeral{

	/**
	 * TODO: not yet commented
	 * 
	 */
	String CONFIGURATION_SECTION = "metrics";

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	String getName();

	/**
	 * TODO: not yet commented
	 * 
	 * @param name
	 */
	void setName(String name);
	
	
	/**
	 * TODO: not yet commented
	 *
	 * @param site
	 * @return
	 */
	Object[] getCurrentValues(Site site);
	
	/**
	 * Writes the latestValue of the Metric
	 */
	void makePermanent();

	void setSite(Site site);
	
}
