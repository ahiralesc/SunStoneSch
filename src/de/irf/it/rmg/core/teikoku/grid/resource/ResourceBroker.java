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
package de.irf.it.rmg.core.teikoku.grid.resource;

import java.util.List;

import de.irf.it.rmg.core.teikoku.exceptions.NoResourceRequestAllowedException;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationTarget;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;

/**
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public interface ResourceBroker extends ResourceDelegationSource,ResourceDelegationTarget{

	/**
	 * This method is used to make an offer of resources to the Broker
	 * @param ResourceDelegationSession session
	 * @throws OfferingVetoException 
	 */
	public void offer(ResourceDelegationSession session) throws OfferingVetoException;

	/**
	 * This method is used to query the Broker for resources to delegate
	 * 
	 * @return ResourceDelegationSession session
	 * @throws NoResourceRequestAllowedException 
	 */
	public ResourceDelegationSession query(ResourceDelegationTarget dt) throws NoResourceRequestAllowedException;

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
	public List<ResourceBroker> getKnownResourceBroker();

	/**
	 * This method is for Initialization of the resourceBroker to initialize the following
	 * important components:
	 * - public Information available on this activityBroker
	 * @throws InstantiationException
	 */
	public void initialize() throws InstantiationException;
	
	/**
	 * returns the list of all resources, which are managed by the site
	 * @return ResourceBundle
	 */
	public ResourceBundle getListOfManagedResources();

	/**
	 * This is used to take over the responsibility for a remote resource
	 * @param r
	 */
	public void manageNewResource(Resource r);
	
	/**
	 * This is used to take over the responsibility for a remote resourcebundle
	 * @param rb
	 */
	public void manageNewResources(ResourceBundle rb);
	
	/**
	 * returns the list of all activeSessions
	 * @return List<ResourceDelegationSession>
	 */
	public List<ResourceDelegationSession> getActiveSessions();
}
