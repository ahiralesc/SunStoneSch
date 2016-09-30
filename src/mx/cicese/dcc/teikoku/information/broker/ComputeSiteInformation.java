/*
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2013 by the
 *   CICESE Research Center and  
 *   CETYS University, Mexico
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

package mx.cicese.dcc.teikoku.information.broker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.MetricsException;
import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.metrics.MetricsVault;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleSortedSetImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.math.AverageHelper;
import de.irf.it.rmg.core.util.time.Instant;

import mx.cicese.dcc.teikoku.energy.Type;

/**
 * Represents a single site, bundling a set of resources and managing their
 * occupation with a scheduler.
 * <p>
 * 
 * The SiteInformation interfaces provides methods to access ComputeSite state and runtime data.
 * <p>
 *   
 * This implementation is based on GLUE Specification V. 2.0. The GLUE specification is an information 
 * model for Grid entities described using the natural language and UML Class Diagrams.
 * <p>
 *  
 *  @author <a href="mailto:ahiralesc@gmail.com">Adan Hirales Carbajal</a>
 *         	(last edited by $Author$)
 * 	@version $Version$, $Date$
 * 
 *  @category Information system
 * 
 */


public class ComputeSiteInformation implements SiteInformation {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(ComputeSiteInformation.class);
	
	
	/**
	 * The machine clock rate. Since only one clock rate is provided for the all resources, the machine 
	 * model is said to be uniform. 
	 */
	private float clockRate;

	
	/**
	 * Time when the machine becomes unavailable
	 * 
	 * TODO: Not supported
	 */

	public Instant downtimeStart;

	
	/**
	 * Time when the machine becomes available
	 * 
	 */
	public Instant downtimeEnd;
	
	
	/**
	 * The number of running jobs submitted via a local (non-GRID) interface.
	 */
	public int localRunningJobs;
	
	
	/**
	 * The number of jobs, submitted via a local (non-Grid) interface, 
	 * which have started their execution, but are currently suspended 
	 * (e.g.,having been preempted by another job).
	 */
	public int localSuspendedJobs;
	
	
	/**
	 * The number of jobs which are currently waiting to start execution, 
	 * submitted via a local (non-Grid) interface. Usually these will 
	 * be queued in the underlying Computing Manager (i.e., a Local 
	 * Resource Management System or LRMS).
	 */
	public int localWaitingJobs;
	
	
	/**
	 * The maximum allowed number of jobs that are in the Grid layer waiting to be passed to
	 * the underlying computing manager (i.e.,LRMS) for this machine (or share if the machine is 
	 * partitioned).
	 */
	public int maxPreLRMSWaitingJobs;
	
	
	/**
	 * The maximum allowed number of jobs in the running state in this machine (or share if the machine is 
	 * partitioned).
	 */
	public int maxRunningJobs;
	
	
	/**
	 * The maximum allowed number of jobs in this machine (or share if the machine is 
	 * partitioned).
	 */
	public int maxTotalJobs;
	
	
	/**
	 * The maximum allowed number of jobs in the  machine (or share if the machine is 
	 * partitioned).
	 */
	public int maxWaitingJobs;
	
	
	/**
	 * The length of longest executing job. 
	 * A value of zero is returned if no jobs are executing.
	 * A value grater than zero is returned, if
	 * - One or more jobs are running are running but have not completed. The job with 
	 *   the longest running time among the running job set is selected 
	 */
	public long maxLengthExecJob;
	
	
	
	/**
	 * The longest processing time job detected in the machine execution environment
	 */
	public long p_max;
	
	
	/**
	 * The number of Grid jobs which are currently managed by the Grid software layer waiting to
	 * be passed to the underlying Computing Manager (LRMS), and hence are not yet candidates to start execution.
	 */
	public int preLRMSWaitingJobs;
	
		
	/**
	 * The site metrics
	 */
	private Set<Metrics> providedMetrics;

	
	/**
	 * The set of computing resources 
	 */
	private ResourceBundle providedResources;
	
	
	/**
	 * The number of jobs which are currently running in an Execution Environment,
	 * submitted via any type of interface (local and Grid).
	 */
	public int runningJobs;
	
	
	/**
	 * The site this ComputeSiteInformation instance is associated with
	 */
	Site site;
	
	
	/**
	 * The number of Grid jobs which are currently either staging files in before starting
	 * execution, or staging files out after finishing execution.
	 */
	public int stagingJobs;
	
	
	/** 
	 * The sum of all job processing times (p_j)
	 */
	public long sumProcTimes;
	
	
	/**
	 * The sum of all job flow times (c_j - r_j)
	 */
	public long sumFlowTimes;
	
	
	/**
	 * The sum of all job waiting times (c_j - r_j - p_j)
	 */
	public long sumWaitingTimes;
	
	
	
