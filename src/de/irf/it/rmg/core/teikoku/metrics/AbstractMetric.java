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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.MetricsException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.Initializable;
import de.irf.it.rmg.core.util.persistence.NullPersistentStore;
import de.irf.it.rmg.core.util.persistence.PersistentStore;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.EventSink;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.core.util.time.Distance;


/**
 * This class serves as the basic abstract model for each implemented metric.
 * Therefore, it includes the basic methodes for persistance management as well
 * as an abstractt facade that establishes a metric specific view of each job.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
@MomentOfNotification()
@EventSink
abstract public class AbstractMetric
		implements Metrics {
	
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(AbstractMetric.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	final private PersistentStoreFactory persistentStoreFactory;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private PersistentStore persistentStore;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private String name;

	/**
	 * Pointer to the Site, the Metric is connected with
	 */
	private Site site;	
	
	/**
	 * LatestValues Of the Metric
	 */
	private Object[] latestValues;
	
	/**
	 * LatestValues of the Metric for
	 * Sitespecific metrics like jobexchange or awrt of delivered jobs
	 */
	private Map<Object, Object[]> latestValuesMap=new HashMap<Object, Object[]>();

	/**
	 * flag for manual metricWriting
	 * if true, the metric will write itself for example
	 * at each eventoccursion
	 * if false (Default) the metric will be written on every statechange in the 
	 * simulated grid
	 */
	private boolean manualPermanent;

	
	/**
	 * Abstract constructor
	 * 
	 * @throws MetricsException
	 */
	protected AbstractMetric() {
		this.initialized = false;	
		this.manualPermanent = true;
		this.persistentStoreFactory = new PersistentStoreFactory();
	}
	
	private void initializeMetric(){
		/*
		 * initialize latest values
		 */
		this.latestValues=this.getLatestValuesPrototype();

		/*
		 * initialize persistent store
		 */
		this.persistentStore = this.persistentStoreFactory.createPersistentStoreInstance();
		persistentStore.makePersistent(this.getHeader());
	
		/*
		 * register for accepted event types
		 */
		
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	abstract protected Object[] getHeader();

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	abstract protected Object[] getLatestValuesPrototype();

	/**
	 * TODO: not yet commented
	 * 
	 * @param p
	 * @return
	 */
	protected URL getOutputPath(){
		URL result = null;
		
		String sitename=this.site==null? "":this.site.getName();
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(RuntimeEnvironment.CONFIGURATION_SECTION);
		String workingDirectory = c
				.getString(Constants.CONFIGURATION_RUNTIME_WORKINGDIRECTORY);
		File file = null;
		if (this.persistentStoreFactory.outputFileName == null) {
			file = new File(workingDirectory + File.separator +
					Constants.CONFIGURATION_METRICS_WRITER_OUTPUTFILE_SUBDIRECTORY + File.separator 
					+ this.getClass().getSimpleName() + File.separator
					+ this.name + "-" + sitename);
		}
		else {
			file = new File(workingDirectory + File.separator +
					Constants.CONFIGURATION_METRICS_WRITER_OUTPUTFILE_SUBDIRECTORY + File.separator 
					+ this.getClass().getSimpleName() + File.separator
					+  sitename + "-" + this.persistentStoreFactory.outputFileName);
		}

		try {
			URI u = file.toURI();
			result = u.toURL();
		} // try
		catch (MalformedURLException e) {
			String msg = "erroneous output path: URL invalid";
			log.error(msg, e);
		} // catch

		return result;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.metrics.Metrics
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.Metrics#getName()
	 */
	final public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.Metrics#setName(java.lang.String)
	 */
	final public void setName(String name) {
		this.name = name;
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
		if (this.name == null) {
			String msg = "initialization failed: no name set";
			log.error(msg);
			throw new InitializationException(msg);
		} // if
		this.persistentStoreFactory.initialize();
		
		initializeMetric();
		
		this.initialized = true;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Ephemeral
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#commence()
	 */
	public void commence() {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#terminate()
	 */
	public void terminate() {
		//Write the latestValues on the end of the simulation
		this.manualMakePermanent();
	}
	
	/**
	 * Sets the latestValues of the Metric
	 * @param value
	 */
	public void setLatestValues(Object[] value){
		this.latestValues=value;
	}
	
	/**returns the latestValues of the Metric
	 * @return
	 */
	public Object[] getCurrentValues(){
		try{
			return latestValues[0]==null?null:latestValues;
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Site-specific return of the latestValues
	 * @param site
	 * @return
	 */
	public Object[] getCurrentValues(Site site){
		if (site==null) return getCurrentValues(); else
		return latestValuesMap.get(site)==null?getCurrentValues():latestValuesMap.get(site);
	}
		
	/**
	 * Site-specific set of the latest Values
	 * @param site
	 * @param values
	 */
	public void setLatestValues(Site site, Object[] values){
		latestValuesMap.put(site, values);
	}

	/**
	 * @return
	 */
	public PersistentStore getPersistentStore() {
		return persistentStore;
	}

	/**
	 * @param persistentStore
	 */
	public void setPersistentStore(PersistentStore persistentStore) {
		this.persistentStore = persistentStore;
	}
	
	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.metrics.Metrics#makePermanent()
	 */
	public void makePermanent(){
		if (!this.manualPermanent){
			if (!this.latestValuesMap.isEmpty()){
				for (Object[] val:this.latestValuesMap.values()){
					this.persistentStore.makePersistent(val);
				}
			} else{
				if (latestValues!=null && latestValues[0]!=null){
					persistentStore.makePersistent(latestValues);
				}
			}
		}
	}
	
	public void manualMakePermanent(){
		if (manualPermanent){
			if (!this.latestValuesMap.isEmpty()){
				for (Object[] val:this.latestValuesMap.values()){
					this.persistentStore.makePersistent(val);
				}
			} else{
				if (latestValues!=null && latestValues[0]!=null){
					persistentStore.makePersistent(latestValues);
				}
			}
		}
	}
	
	/**
	 * configuration/method for metrics with a specific timewindow
	 * like Windowed Utilization
	 * @param windowSize
	 */
	public void setWindowSize(long windowSize){
		//leave implementation to subclass
	}
	
	
	/**
	 * sets the flag manual for manual persistence writing by
	 * the Metric
	 * @param mp
	 */
	public void setManualPermanent(boolean mp){
		this.manualPermanent=mp;
	}
	
	/**
	 * returns the value of the flag for manual metric-writing
	 * @return
	 */
	public boolean getManualPermanent(){
		return this.manualPermanent;
	}
	
	/**
	 * TODO: not yet commented
	 * 
	 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>,
	 *         <a href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>,
	 *         and <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
	 *         Papaspyrou</a> (last edited by $Author$)
	 * @version $Version$, $Date$
	 * 
	 */
	public class PersistentStoreFactory
			implements Initializable {

		/**
		 * TODO: not yet commented
		 * 
		 */
		private boolean initialized = false;

		/**
		 * TODO: not yet commented
		 * 
		 */
		private Class<? extends PersistentStore> persistentStorePrototype;

		/**
		 * TODO: not yet commented
		 * 
		 */
		private boolean allowOverwritePrototype;

		/**
		 * TODO: not yet commented
		 * 
		 */
		private String outputFileName = null;

		/**
		 * TODO: not yet commented
		 * 
		 * @param a
		 * @return
		 */
		public PersistentStore createPersistentStoreInstance() {
			PersistentStore p = null;
			try {
				p = this.persistentStorePrototype.newInstance();
			} // try
			catch (InstantiationException e) {
				throw new Error(e);
			} // catch
			catch (IllegalAccessException e) {
				throw new Error(e);
			} // catch
			p.setUrl(getOutputPath());
			p.setAllowOverwrite(this.allowOverwritePrototype);
			p.setOutputFileName(this.outputFileName);
			try {
				p.initialize();
			} // try
			catch (InitializationException e) {
				String msg = "initalization error: " + e.getMessage()
						+ "; using " + NullPersistentStore.class.getName();
				log.error(msg, e);
				p = new NullPersistentStore();
				p.setUrl(null);
				try {
					p.initialize();
				} // try
				catch (InitializationException ex) {
					throw new Error(ex);
				} // catch
			} // catch
			return p;
		}

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
			Configuration c = RuntimeEnvironment.getInstance()
					.getConfiguration().subset(Metrics.CONFIGURATION_SECTION);

			/*
			 * load persistent store prototype
			 */
			String writerClass = c.getString(ConfigurationHelper
					.retrieveRelevantKey(c, name,
							Constants.CONFIGURATION_METRICS_WRITER_CLASS));
			try {
				this.persistentStorePrototype = ClassLoaderHelper
						.loadInterface(writerClass, PersistentStore.class)
						.getClass();
			} // try
			catch (InstantiationException e) {
				throw new InitializationException(e);
			} // catch

			this.outputFileName = c.getString(ConfigurationHelper
					.retrieveRelevantKey(c, name,
							Constants.CONFIGURATION_METRICS_WRITER_OUTPUTFILE));

			this.allowOverwritePrototype = c
					.getBoolean(ConfigurationHelper
							.retrieveRelevantKey(
									c,
									name,
									Constants.CONFIGURATION_METRICS_WRITER_MAYOVERWRITE));
			this.initialized = true;
		}
	}

	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	protected double calculateResponseTime(Job j) {
		Instant releaseTime = j.getReleaseTime();
		Instant endTime = j.getDuration().getCessation();
		
		Distance d = TimeHelper.distance(releaseTime, endTime);
		return TimeHelper.toSeconds(d);
	}
}

