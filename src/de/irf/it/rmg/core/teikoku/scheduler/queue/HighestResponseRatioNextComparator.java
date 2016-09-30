package de.irf.it.rmg.core.teikoku.scheduler.queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.collections.AbstractInvertibleComparator;
import de.irf.it.rmg.sim.kuiga.Clock;

public class HighestResponseRatioNextComparator extends AbstractInvertibleComparator {

	@Override
	public int compare(Job job1, Job job2) {
		
		long currentTime = Clock.instance().now().timestamp();
		
		long j1wt = currentTime - job1.getReleaseTime().timestamp();
		long j2wt = currentTime - job2.getReleaseTime().timestamp();
		
		double j1p = ((SWFJob)job1).getRequestedTime();
		double j2p = ((SWFJob)job2).getRequestedTime();
		
		double j1_ResponseRatio = (j1wt + j1p)/j1p;
		double j2_ResponseRatio = (j2wt + j2p)/j2p;
		
		//((SWFJob)job2).setResposeRatoi(j1_ResponseRatio);
		
		if( j1_ResponseRatio >= j2_ResponseRatio )
				return -1;
			else
				return 1;
	}

}
