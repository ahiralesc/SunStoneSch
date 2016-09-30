package de.irf.it.rmg.core.teikoku.job;

import de.irf.it.rmg.core.teikoku.common.Slot;

public class JobControlBlock {
	private Slot slot;
	private Job job;
	
	public JobControlBlock( Slot slot, Job job ){
		this.slot = slot;
		this.job = job;
	}
	
	public Job getJob() {
		return this.job;
	}
	
	public Slot getSlot() {
		return this.slot;
	}
}
