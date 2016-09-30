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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.common.Reservation;
import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSession;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleBitVectorImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * @author Administrator
 *
 */
public abstract class AbstractResourceBroker implements ResourceBroker {

	
	/**
	 * Holds a reference to the site the broker is connected with
	 */
	private Site site;
	/**
	 * Holds a list of known broker
	 */
	private List<ResourceBroker> knownResourceBroker=new ArrayList<ResourceBroker>();
	/**
	 * Holds a List of valid Sessions, which can be used to accept remote resources
	 */
	private List<ResourceDelegationSession> activeSessions=new ArrayList<ResourceDelegationSession>();
	
	/**
	 * This is a list of all managedResources, which is synchronized to implement, that in each moment each resource is present
	 * on ONE site
	 */
	private List<Resource> managedResources;
	/**
	 * This Map holds the public Information of the site, for default its only the number of
	 * given own resources
	 */
	private Map<String, Object> publicInformation=new HashMap<String, Object>();
	
	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#getActiveSessions()
	 */
	@Override
	public List<ResourceDelegationSession> getActiveSessions() {
		//First clean the list of inactive sessions
		int index=0;
		while(index<activeSessions.size()){
			if (!activeSessions.get(index).isValid()){
				activeSessions.remove(index);
			} else{
				index++;
			}
		}
		return activeSessions;
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#getKnownResourceBroker()
	 */
	@Override
	public List<ResourceBroker> getKnownResourceBroker() {
		if (knownResourceBroker.size()==0){
			initializeKnownResourceBroker();
		}
		return knownResourceBroker;
	}
	
	/**
	 * Must be implemented by subclass
	 */
	public abstract void initializeKnownResourceBroker();

	
	/**
	 * Method for adding knownResourceBroker
	 * @param ab
	 */
	public void addKnownResourceBroker(ResourceBroker rb){
		this.knownResourceBroker.add(rb);
	}
	
	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#getListOfManagedResources()
	 */
	@Override
	public ResourceBundle getListOfManagedResources() {
		return (ResourceBundle) this.managedResources;
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#getSite()
	 */
	@Override
	public Site getSite() {
		return this.site;
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#initialize()
	 */
	@Override
	public void initialize() throws InstantiationException {
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);
		
		//Initialize public Information here
		String key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_RESOURCEBROKER_PUBLIC_INFORMATION);
		if (key != null) {
			List<String> infos=c.getList(key);
			
			//Fill Public Information with Standardparts
			if (infos.contains("size")){
				publicInformation.put("size", this.getSite().getSiteInformation().getProvidedResources().size());
			}
			
		} // if	
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#manageNewResource(de.irf.it.rmg.core.teikoku.site.Resource)
	 */
	@Override
	public void manageNewResource(Resource r) {
		/*r.setName(r.getSite().getName()+"-"+r.getName());
		getListOfManagedResources().add(r);
		this.getSite().getScheduler().getSchedule().addResource(r);*/
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#manageNewResources(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	@Override
	public void manageNewResources(ResourceBundle rb) {
		/*for (Resource r:rb){
			r.setName(r.getSite().getName()+"-"+r.getName());
		}
		getListOfManagedResources().addAll(rb);
		this.getSite().getScheduler().getSchedule().addResources(rb,);*/
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker#setSite(de.irf.it.rmg.core.teikoku.site.Site)
	 */
	@Override
	public void setSite(Site site) {
		this.site=site;
		this.managedResources=Collections.synchronizedList(new ResourceBundleBitVectorImpl(this.site.getSiteInformation()));
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSource#delegate(de.irf.it.rmg.core.teikoku.site.Resource)
	 */
	@Override
	public void delegate(Resource resource,Period timespan) throws AcceptanceVetoException {
		//Delegate a single resource
		//Therefore, a reservation must be made
		Resource[] array={resource};
		Reservation rBegin=null;
		if (Clock.instance().now().before(timespan.getAdvent())){
			rBegin=new Reservation(Clock.instance().now(),timespan.getAdvent(),new ResourceBundleBitVectorImpl(this.site.getSiteInformation(),array));
		}
		Reservation rEnd=new Reservation(timespan.getAdvent(),timespan.getCessation(),new ResourceBundleBitVectorImpl(this.site.getSiteInformation(),array));
		try {
			this.site.getScheduler().getSchedule().addReservation(rEnd);
			if (rBegin!=null) this.site.getScheduler().getSchedule().addReservation(rBegin);
			this.site.getSiteInformation().addConferedResources(1);
		} catch (IllegalOccupationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalScheduleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSource#delegate(de.irf.it.rmg.core.teikoku.site.ResourceBundle)
	 */
	@Override
	public void delegate(ResourceBundle resources,Period timespan)
			throws AcceptanceVetoException {
		/*Resource[] array
		Reservation rBegin=null;
		if (Clock.instance().now().before(timespan.getAdvent())){
			rBegin=new Reservation(Clock.instance().now(),timespan.getAdvent(),new ResourceBundleBitVectorImpl(this.site.getSiteInformation(),resources));
		}*/
		Reservation r=new Reservation(timespan.getAdvent(),timespan.getCessation(),resources);
		try {
			this.site.getScheduler().getSchedule().addReservation(r);
			this.site.getSiteInformation().addConferedResources(resources.size());
		} catch (IllegalOccupationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalScheduleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.delegation.ResourceDelegationSource#getPublicInformation()
	 */
	@Override
	public Map<String, Object> getPublicInformation() {
		return this.publicInformation;
	}

}
