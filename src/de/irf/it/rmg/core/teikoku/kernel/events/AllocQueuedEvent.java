package de.irf.it.rmg.core.teikoku.kernel.events;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.core.teikoku.site.Site;


public class AllocQueuedEvent extends Event{
	/**
	 * TODO: not yet commented
	 * 
	 */
	private Job queuedJob;

	/**
	 * TODO: not yet commented
	 *
	 * @param ep
	 * @param timestamp
	 * @param queuedJob
	 */
	public AllocQueuedEvent(final Instant timestamp, Job queuedJob, final Site location) {
		super(timestamp,location.getUUID().toString(),location);
		this.queuedJob = queuedJob;
	}

	/**
	 * Getter method for the "queuedJob" field of this type.
	 * 
	 * @return Returns the current contents of "this.queuedJob".
	 */
	final public Job getQueuedJob() {
		return this.queuedJob;
	}

	/**
	 * Setter method for the "queuedJob" field of this type.
	 * 
	 * @param queuedJob
	 *            Sets the contents of "this.queuedJob" to the given value.
	 */
	final public void setQueuedJob(Job queuedJob) {
		this.queuedJob = queuedJob;
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
		return EventType.ALLOC_QUEUED.ordinal();
	}

}
