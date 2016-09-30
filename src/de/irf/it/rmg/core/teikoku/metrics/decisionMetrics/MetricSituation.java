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
package de.irf.it.rmg.core.teikoku.metrics.decisionMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.grid.rule.AbstractRuleBase;
import de.irf.it.rmg.core.teikoku.grid.rule.RuleBase;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.math.CalculationFabric;


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
/**
 * @author alexander
 *
 */

final public class MetricSituation extends AbstractMetric {
	
	private int criteriaCount=2;
	private List<String> criteria=null;
	private CalculationFabric calculationFabric;
	private boolean initialized=false;
	private static List<String> order;
	
	public MetricSituation() {
		super();
		this.order=new ArrayList<String>();
		this.order.add("rwp");
		this.order.add("rjp");
	}

	@Override
	public void setSite(Site site){
		super.setSite(site);
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);
		String key = ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
				"activitybroker.transferpolicy.criteria");
		this.criteriaCount = c.getStringArray(key).length;
		this.calculationFabric=new CalculationFabric();
	}
	
	@Override
	public Object[] getHeader() {
		Object[] values = new Object[1];

		values[0] = "% value pairs";
		return values;
	}
	
	public void calculateNewValue(Map<String, Double> inputVector,RuleBase rb){
		Object[] values=this.getLatestValuesPrototype();
		for (String crit:inputVector.keySet()){
			values[this.order.indexOf(crit)]=inputVector.get(crit);
		}
		values[values.length-1]=((AbstractRuleBase) rb).getSize();
		super.setLatestValues(values);	
		super.manualMakePermanent();
	}

	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[criteriaCount+1];
	}
}