	/**
	 * The number of jobs, submitted via any type of interface (local and Grid), which have started
	 * their execution, but are currently suspended (e.g., having been preempted by another job).
	 */
	public int suspendedJobs;
	
	
	
	/**
	 * The total number of jobs in any state (the sum of RunningJobs, WaitingJobs, StagingJobs,
	 * SuspendedJobs and PreLRMSWaitingJobs). Note that this number includes the locally submitted jobs.
	 */
	public int totalJobs;
	
	
	/**
	 * The total number of jobs that have completed their execution. The amount considers both locally
	 * submitted jobs and those forward by the Grid scheduler.
	 */
	public int totalNumExecJobs;
	
	
	/**
	 * The number of jobs which are currently waiting to start execution, submitted via any
	 * type of interface (local and Grid). Usually these will be queued in the underlying
	 * Computing Manager (i.e., a Local Resource Management System or LRMS).
	 */
	public int waitingJobs;
	
	
	
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private int numberOfConferedResources;
	
    
	/**
     * TODO: not yet commented
     */
    private ResourceBundle lentResources;

    
    /**
     * TODO: not yet commented
     */
    private Map<Integer, Instant> mapLentResToTime = new HashMap<Integer, Instant>();
		
	
    /**
	 * The site energy consumption
	 * 
	 */
	private double energyConsumption;
	
	
	
	/**
	 * The number of components that are currently ON
	 * 
	 */
	private int[] onComponentFrequency;
	
	
	
	/**
	 * Used for creating the dummy site and schedule 
	 * 
	 * @param site
	 * @throws InstantiationException
	 */
	public ComputeSiteInformation(Site site, String dummy) throws InstantiationException {
		this.loadResources(site);
		this.site = site;
	}
	
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 */
	public ComputeSiteInformation(Site site) throws InstantiationException {
		this.loadResources(site);
		this.loadMetrics(site);
		this.loadClockRateSpeed(site);
		
		// Information system
		this.downtimeStart = null;
		this.downtimeEnd = null;
		this.localRunningJobs = 0;
		this.localSuspendedJobs = 0;
		this.localWaitingJobs = 0;
		this.maxPreLRMSWaitingJobs = 0;
		this.maxRunningJobs = 0;
		this.maxTotalJobs = 0;
		this.maxWaitingJobs = 0;
		this.p_max = 0;
		this.preLRMSWaitingJobs = 0;
		this.runningJobs = 0;
		this.stagingJobs = 0;
		this.sumProcTimes = 0;
		this.sumFlowTimes = 0;
		this.sumWaitingTimes = 0;
		this.suspendedJobs = 0;
		this.totalJobs = 0;
		this.totalNumExecJobs = 0;
		this.waitingJobs = 0;
		this.energyConsumption = 0;
		this.maxLengthExecJob = 0;
		this.onComponentFrequency = new int[Type.values().length+1];
		
		// Site
		this.site = site;
	}
	
	
	/**
	 * Copy constructor
	 */
	public ComputeSiteInformation(ComputeSiteInformation another) {
	
		// Information system
		this.downtimeStart =  another.downtimeStart;
		this.downtimeEnd = another.downtimeEnd;
		this.localRunningJobs = another.localRunningJobs;
		this.localSuspendedJobs = another.localSuspendedJobs;
		this.localWaitingJobs = another.localWaitingJobs;
		this.maxPreLRMSWaitingJobs = another.maxPreLRMSWaitingJobs;
		this.maxRunningJobs = another.maxRunningJobs;
		this.maxTotalJobs = another.maxTotalJobs;
		this.maxWaitingJobs = another.maxWaitingJobs;
		this.p_max = another.p_max;
		this.preLRMSWaitingJobs = another.preLRMSWaitingJobs;
		this.runningJobs = another.runningJobs;
		this.stagingJobs = another.stagingJobs;
		this.sumProcTimes = another.sumProcTimes;
		this.sumFlowTimes = another.sumFlowTimes;
		this.sumWaitingTimes = another.sumWaitingTimes;
		this.suspendedJobs = another.suspendedJobs;
		this.totalJobs = another.totalJobs;
		this.totalNumExecJobs = another.totalNumExecJobs;
		this.waitingJobs = another.waitingJobs;
			
		// Clone the resources
		this.providedResources = new ResourceBundleSortedSetImpl();
		for(Iterator<Resource> it=another.providedResources.iterator(); it.hasNext();)
			this.providedResources.add(it.next().clone());
			

		// Clone the number of conferred resources
		this.numberOfConferedResources = another.getNumberOfConferedResources();
		
		// Clone the length of resources
		this.lentResources = new ResourceBundleSortedSetImpl();
		for(Iterator<Resource> it=another.lentResources.iterator(); it.hasNext();)
			this.lentResources.add(it.next().clone());
		
		for(Integer k : another.mapLentResToTime.keySet()) 
			this.mapLentResToTime.put(k, another.mapLentResToTime.get(k));
	}

	
	/* Methods corresponding to the SiteInformation interface: BEGIN */
	
