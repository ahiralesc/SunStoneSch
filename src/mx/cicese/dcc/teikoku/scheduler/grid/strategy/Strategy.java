/**
 * 
 */
package mx.cicese.dcc.teikoku.scheduler.grid.strategy;

import de.irf.it.rmg.core.teikoku.job.Job;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.site.Site;


public interface Strategy {
	
	public AllocationEntry schedule(Job job, JobControlBlock jcb);
	
	public void bind(Site site, String name);
}
