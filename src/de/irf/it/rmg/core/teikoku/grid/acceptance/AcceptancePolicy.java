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
package de.irf.it.rmg.core.teikoku.grid.acceptance;

import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy;
import de.irf.it.rmg.core.teikoku.job.Job;

/**
 * This is the Interface for an AcceptancePolicy, which has to decide, if locally submitted
 * jobs have to remain on the site or how they are distributed to other sites if not.
 * On the other hand, it has to decide, under which cirumstances remote jobs are accepted
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public interface AcceptancePolicy {

	/**
	 * Decide the acceptance of jobs submitted by a user over the localSubmissionComponent
	 * @param session
	 */
	public boolean decideLocalJob(Job job);
	/**
	 * Decide the acceptance of jobs offered by other sites/ components
	 * @param session
	 */
	public boolean decideRemoteJob(Job job, ActivityDelegationSource creator);

	/**
	 * Standard-setter for the activityBroker, which uses this acceptancePolicy
	 * @param broker
	 */
	public void setActivityBroker(ActivityBroker broker);

	/**
	 * initialization of the component
	 * @throws InstantiationException
	 */
	public void initializeTransferPolicy() throws InstantiationException;
	
	/**
	 * standard-setter for the transferPolicy
	 * @param tp
	 */
	public void setTransferPolicy(TransferPolicy tp);
	
	public TransferPolicy getTransferPolicy(); 
}
