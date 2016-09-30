package mx.cicese.dcc.teikoku.scheduler;

import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.Initializable;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSink;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.time.Instant;

public interface Scheduler extends Initializable, WorkloadSink{
	
	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	public Site getSite();

	/**
	 * TODO: not yet commented
	 *
	 */
	public void setSite(Site site);
	
	/**
	 * Manages a new job
	 */
	public void manageNewJob(Job job);
	
	/**
	 * Reschedules jobs. A worflow scheduling strategy would search for
	 * jobs with broken precedence constraints and schedules them. 
	 */
	public void reSchedule(Job job, Instant completionTime);
	
}
