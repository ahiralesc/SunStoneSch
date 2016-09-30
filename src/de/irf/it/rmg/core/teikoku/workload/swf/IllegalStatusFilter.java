package de.irf.it.rmg.core.teikoku.workload.swf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.WorkloadFilter;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
* Checks whether the jobs status is 1, completed. 
* 
*  Jobs with status
*  -1, not relevant
*  0, failed
*  2, partial execution will be continued
*  3, last partial execution job completed
*  4, last partial execution job failed
*  5, cancelled
*  
*  Are dropped.
* 
* @author <a href="mailto:ahirales@cicese.edu.mx">Adan Hirales Carbajal</a>
*         (last edited by $Author$)
* @version $Version$, $Date$
* 
*/
public class IllegalStatusFilter implements WorkloadFilter {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(BrokenProcessorsFilter.class);
	
	public IllegalStatusFilter() {	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadFilter#apply(de.irf.it.rmg.core.teikoku.site.Site,
	 *      de.irf.it.rmg.core.teikoku.Job, boolean)
	 */
	public boolean apply(Job job, boolean fix) {
		boolean result = true;

		if (job instanceof SWFJob) {
			byte status = ((SWFJob) job).getStatus(); 
			if( status != 1 && status == 5) {
				result = false;
			}
		} // if

		return result;
	}
	
}
