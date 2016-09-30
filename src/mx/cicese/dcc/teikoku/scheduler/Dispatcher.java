package mx.cicese.dcc.teikoku.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.DelegationFabric;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import mx.cicese.dcc.teikoku.information.broker.InstrHelper;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;

/* 
 * The allocation component in this implementation is very basic 
 * in its functionality. Two types of Allocation policies can by
 * applied:
 * <ul>
 * 		<li> Assign, when a job is assign the destination broker must process it
 * 		<li> Offered, when a job is offered the destination broker can turn it down or
 * 			migrate the job elsewhere.
 * </ul>  
 * 
 * I/O data setup at destination sites, by means of requests of I/O related services.
 * Storage of checkpoints, in case the jobs fails at the target site.
 * Re-submission of the job, etc.
 */
public class Dispatcher implements ActivityDelegationSource{
	/**
	 * Field for the standardLifeTime of session
	 */
	private long sessionLifetime=-1;
	
	/*
	 * The site the allocation component belongs to
	 */
	Site site;
	
	/*
	 * Class constructor
	 */
	public Dispatcher() {
		this.site = null;
	}
	
	public void setSite(Site site) {
		this.site = site;
	}
	
	
	public void dispatch(Site destSite, AllocationEntry entry) {
		
		/* Set provenance information */
		Job job = entry.getJob();
		job.setReleasedSite(site);
		job.getProvenance().addEpisode(site);
		// job.getLifecycle().addEpisode(State.RELEASED); ??? No se si deba incluirlo
		
		/* create the delegation session */
		List<Job> joblist=new ArrayList<Job>();
		joblist.add(job);
		ActivityDelegationSession session = DelegationFabric.generateActivityDelegationSession(joblist, this); 
		
		/* INSTRUMENTATION: START */
		InstrHelper instrSystem = new InstrHelper();
		instrSystem.instrCSIReleasedEv(destSite);		
		/* INSTRUMENTATION: END */
		
			
		try {
				destSite.getActivityBroker().offer(session);
		} catch (OfferingVetoException e) {
			e.printStackTrace();
		} //End try 
	} // End allocate
	
	
	/* Garbage functions */
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.teikoku.grid.delegation.SessionCreator#getSessionLifetime()
	 */
	public long getSessionLifetime() {
		return sessionLifetime;
	}

	
	/* Garbage methods */
	@Override
	public void delegate(Job job) throws AcceptanceVetoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getPublicInformation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

