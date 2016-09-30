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
package de.irf.it.rmg.core.teikoku.grid.location;

import java.util.Comparator;
import java.util.List;

import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;

/**
 * The locationPolicy serves for ordering the partners of job-exchange by a specific ordering
 * to specify, which one should be asked first, when a job should be given to a remote site
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public interface LocationPolicy extends Comparator<ActivityBroker>{

	/**
	 * This method returns an ordered List of activityBrokers by a locationPolicy-dependent ordering
	 * 
	 * @param knownSites
	 * @return List<ResourceBroker>
	 */
	public List<ActivityBroker> getOrder(List<ActivityBroker> knownSites);

	/**
	 * standard-setter for the activityBroker, which uses this policy
	 * @param activityBroker
	 */
	public void setActivityBroker(ActivityBroker activityBroker);

	/**
	 * This is for initialization of the policy (maybe loading some metrics for the decision)
	 * @throws InstantiationException
	 */
	public void initialize() throws InstantiationException;
	
}
