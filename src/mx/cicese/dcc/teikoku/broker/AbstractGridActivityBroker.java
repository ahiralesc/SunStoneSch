/**
 * 
 */
package mx.cicese.dcc.teikoku.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.scheduler.Scheduler;
import mx.cicese.dcc.teikoku.scheduler.CompJobScheduler;
import mx.cicese.dcc.teikoku.scheduler.IndpJobScheduler;
import mx.cicese.dcc.teikoku.scheduler.Dispatcher;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedOnForeignSiteEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import org.apache.commons.configuration.Configuration;


/**
 * @author ahirales
 *
 */
public abstract class AbstractGridActivityBroker implements GridActivityBroker{
	/**
	 * Holds a reference to the site the broker is a part of. 
	 */
	private Site site;
	
	/**
	 * Holds a list of known brokers
	 */
	private List<ActivityBroker> knownActivityBroker=new ArrayList<ActivityBroker>();
	
	/**
	 * This Map holds the public Information of the site, for default its only the number of
	 * given ressources
	 */
	private Map<String, Object> publicInformation = new HashMap<String, Object>();

	/**
	 * Holds a List of valid Sessions, which can be used to accept remote Jobs
	 */
	private List<ActivityDelegationSession> activeSessions=new ArrayList<ActivityDelegationSession>();
	
	/**
	 * This is the standard-sessionlifetime of the broker, which will be used, when a new session should be generated
	 * At default it is -1, what represent an eternal lifetime
	 */
	private long sessionLifetime=-1;
	
	/**
	 * Differentiates the role of a site:
	 * - If set to true, the site role is grid broker.
	 * - If set to false, the site role is a compute or data site
	 * 
	 * The role is determined when this site is associated to a activity broker (compute or data site) 
	 * or a grid activity broker (grid broker).
	 */
	private ActivityBrokerRole role;
	
	/**
	 * The composite job scheduler
	 */
	Scheduler compJobSch;
	
	/**
	 * The independent job scheduler
	 */
	Scheduler inpJobSch;
	
	/**
	 * The allocation component
	 */
	Dispatcher allocComp;
	
	
	/**
	 * Class constructor
	 *
	 */
	public AbstractGridActivityBroker() {} //End AbstractGridActivityBroker
	
	
	/**
	 * Offers jobs to this broker. The jobs must be contained whitin 
	 * a session container.
	 * 
	 * @param DelegationSession session
	 */
	public void offer(ActivityDelegationSession session) throws OfferingVetoException{
		
		if (session.getCreator() instanceof LocalSubmissionComponent)
		{
			for (Job job:session.getJoblist()){
				// Set this job released site
				job.getLifecycle().addEpisode(State.RELEASED);
				job.setReleasedSite(site);
				job.getProvenance().addEpisode(getSite());
				job.getReleaseTime(); //Used to initialize the release time variable in Job
				
				if (!job.getJobType().equals(JobType.INDEPENDENT)) 
					this.compJobSch.manageNewJob(job);
				else 
					this.inpJobSch.manageNewJob(job);
			} //End for
		} // End if
	} //End offer
	
		
	/**
	 * Accepts a job completion event at a foreing site.
	 * 
	 * @param event		the job completion event 
	 */
	public void hadleJobCompletionOnForeignSite(Event event){
		Job completedJob = ((JobCompletedOnForeignSiteEvent)event).getCompletedJob();
		Instant completionTime = ((JobCompletedOnForeignSiteEvent)event).getTimestamp();
				
		if(!completedJob.getJobType().equals(JobType.INDEPENDENT)){
			this.compJobSch.reSchedule(completedJob, completionTime);
		}//End if
		// this.logStateInformation();
	} // acceptJobCompletionEvent
	
	
	/**
	 * Retreives a list of known activity brokers
	 * 
	 * @return 	the list of known activity broker (List<ActivityBroker>)
	 */
	public List<ActivityBroker> getKnownActivityBrokers(){
		if (knownActivityBroker.size()==0){
			initializeKnownActivityBroker();
		}
		return knownActivityBroker;
	} // getKnownActivityBrokers
	
	
	/**
	 * Must be implemented by subclass
	 */
	public abstract void initializeKnownActivityBroker();
	
	
	/**
	 * Method for adding knownActivityBroker
	 * 
	 * @param ab	an activity broker
	 */
	public void addKnownActivityBroker(ActivityBroker ab){
		this.knownActivityBroker.add(ab);
	} // End addKnownActivityBroker
	
	
	/**
	 * Gets this site public information
	 * 
	 * @return	the sites public information of type Map<String,Object> 
	 */
	public Map<String,Object> getPublicInformation(){
		return this.publicInformation;
	} // End getPublicInformation
	
	
	/**
	 * Gets this site active sessions 
	 * 
	 * @return 	a list of active sessions of type List<DelegationSession>
	 */
	public List<ActivityDelegationSession> getActiveSessions(){
		//First clean the list of inactive sessions
		int index=0;
		while(index<activeSessions.size()){
			if (!activeSessions.get(index).isValid()){
				activeSessions.remove(index);
			} else
				index++;
		} //End while
		return activeSessions;
	} // End getActiveSessions
	
	
	/**
	 * Gets the active session life time associated to this
	 * activity broker
	 * 
	 */
	public long getSessionLifetime(){
		return sessionLifetime;
	} // End getSessionLifetime
	
