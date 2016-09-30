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
package de.irf.it.rmg.core.teikoku.scheduler;

import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.AbortionNotHandledException;
import de.irf.it.rmg.core.teikoku.exceptions.DequeuingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.Preselector;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.collections.InvertibleComparator;
import de.irf.it.rmg.core.util.collections.Ordering;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

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

abstract public class AbstractScheduler
		implements Scheduler, StrategyListener{

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(AbstractScheduler.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;

	/**
	 * Holds the site this scheduler belongs to.
	 * 
	 */
	private Site site;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private SortedQueue<Job> localJobQueue;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Ordering localQueueOrdering;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Schedule schedule;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Strategy strategy;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private InvertibleComparator<Job> localQueueComparator;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Preselector localJobPreselector;
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public AbstractScheduler(Scheduler another) {
		this.initialized = true;
		
		this.site = null;
		
		/** 
		 * Copy jobs in this scheduler
		 */
		this.localJobQueue = new SortedQueueBinarySearchImpl<Job>();
		this.localJobQueue.addAll(another.getQueue());
		
		this.localQueueOrdering = null; 
		
		this.schedule = another.getSchedule().clone(); 
		
		this.strategy = another.getStrategy().clone();
		
		this.localQueueComparator = null;
		
		this.localJobPreselector = null;
			
	}

	
	public Scheduler clone(){return null;}

	

	/**
	 * TODO: not yet commented
	 * 
	 * @throws InstantiationException
	 */
	public AbstractScheduler()
			throws InstantiationException {
		/*
		 * default to non-initialized status
		 */
		this.initialized = false;

		/*
		 * instantiate queue and schedule data structures
		 */
		this.localQueueComparator = null;
		this.localJobQueue = new SortedQueueBinarySearchImpl<Job>();
		this.schedule = new Schedule(this);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final protected Strategy getLocalStrategy() {
		return this.strategy;
	}

	/**
	 * Setter method for the "strategy" field of this type.
	 * 
	 * @param strategy
	 *            Sets the contents of "this.strategy" to the given value.
	 */
	final protected void setLocalStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * Getter method that returns a copy of the local strategy
	 * 
	 * @return	a copy of the local strategy
	 */
	final public Strategy getStrategy() {
		return this.strategy;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param job
	 */
	protected void createEvents(Job job, State state) {
		Event event = null;
		switch (state.ordinal()) {
		/*
		 * Create job queued events aver jobs have been released
		 */
		case 1:
			Instant time = Clock.instance().now();
			event = new JobQueuedEvent(time, job,this.getSite());
			event.getTags().put(job.getReleasedSite().getUUID().toString(), job.getReleasedSite());
			break;
			/*
			 * Create job started event after  
			 */
		case 3:
			/*
			 * create start event
			 */
			Instant startTime = job.getDuration().getAdvent();
			event = new JobStartedEvent(startTime, job,this.getSite());
			break;
		} // switch
		Kernel.getInstance().dispatch(event);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	protected Preselector getLocalJobPreselector() {
		return this.localJobPreselector;
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

	/**
	 * TODO: not yet commented
	 * 
	 * @param initialized
	 */
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * This function is called when the EnergyManager is ON.
	 * It analyzes the state of the allocated cores and computes the following statistics:
	 * - The ratio of reallocated cores
	 * - The job fragmentation
	 * 
	 * @param jobToSchedule
	 * @param rb
	 */
	protected void setAllocationProperties (Job jobToSchedule, ResourceBundle rb) {
		// The following variables are used to estimate the average time length between
		// the time instance a core is freed and the time instance it is reallocated
		long sumLength =0, numReallocatedCores = 0;
		
		float numIdleCores = 0, fragmentation = 0;
		float numAllocatedCores = rb.size();
		Resource predecessorCore = null;
						
		for(Resource core : rb ){
			
			if(core.getState().equals(mx.cicese.dcc.teikoku.energy.State.idle))
				numIdleCores++;
						
			if( predecessorCore == null )
				predecessorCore = core;
			else { 
				fragmentation += core.getOrdinal() - predecessorCore.getOrdinal();
				predecessorCore = core;
			}
			
			//long delay = this.site.getEnergyManager().getHierarchy().getEnergySettings().coreTimeOutDelay;
			long delay = 0; 
			
			Instant past = core.getInstance();
			if( past != null) {
				if(core.getState().equals(mx.cicese.dcc.teikoku.energy.State.idle) ){
					long length = (Clock.instance().now().timestamp() - past.timestamp());
					if (length <= delay) {
						sumLength += length;
						numReallocatedCores++;
					}
				}
			}// End if 
		}
		
		float ratioIdleCores = (numIdleCores/numAllocatedCores);
		float jobFragmentation = (numAllocatedCores > 1)? (fragmentation / (numAllocatedCores - 1)) : 1;
		float avgReallocLength = (numReallocatedCores > 0)? (sumLength/numReallocatedCores) : 0;//-1;
		
		
		jobToSchedule.setRatioAllocatedCores(ratioIdleCores);
		jobToSchedule.setFragmentation(jobFragmentation);
		jobToSchedule.setIdleCoreReallocLength(avgReallocLength);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#initialize()
	 */
	public void initialize()
			throws InitializationException {
		if (this.site == null) {
			String msg = "initialization failed: no site set";
			log.error(msg);
			throw new InitializationException(msg);
		} // if
		
		// load the local strategy
		this.loadLocalStrategy();
		// load the local job queue comparator
		if (this.loadLocalQueueComparator()) {
			// set ascending or decending ordering for the comparator
			this.loadLocalQueueComparatorOrdering();
			this.localQueueComparator.setOrdering(this.localQueueOrdering);
			// Sort the local job queue according the given comparator
			this.getQueue().setOrdering(this.localQueueComparator);
			this.getQueue().setKeepOrdered(true);
			this.getQueue().reorder();
		} // if
		this.strategy.register(this);
		this.strategy.setSite(this.site);
		this.initialized = true;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#initialize()
	 */
	public void initialize2()
			throws InitializationException {
		if (this.site == null) {
			String msg = "initialization failed: no site set";
			log.error(msg);
			throw new InitializationException(msg);
		} // if
		
		// load the local strategy
		this.loadLocalStrategy();
		// load the local job queue comparator
		if (this.loadLocalQueueComparator()) {
			// set ascending or decending ordering for the comparator
			this.loadLocalQueueComparatorOrdering();
			this.localQueueComparator.setOrdering(this.localQueueOrdering);
			// Sort the local job queue according the given comparator
			this.getQueue().setOrdering(this.localQueueComparator);
			this.getQueue().setKeepOrdered(true);
			this.getQueue().reorder();
		} // if
		//this.strategy.register(this);
		this.strategy.setSite(this.site);
		this.initialized = true;
	}

	private boolean loadLocalQueueComparatorOrdering()
			throws InitializationException {
		boolean loadSuccess;
		StringTokenizer p = new StringTokenizer(this.site.getName(),":");
		String siteName = p.nextToken();

		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(ComputeSite.CONFIGURATION_SECTION);

		String key = ConfigurationHelper
				.retrieveRelevantKey(
						c,
						siteName,
						Constants.CONFIGURATION_SITES_SCHEDULER_LOCALQUEUECOMPARATORORDERING_CLASS);

		if (key == null) {
			String msg = "local queue comparator ordering entry ("
					+ ComputeSite.CONFIGURATION_SECTION
					+ "["
					+ this.site.getName()
					+ "]"
					+ Constants.CONFIGURATION_SITES_SCHEDULER_LOCALQUEUECOMPARATORORDERING_CLASS
					+ ") not found in configuration: using default ordering ascending";
			log.debug(msg);
			this.localQueueOrdering = null;
			loadSuccess = false;
		} // if
		else {
			String order = c.getString(key);

			if (order.equals("ascending")) {
				this.localQueueOrdering = Ordering.ASCENDING;
				loadSuccess = true;
			} // if
			else if (order.equals("descending")) {
				this.localQueueOrdering = Ordering.DESCENDING;
				loadSuccess = true;
			} // else if
			else {
				String msg = "Invalid entry " + order
						+ " for queue comparator ordering.";
				throw new InitializationException(msg);
			} // else
		}// else
		return loadSuccess;
	}

	private boolean loadLocalQueueComparator()
			throws InitializationException {
		boolean loadSucess = false;

		StringTokenizer p = new StringTokenizer(this.site.getName(),":");
		String siteName = p.nextToken();
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(ComputeSite.CONFIGURATION_SECTION);

		String key = ConfigurationHelper
				.retrieveRelevantKey(
						c,
						siteName,
						Constants.CONFIGURATION_SITES_SCHEDULER_LOCALQUEUECOMPARATOR_CLASS);

		if (key == null) {
			String msg = "local queue comparator entry ("
					+ ComputeSite.CONFIGURATION_SECTION
					+ "["
					+ this.site.getName()
					+ "]"
					+ Constants.CONFIGURATION_SITES_SCHEDULER_LOCALQUEUECOMPARATOR_CLASS
					+ ") not found in configuration: using default natural order comparator";
			log.debug(msg);
			this.localQueueComparator = null;
			loadSucess = false;
		} // if
		else {
			String className = c.getString(key);
			try {
				this.localQueueComparator = ClassLoaderHelper.loadInterface(
						className, InvertibleComparator.class);
			} // try
			catch (InstantiationException e) {
				throw new InitializationException(e);
			} // catch

			String msg = "successfully loaded \"" + className
					+ "\" as comparator for the queue of site \""
					+ this.site.getName() + "\"";
			log.debug(msg);
			loadSucess = true;
		} // else

		return loadSucess;
	}

	private void loadLocalStrategy()
			throws InitializationException {
    	
		StringTokenizer p = new StringTokenizer(this.site.getName(),":");
		String siteName = p.nextToken();
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(ComputeSite.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, siteName,
				Constants.CONFIGURATION_SITES_SCHEDULER_LOCALSTRATEGY_CLASS);

		if (key == null) {
			String msg = "local strategy entry ("
					+ ComputeSite.CONFIGURATION_SECTION
					+ "["
					+ siteName
					+ "]"
					+ Constants.CONFIGURATION_SITES_SCHEDULER_LOCALSTRATEGY_CLASS
					+ ") not found in configuration";
			throw new InitializationException(msg);
		} // if

		String className = c.getString(key);
		try {
			this.setLocalStrategy(ClassLoaderHelper.loadInterface(className,
					Strategy.class));
		} // try
		catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch

		String msg = "successfully loaded \"" + className
				+ "\" as strategy for the scheduler of site \""
				+ this.site.getName() + "\"";
		log.debug(msg);

	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.Scheduler
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#getSite()
	 */
	final public Site getSite() {
		return this.site;
	}

	/**
	 * Setter method for the "site" field of this type.
	 * 
	 * @param site
	 *            Sets the contens of "this.site" to the given value.
	 */
	public void setSite(Site site) {
		this.site = site;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#getQueue()
	 */
	final public SortedQueue<Job> getQueue() {
		return this.localJobQueue;
	}

	/**
	 * Setter method for the "queue" field of this type.
	 * 
	 * @param queue
	 *            Sets the contens of "this.queue" to the given value.
	 */
	final protected void setQueue(SortedQueue<Job> queue) {
		this.localJobQueue = queue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#getSchedule()
	 */
	final public Schedule getSchedule() {
		return this.schedule;
	}

	/**
	 * Setter method for the "schedule" field of this type.
	 * 
	 * @param schedule
	 *            Sets the contens of "this.schedule" to the given value.
	 */
	final public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.workload.WorkloadSink
	// -------------------------------------------------------------------------

	public void putNextJob(Job j) {
		// TODO: change here to new Job implementation and Lifecycle handling
		this.getQueue().offer(j);
		j.getLifecycle().addEpisode(State.QUEUED);
		createEvents(j, State.QUEUED);
	}
	
	/**
	 * This method is for dequeuing Jobs for example by an activityBroker, 
	 * which declares scheduled jobs for distribution
	 * 
	 * In this case it must be overwritten and a transaction-save behaviour on the queue must be implemented
	 * @param job
	 * @throws Exception
	 */
	public void dequeue(Object Caller,Job job) throws DequeuingVetoException{
		if (Caller!=this){
			throw new DequeuingVetoException("Dequeuing of Job is denied");
		} else{
			//remove Job from Queue and list of managed Jobs
			if (!(getSite().getActivityBroker().getListOfManagedJobs().remove(job))){
				//The Job is not on the site anymore and so could not be scheduled
				throw new DequeuingVetoException("The Scheduler is not able to dequeue an own job");
			} else{
				getQueue().remove(job);
			}
		}
	}

	public void handleAbortion(Job job) throws AbortionNotHandledException {
		// TODO Auto-generated method stub
		throw new AbortionNotHandledException("Abortion not handled");
	}
	
	public void handleCompletion(Job job){
		// TODO Auto-generated method stub
		
	}
	
}
