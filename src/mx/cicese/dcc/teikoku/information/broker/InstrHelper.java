package mx.cicese.dcc.teikoku.information.broker;

import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobAbortedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobQueuedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;

public class InstrHelper {
	
	/**
	 * The longest executing job
	 */
	long p_max;
	
	
	public InstrHelper(){
		p_max = Long.MIN_VALUE;
	}
	
	
	public void instrCSIAbortEv( Site site, JobAbortedEvent event, SWFJob job ){
		
		/* Get job runtime properties */
		long p = TimeHelper.toSeconds(job.getDuration().distance());
		long c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		long r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
		
		ComputeSiteInformation csi = (ComputeSiteInformation) site.getSiteInformation();
		State state = job.getLifecycle().getLastCondition();
		
		/* Transition: Released to Aborted state*/
		if(	state.equals(State.RELEASED) ) {
			csi.preLRMSWaitingJobs--;
		}
		
		/* Transition: Queued to Aborted state */
		if( state.equals(State.QUEUED) ||  state.equals(State.SCHEDULED) ) {
			csi.waitingJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localWaitingJobs--;
		}
		
		/* Transition: Started to Aborted state */
		if( state.equals(State.STARTED) ){
			csi.runningJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localRunningJobs--;
		}
		
		/* Transition: Suspended to Aborted state */
		if( state.equals(State.SUSPENDED)) {
			csi.suspendedJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localSuspendedJobs--;
		}
				
		/* The job aborted, but it consumed processing capabilities */
		if(state.equals(State.SUSPENDED) || state.equals(State.STARTED)) {
			csi.sumProcTimes += p;
			csi.sumWaitingTimes += (c - p - r); 
			csi.sumFlowTimes += (c - r);
			csi.totalNumExecJobs++;
		}
	}
	
	
	
	public void instrCSIReleasedEv(Site site){
		ComputeSiteInformation csi = (ComputeSiteInformation)site.getSiteInformation();
		csi.preLRMSWaitingJobs++;
		//((ComputeSiteInformation)site.getSiteInformation()).preLRMSWaitingJobs++;
	}

	
	public void instrCSIQueuedEv( Site site, JobQueuedEvent event, SWFJob job ){
		ComputeSiteInformation csi = (ComputeSiteInformation) site.getSiteInformation();
		State state = job.getLifecycle().getLastCondition();
		
		/* Transition: Released to Queued state */
		if(	state.equals(State.QUEUED) ) {
			csi.preLRMSWaitingJobs--;
			csi.waitingJobs++;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localWaitingJobs++;
			
		}
	}
	
	
	public void instrCSIStartedEv(Site site, JobStartedEvent event, SWFJob job) {
		ComputeSiteInformation csi = (ComputeSiteInformation) site.getSiteInformation();
		State state = job.getLifecycle().getLastCondition();
		
		csi.runningJobs++;
		if (job.getReleasedSite().getUUID().equals(site.getUUID()))
			csi.localRunningJobs++;
				
		/* Transition: Scheduled or Queued to Started state */
		if(	state.equals(State.QUEUED) || state.equals(State.SCHEDULED) ) {
			csi.waitingJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localWaitingJobs--;
		}
		
		/* Transition: Resumed or Suspended to Started state */
		if(	state.equals(State.SUSPENDED) || state.equals(State.RESUMED) ) {
			csi.suspendedJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localSuspendedJobs--;
		}
	}
	
	
	public void instrCSICompletedEv(Site site, JobCompletedEvent event, SWFJob job) {
		/* Get job runtime properties */
		long p = TimeHelper.toSeconds(job.getDuration().distance());
		long c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		long r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
				
		ComputeSiteInformation csi = (ComputeSiteInformation) site.getSiteInformation();
		State state = job.getLifecycle().getLastCondition();
		
		/* Record the longest executing job allocated to this site */
		if( p > p_max ) {
			p_max = p;
			csi.p_max = p;
		}
			
		
		/* Transition: Resumed or Started to Completed state */
		if(	state.equals(State.STARTED) ) {
			csi.runningJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localRunningJobs--;
		}
		
		/* Transition: Suspended (Resumed) to Completed state */
		if(	state.equals(State.SUSPENDED) || state.equals(State.RESUMED)) {
			csi.suspendedJobs--;
			if (job.getReleasedSite().getUUID().equals(site.getUUID()))
				csi.localSuspendedJobs--;
		}
		
		
		/* The job aborted, but it consumed processing capabilities */
		if(state.equals(State.SUSPENDED) || state.equals(State.RESUMED) || state.equals(State.STARTED)) {
			csi.sumProcTimes += p;
			csi.sumWaitingTimes += (c - p - r); 
			csi.sumFlowTimes += (c - r);
			csi.totalNumExecJobs++;
		}
		
	}
	

}
