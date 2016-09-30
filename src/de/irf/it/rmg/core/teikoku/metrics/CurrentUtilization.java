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
import de.irf.it.rmg.core.teikoku.kernel.events.EventType;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

/**
 * This metric computes the current utilization (UTIL<sub>m</sub>(t)) at a
 * given time t. The values are computed as </br> UTIL<sub>m</sub>(t)= 1/m
 * &Sigma;<sub>j&isin;&pi;(t)</sub> m<sub>j</sub> of all currently running
 * jobs j&isin;&pi;(t) at time t. Thereby, m denotes the total number of
 * availabel machines and m<sub>j</sub> the number of required machines of a
 * specific job j.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class CurrentUtilization extends AbstractMetric {
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public CurrentUtilization() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getHeader()
	 */
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "%Timestamp";
		values[1] = "%Util_m";
		
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#acceptJobStartedEvent(de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent)
	 */
	@AcceptedEventType(value=JobStartedEvent.class)
	public void deliverStartedEvent(JobStartedEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			final Job job = event.getStartedJob();
			this.updateLatestValues(event,job.getProvenance().getLastCondition());
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#acceptJobCompletedEvent(de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent)
	 */
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			final Job job = event.getCompletedJob();
			if (job.getProvenance().getLastCondition()==this.getSite()){
				this.updateLatestValues(event, job.getProvenance().getLastCondition());
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
	private void updateLatestValues(Event event,Site site) {
		Object[] newValues = getLatestValuesPrototype();
		Long currentTimestamp = DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp()));
		
		newValues[0] = currentTimestamp;

		double actualFreeness = site.getScheduler().getSchedule()
				.getActualFreeness();
		double numberOfProvidedResources = site.getSiteInformation()
				.getNumberOfAvailableResources();
		double util = ((numberOfProvidedResources-actualFreeness) / numberOfProvidedResources);
		newValues[1] = util;
		super.setLatestValues(newValues);
		//Comment the next line, if you want only one output for the
		//metric in the end of the simulation
		super.manualMakePermanent();
	}

}
