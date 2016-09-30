package de.irf.it.rmg.core.teikoku.kernel.events;

import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.teikoku.site.Site;

public class WorkflowCompletedEvent extends Event {
	/**
	 * TODO: not yet commented
	 * 
	 */
	private Job completedJob;
	
	/* TODO: not yet commented
	 *
	 * @param ep
	 * @param timestamp
	 * @param queuedJob
	 */
	public WorkflowCompletedEvent(final Instant timestamp, Job completedJob, final Site location) {
		super(timestamp,location.getUUID().toString(),location);
		this.completedJob = completedJob;
	}
	
	/**
	 * Getter method 
	 * 
	 * @return Returns the current contents of "this.completedJob".
	 */
	final public Job getCompletedJob() {
		return this.completedJob;
	}
	
	/**
	 * Setter method
	 * 
	 * @param queuedJob
	 *            Sets the contents of "this.queuedJob" to the given value.
	 */
	final public void setCompletedJob(Job completedJob) {
		this.completedJob = completedJob;
	}
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.sim.kuiga.Event
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.kernel.events.Event#getType()
	 */
	@Override
	final public int getOrdinal() {
		return EventType.WORKFLOW_COMPLETED.ordinal();
	}

	
	
}
