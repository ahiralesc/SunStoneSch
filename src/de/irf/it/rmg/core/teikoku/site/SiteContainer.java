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
 * Copyright (c) 2006 by the
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
package de.irf.it.rmg.core.teikoku.site;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.util.Initializable;

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
final public class SiteContainer
		implements Initializable {

	/**
	 * Keeps the only instance of this type, ensuring Singleton behaviour.
	 * 
	 */
	private static SiteContainer instance = null;

	/**
	 * Holds all sites managed by this manager. Note that identical sites (in
	 * terms of {@link Site#equals(Object)} are stored only once.
	 * 
	 */
	private Map<String, Site> sites;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;

	/**
	 * Returns the only instance of this class, performing a lazy initialization
	 * if the object does not already exist. This behavior follows the Singleton
	 * pattern.
	 * 
	 * @return The only instance of this class
	 */
	public static SiteContainer getInstance() {
		if (instance == null) {
			instance = new SiteContainer();
		} // if
		return instance;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	private SiteContainer() {
		this.sites = new HashMap<String, Site>();
	}

	public Set<Site> getSites() {
		return new HashSet<Site>(this.sites.values());
	}
	
	/**
	 * Returns the site being identified by the name given as a parameter.
	 * Non-registered sites are ignored.
	 * 
	 * @param name
	 * @return The <code>Site</code> instance identified by the given name,
	 *         iif registered; <code>null</code>, otherwise.
	 * 
	 * @see ComputeSite
	 */
	public Site getSite(String name) {
		return this.sites.get(name);
	}

	/**
	 * Adds the given site to the set of managed sites. If this site has been
	 * already registered, nothing happens.
	 * 
	 * @param s
	 *            The site to be registered to the manager
	 */
	public void addSite(ComputeSite s) {
		this.sites.put(s.getName(), s);
	}

	/**
	 * Removes the given site from the set of managed sites. If this site has
	 * been already unregistered, nothing happens.
	 * 
	 * @param s
	 *            The site to be unregistered from the manager.
	 */
	public void removeSite(Site s) {
		this.sites.remove(s);
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Initializable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#isInitialized()
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#initialize()
	 */
	public void initialize()
			throws InitializationException {
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(ComputeSite.CONFIGURATION_SECTION);
		String[] siteIds = c
				.getStringArray(Constants.CONFIGURATION_ID_COMPONENT);
		if (siteIds.length == 0) {
			String msg = "no sites defined in configuration";
			throw new InitializationException(msg);
		} // if
		for (String siteId : siteIds) {
			try {
				this.addSite(new ComputeSite(siteId));
			} // try
			catch (InstantiationException e) {
				throw new InitializationException(e);
			} // catch
		} // for

		this.initialized = true;
	}
	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	public Map<String, Site> getAllAvailableSites(){
		return this.sites;
	}
	
}
