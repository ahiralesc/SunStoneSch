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
/**
 * 
 */
package de.irf.it.rmg.core.teikoku.grid.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class FuzzyRule implements Rule {

	Map<String,Map<String,Double>> features;
	
	double[] consequences;
	
	public FuzzyRule(){
		this.features=new HashMap<String, Map<String,Double>>();
	}
	
	public void addFeature(String featureName,Map<String,Double> params){
		features.put(featureName, params);
	}
	
	public Map<String,Double> getFeatureParameter(String featureName){
		return features.get(featureName);
	}
	
	public void setConsequence(double consequence){
		this.consequences=new double[1];
		this.consequences[0]=consequence;
	}
	
	public double getSuperposition(Map<String, Double> inputVector) {
		double superposition=1.0;
		for (String featureName:inputVector.keySet()){
			superposition*=calculateFunction(featureName, inputVector.get(featureName));
		}
		return superposition;
	}

	public boolean isValidRule(Set<String> keys) {
		return this.features.keySet().containsAll(keys);
	}
	
	private double calculateFunction(String featureName, double value){
		double mu=features.get(featureName).get("mu");
		double sigma=features.get(featureName).get("sigma");
		return Math.exp(-0.5*Math.pow(((value-mu)/sigma),2));
	}

	public double getConsequence() {
		//Dirty for only one consequence
		return this.consequences[0];
	}

}
