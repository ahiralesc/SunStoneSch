package mx.cicese.dcc.teikoku.scheduler.plan;

import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Set;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
 *	Allocation plan is a data structure that has the purpose of keeping knowledge
 *	of composite jobs scheduling information. It allows filing information such as:
 *	<p>
 *	<ul>
 *		<li> The Absolute Finishing Times (AFT)of member jobs.
 *		<li> The priority ranking estimates.
 *		<li> A rank order list of member jobs.
 *		<li> The composite job is the owner of a given plan.
 *		<li> And allocation information.
 *		<li> Estimates the As Late as Possible Ranking of a composite job.
 *	</ul>
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */


public class JobControlBlock{
	/**
	 * The critical path
	 */
	List<SWFJob>  cp;
	
	/**
	 * The critical path processor
	 */
	UUID cpProc;
	
	/**
	 * The allocation map
	 */
	private Map<UUID,List<AllocationEntry>> allocationSet;
	
	/**
	 * The absolute finishing times of member jobs 
	 */
	private Map<SWFJob,Number> finishedJobs;
	
	/**
	 * An ordered list. The ordering is dependent on the ranking strategy
	 */
	private List<SWFJob> ordRanking;
	
	/**
	 * Ranking information
	 */
	private HashMap<String, HashMap<SWFJob, Number>> rank;
	
		
	/**
	 * The composite job owner of this job
	 */
	private Job compositeJob;
	
	/**
	 * Buffered jobs to schedule in future time
	 */
	private HashMap<SWFJob,Number> bufferedJobs;
	private Hashtable<String,Hashtable<SWFJob,Number>> estimates;
	private Hashtable<String,UUID> assigment;
	
	/**
	 * Indicates if the JCB has been initialized
	 */
	private boolean initialized;
	
	/**
	 * Class constructor
	 *
	 */
	public JobControlBlock() {
		this.allocationSet = new HashMap<UUID,List<AllocationEntry>>();
		this.rank = new HashMap<String,HashMap<SWFJob,Number>>();
		this.compositeJob = null;
		this.finishedJobs = new HashMap<SWFJob,Number>();
		this.ordRanking = new LinkedList<SWFJob>();
		this.cp = null;
		this.cpProc = null;
		this.bufferedJobs = new HashMap<SWFJob,Number>();
		this.estimates = null;
		this.assigment = null;
		this.initialized = false;
	}// End AllocationPlan
	
	/**
	 * Adds an entry pending allocation. 
	 * 
	 * @param entry			the allocation entry (AllocationEntry)
	 * @param siteId		the site id (UUID)
	 */
	public void addEntry(AllocationEntry entry, UUID siteId){
		
		List<AllocationEntry> allocationList = allocationSet.get(siteId);
		if(allocationList == null){
			List<AllocationEntry> newAllocList = new LinkedList<AllocationEntry>();
			newAllocList.add(0, entry);
			allocationSet.put(siteId,newAllocList);
		} else 
			allocationList.add(0, entry);
	}// End addEntry
	
	/**
	 * Removes an allocation entry.
	 * 
	 * @param entry			the allocation entry (AllocationEntry).
	 * @param siteId		the site id (UUID)
	 */
	public void removeEntry(AllocationEntry entry, UUID siteId) {
		List<AllocationEntry> allocationList = allocationSet.get(siteId);
		if(allocationList != null) {
			if(allocationList.size() != 0) 
				allocationList.remove(entry);
			if(allocationList.size() == 0) 
				allocationSet.remove(siteId);
		}//End if
	}// End removeEntry
	
	/**
	 * Retrieves an allocation list for a given site.
	 * 
	 * @param siteId		the site id (UUID)
	 * @return				an allocation List containing allocation entries
	 */
	public List<AllocationEntry> getEntries(UUID siteId) {
		return this.allocationSet.get(siteId);
	}// End getEntries
	
	/**
	 * Retrieves a set of sites where pending jobs are to be allocated.
	 * 
	 * @return		a set of UUID's of sites
	 */
	public Set<UUID> getAllocatableSet() {
		return this.allocationSet.keySet();
	}// End getAllocatableSet
	
	/**
	 * Clears the allocation set (this.allocationSet) map.
	 *
	 */
	public void removeAllEntries() {
		if(this.allocationSet.size() != 0)
			this.allocationSet.clear();
	}// End removeAllEntries
	
	/**
	 * 	Sets the composite owner of this plan
	 * 
	 * @param job
	 */
	public void setJob(Job job) {
		this.compositeJob = job;
	}// End setJob
	
	
	/**
	 * 	Gets the composite owner of this plan
	 * 
	 * @param job
	 */
	public Job getJob() {
		return this.compositeJob;
	}// End setJob
	
	
	/**
	 * Adds a member job AFT to the finished job map.
	 * 
	 * @param job		the	member job that completed execution
	 * @param aft		the AFT of the completed member job
	 */
	public void addAFT( SWFJob job, Number aft ) {
		this.finishedJobs.put(job, aft);
	}// End add
	
