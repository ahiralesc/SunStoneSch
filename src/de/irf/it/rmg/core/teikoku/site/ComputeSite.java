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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;
import mx.cicese.dcc.teikoku.information.broker.InfBrokerType;
import mx.cicese.dcc.teikoku.information.broker.InformationBroker;
import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationBroker;
import mx.cicese.dcc.teikoku.broker.ActivityBrokerRole;
import mx.cicese.dcc.teikoku.broker.GridActivityBroker;
import mx.cicese.dcc.teikoku.energy.EnergyBroker;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.resource.ResourceBroker;
import de.irf.it.rmg.core.teikoku.kernel.events.VoltageFrequencyRegulationEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.scheduler.Scheduler;
import de.irf.it.rmg.core.teikoku.submission.Executor;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;
import de.irf.it.rmg.core.teikoku.submission.SimulationAdapter;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.annotations.InvalidAnnotationException;

/**
 * <p>
 * Represents a single site, bundling a set of resources and managing their
 * occupation with a scheduler.
 * </p>
 * 
 *  
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *          href="mailto:adan.hirales@cetys.mx">Adan Hirales</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */
public class ComputeSite implements Site {

    /**
     * The default log facility for this class, using the <a
     * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
     *
     * @see org.apache.commons.logging.Log
     */
    final private static Log log = LogFactory.getLog(ComputeSite.class);

