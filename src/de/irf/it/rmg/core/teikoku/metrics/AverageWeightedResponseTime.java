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

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.math.AverageHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
final public class AverageWeightedResponseTime extends ResponseTime {

	/**
	 * The calculator for this metric's average values.
	 * 
	 */
	private AverageHelper ah;
	
	/**
	 * Is used to limit the AverageWeightedResponseTime for equitable values within an evolutionary
	 * optimization
	 */
	private int limit=-1;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public AverageWeightedResponseTime() {
		super();
		this.ah = new AverageHelper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.ResponseTime#getHeader()
	 */
	@Override
	protected Object[] getHeader() {
		Object[] values = super.getLatestValuesPrototype();

		values[0] = "% timestamp ";
		values[1] = "| job name ";
		values[2] = "| current AWRT";
		values[3] = "| p_j";
		values[4] = "| m_j";

		return values;
	}

	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
		//	if (job.getReleasedSite()==this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
		//	}
		}	
	}
	
	
	private void handleEvent(Job job, long timestamp){
		//calculate resourceConsumption for the job
		double resourceConsumption = TimeHelper.toSeconds(job.getDuration().distance())* job.getResources().size(); 
		Object[] values=super.getLatestValuesPrototype();
		values[0] = timestamp;
		values[1] = job.getName();
		values[2] = ah.calculateWeightedAverage(super.calculateResponseTime(job),
				resourceConsumption);
		values[3] = TimeHelper.toSeconds(job.getDuration().distance());
		values[4] = job.getDescription().getNumberOfRequestedResources();
		super.setLatestValues(values);
		super.manualMakePermanent();
	}
	
	@Override
	public void initialize() throws InitializationException{
		super.initialize();
		
		Configuration c = RuntimeEnvironment.getInstance()
        .getConfiguration().subset(
                Metrics.CONFIGURATION_SECTION);
		String key=this.getName()
        + "."
        + "limit";
		if (c.containsKey(key)){
			this.limit = c
	        .getInt(key);
		}
		
	}
}