	/**
	 * Gets the AFT of a given job
	 * 
	 * @param job		a member job
	 * @return			the AFT of the member job. If the job does not
	 * 					exist. The AFT is 0
	 */
	public long getAFT(SWFJob job) {
		long aftVal = 0;
		try {
			Number aft = this.finishedJobs.get(job);
			aftVal = ( aft == null)? 0 : aft.longValue();
		}catch(NullPointerException e) { }
		return aftVal;
	}// End getAFT
	
	/**
	 * Sets an ordered list of member jobs 
	 * 
	 * @param ordRanking	an list ordered by some strategy (by ranking)
	 */
	public void setOrdRanking(List<SWFJob> ordRanking) {
		this.ordRanking.addAll(ordRanking);
	}// End setOrdRanking
	
	/**
	 * Retrieves the ordered list of member jobs.
	 * 
	 * @return		an ordered list of member jobs
	 */
	public List<SWFJob> getOrdRanking() {
		return this.ordRanking;
	}// End getOrdRanking
	
	/**
	 * Gets the ranking of a given job.
	 * 
	 * @param job		a member job
	 * @return			the ranking of the member job
	 */
	public long getRanking(String rankName, SWFJob job) {
		return this.rank.get(rankName).get(job).longValue();
	}// End getRanking
	
	
	/**
	 * Gets this composite job ranking.
	 * 
	 * @return		the ranking set. 
	 */
	public Map<SWFJob,Number> getRanking(String rankName) {
		return this.rank.get(rankName);
	}// End getRanking
	
	/**
	 * Stores previouslu computed rankings
	 * 
	 * @param ranking	a map of precomputed rankings
	 */
	public void setRanking(String rankName, Map<SWFJob,Number> ranking) {
		if(this.rank.get(rankName) == null)
			this.rank.put(rankName, new HashMap<SWFJob,Number>());
			
		if(ranking != null){
			this.rank.get(rankName).clear();
            this.rank.get(rankName).putAll(ranking);
		}
	}// End setRanking
	
	
	/**
	 * Sets the critical path
	 * 
	 * @param cp
	 */
	public void setCP(List<SWFJob> cp) {
		this.cp = cp;
	}
	
	
	/**
	 * Gets the critical path
	 * @return
	 */
	public List<SWFJob> getCP() {
		return this.cp;
	}
	
	
	/**
	 * Sets the critical path processor
	 * 
	 * @param 	the critical path processor
	 */
	public void setCPProc(UUID cpProc) {
		this.cpProc = cpProc;
	}
	
	
	/**
	 * Gets the critical path processor
	 * @return
	 */
	public UUID getCPProc() {
		return this.cpProc;
	}
	
	
	/**
	 * Gets jobs that have an allocation, but have not been released
	 * 
	 * @return bufferedJobs
	 */
	public Map<SWFJob,Number> getBufferedJobs() {
		return this.bufferedJobs;
	}
	
	
	/**
	 * Stores a job that has an allocation, but it is not released 
	 * 
	 */
	public void bufferJob(SWFJob j, long r) {
		this.bufferedJobs.put(j, r);
	}
	
	
	/**
	 * Removes a job from the bufferJobs data structure
	 * 
	 * @param j
	 */
	public void removeBufferedJob(SWFJob j) {
		if( this.bufferedJobs != null)
			this.bufferedJobs.remove(j);
	}
	
	/**
	 * Stores jobs AEFT estimates, used by PCH
	 * 
	 * @param estimates
	 * 
	 * @see PCH
	 */
	@SuppressWarnings("unchecked")
	public void setEstimates(Hashtable<String,Hashtable<SWFJob,Number>> estimates){
		this.estimates = (Hashtable<String,Hashtable<SWFJob,Number>>) estimates.clone();
	}

	/**
	 * Stores jobs site assignments, used by PCH
	 * 
	 * @param estimates
	 * 
	 * @see PCH
	 */
	@SuppressWarnings("unchecked")
	public void setAssigments(Hashtable<String,UUID> assigment){
		this.assigment =  (Hashtable<String,UUID>)assigment.clone();
	}
	
	/**
	 * Gets job AEFT times
	 * 
	 * @return estimates
	 */
	public Hashtable<String,Hashtable<SWFJob,Number>> getEstimates() {
		return this.estimates;
	}
	
	/**
	 * Gets job assignments, used by PCH
	 * 
	 * @return assignments
	 */
	public Hashtable<String,UUID> getAssigment() {
		return this.assigment;
	}
	
	public void setInitialize() {
		this.initialized = true;
	}
	
	public void unSetInitialize() {
		this.initialized = false;
	}
	
	public boolean getState() {
		return this.initialized;
	}
	
	/**
	 * clear all mappings
	 * 
	 */
	public void clear() {
		this.removeAllEntries();
		if(this.finishedJobs != null)
			this.finishedJobs.clear();
		if(this.ordRanking != null)
			this.ordRanking.clear();
		this.compositeJob = null;
		if(this.cp != null)
			this.cp.clear();
		this.cpProc = null;
		if(this.bufferedJobs != null) 
			this.bufferedJobs.clear();
		if(this.assigment != null)
			this.assigment.clear();
		if(this.estimates != null)
			this.estimates.clear();
		
		for(String rankName : this.rank.keySet())
			if(this.rank.get(rankName) != null)
				this.rank.get(rankName).clear();
	}// End setRanking

}// end AllocationPlan