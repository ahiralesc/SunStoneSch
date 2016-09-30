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

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

/**
 * This metric is the basis for plotting the whole schedule for a site.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class MATLAB extends AbstractMetric {
	
	private int resourceCount=1;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public MATLAB() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getHeader()
	 */
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "%JobNr ";
		values[1] = "SubmitTime ";
		values[2] = "StartTime ";
		values[3] = "EndTime  ";
		values[4] = "RunTime ";
		values[5] = "ResCount ";
		values[6] = "ResourceVector...";
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[6+resourceCount];
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
			Job job=event.getCompletedJob();
			if (job.getProvenance().getLastCondition()==this.getSite()){
				if (this.resourceCount==1){
					this.resourceCount=super.getSite().getSiteInformation().getNumberOfAvailableResources();
				}
				this.updateLatestValues(event,(SWFJob) job);
			}
		}
	}
	/**
	 * TODO: not yet commented
	 * 
	 */
	/**
	 * @param event
	 * @param site
	 */
	private void updateLatestValues(Event event,SWFJob job) {
		Object[] newValues = getLatestValuesPrototype();
		newValues[0] = job.getJobNumber();
		newValues[1] = job.getSubmitTime();
		newValues[2] = DateHelper.convertToSeconds(TimeHelper.toLongValue(job
				.getLifecycle().findPeriodsFor(State.STARTED)[0].getAdvent()));
		newValues[3] = DateHelper
				.convertToSeconds(TimeHelper.toLongValue(job.getLifecycle()
						.findPeriodsFor(State.COMPLETED)[0].getAdvent()));
		newValues[4] = job.getRunTime();
		newValues[5] = job.getResources().size();

		Resource[] occupiedResources = job.getResources().toArray();
		int currentOccupiedResourceIndex = 0;
		for (int i = 6; i < newValues.length; i++) {
			if (currentOccupiedResourceIndex < occupiedResources.length
					&& occupiedResources[currentOccupiedResourceIndex]
							.getOrdinal() - 1 == i - 6) {
				newValues[i] = 1;
				currentOccupiedResourceIndex++;
			} // if
			else {
				newValues[i] = 0;
			}
		} // for
		super.setLatestValues(newValues);
		//Comment the next line, if you want only one output for the
		//metric in the end of the simulation
		super.manualMakePermanent();
	}

}
