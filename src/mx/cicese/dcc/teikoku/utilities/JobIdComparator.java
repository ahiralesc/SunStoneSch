package mx.cicese.dcc.teikoku.utilities;

import java.util.Comparator;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class JobIdComparator implements Comparator{
	
	public int compare(Object j1, Object j2) {
		long jid1 = ((SWFJob)j1).getJobNumber();
		long jid2 = ((SWFJob)j2).getJobNumber();
		
		if( jid1 > jid2)
			return -1;
		else
			if(jid1 == jid2)
				return 0;
			else 
				return 1;
	}
}
