/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2009 by the
 *   Robotics Research Institute (Section Information Technology)
 *   at TU Dortmund University, Germany
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2006 by the
 *   Robotics Research Institute (Information Technology Section)
 *   Dortmund University, Germany
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
package de.irf.it.rmg.core.teikoku.metrics;

import java.util.Arrays;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.workload.swf.SWFConstants;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

/**
 * <p>
 * A text file writer for HPC job traces stored in the <a
 * href="http://www.cs.huji.ac.il/labs/parallel/workload/swf.html">Standard
 * Workload Format (SWF)</a> by Dror G. Feitelson.
 * </p>
 * 
 * <p>
 * This class writes SWF at version 2.2 with both header (comments) and body
 * (trace entry) support.
 * </p>
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class SWFTrace extends AbstractMetric {

	final private int numberOfElements = 19;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public SWFTrace() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		String version = SWFConstants.COMMENT_DELIMITER
				+ SWFConstants.HeaderFields.VERSION.getIdentifier() + " "
				+ SWFConstants.VERSION;

		Object[] values = this.getLatestValuesPrototype();
		Arrays.fill(values, new String());

		values[0] = version;

		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[numberOfElements];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#acceptJobCompletedEvent(de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent)
	 */
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			Job job=event.getCompletedJob();
			if (job.getReleasedSite()==this.getSite()){
				Object[] values = this.getLatestValuesPrototype();
				values = this.setValues(event, (SWFJob) job, values);
				super.setLatestValues(values);
				super.getPersistentStore().makePersistent(values);
			}
		}
	}

	private Object[] setValues(JobCompletedEvent event, SWFJob job,
			Object[] values) {
		values[0] = job.getJobNumber();
		values[1] = job.getSubmitTime();
		values[2] = job.getWaitTime();
		values[3] = job.getRunTime();
		values[4] = job.getNumberOfAllocatedProcessors();
		values[5] = job.getAverageCPUTimeUsed();
		values[6] = job.getUsedMemory();
		values[7] = job.getRequestedNumberOfProcessors();
		values[8] = job.getRequestedTime();
		values[9] = job.getRequestedMemory();
		values[10] = job.getStatus();
		values[11] = job.getUserID();
		values[12] = job.getGroupID();
		values[13] = job.getExecutableApplicationNumber();
		values[14] = job.getQueueNumber();
		values[15] = job.getPartitionNumber();
		values[16] = job.getPrecedingJobNumber();
		values[17] = job.getThinkTimeFromPrecedingJob();
		values[18] = job.getReleasedSite().getName().replaceAll("\\D", "");
		
		return values;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.metrics.Metrics
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#terminate()
	 */
	@Override
	public void terminate() {
		// TODO: sort output file by job numbers ascending (unclear how).
		super.terminate();
	}
	
	@Override
	public void makePermanent(){
		//do nothing
		//SWFTrace is a metric that could not be logged out automatically
	}
}
