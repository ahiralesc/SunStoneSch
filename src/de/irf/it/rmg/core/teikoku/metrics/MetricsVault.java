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
package de.irf.it.rmg.core.teikoku.metrics;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.MetricsException;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.Ephemeral;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.annotations.InvalidAnnotationException;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>,
 * 		   <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class MetricsVault implements Ephemeral {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(MetricsVault.class);

	/**
	 * Keeps the only instance of this class, following the Singleton pattern.
	 * 
	 */
	private static MetricsVault instance = null;

	/**
	 * Holds the currently available metrics, indexed by their site-identifier-pair.
	 * 
	 */
	private Map<Site, Map<String,Metrics>> metrics;
	
	/**
	 * Returns the only instance of this class, performing a lazy initialization
	 * if the object does not already exist. This behavior follows the Singleton
	 * pattern.
	 * 
	 * @return The only instance of this class
	 */
	public static MetricsVault getInstance() {
		if (instance == null) {
			instance = new MetricsVault();
		} // if
		return instance;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	private MetricsVault() {
		this.metrics = new HashMap<Site, Map<String, Metrics>>();
	}

	/**
	 * Returns the {@link Metrics} instance denoted by the identifier given in
	 * the parameter, performing lazy initialization of the metric, if
	 * necessary. Connect it to the site
	 * 
	 * @param site
	 * @param identifier
	 * @return
	 * @throws MetricsException
	 */
	public Metrics retrieveMetric(Site site,String identifier) throws MetricsException {
		if (!this.metrics.containsKey(site)){
			metrics.put(site, new HashMap<String, Metrics>());
		}
		Map metricsForSite=metrics.get(site);
		
		if (!metricsForSite.containsKey(identifier)) {
			Metrics m = null;
			String metricClassName = this
					.determineRequestedMetricClassName(identifier);
			m = this.createMetricsInstance(identifier, metricClassName);
			m.setName(identifier);
			m.setSite(site);
			try {
				m.initialize();
			} // try
			catch (InitializationException e) {
				throw new MetricsException(e);
			} // catch
			m.commence();
			metricsForSite.put(identifier, m);
			try {
				Kernel.getInstance().registerEventSink(m);
			} catch (InvalidAnnotationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // if
		return this.metrics.get(site).get(identifier);
	}
	
	/**
	 * Register the {@link Metrics} instance denoted by the identifier given in
	 * the parameter, performing lazy initialization of the metric, if
	 * necessary. Connect it to the site
	 * 
	 * @param site
	 * @param identifier
	 * @param source
	 * @param style
	 * @return
	 * @throws MetricsException
	 */
	public Metrics registerMetricAt(Site site,String identifier)
			throws MetricsException {
		Metrics m = this.retrieveMetric(site,identifier);
		return m;
	}

	/**
	 * Determines for a given identifier the class name of the associated metric
	 * as defined in the system configuration.
	 * 
	 * @param metricId
	 *            The identifier of the requested metric.
	 * @return The name of the class for the given identifier.
	 * @throws MetricsException
	 *             iif the given identifier has no corresponding entry in the
	 *             system configuration.
	 */
	private String determineRequestedMetricClassName(String metricId)
			throws MetricsException {
		String result = null;

		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(Metrics.CONFIGURATION_SECTION);

		String key = metricId + "." + Constants.CONFIGURATION_CLASS_COMPONENT;
		if (c.containsKey(key)) {
			result = c.getString(key);
		} // if
		else {
			String msg = "configuration error: class specification for metrics identifier ("
					+ metricId + ") not found";
			log.error(msg);
			throw new MetricsException(msg);
		} // else

		return result;
	}

	/**
	 * Creates a new instance of a {@link Metrics} implementation, denoted by
	 * the class name given in the parameter.
	 * 
	 * @param identifier
	 * @param className
	 * @return
	 * @throws MetricsException
	 */
	private Metrics createMetricsInstance(String identifier, String className)
			throws MetricsException {
		Metrics result = null;
		try {
			result = ClassLoaderHelper.loadInterface(className, Metrics.class);
		} // try
		catch (InstantiationException e) {
			throw new MetricsException(e);
		} // catch

		return result;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Ephemeral
	// -------------------------------------------------------------------------

	/**
	 * Performs startup initializations for the {@link MetricsVault} itself and
	 * its registered {@link Metrics}, if appropriate.
	 * 
	 * This method must be called directly after measurement beginning (probably
	 * in conjunction with {@link #getInstance()}). Otherwise, this vault and
	 * its associated object instances might not be able to initialize
	 * measurement and lead to unpredictable results.
	 * 
	 * @see #commence()
	 * @see Ephemeral
	 */
	public void commence() {
		// do nothing here.
	}

	/**
	 * Performs shutdown preparation for the {@link MetricsVault} itself and its
	 * registered {@link Metrics}, if appropriate.
	 * 
	 * This method must be called directly before measurement completion
	 * (probably after shutting down the system). Otherwise, this vault and its
	 * associated object instances might not be able to complete measurement and
	 * lead to unpredictable results.
	 * 
	 * @see #terminate()
	 * @see Ephemeral
	 */
	public void terminate() {
		//At least write out all Metrics - local and global
		for(Site s:metrics.keySet()){
			for (Metrics m : this.metrics.get(s).values()) {
				m.terminate();
			} // for
		}// for
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.terminate();
	}
	
	public Metrics getMetric(Site site,String identifier){
		return this.metrics.get(site).get(identifier);
	}
}
