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
 * This Transferpolicy accepts every job, regardless the ActivityDelegationSource or -Target 
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public class DummyTransferPolicy implements TransferPolicy {
	
	/**
	 * Field for the activityBroker, the transferpolicy is realized at
	 */
	private ActivityBroker activityBroker;

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.TransferPolicy#setSite(de.irf.it.rmg.core.teikoku.site.Site)
	 */
	public void setActivityBroker(ActivityBroker activityBroker) {
		this.activityBroker=activityBroker;
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.TransferPolicy#decide(de.irf.it.rmg.core.teikoku.job.Job)
	 */
	public boolean decide(Job job,ActivityDelegationSource ds) {
		return true;
	}

	public void initialize(String prefix) {
		// Do nothing here
	}

	public boolean decide(Job job, ActivityDelegationTarget dt) {
		return true;
	}

}
