package mx.cicese.dcc.teikoku.scheduler;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.Strategy;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.Dispatcher;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobState;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.kernel.events.AllocQueuedEvent;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.sim.kuiga.Event;
import java.util.UUID;
import org.apache.commons.configuration.Configuration;


public class IndpJobScheduler extends AbstractScheduler {
	
	Strategy rigidStgy;
	
	/**
	 * Manages a new composite job.
	 * 
	 * @param job	the composite job
	 */
	@Override
	public void manageNewJob(Job job) {
		AllocationEntry entry = rigidStgy.schedule(job, null);
		createEvents(entry.getJob(),State.RELEASED);
		AbstractScheduler.indpJobQueue.add(entry);
	}
	
	
	public void onAllocQueueEvent(Event e) {
		
		Job job = ((AllocQueuedEvent)e).getQueuedJob();
		
		/* Take out ANY job from the queue, the event is not important,
		 * the number of queued events must sum the number of queued jobs.
		 * 
		 * The first element from the queue is extracted. The order of
		 * the jobs in the queue is defined by a queue ordering policy
		 */
		
		AllocationEntry entry = (AllocationEntry) AbstractScheduler.indpJobQueue.poll(); 
				
		/* in this first version no buffering is performed, each job is sent 
		 * as soon as it is ready. A better implementation would buffer jobs, 
		 * determine if more than one jobs is to be allocated to a site, and
		 * send a set of jobs to the destination site.
		 */
		
		/* Before allocation of the job, if the job to is a member job
		 * then its plan must be updated. Must remove the job from the allocatableJob
		 * map
		 */
		if( !job.getJobType().equals(JobType.INDEPENDENT) ) {
			UUID cJobId = ((SWFJob)job).getJobContainerId();
			JobControlBlock plan = AbstractScheduler.planRegistry.get(cJobId);
			plan.removeEntry(entry, entry.getDestination());
			
			/* Set the composite job state. One state must be executing at least */ 
			CompositeJob cJob = (CompositeJob) AbstractScheduler.compJobQueue.get(cJobId);
			cJob.setState(CompositeJobState.ACTIVE);
		}
				
		/* Add provenance information */
		job.setReleasedSite(super.getSite());
		job.getProvenance().addEpisode(super.getSite());
		
		//Site destSite = super.getDestinationSite(entry.getDestination());
		
		Site destSite = ((GridInformationBroker)this.getSite().getInformationBroker()).getKnownMachine(entry.getDestination());
		
		Dispatcher alloc = super.getSite().getGridActivityBroker().getDispatcher();
		alloc.dispatch(destSite, entry); 
	}// End onQueueEvent
	
	
	/**
	 * Initializes the grid scheduler by loading the 
	 * scheduling strategies used to manage independent 
	 * and composite jobs.
	 * 
	 */
	@Override
	public void initialize() throws InitializationException{
		//super.registerEventProducer(this);
		try {
			this.loadStrategy();
		}catch(InstantiationException e) {
			throw new InitializationException();
		}
	}//end initialize

	
	/**
	 * Loads the rigid job scheduling strategy.
	 * 
	 * @throws InstantiationException
	 */
	private void loadStrategy() throws InstantiationException {
		
		// Loads the first activity broker (node level)
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(Site.CONFIGURATION_SECTION);

		String local_key = ConfigurationHelper.retrieveRelevantKey(c, super.getSite().getName(),
				Constants.CONFIGURATION_SITES_GRID_STRATEGY_RIGID_CLASS);
		
		if (local_key == null) {
			String msg = "Rigid job scheduling strategy ("
					+ Site.CONFIGURATION_SECTION + "[" + super.getSite().getName()
					+ "]" + Constants.CONFIGURATION_SITES_GRID_STRATEGY_RIGID_CLASS 
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if

		String className = c.getString(local_key);
		if( local_key != null ) {
			rigidStgy = ClassLoaderHelper.loadInterface(className, Strategy.class);
			rigidStgy.bind(super.getSite(), className);
		} // End if
		
		String msg = "successfully loaded \"" + className;
		log.debug(msg);
	} //End loadStrategy
	
	
} // IndpJobScheduler
