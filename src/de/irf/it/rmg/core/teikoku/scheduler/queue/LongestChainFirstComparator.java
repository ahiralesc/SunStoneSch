package de.irf.it.rmg.core.teikoku.scheduler.queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.collections.AbstractInvertibleComparator;

public class LongestChainFirstComparator  extends AbstractInvertibleComparator {

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param order
	 */
	public LongestChainFirstComparator(){
		super();
	}
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.util.Comparator
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Job left, Job right) {
		int result = 0; 
		
		if(left.getPriority() < right.getPriority())
			result = 1;
		if(left.getPriority() > right.getPriority())
			result = -1;
		
		return result;
	}
}
