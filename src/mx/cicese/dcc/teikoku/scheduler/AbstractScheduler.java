package mx.cicese.dcc.teikoku.scheduler;

import mx.cicese.dcc.teikoku.scheduler.grid.strategy.composite.CompositeStgy;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.AllocQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.WorkflowCompletedEvent;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


abstract public class AbstractScheduler implements Scheduler{
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final protected static Log log = LogFactory.getLog(AbstractScheduler.class);
	
	/**
	 * TODO: not yet commented
	 */
	protected boolean initialized;
	
	/**
	 * Holds the site this scheduler belongs to.
	 * 
	 */
	private static Site site;
	
	/**
	 * The independent job queue
	 */
	protected static SortedQueue<AllocationEntry> indpJobQueue;
	
	/**
	 * The independent job queue
	 */
	protected static Map<UUID,Job> compJobQueue;

	/**
	 * The composite job scheduling strategy
	 */
	protected CompositeStgy compStrategy;
	
	/**
	 * The composite independent job scheduling strategy
	 */
	protected CompositeStgy indpStrategy;

	/**
	 * Allocation plan, UUID is the ID of the composite job
	 */
	protected static Map<UUID,JobControlBlock> planRegistry;
	
	/**
	 * Determines if one scheduler event producer has been registered
	 * Only one event producer can be registered. The Abstract scheduler
	 * operates as a multiplexor when it receives the events, forwarding them
	 * to the proper instance of the scheduler.
	 */
	private static boolean isRegistered; 
	
	
	/**
	 * Class constructor
	 */
	public AbstractScheduler() {
		AbstractScheduler.indpJobQueue = new SortedQueueBinarySearchImpl<AllocationEntry>();
		//AbstractScheduler.indpJobQueue.setKeepOrdered(true);
		AbstractScheduler.compJobQueue = new HashMap<UUID,Job>();
		AbstractScheduler.planRegistry = new HashMap<UUID,JobControlBlock>();
		
		this.initialized = false;
		AbstractScheduler.isRegistered = false;
	}//End CompositeAbstractScheduler
	
	/*
	 * Creates events
	 */
	protected void createEvents(Job job, State state ) {
		Event event = null;
		Instant time = Clock.instance().now();
		switch (state.ordinal()) {
			/*
			 * Create job queued events after jobs have been released
			 */
		
			case 0:
				/* INSTRUMENTATION: can be inserted here */
				job.getProvenance().addEpisode(site);
				event = new AllocQueuedEvent(time, job, this.getSite());
				event.getTags().put(this.getSite().getUUID().toString(), this.getSite());
				break;
			case 7:
				job.setDuration(new Period(((CompositeJob)job).getReleaseTime(), time));
				((CompositeJob)job).setcompletionTime(time);
				event = new WorkflowCompletedEvent(time, job, this.getSite());
				break;
		}//End switch
		
		Kernel.getInstance().dispatch(event);
	}//End createEvents
	
	
	/**
	 * Indicates if the scheduler is initialized or not
	 * 
	 * @return 	the initialization flag
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Initializes the grid scheduler by loading the 
	 * scheduling strategies used to manage independent 
	 * and composite jobs.
	 * 
	 */
	public void initialize() throws InitializationException {
		/* TODO: Overridden by child implementations */
	}//end initialize

	
	
	protected void loadLocalCompStrategy() throws InitializationException {
		
		/* Falta cargar la estrategia de calendarizacion de tareas independientes */
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(ComputeSite.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, this.site.getName(),
		Constants.CONFIGURATION_SITES_SCHEDULER_COMPOSITE_STRATEGY_CLASS);

		if (key == null) {
			String msg = "local strategy entry ("
			+ ComputeSite.CONFIGURATION_SECTION
			+ "["
			+ this.site.getName()
			+ "]"
			+ Constants.CONFIGURATION_SITES_SCHEDULER_COMPOSITE_STRATEGY_CLASS
			+ ") not found in configuration";
			throw new InitializationException(msg);
		} // if

		String className = c.getString(key);
		try {
			this.compStrategy = ClassLoaderHelper.loadInterface(className,CompositeStgy.class);
			((CompositeStgy)this.compStrategy).bind(site, className);

		} // try
		catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch

		String msg = "successfully loaded \"" + className
		+ "\" as strategy for the scheduler of site \""
		+ this.site.getName() + "\"";
		log.debug(msg);
	}// End  loadLocalStrategy
	
	
	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	public Site getSite() {
		return this.site;
	}

	/**
	 * TODO: not yet commented
	 *
	 */
	public void setSite(Site site) {
		this.site = site;
	}
	
	/**
	 * Overridden by concrete class 
	 * manageNewJob implementations.
	 */
	abstract public void manageNewJob(Job job);
	
	/**
	 * Overridden by concrete class 
	 * manageNewJob implementations.
	 */
	public void reSchedule(Job job, Instant i) {}
	
	/**
	 * Given a UUID of an activity broker, retrieves the broker
	 * 
	 * @param destSiteId	the activity broker UUID
	 * @return				the instance of the activity broker
	 */
	protected Site getDestinationSite(UUID destSiteId) {
		Site destSite = null;
		
		/* When errors are introduced the site might be down, 
		 * an Exception or come kind of warning must be generated
		 */
		for( Iterator<ActivityBroker> it = site.getGridActivityBroker().getKnownActivityBrokers().iterator(); it.hasNext();){
			ActivityBroker ab = (ActivityBroker) it.next();
			if( destSiteId == ab.getSite().getUUID()) {
				destSite = ab.getSite();
				break;
			} // End if
		} //End for
		
		return destSite;
	}// End getDestinationSite
	

	@Override
	public void putNextJob(Job j) {
		// TODO Auto-generated method stub
		
	}
	
}//End AbstractScheduler
