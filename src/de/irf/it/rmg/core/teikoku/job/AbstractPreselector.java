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
package de.irf.it.rmg.core.teikoku.job;

import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;

import mx.cicese.dcc.teikoku.information.broker.SiteInformation;


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
abstract public class AbstractPreselector
		implements Preselector {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private SiteInformation siteInformation;
	
	/**
	 * TODO: not yet commented
	 *
	 * @param j
	 * @return
	 */
	abstract protected boolean decide(Job j);
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.job.Preselector
	// -------------------------------------------------------------------------

	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.job.Preselector#preselect(de.irf.it.rmg.core.util.collections.SortedQueue)
	 */
	public SortedQueue<Job> preselect(SortedQueue<Job> jobs) {
		SortedQueue<Job> result = new SortedQueueBinarySearchImpl<Job>(jobs.getOrdering()); 
		for (Job job : jobs) {
			if (this.decide(job)) {
				result.add(job);				
			} // if
		} // for
		return result;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.job.Preselector#preselect(de.irf.it.rmg.core.teikoku.job.OldJob)
	 */
	public boolean preselect(Job j){
		return this.decide(j);
	}

	
	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	protected SiteInformation getSiteInformation() {
		return siteInformation;
	}

	
	/**
	 * TODO: not yet commented
	 *
	 * @param siteInformation
	 */
	public void setSiteInformation(SiteInformation siteInformation) {
		this.siteInformation = siteInformation;
	}
	
	
}
