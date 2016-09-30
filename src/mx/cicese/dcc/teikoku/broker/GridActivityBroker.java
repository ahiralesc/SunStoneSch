package mx.cicese.dcc.teikoku.broker;

import java.util.List;
import mx.cicese.dcc.teikoku.scheduler.Dispatcher;
import mx.cicese.dcc.teikoku.scheduler.Scheduler;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.sim.kuiga.Event;

/**
* @author <a href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public interface GridActivityBroker extends ActivityDelegationSource,ActivityDelegationTarget{

	/**
	 * This method is used to make an offer of Jobs to the Broker
	 * @param DelegationSession session
	 * @throws OfferingVetoException 
	 */
	public void offer(ActivityDelegationSession session) throws OfferingVetoException;

	/**
	 * Standard-getter
	 * @return Site
	 */
	public Site getSite();
	
	/**
	 * Standard-setter
	 * @param site
	 */
	public void setSite(Site site);
	
	/**
	 * returns the list of all known ActivityBroker
	 * @return List<ActivityBroker>
	 */
	public List<ActivityBroker> getKnownActivityBrokers();

	/**
	 * This method is for Initialization of the activityBroker to initialize the following
	 * important components:
	 * - sessionLifetime
	 * - public Information available on this activityBroker
	 * @throws InstantiationException
	 */
	public void initialize() throws InstantiationException;
	
	
	/**
	 * This is used to take over the responsibility for a remote Job
	 * @param j
	 */
	public void manageNewJob(Job j);
	
		
	/**
	 * returns the list of all activeSessions
	 * @return List<DelegationSession>
	 */
	public List<ActivityDelegationSession> getActiveSessions();
	
		
	Scheduler getIndependentJobScheduler();	
	
	
	/**
	 * Gets the role of this activity broker
	 * 
	 * @return ActivityBrokerRole the role the broker
	 * 
	 * @see ActivityBrokerRole
	 */
	public ActivityBrokerRole getRole();
	
	/**
	 * Sets the role of this activity broker, roles can be:
	 * -	Grid activity broker (GRID)
	 * -	Compute site activity broker  (COMPUTE_SITE)
	 * -	Other possibilities are: Data site activity broker (DATA_SITE)
	 * 
	 * @param ActivityBrokerRole the role this activity broker 	
	 * 
	 * @see ActivityBrokerRole
	 */
	public void setRole(ActivityBrokerRole role);
	
	/**
	 * Retrieves the allocation component
	 *
	 * @return 	the allocation component 
	 */
	public Dispatcher getDispatcher();	
	
	/*
	 * 
	 */
	public void hadleJobCompletionOnForeignSite(Event event);

}
