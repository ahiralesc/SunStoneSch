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

import java.util.HashMap;
import java.util.Map;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.math.AverageHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

/**
 * This metric calculates the AWRT only for Jobs, which are delivered to other sites.
 * 
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
final public class AverageWeightedResponseTimeDeliveredJobs extends ResponseTime {

	/**
	 * holds the avgCalculator for each site
	 */
	private Map<Site,AverageHelper> siteResponseMap;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public AverageWeightedResponseTimeDeliveredJobs() {
		super();
		this.siteResponseMap=new HashMap<Site, AverageHelper>();
	}
	
	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.metrics.ResponseTime#getLatestValuesPrototype()
	 */
	@Override
	public Object[] getLatestValuesPrototype(){
		return new Object[6];
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.ResponseTime#getHeader()
	 */
	@Override
	protected Object[] getHeader() {
		Object[] values = getLatestValuesPrototype();

		values[0] = "% timestamp ";
		values[1] = "| job name ";
		values[2] = "| current AWRT";
		values[3] = "| p_j";
		values[4] = "| m_j";
		values[5] = "| Site";
		
		return values;
	}

	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			if (job.getReleasedSite()==this.getSite() && job.getProvenance().getLastCondition()!=this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			}
		}	
	}
	
	private void handleEvent(Job job, long timestamp){
		Site completionSite=job.getProvenance().getLastCondition();
		//Check existence of avgCalculator for given completion site
		if (!this.siteResponseMap.containsKey(completionSite)){
			siteResponseMap.put(completionSite, new AverageHelper());
		}
		AverageHelper ah=siteResponseMap.get(completionSite);
		
		//calculate resourceConsumption of the completed Job
		double resourceConsumption = TimeHelper.toSeconds(job.getDuration().distance())* job.getResources().size(); 
		Object[] values=getLatestValuesPrototype();
		
		values[0] = timestamp;
		values[1] = job.getName();
		values[2] = ah.calculateWeightedAverage(super.calculateResponseTime(job),
				resourceConsumption);
		values[3] = TimeHelper.toSeconds(job.getDuration().distance());
		values[4] = job.getDescription().getNumberOfRequestedResources();
		values[5] = completionSite.getName().replaceAll("\\D", "");
		
		super.setLatestValues(completionSite,values);
		super.manualMakePermanent();
	}
}
