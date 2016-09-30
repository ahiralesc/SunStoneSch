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
package de.irf.it.rmg.core.teikoku.grid.transfer;

import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;
import de.irf.it.rmg.core.teikoku.job.Job;

/**
 * This transferPolicy only accepts jobs, which ressource-demand is lower or equal the ressources, which are
 * idle
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public class FittingTransferPolicy implements TransferPolicy {
	
	/**
	 * Field for the activityBroker, the transferpolicy is realized at
	 */
	ActivityBroker activityBroker;
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy#decide(de.irf.it.rmg.core.teikoku.job.Job, de.irf.it.rmg.core.teikoku.grid.DelegationSource)
	 */
	public boolean decide(Job job,ActivityDelegationSource ds) {
		return decideByRequestedRessources(job);
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy#decide(de.irf.it.rmg.core.teikoku.job.Job, de.irf.it.rmg.core.teikoku.grid.DelegationTarget)
	 */
	public boolean decide(Job job,ActivityDelegationTarget dt) {
		return decideByRequestedRessources(job);
	}
	
	/**
	 * Help method
	 * @param job
	 * @return boolean
	 */
	private boolean decideByRequestedRessources(Job job){
		return (job.getDescription().getNumberOfRequestedResources()<=
			activityBroker.getSite().getScheduler().getSchedule().getActualFreeness());
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.TransferPolicy#setSite(de.irf.it.rmg.core.teikoku.site.Site)
	 */
	public void setActivityBroker(ActivityBroker activityBroker) {
		this.activityBroker=activityBroker;
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy#initialize(java.lang.String)
	 */
	public void initialize(String prefix) {
		// nothng to initialize here
		
	}
}
