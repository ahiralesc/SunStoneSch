package mx.cicese.dcc.teikoku.scheduler;

import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobState;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.util.time.Instant;
import java.util.Iterator;
import java.util.UUID;


public class CompJobScheduler extends AbstractScheduler {

	/**
	 * Manages a new composite job.
	 * 
	 * @param job	the composite job
	 */
	@Override
	public void manageNewJob(Job job) {
		compJobQueue.put(job.getUUID(), job);
		
		/* Set the composite job state */
		((CompositeJob)job).setState(CompositeJobState.INACTIVE);
		
		/* The plan files all scheduling decisions */
		JobControlBlock jcb = new JobControlBlock();
		
		/* Fist try to apply some ranking strategy */
		compStrategy.setRanking(((CompositeJob)job).getStructure(), jcb);
		jcb.setJob(job);
				
		/* Call the strategy and let it make decisions, 
		 * this process updates the jobs plan */
		//compStrategy.setSite(super.getSite());
		compStrategy.schedule(job, jcb);
		
		/* Make a backup of the plan */
		planRegistry.put(job.getUUID(), jcb);
		
		/* Add each job to the independent job queue,
		 * the queue must be ordered by ranking */
		for(Iterator<UUID> it = jcb.getAllocatableSet().iterator(); it.hasNext();) {
			UUID s = it.next();
			/* Gets jobs to be allocated to this site */
			for(AllocationEntry e : jcb.getEntries(s)) {
				/* Create a job queue event and put the 
				 * entry in the independent job queue */
				createEvents(e.getJob(),State.RELEASED);
				AbstractScheduler.indpJobQueue.add(e);
			}// End for	
		}// End for 		
	}// End manageNewJob
	
	
	/**
	 * Reschedules and updates an existing allocation plan. As long as independent
	 * jobs exist.
	 * 
	 * @param compJob			the completed member job
	 * @param completionTime	the AFT the member job completed execution
	 */
	public void reSchedule(Job compJob, Instant completionTime){
		UUID cId = ((SWFJob)compJob).getJobContainerId();
		CompositeJob cJob = (CompositeJob) AbstractScheduler.compJobQueue.get(cId);
		JobControlBlock jcb = (JobControlBlock) AbstractScheduler.planRegistry.get(cId);
		
		CompositeJobUtils.updatePrecedenceConstrains(cJob.getStructure(), (SWFJob)compJob);
		
		/* Update this composite job scheduling plan */
		jcb.addAFT((SWFJob)compJob, completionTime.timestamp());
		
		/* 
		 * Check if all member jobs have completed, if so. Transition to 
		 * CompositeJobState.INACTIVE state and create an event
		 */
		int compJobs = CompositeJobUtils.getNumberOfCompletedJobs(cJob.getStructure());
		int numJobs = cJob.getStructure().getVertexCount();
		if( compJobs == numJobs) {
			/* Free up memory: clean the plan and registry */
			AbstractScheduler.compJobQueue.remove(cJob);
			jcb.clear();
			AbstractScheduler.planRegistry.remove(jcb);
			createEvents(cJob,State.COMPLETED);
		}
		
		/* Schedule remaining jobs */
		compStrategy.schedule(cJob, jcb);
		
		/* Make a backup of the new plan */
		planRegistry.put(cId, jcb);
		
		/* Add each job to the independent job queue,
		 * the queue must be ordered by ranking */
		for(Iterator<UUID> it = jcb.getAllocatableSet().iterator(); it.hasNext();) {
			UUID s = it.next();
			/* Gets jobs to be allocated to this site */
			for(AllocationEntry e : jcb.getEntries(s)) {
				if( !AbstractScheduler.indpJobQueue.contains(e) ){
					/* Create a job queue event and put the 
					 * entry in the independent job queue */
					createEvents(e.getJob(),State.RELEASED);
					AbstractScheduler.indpJobQueue.add(e);
				}//end if
			}// End for	
		}// End for 		
		
	}//End reSchedule
	
	
	/**
	 * Initializes the grid scheduler by loading the 
	 * scheduling strategies used to manage independent 
	 * and composite jobs.
	 * 
	 */
	@Override
	public void initialize() throws InitializationException {
		if (getSite()== null) {
			String msg = "initialization failed: no site set";
			log.error(msg);
			throw new InitializationException(msg);
		} // if

		// load the local strategy
		loadLocalCompStrategy();
		//compStrategy.setSite(getSite());
		compStrategy.initialize();
		initialized = true;
		//super.registerEventProducer(this);
	}//end initialize

		
}// End manageNewJob



