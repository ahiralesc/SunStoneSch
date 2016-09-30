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
package de.irf.it.rmg.core.teikoku.grid.rule;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.math.CalculationFabric;

/**
 * This abstract class combines common functionalities of all used RuleBases 
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public abstract class AbstractRuleBase
		implements RuleBase {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://logging.apache.org/log4j">Apache "log4j" API</a>.
	 * 
	 * @see org.apache.log4j.Logger
	 */
	final private static Logger log = Logger.getLogger(AbstractRuleBase.class);

	/**
	 * Field for the used Rules
	 * 
	 */
	protected Set<Rule> rules=new HashSet<Rule>();;

	/**
	 * Reference to the site, which may be important for evaluation of single Rules
	 * 
	 */
	protected Site site;
	
	private List<String> criteria;
	
	private CalculationFabric calculationFabric;
	
	/**
	 * This field is for the size, the rulebase is trained against (size=number of available Ressources on that site)
	 */
	protected int size=0;
	
	
	public AbstractRuleBase(){
		this.calculationFabric=new CalculationFabric();
	}
	
	/**standard-getter
	 * @return int
	 */
	public int getSize() {
		return size;
	}

	/**
	 * standard-setter
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setSite(Site site){
		this.site=site;
	}
	
	public Site getSite(){
		return site;
	}
	

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.grid.RuleBase
	// -------------------------------------------------------------------------

	public abstract void createRuleBaseFromFile(String Filename);

	public void setCriteria(List<String> criteria) {
		// TODO Auto-generated method stub
		this.criteria=criteria;
	}
	
	/**
	 * Method to create the inputVector of criterion-allocation Pairs for all given criteria
	 * 
	 * @param job
	 * @return Map<String, Double>
	 */
	public Map<String, Double> createInputVector(Job job) {
		Map<String, Double> result=new HashMap<String, Double>();
		for (String criterion:this.criteria){
			//Use the calculationFabric to calculate the actual assignment of each criterion
			result.put(criterion, this.calculationFabric.calculateValue(criterion, job, this.site));
		}
		return result;
	}
	
	
}
