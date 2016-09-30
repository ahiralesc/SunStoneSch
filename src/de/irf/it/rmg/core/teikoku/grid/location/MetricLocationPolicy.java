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
package de.irf.it.rmg.core.teikoku.grid.location;

import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.MetricsException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.metrics.MetricsVault;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.collections.Ordering;

/**
 * The MetricLocationPolicy is used to order the known sits by the actual characteristic of a single Metric
 * (e.g. order them by the ratio of exchanged Jobs to and from them or by the AWRT of deleiverd jobs)
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public class MetricLocationPolicy implements LocationPolicy{

	private Ordering ordering;
	private Metrics metric;
	private ActivityBroker activityBroker;
	
	
	public List<ActivityBroker> getOrder(
			List<ActivityBroker> knownSites) {
		Collections.sort(knownSites,this);
		return knownSites;
	}

	public int compare(ActivityBroker ab1, ActivityBroker ab2) {
		double val1=0;
		double val2=0;
		int result=0;
		
		Object[] valvec1=metric.getCurrentValues(ab1.getSite());
		Object[] valvec2=metric.getCurrentValues(ab2.getSite());
		
		if (valvec1!=null) val1=Double.parseDouble(valvec1[2].toString());
		if (valvec2!=null) val2=Double.parseDouble(valvec2[2].toString());
		
		if(val1 == 0 && val2 != 0)
            return -1;
        if(val1 != 0 && val2 == 0)
            return 1;
        if(val1 == 0 && val2 == 0)
            return 0;
		
		if (val1>val2) result=1;
		else if (val1<val2) result=-1;

		if (ordering==Ordering.DESCENDING){
			return (-1*result);
		} else return result;
		
	}
	
	public void setOrdering(Ordering ordering){
		this.ordering=ordering;
	}
	
	public void setMetric(Metrics metric){
		this.metric=metric;
	}

	public void setActivityBroker(ActivityBroker activityBroker) {
		this.activityBroker=activityBroker;
		
	}

	public void initialize() throws InstantiationException{
		//Initialize the Metric that should be used
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);
		String key = ConfigurationHelper.retrieveRelevantKey(c, activityBroker.getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_LOCATIONPOLICY_METRIC);
		if (key == null) {
			String msg = "ResourceBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + activityBroker.getSite().getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_LOCATIONPOLICY_METRIC
					+ ") not found in configuration - you must provide a metric for a metricLocationPolicy";
			throw new InstantiationException(msg);
		} // if
		String metricName = c.getString(key);
		try {		
			//Get Configuration-part for Metric
			Configuration csub=RuntimeEnvironment.getInstance()
			.getConfiguration().subset(
					Metrics.CONFIGURATION_SECTION+"."+metricName);	
			
			this.metric = MetricsVault.getInstance().registerMetricAt(activityBroker.getSite(),
					metricName);
		} catch (MetricsException e) {
			throw new InstantiationException(e.getMessage());
		}
		
	}

}
