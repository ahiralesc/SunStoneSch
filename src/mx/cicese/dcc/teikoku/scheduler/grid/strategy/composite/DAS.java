package mx.cicese.dcc.teikoku.scheduler.grid.strategy.composite;

import java.util.Iterator;
import java.util.List;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.scheduler.grid.strategy.Strategy;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.priority.Priority;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import org.apache.commons.configuration.Configuration;


/**
 *	Dynamic Scheduling Mechanism (Strategy)
 *	- Since policy and Scheduling Mechanism is separated.
 *	- Policies don't change, mechanism do changes.  
 * 
 * @author Adan Hirales Carbajal
 * @author Andrei Tchernykh
 */

public class DAS extends CompositeStgy {
	/**
	 * The DSS labeling strategy
	 */
	private Priority<SWFJob,Precedence> priority;
	
	
	/**
	 * The labeling strategy name
	 */
	private String labelingStrategyName;
	
	
	/**
	 * DSS Allocation strategy
	 */
	private Strategy strategy;
		
	
	public void initialize(){
		labelingStrategyName = new String();
		try {
			loadLocalRigidStrategy();
		} catch (InitializationException ex) {
			//Logger.getLogger(DSS.class.getName()).log(Level.SEVERE, null, ex);
		} //try
	} // initialize
	
	
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
		long r = -1;
		
		// Get independent jobs and schedule them.
		/* Create the list of jobs to schedule*/
		Hypergraph<SWFJob,Precedence> g = ((CompositeJob)job).getStructure();
		List<SWFJob> indJobs = CompositeJobUtils.getIndependentJobs(g);
				
		/* Should use priority to iterate over jobs. Have not figured out its use */
		for(Iterator<SWFJob> it = indJobs.iterator(); it.hasNext();) {
			SWFJob m = (SWFJob)it.next();
			/* Update m's release time to max AFT of predecessor nodes (if any) */
			if(g.inDegree(m) != 0) 
				r = DateHelper.convertToSeconds(CompositeJobUtils.getMaximumStartTime(g, m, jcb));
			else 
				r = DateHelper.convertToSeconds(Clock.instance().now().timestamp());
			m.setSubmitTime(r);
			// The moment the job is freed, then the member job state must be set to RELEASED
			m.getLifecycle().addEpisode(State.RELEASED);
			
			//Set the job type as independent, then reset to its original type.
			JobType originalType = m.getJobType();
			m.setJobType(JobType.INDEPENDENT);
			// The job is then scheduled.
			AllocationEntry entry = strategy.schedule(m,null);
			m.setJobType(originalType);
			
			//Add the job priority
			long rank = jcb.getRanking(this.labelingStrategyName, m);
			m.setPriority(rank);
						
			jcb.addEntry(entry, entry.getDestination());
		}//End for
		
		return null;
	} // End schedule

		
	public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb) {
		priority.compute(g);
		jcb.setRanking(this.labelingStrategyName,priority.getRanking());
	} // getRanking

	
    @SuppressWarnings("unchecked")
	protected void loadLocalRigidStrategy() throws InitializationException {
    	Site site = super.gridInfBroker.getSite();
    	String siteName = site.getName();
    	    	
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(ComputeSite.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, siteName,
		Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_ALLOCATION_CLASS);

		if (key == null) {
			String msg = "local strategy entry (" + ComputeSite.CONFIGURATION_SECTION
				+ "[" + siteName	+ "]"
				+ Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_ALLOCATION_CLASS
				+ ") not found in configuration";
				throw new InitializationException(msg);
		} // if

		String className = c.getString(key);
		try {
			this.strategy = ClassLoaderHelper.loadInterface(className,Strategy.class);
			strategy.bind(site, className);
		} catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch


		key = ConfigurationHelper.retrieveRelevantKey(c, siteName,
				Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_PRIORITY_CLASS);

		if (key == null) {
				String msg = "local strategy entry (" + ComputeSite.CONFIGURATION_SECTION
				+ "[" + siteName
				+ "]" + Constants.CONFIGURATION_SITES_SCHEDULER_RIGID_PRIORITY_CLASS
				+ ") not found in configuration";
				throw new InitializationException(msg);
		} // if

		className = c.getString(key);
		try {
			this.priority = ClassLoaderHelper.loadInterface(className,Priority.class);
			this.labelingStrategyName = this.priority.getName();
			
		} catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch
		
	} // End  loadLocalStrategy
	
}
