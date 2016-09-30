package mx.cicese.dcc.teikoku.scheduler.plan;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import java.util.UUID;

public class AllocationEntry {
	/**
	 * The job to map onto a site
	 */
	private SWFJob job;
	
	/**
	 * The id of the site where the job is to be allocated
	 */
	private UUID destination;
	
	/**
	 * The priority of the job (ranking)
	 */
	private long priority;
	
	/**
	 * Class constructor
	 *  
	 * @param job			the job to allocate
	 * @param destination	the allocation site
	 * @param priority		the priority of the job
	 */
	public AllocationEntry(SWFJob job, UUID destination, long priority){
		this.job = job;
		this.destination = destination;
		this.priority = priority;
	}

	/**
	 * Gets the job to allocate
	 * 
	 * @return	a job
	 */
	public SWFJob getJob() {
		return this.job;
	}
	
	/**
	 * Gets the destination site UUID
	 * 
	 * @return	the uuid of the destination site
	 */
	public UUID getDestination() {
		return this.destination;
	}
	
	/**
	 * Gets the job priority
	 * 
	 * @return	the priority
	 */
	public long getPriority() {
		return this.priority;
	}
}