    /**
     * The compute site is composed by several subcomponents
     * - Activity broker or Grid activity broker
     * - Executer
     * - Site information, stores information that the site produces
     * - Local submission component, used to produce local jobs
     * - ResourceBroker
     * - Energy manager emulates energy consumption
     */
    
    
    /**
     * The site activity broker
     */
    private ActivityBroker activityBroker;
    
    
    /**
     * The site Grid activity broker
     */
    private GridActivityBroker gridActivityBroker;

    
    /**
     * TODO: not yet commented
     */
    private Executor executor;

    
    /**
     * The name (as an arbitrary string) of this site.
     */
    private String name;

    
    /**
     * The scheduler, manages the resource's occupation.
     */
    private Scheduler scheduler;

    
    /**
     * The site information 
     * @see mx.cicese.dcc.teikoku.information.broker.SiteInformation
     */
    private SiteInformation siteInformation;

    
    /**
     * The site unique identifier 
     */
    private UUID uuid;

    
    /**
     * The site local submission system
     */
    private LocalSubmissionComponent localSubmissionComponent;

    
    /**
     * The resource broker
     */
    private ResourceBroker resourceBroker;

    
    /**
	 * The information broker
	 * 
	 * @see mx.cicese.dcc.teikoku.information.InformationBroker
	 * @category Information Management
	 */
    private InformationBroker informationBroker;
    
    
    /**
     * The site energy manager
     */
    private EnergyBroker energyManager;
	
			
	/**
	 * Indicates the components that have been loaded
	 */
	private Map<String, Boolean>components;
	
	
	/**
	 * Copy constructor
	 * 
	 * Note: Not all variables have been initiated, only those that are
	 * relevant to the scheduling process.
	 * 
	 * @param another
	 */
	public ComputeSite(ComputeSite another) {
		this.activityBroker = null;
		this.gridActivityBroker = null;
		this.executor = null;
		this.name = another.name;
		this.scheduler = another.scheduler.clone();
		this.siteInformation = ((ComputeSiteInformation)another.siteInformation).clone();
		this.uuid = another.uuid;
		this.localSubmissionComponent = null;
		this.resourceBroker = null;
		this.informationBroker = null;
	}
	
	
	/**
	 * Creates a clone copy of this object
	 */
	public ComputeSite clone() {
		return new ComputeSite(this);
	}
	
	
	/**
     * Creates a new instance of this class, using the given parameters.
     *
     * @param name
     * @throws InstantiationException
     */
    public ComputeSite(String name)
            throws InstantiationException {
        /*
         * set UUID (generate random) and name of this site
         */
        this.uuid = UUID.randomUUID();
        this.name = name;

		this.components = new Hashtable<String,Boolean>();
		
        this.localSubmissionComponent = new LocalSubmissionComponent(this);
        localSubmissionComponent.initialize();
       
        /*
         * initialize site information
         */
        this.siteInformation = new ComputeSiteInformation(this);

        
        /*
         * load decision maker, scheduler and strategy
         */
        this.loadInformationBroker();
        this.loadEnergyManager();
        this.loadActivityBroker();
        this.loadResourceBroker();
        this.loadSchedulerAndStrategy();
        
        /*
         * TODO: just for testing, make this configurable
        */
        this.executor = new SimulationAdapter(this);
        try {
			new ComputeSiteEventHandler(this);
		} catch (InvalidAnnotationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    

    /**
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getExecutor()
      */
    public Executor getExecutor() {
        return this.executor;
    }
    

    /**
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getName()
      */
    public String getName() {
        return this.name;
    }

    
    /**
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getScheduler()
      */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    
    /**
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getSiteInformation()
      * @category Information system
      */
    public SiteInformation getSiteInformation() {
        return this.siteInformation;
    }
    
    
    /**
	 * Sets the site information broker associated to this site
	 * 
	 * @see SiteInformationBrokerOld
	 */
    public void setSiteInformation(SiteInformation information) {
    	this.siteInformation = (ComputeSiteInformation) information;

	}

    
    // -------------------------------------------------------------------------
    // Implementation/Overrides for de.irf.it.rmg.sim.kuiga.EventIncidenceListener
    // -------------------------------------------------------------------------

    /**
      * (non-Javadoc)
      *
      * @see de.irf.it.rmg.core.teikoku.site.Site#getUUID()
      */

    public UUID getUUID() {
        return this.uuid;
    }

    
    // -------------------------------------------------------------------------
    // Implementation/Overrides for
    // de.irf.it.rmg.core.teikoku.kernel.events.TeikokuEventConsumer
    // -------------------------------------------------------------------------

    /**
      * (non-Javadoc)
      *
      * @see java.lang.Object#toString()
      */

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", this.getName()).toString();
    }

   

    public static void configureMetric(Map<String, LinkedList> parameters, Metrics m) {
//		Check manualPermanent-parameter
        LinkedList<Boolean> tempManual = parameters.get("manual");
        if (tempManual != null) {
            ((AbstractMetric) m).setManualPermanent(tempManual.getFirst());
        }
    }

    // -------------------------------------------------------------------------
    // Implementation/Overrides for java.lang.Object
    // -------------------------------------------------------------------------

    /**
     * Gets the activity broker
     */
    public ActivityBroker getActivityBroker() {
        return this.activityBroker;
    }
    
    
    /**
     * Gets the Grid activity broker.
     * 
     * The Grid activity broker delegates jobs to computational sites
     */
    public GridActivityBroker getGridActivityBroker() {
		return this.gridActivityBroker;
	}

    
    /**
     * Returns the site public information
     * 
     * @return a hash map with status information
     * @category Information system
     */
    public Map<String, Object> getPublicInformation() {
        return activityBroker.getPublicInformation();
    }
    
    
    /**
     * Provides a mechanism to update site information in terms of
     * the number of running jobs in the computational site.
     * 
     * Other state information is updated each time events occur.
     * 
     * @see de.irf.it.rmg.core.teikoku.site.ComputeSiteEventHandler
     * @category Information system 
     */
	public void updateSiteInformation() {
		((ComputeSiteInformation)siteInformation).runningJobs = 
		scheduler.getSchedule().getScheduledJobs().size();	
	}

   
	/**
	 * Gets the local submission component.
	 * The local submission component is used by users to send jobs.
	 * 
	 * @return LocalSubmissionComponent
	 */
	public LocalSubmissionComponent getLocalSubmissionComponent() {
        return this.localSubmissionComponent;
    }
	
	
	/**
	 * Gets the resource broker
	 * 
	 * @return ResourceBroker
	 */
    public ResourceBroker getResourceBroker() {
        return this.resourceBroker;
    }

    
   /**
	 * Gets the site information broker
	 * 
	 * @return an InformationBroker
	 *  
	 * @category Information system
	 */
    public InformationBroker getInformationBroker() {
		return this.informationBroker;
	}
    
   
  
    
    /**
	 * Returns true if the site contains a Grid information system
	 * 
	 * @return The GridInformationBroker
	 * 
	 * @see SiteInformationBrokerOld
	 * 
	 * @category Information system
	 */
	public boolean hasGridActivityBroker() {
		return (this.gridActivityBroker != null)? true : false;
	}
	
	
	/**
	 * Sets the scheduler
	 */
	public void setScheduler(Scheduler s) {
		this.scheduler = s;
	}
	
	
	/**
	 * Gets this site available components
	 */
	public Map<String,Boolean> getComponents() {
		return this.components;
	}
	
	public EnergyBroker getEnergyManager() {
		return this.energyManager;
	}
	
	
	 /**
     * The compute site can assume one of the two following roles:
     * - A compute site, processes jobs assigned by a grid activity broker 
     * - A grid activity broker, delegates jobs to sites
     * 
     * @throws InstantiationException
     */
    private void loadActivityBroker()
            throws InstantiationException {
    	
    	// Loads the first activity broker (node level)
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(Site.CONFIGURATION_SECTION);

		String local_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_CLASS);
		
		String grid_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_GRIDACTIVITYBROKER_CLASS);
		