	/**
	 * Returns the independent job scheduler
	 * 
	 * @return	the indipendent job scheduler
	 */
	public Scheduler getIndependentJobScheduler() {
		return this.inpJobSch;
	}
	
	/* Dummy methods they are not usefull for the now for the Grid broker */
	public void manageNewJob(Job j) {
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * Initialize ActivityBroker and all subPolicys
	 */
	public void initialize() throws InstantiationException{
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);
		
		//initialize sessionLifeTime
		String key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_SESSIONLIFETIME);
		if (key != null) {
			sessionLifetime=c.getLong(key);
		} // if
		
		//Initialize public Information here
		key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_PUBLIC_INFORMATION);
		if (key != null) {
			List<String> infos=c.getList(key);
			
			//Fill Public Information with Standardparts
			if (infos.contains("size")){
				publicInformation.put("size", this.getSite().getSiteInformation().getProvidedResources().size());
			}
		} // if	
		
		/* Initialize the schedulers */
		this.compJobSch = new CompJobScheduler();
		this.inpJobSch = new IndpJobScheduler();
		this.compJobSch.setSite(site);
		this.inpJobSch.setSite(site);
		/* Initialize allocation component */
		this.allocComp = new Dispatcher();
		this.allocComp.setSite(site);
		try {
			this.inpJobSch.initialize();
			this.compJobSch.initialize();
		} catch(InitializationException e) {
			e.printStackTrace();
		}
		
		
	}// End initialize
	
	
	/**
	 * Gets the activity broker role
	 * 
	 *  @return 	the activity broker role
	 */
	public ActivityBrokerRole getRole() {
		return this.role;
	}// End getRole
	
	
	/**
	 * Sets the role of the activity broker. 	 
	 *  
	 *  @param role		the activity broker role 
	 *  @see			ActivityBrokerRole
	 */
	public void setRole(ActivityBrokerRole role) {
		this.role = role;
	}
	
	
	/**
	 * Gets ths activity broker site
	 */
	public Site getSite(){
		return site;
	} // End getSite
	
	
	/**
	 * Sets the activity broker site
	 */
	public void setSite(Site site){
		this.site=site;
	} // End setSite
	
	
	/**
	 * Retrieves the allocation component
	 *
	 * @return 	the allocation component 
	 */
	public Dispatcher getDispatcher() {
		return this.allocComp;
	} // getAllocationComponent
	
	
}// End AbstractGridActivityBroker
