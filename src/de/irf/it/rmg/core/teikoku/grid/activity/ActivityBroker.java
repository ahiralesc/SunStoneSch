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
package de.irf.it.rmg.core.teikoku.grid.activity;

import java.util.List;

import de.irf.it.rmg.core.teikoku.exceptions.NoJobRequestAllowedException;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.acceptance.AcceptancePolicy;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;
import de.irf.it.rmg.core.teikoku.grid.distribution.DistributionPolicy;
import de.irf.it.rmg.core.teikoku.grid.location.LocationPolicy;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;

import mx.cicese.dcc.teikoku.broker.ActivityBrokerRole;

/**
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public interface ActivityBroker extends ActivityDelegationSource,ActivityDelegationTarget{

	/**
	 * This method is used to make an offer of Jobs to the Broker
	 * @param ActivityDelegationSession session
	 * @throws OfferingVetoException 
	 */
	public void offer(ActivityDelegationSession session) throws OfferingVetoException;

	/**
	 * This method is used to query the Broker for Jobs to delegate
	 * 
	 * @return DelegationsSession session
	 * @throws NoJobRequestAllowedException 
	 */
	public ActivityDelegationSession query(ActivityDelegationTarget dt) throws NoJobRequestAllowedException;

	/**
	 * Standard-getter
	 * @return Site
	 */
	public Site getSite();
	
	/**
	 * Standard-setter
	 * @param site
	 */
	public void setSite(Site site);
	
	/**
	 * returns the list of all known ResourceBroker
	 * @return List<ResourceBroker>
	 */
	public List<ActivityBroker> getKnownActivityBroker();

	/**
	 * This method is for Initialization of the activityBroker to initialize the following
	 * importnt components:
	 * - locationPolicy
	 * - distributionPolicy
	 * - acceptancePolicy
	 * - sessionLifetime
	 * - public Information available on this activityBroker
	 * @throws InstantiationException
	 */
	public void initialize() throws InstantiationException;
	
	/**
	 * returns the list of all jobs, which are managed by the site
	 * @return List<Job>
	 */
	public List<Job> getListOfManagedJobs();

	/**
	 * This is used to take over the responsiblity for a remote Job
	 * @param j
	 */
	public void manageNewJob(Job j);
	
	/**
	 * returns the list of all activeSessions
	 * @return List<ActivityDelegationSession>
	 */
	public List<ActivityDelegationSession> getActiveSessions();

	/**
	 * Standard-getter
	 * @return DistributionPolicy
	 */
	public DistributionPolicy getDistributionPolicy();
	
	/**
	 * Standard-getter
	 * @return LocationPolicy
	 */
	public LocationPolicy getLocationPolicy();
	
	/**
	 * Standard-getter
	 * @return AcceptancePolicy
	 */
	public AcceptancePolicy getAcceptancePolicy();
	
	/**
	 * Gets the role of this activity broker
	 * 
	 * @return ActivityBrokerRole the role the broker
	 * 
	 * @see ActivityBrokerRole
	 */
	ActivityBrokerRole getRole();
	
	/**
	 * Sets the role of this activity broker, roles can be:
	 * -	Grid activity broker (GRID)
	 * -	Compute site activity broker  (COMPUTE_SITE)
	 * -	Other possibilities are: Data site activity broker (DATA_SITE)
	 * 
	 * @param ActivityBrokerRole the role this activity broker 	
	 * 
	 * @see ActivityBrokerRole
	 */
	void setRole(ActivityBrokerRole role);
}