	/**
	 * Creates a clone copy of this object
	 * 
	 * @return a copy of this object
	 */
	public ComputeSiteInformation clone() {
		return new ComputeSiteInformation(this);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#addConferedResources()
	 */
	public void addConferedResources(int amount) {
	    numberOfConferedResources += amount;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#addLentResources()
	 */
	public void addLentResources(List<Resource> rl) {
	    for (Resource r : rl) {
	        this.lentResources.add(r);
	    }
	    this.mapLentResToTime.put(rl.get(0).getOrdinal(), Clock.instance().now());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getMapLentResToTime()
	 */
	public Map<Integer, Instant> getMapLentResToTime() {
		return this.mapLentResToTime;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getNumberOfAvailableResources()
	 */
	public int getNumberOfAvailableResources() {
		return this.providedResources.size();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getLentResources()
	 */
	public ResourceBundle getLentResources() {
	    return this.lentResources;
	
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getProvidedMetrics()
	 */
	public Set<Metrics> getProvidedMetrics() {
		return this.providedMetrics;
	} // End getProvidedMetrics


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getProvidedResources()
	 */
	public ResourceBundle getProvidedResources() {
		return this.providedResources;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getServingState()
	 */
	public boolean getServingState() {
		boolean servingState = true;
		Instant currentTime = Clock.instance().now();
	
		// No down time has been scheduled.
		if( this.downtimeStart == null)
			return (servingState=true);
		// The site has been scheduled for downtime
		if (currentTime.after(this.downtimeStart) && currentTime.before(this.downtimeEnd))
			servingState = false;
		return servingState;
	} // End getServingState


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#getTimeOfLoan()
	 */
	public Instant getTimeOfLoan(Resource r) {
	    return mapLentResToTime.get(r.getOrdinal());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.site.SiteInformation#removeConferedResources()
	 */
	public void removeConferedResources(int amount) {
	    numberOfConferedResources -= amount;
	}


	
	/* Methods corresponding to the SiteInformation interface: END */
	
	
	
	/**
     * TODO: not yet commented
     */
	public AverageHelper getAvgHelper() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Gets the site clock rate
	 * 
	 * @return machineClockRate
	 */
	public float getClockRate() {
		return this.clockRate;
	}

	
	/**
	 * Gets the number of conferred resources 
	
	 * @return the number of conferred resources
	 */
	public int getNumberOfConferedResources() {
		return this.numberOfConferedResources;
	}


	/**
	 * Loads the machine clock rate
	 */
	private void loadClockRateSpeed(Site site) throws InstantiationException {
		float siteClockRate = 0;
		float minClockRate = Float.MAX_VALUE;
		int numSites = 0;
		
		String siteName = site.getName();
		
		if( !siteName.equals("site0")) {
		// Get total number of sites
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);
		
		if( c.containsKey(Constants.CONFIGURATION_ID_COMPONENT)) {
			String[] sites = c.getStringArray(Constants.CONFIGURATION_ID_COMPONENT);
			numSites = sites.length - 1;
		}
	
		// Load machine information from all sites except site 0
		for(int i=1; i<=numSites; i++){
			String key = Site.CONFIGURATION_SECTION + ".site" + i;
			c = RuntimeEnvironment.getInstance().getConfiguration().subset(key);
	
			key = Constants.CONFIGURATION_SITES_CLOCKRATE;
			if (c.containsKey(key)) { 
					float clockRate = (new Float(c.getString(key)).floatValue());
					if( clockRate < minClockRate) 
						minClockRate = clockRate;
					if( siteName.equals("site"+i))
						siteClockRate = clockRate;
			}
		}
		
		// Set this site processing speed (this site speed/overall minimum site speed) 
		this.clockRate = (siteClockRate/minClockRate);
		}
	} // End loadResources

	
	/**
     * Loads the machine metrics
     */
    private void loadMetrics(Site site) {
    	StringTokenizer p = new StringTokenizer(site.getName(),":");
		String siteName = p.nextToken();
    	
    	/*
            * determine configuration keys and load metrics identifiers
            */
        Configuration c = RuntimeEnvironment.getInstance()
                .getConfiguration().subset(
                        Site.CONFIGURATION_SECTION);
        String[] identifiers = c
                .getStringArray(siteName
                        + "."
                        + Constants.CONFIGURATION_SITES_REGISTEREDMETRIC_IDENTIFIER);

        this.providedMetrics = new HashSet<Metrics>();
        /*
            * load and register metrics at event manager
            */
        for (String identifier : identifiers) {
            try {
                //Get Configuration-part for Metric
               // Configuration csub = RuntimeEnvironment.getInstance().getConfiguration().subset(  Metrics.CONFIGURATION_SECTION + "." + identifier);

                Metrics m = MetricsVault.getInstance().registerMetricAt(site,
                        identifier);
                this.providedMetrics.add(m);
            } // try
            catch (MetricsException e) {
                String msg = e.getMessage();
                log.warn(msg, e);
            } // catch
        } // for
    } // loadMetrics


	/**
	 * Loads the set of resources
	 */
	private void loadResources(Site site) throws InstantiationException {
		StringTokenizer p = new StringTokenizer(site.getName(),":");
		String siteName = p.nextToken();
		
		/*
		 * try to load named resources
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION + "." + siteName);

		String key = Constants.CONFIGURATION_SITES_LISTOFPROVIDEDRESOURCES;
		if (c.containsKey(key)) {
			try {
				String[] listOfProvidedResources = c.getStringArray(key);
				this.providedResources = new ResourceBundleSortedSetImpl(Resource
						.newGroupOfResources(listOfProvidedResources));
			} // catch
			catch (ConversionException e) {
				String msg = "creation of named resources failed for site \""
					+ siteName + "\", trying anonymous creation";
				log.warn(msg, e);
			} // catch
		} // if

		/*
		 * load anonymous (numbered) resources
		 */
		int numberOfProvidedResources = 0;
		numberOfProvidedResources = c.getInt(Constants.CONFIGURATION_SITES_NUMBEROFPROVIDEDRESOURCES);
		if (numberOfProvidedResources < 1) {
			String msg = "could not instantiate site \""
				+ siteName
				+ "\": specified "
				+ Constants.CONFIGURATION_SITES_NUMBEROFPROVIDEDRESOURCES
				+ " must be larger than 0";
			log.error(msg);
			throw new InstantiationException(msg);
		} // if
		else {
			this.providedResources = new ResourceBundleSortedSetImpl(Resource
					.newGroupOfAnonymousResources(numberOfProvidedResources));
		} // End if
		Resource[] empty = new Resource[0];
        this.lentResources = new ResourceBundleSortedSetImpl(empty);

			log.debug("resource creation for site \"" + site.getName()
				+ "\" successful with " + this.providedResources.size()	+ " resources");
	} // End loadResources
	
		
	/*
	 * Sets the component site
	 * 
	 * @param site 	the site
	 */
	public void setSite(Site site){
		this.site = site;
	}

	
	public void addComponent(Type type) {
		this.onComponentFrequency[type.ordinal()]++;
	}
	
	public void removeComponent(Type type) {
		this.onComponentFrequency[type.ordinal()]--;
	}
	
	public int getComponentCount(int index) {
		return this.onComponentFrequency[index];
	}
			
}// End ComputeSiteInformation



