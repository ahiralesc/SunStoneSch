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
package de.irf.it.rmg.core.teikoku.grid.resource;

import java.util.Map;

import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.NoResourceRequestAllowedException;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.SessionInvalidException;
import de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationTarget;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;

/**
 * This is the TestResourceBrokerTake, which especially knows all other ResourceBroker in the Grid
 * 
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class TestResourceBrokerTake extends AbstractResourceBroker {
	
	/**
	 * returns the list of known resourceBrokers and initializes it on start
	 * In this Implementation the ResourceBroker knows every other ResourceBroker in the Grid
	 */
	@Override
	public void initializeKnownResourceBroker(){
		Map<String,Site> sites=SiteContainer.getInstance().getAllAvailableSites();
		for (Site s:sites.values()){
			if (!(s==this.getSite())){
				super.addKnownResourceBroker(s.getResourceBroker());
			}
		}
	}

	@Override
	public void offer(ResourceDelegationSession session)
			throws OfferingVetoException {		
		//Coordinate the acceptance of all offered Resources
		try {
			session.accept(session.getResourcelist(), session.getTimespan());
			this.getSite().getScheduler().getSchedule().addResources(session.getResourcelist(), session.getTimespan(), ((ResourceBroker) session.getCreator()).getSite());
		} catch (AcceptanceVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SessionInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ResourceDelegationSession query(ResourceDelegationTarget dt)
			throws NoResourceRequestAllowedException {
		throw new NoResourceRequestAllowedException();
	}
}
