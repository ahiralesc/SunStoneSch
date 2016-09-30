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
package de.irf.it.rmg.core.teikoku.grid.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.irf.it.rmg.core.teikoku.exceptions.InvalidRuleBaseException;
import de.irf.it.rmg.core.util.Pair;

/**
 * This specific kind of rules contains a rigid conditional Part containing two rigid limits per used characteristic
 * and a single consequence-part, that defines the behaviour of the rule
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class RidgidRule
		implements Rule {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Map<String, Pair<Double, Double>> conditionalPart;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private double consequence;

	/**
	 * TODO: not yet commented
	 * 
	 */
	public RidgidRule() {
		this.conditionalPart = new HashMap<String, Pair<Double, Double>>();
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.grid.rule
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.grid.rule.Rule#setParametersOfCondition(java.lang.String,
	 *      java.util.ArrayList)
	 */
	public void setParametersOfCondition(String cond, Pair<Double, Double> param) {
		this.conditionalPart.put(cond, param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.grid.rule.Rule#setParametersOfConsequence(java.lang.String,
	 *      java.util.ArrayList)
	 */
	public void setConsequence(double param) {
		this.consequence=param;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.grid.rule.Rule#getAttributesForCondition(java.lang.String)
	 */
	public Pair<Double, Double> getParametersOfCondition(String s) {
		return this.conditionalPart.get(s);
	}

	public boolean isValidRule(Set<String> keys) {
		boolean result = true;
		for (String key : keys) {
			if (!this.conditionalPart.containsKey(key)) {
				result = false;
				break;
			} // if
		} // for
		return result;
	}

	/* (Kein Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s += "Conditional Part: \n";
		Set<String> keys = this.conditionalPart.keySet();
		for (String key : keys) {
			s += (key + " " + this.conditionalPart.get(key) + "\n");
		}
		s += "\nConsequence: "+ this.consequence;
		return s;
	}

	public double getConsequence(Map<String, Double> inputVector) {
		//Check, if it's matching and return the decisionValue between 0 and 1
		if (this.ruleMatches(this, inputVector)){
			return this.consequence;
		}else{
			return Double.NEGATIVE_INFINITY;
		}
	}
	
	/*
	 * Method for the combination of ridgidRules
	 */
	public double getConsequence(){
		return this.consequence;
	}
	
	/** This method checks, if this rule matches to the values given in the inputVector
	 *
	 * @param rule
	 * @param inputVector
	 * @return
	 * @throws InvalidRuleBaseException 
	 */
	private boolean ruleMatches(RidgidRule rule, Map<String, Double> inputVector) {
		boolean result = true;
		Set<String> keys = inputVector.keySet();
		for (String key : keys) {
			Pair<Double, Double> interval = rule.getParametersOfCondition(key);
			Double inputValue = inputVector.get(key);
			if (inputValue.compareTo(interval.getLeft()) >= 0
					&& inputValue.compareTo(interval.getRight()) < 0
					|| (inputValue.equals(Double.POSITIVE_INFINITY) && interval
							.getRight().equals(Double.POSITIVE_INFINITY))) {
				result = true;
			} // if
			else {
				result = false;
				break;
			} // else
		} // for
		return result;
	}

}
