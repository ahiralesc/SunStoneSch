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
package de.irf.it.rmg.core.teikoku;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.time.StopWatch;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.metrics.MetricsVault;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.util.Ephemeral;
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
final public class RuntimeEnvironment implements Initializable, Ephemeral {

	/**
	 * TODO: not yet commented
	 * 
	 */
	final public static String CONFIGURATION_SECTION = "runtime";

	/**
	 * Keeps the only instance of this type, ensuring Singleton behavior.
	 * 
	 */
	private static RuntimeEnvironment instance = null;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private StopWatch runtimeStopWatch;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Configuration configuration;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private SiteContainer siteContainer;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private MetricsVault metricsVault;

	/**
	 * Returns the only instance of this class, performing a lazy initialization
	 * if the object does not already exist. This behavior follows the Singleton
	 * pattern.
	 * 
	 * @return The only instance of this class
	 */
	public static RuntimeEnvironment getInstance() {
		if (instance == null) {
			instance = new RuntimeEnvironment();
		} // if
		return instance;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	private RuntimeEnvironment() {
		this.initialized = false;
		this.configuration = new BaseConfiguration();
	}

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	final public StopWatch getRuntimeStopWatch() {
		return this.runtimeStopWatch;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(Configuration c) {
		CompositeConfiguration cc = new CompositeConfiguration();
		cc.addConfiguration(this.getConfiguration());
		cc.addConfiguration(c);
		this.configuration = cc;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public SiteContainer getSiteContainer() {
		return siteContainer;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public MetricsVault getMetricsVault() {
		return metricsVault;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Initializable
	// -------------------------------------------------------------------------

	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.util.Initializable#isInitialized()
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.util.Initializable#initialize()
	 */
	public void initialize() throws InitializationException {
		/*
		 * initialize siteContainer
		 */
		SiteContainer sc = SiteContainer.getInstance();
		if (!sc.isInitialized()) {
			sc.initialize();
		} // if
		siteContainer = sc;

		/*
		 * initialize metricsVault
		 */
		MetricsVault mv = MetricsVault.getInstance();
		mv.commence();
		metricsVault = mv;

		initialized = true;
	}

	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Ephemeral
	// -------------------------------------------------------------------------

	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.util.Ephemeral#commence()
	 */
	public void commence() {
		// XXX: nothing to commence.
	}

	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.util.Ephemeral#terminate()
	 */
	public void terminate() {
		this.metricsVault.terminate();
	}
}
