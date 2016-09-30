package de.irf.it.rmg.core.teikoku.kernel.events;

import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;

public class WaitTimeTimeoutEvent extends Event{

	/**
	 *	The set of cores in idle state
	 * 
	 */
	private ResourceBundle cores;
	
	
	private Instant completionTime;

	
	private Site site;
	
	
	/**
	 * Class constructor 
	 * 
	 * @param timestamp, the time instance the event must occur
	 * @param timedOutJob, the job that held the resources
	 * @param location, the site where the time-out event will occur
	 */
	public WaitTimeTimeoutEvent(final Instant timestamp, final ResourceBundle idleCoreSet,
	final Site location) {
		super(timestamp, location.getUUID().toString(), location);
		this.cores = idleCoreSet;
		this.site = location;
	}

	
	
	/**
	 * Gets the cores associated to this event
	 * 
	 * @return, the set of cores
	 */
	final public ResourceBundle getCores() {
		return this.cores;
	}
	
	/**
	 * Sets the cores associated to this event
	 * 
	 */
	final public void setCores(ResourceBundle idleCoreSet) {
		this.cores = idleCoreSet;
	}
	
	
	final public Instant getCompletionTime() {
		return this.completionTime;
	}
	
	
	public void setCompletionTime(Instant completionTime){
		this.completionTime = completionTime;
	}

	
	/**
	 * Gets the ID of the event
	 */
	@Override
	protected int getOrdinal() {
		return EventType.WAITING_TIME_TIMEDOUT.ordinal();
	}
	
	
	public final Site getSite() {
		return site;
	}
	
}
