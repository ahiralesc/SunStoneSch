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
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.JobStartedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

/**
 * 
 * This metric computes the overall utilization (UTIL) of all finished jobs. The
 * values are computed as </br>UTIL= 1/m(max<sub>j&isin;&xi;(t)</sub>{C<sub>j</sub>(S)} -
 * min<sub>j&isin;&xi;(t)</sub>{C<sub>j</sub>(S) - p<sub>j</sub>}) &Sigma;<sub>j&isin;&xi;(t)</sub>
 * p<sub>j</sub>m<sub>j</sub> <br>
 * of all finished jobs j&isin;&xi;(t) at time t. Thereby, m denotes the total
 * number of availabel machines and m<sub>j</sub> the number of required
 * machines of a specific job j.
 * 
 * @author <a href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>,
 * 		   <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class OverallUtilization extends AbstractMetric {

	/**
	 * Holds the start time of the first job ever inserted into the schedule,
	 * denoted by
	 * 
	 * min<sub>j&isin;&tau;</sub>{C<sub>j</sub>(S) - p<sub>j</sub>}.
	 * 
	 * This represents the lower time bound of the current makespan.
	 */
	private Instant earliestStartTimeInSchedule;

	/**
	 * Holds the aggregated resource consumption up to the current time, denoted
	 * by
	 * 
	 * &Sigma;<sub>j&isin;&tau;</sub> RC(j).
	 * 
	 */
	private Double currentResourceConsumption;

	private int numberOfProvidedResources;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public OverallUtilization() {
		super();
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
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "% timestamp ";
		values[1] = "| overall UTIL ";

		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#acceptJobReleasedEvent(de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent)
	 */
	@AcceptedEventType(value=JobReleasedEvent.class)
	protected void deliverReleasedEvent(JobReleasedEvent event) {
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			if (earliestStartTimeInSchedule==null) {
				earliestStartTimeInSchedule=
					event.getTimestamp();
			}
		}
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
			if (earliestStartTimeInSchedule==null) {
				earliestStartTimeInSchedule=
					event.getTimestamp();
			}
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
			Job job = event.getCompletedJob();
			if (job.getReleasedSite()==this.getSite()){
				this.numberOfProvidedResources = this.getSite().getSiteInformation()
				.getNumberOfAvailableResources();
				Object[] values = this.getLatestValuesPrototype();
				Instant latestEndTimeInSchedule = event.getTimestamp();
				if (currentResourceConsumption==null) {
					currentResourceConsumption=0.0;
				}
				currentResourceConsumption += TimeHelper.toSeconds(job.getDuration()
						.distance())
						* job.getResources().size();
				values[1] = currentResourceConsumption
						/ (this.numberOfProvidedResources * (DateHelper.convertToSeconds(TimeHelper.distance(earliestStartTimeInSchedule,latestEndTimeInSchedule).length())));
				values[0] =DateHelper.convertToSeconds(TimeHelper.toLongValue(latestEndTimeInSchedule));
	
				super.setLatestValues(values);
				//Comment the next line, if you want only one output for the
				//metric in the end of the simulation
				super.manualMakePermanent();
			}
		}
	}	
	
}
