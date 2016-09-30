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
/**
 * 
 */
package de.irf.it.rmg.core.teikoku.grid.location;

import java.util.List;

import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;

/**
 * @author Alex
 *
 */
public class DummyLocationPolicy implements LocationPolicy {

	
	
	public List<ActivityBroker> getOrder(
			List<ActivityBroker> knownSites) {
		return knownSites;
	}

	public void initialize() throws InstantiationException{
		//Initialize the Metric that should be used
	}


	public int compare(ActivityBroker o1, ActivityBroker o2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setActivityBroker(ActivityBroker activityBroker) {
		
	}

}
