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
 * "Teikoku" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the
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
package de.irf.it.rmg.core.teikoku.grid;

import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.JobType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSink;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

/**
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander Folling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class SinkToSourceAdapter
		implements WorkloadSink{

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(SinkToSourceAdapter.class);
	
	private LocalSubmissionComponent localSubmissionComponent;

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.workload.WorkloadSink
	// -------------------------------------------------------------------------

	public SinkToSourceAdapter(LocalSubmissionComponent localSubmissionComponent){
		this.localSubmissionComponent=localSubmissionComponent;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadSink#putNextJob(de.irf.it.rmg.core.teikoku.job.Job)
	 */
	public void putNextJob(Job j) {
		//Event releaseEvent = new JobReleasedEvent(this.localSubmissionComponent,TimeFactory.newMoment(DateHelper.convertToMilliseconds(((SWFJob)j).getSubmitTime())), j,this.localSubmissionComponent.getSite());
		//Kernel.getInstance().dispatch(releaseEvent);
		
		long submitTime = -1;
		
		if (j.getJobType().equals(JobType.INDEPENDENT))
			submitTime = ((SWFJob)j).getSubmitTime();
		else
			submitTime = ((CompositeJob)j).getSubmitTime();
		
		Event releaseEvent = new JobReleasedEvent(this.localSubmissionComponent, TimeFactory.newMoment(DateHelper.convertToMilliseconds(submitTime)), j,this.localSubmissionComponent.getSite());
		Kernel.getInstance().dispatch(releaseEvent);
		
	}

	/**
	 * @return the localSubmissionComponent
	 */
	public LocalSubmissionComponent getLocalSubmissionComponent() {
		return localSubmissionComponent;
	}
}