		if (local_key == null && grid_key == null) {
			String msg = "ActivityBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + this.getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_CLASS 
					+ " and "
					+ Constants.CONFIGURATION_SITES_GRIDACTIVITYBROKER_CLASS
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if

		String local_className = c.getString(local_key);
		if( local_key != null ) {
			ActivityBroker ab = ClassLoaderHelper.loadInterface(local_className, ActivityBroker.class);
			ab.setSite(this);
			ab.setRole(ActivityBrokerRole.COMPUTE_SITE);
			ab.initialize();
			this.activityBroker = ab;
		}
		
		String grid_className = c.getString(grid_key);
		if( grid_key != null ){
			GridActivityBroker	gb = ClassLoaderHelper.loadInterface(grid_className, GridActivityBroker.class);
			gb.setSite(this);
			gb.setRole(ActivityBrokerRole.GRID);
			gb.initialize();
			this.gridActivityBroker = gb;
		}
		
		String msg = "successfully loaded \"" + local_className + " and / or " + grid_className
				+ "\" as activity brokers of site \"" + this.getName() + "\"";
		
		log.debug(msg);
    }
    
    
	/**
	 * Loads the site information broker or grid information broker
	 * 
	 * @throws InstantiationException
	 */
	private void loadInformationBroker()
			throws InstantiationException {
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration().subset(Site.CONFIGURATION_SECTION);

		String local_key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
				Constants.CONFIGURATION_SITES_INFORMATIONBROKER_CLASS);
		
		if (local_key == null) {
			String msg = "ActivityBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + this.getName()
					+ "]" + Constants.CONFIGURATION_SITES_INFORMATIONBROKER_CLASS 
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if

		String local_className = c.getString(local_key);
		if( local_key != null ) {
			
			informationBroker = ClassLoaderHelper.loadInterface(local_className, InformationBroker.class);
			informationBroker.setSite(this);
			if( informationBroker instanceof SiteInformationBroker){

				informationBroker.setType(InfBrokerType.SITE);
				String refreshKey = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
						Constants.CONFIGURATION_SITES_INFORMATIONBROKER_REFRESHRATE);
				
				long refreshRate = c.getLong(refreshKey);
				
				((SiteInformationBroker)informationBroker).setRefreshRate(refreshRate);
				
			} else
				informationBroker.setType(InfBrokerType.GRID);
		}
		
		String msg = "successfully loaded \"" + local_className +
				 "\" as the information broker of site \"" + this.getName() + "\"";
		
		log.debug(msg);
	}
	
	
	/**
     * Loads the scheduler and a strategy based on the configuration.
     *
     * @throws InstantiationException
     */
    private void loadSchedulerAndStrategy()
            throws InstantiationException {
    	
    	StringTokenizer p = new StringTokenizer(this.getName(),":");
		String siteName = p.nextToken();
		
        Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
                .subset(Site.CONFIGURATION_SECTION);

        String key = ConfigurationHelper.retrieveRelevantKey(c, siteName,
                Constants.CONFIGURATION_SITES_SCHEDULER_CLASS);

        if (key == null) {
            String msg = "scheduler entry ("
                    + Site.CONFIGURATION_SECTION + "[" + siteName
                    + "]" + Constants.CONFIGURATION_SITES_SCHEDULER_CLASS
                    + ") not found in configuration";
            throw new InstantiationException(msg);
        } // if

        String className = c.getString(key);
        Scheduler s = ClassLoaderHelper.loadInterface(className, Scheduler.class);
        s.setSite(this);
        
        try {
            s.initialize();
        }
        catch (InitializationException e) {
            throw new InstantiationException(e.getMessage());
        }
        
        this.scheduler = s;

        String msg = "successfully loaded \"" + className
                + "\" as scheduler of site \"" + siteName + "\"";
        log.debug(msg);
    }

    
    // -------------------------------------------------------------------------
    // Implementation/Overrides for de.irf.it.rmg.core.teikoku.site.Site
    // -------------------------------------------------------------------------

    /**
     * Loads the strategy this scheduler uses based on the configuration.
     *
     * @throws InstantiationException
     */
    private void loadResourceBroker()
            throws InstantiationException {
        Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
                .subset(Site.CONFIGURATION_SECTION);

        String key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
                Constants.CONFIGURATION_SITES_RESOURCEBROKER_CLASS);

        if (key == null) {
            /*String msg = "ResourceBroker entry ("
                    + Site.CONFIGURATION_SECTION + "[" + this.getName()
                    + "]" + Constants.CONFIGURATION_SITES_RESOURCEBROKER_CLASS
                    + ") not found in configuration";
            throw new InstantiationException(msg);*/
        	System.out.println("WARNING: No Rersourcebroker set");
        } else{

            String className = c.getString(key);
            ResourceBroker rb = ClassLoaderHelper.loadInterface(className,
                    ResourceBroker.class);
            rb.setSite(this);
            rb.initialize();
            this.resourceBroker = rb;
            String msg = "successfully loaded \"" + className
                    + "\" as activity broker of site \"" + this.getName() + "\"";
            log.debug(msg);
        }
    }
    
    
    private void loadEnergyManager() 
    		throws InstantiationException {
    	Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
    			.subset(Site.CONFIGURATION_SECTION);
    	
    	String key = ConfigurationHelper.retrieveRelevantKey(c, this.getName(),
                   Constants.CONFIGURATION_ENERGY_COMPONENT);
    	
    	String value = c.getString(key);
    	if(value.equals("ON") && !this.getName().equals("site0")) {
    		// The energy management component set ON for all machines except the grid machine
    		components.put(new String("EnergyManager"), true);
    		energyManager = new EnergyBroker(this);
    	} else {
    		components.put(new String("EnergyManager"), false);
    		energyManager = null;
    	}
    }
    
}
	