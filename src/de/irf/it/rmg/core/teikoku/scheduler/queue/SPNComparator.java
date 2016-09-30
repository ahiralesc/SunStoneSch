package de.irf.it.rmg.core.teikoku.scheduler.queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.collections.AbstractInvertibleComparator;

public class SPNComparator extends AbstractInvertibleComparator {

	@Override
	public int compare(Job job1, Job job2) {
			
		double j1p = ((SWFJob)job1).getRequestedTime();
		double j2p = ((SWFJob)job2).getRequestedTime();
			
		if( j1p >= j2p )
				return 1;
			else
				return -1;
	}

}
