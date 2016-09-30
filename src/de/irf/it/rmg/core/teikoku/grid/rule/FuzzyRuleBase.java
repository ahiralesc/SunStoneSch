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

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.irf.it.rmg.core.teikoku.exceptions.InvalidRuleBaseException;
import de.irf.it.rmg.core.teikoku.job.Job;

/**
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class FuzzyRuleBase extends AbstractRuleBase {

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.rule.AbstractRuleBase#createRuleBaseFromFile(java.lang.String)
	 */
	@Override
	public void createRuleBaseFromFile(String Filename) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(Filename));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//read the size-property
		String sizeprop=properties.getProperty("size");
		if (sizeprop!=null) super.size = Integer.parseInt(sizeprop);
		
		int numberOfFeatures = Integer.parseInt(properties.getProperty("number_of_features"));
        String featureNames[] = properties.getProperty("featurenames").split("\\s");
        if(numberOfFeatures != featureNames.length)
            System.err.println((new StringBuilder("Number of features does not match the feature descriptions in featurenames")));
        
        int numberOfParameters=Integer.parseInt(properties.getProperty("number_of_parameters"));
        String parameterNames[] = properties.getProperty("parameternames").split("\\s");
        if(numberOfParameters != parameterNames.length)
            System.err.println((new StringBuilder("Number of parameters does not match the parameter descriptions in parameternames")));
        
        int numberOfRules=Integer.parseInt(properties.getProperty("number_of_rules"));
        
        Map<String,Map<String,Double[]>> featureMap=new HashMap<String, Map<String,Double[]>>();
        for (String fname:featureNames){
        	featureMap.put(fname, new HashMap<String,Double[]>());
        	for (String pname:parameterNames){
        		String key=fname+"."+pname;
        		if (!(properties.containsKey(key))){
        			System.err.println("In rulefile ["+Filename+"] must be an entry like ["+key+"]");
        		} else{
        			String[] params=properties.getProperty(key).split("\\s");
        			Double[] paramsd=new Double[params.length];
        			for(int i=0;i<params.length;i++){
        				paramsd[i]=Double.parseDouble(params[i]);
        			}
        			featureMap.get(fname).put(pname, paramsd);
        		}
        	}
        }
        Double[] consequences=new Double[]{};
		if (!(properties.containsKey("consequence"))){
			System.err.println("In rulefile ["+Filename+"] must be an entry like [consequence]");
		} else{
			String[] params=properties.getProperty("consequence").split("\\s");
			Double[] paramsd=new Double[params.length];
			for(int i=0;i<params.length;i++){
				paramsd[i]=Double.parseDouble(params[i]);
			}
			consequences=paramsd;
		}
        
        for (int i= 0;i<numberOfRules; i++){
        	FuzzyRule rule=new FuzzyRule();
        	for (String fname:featureNames){
        		Map<String, Double> fmap=new HashMap<String, Double>();
        		for (String pname:parameterNames){
        			fmap.put(pname, featureMap.get(fname).get(pname)[i]);
        		}
        		rule.addFeature(fname,fmap);
        	}
        	rule.setConsequence(consequences[i]);
        	super.rules.add(rule);
        }
	}

	/* (non-Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.rule.RuleBase#decide(de.irf.it.rmg.core.teikoku.job.Job)
	 */
	public boolean decide(Job j) throws InvalidRuleBaseException {
		Map<String,Double> inputVector=super.createInputVector(j);
		
		double decisionSum=0.0;
		double superPositionSum=0.0;
		
		for (Rule rule:super.rules){
			FuzzyRule frule=(FuzzyRule) rule;
			if (frule.isValidRule(inputVector.keySet())){
				double superPosition=frule.getSuperposition(inputVector);
				decisionSum+=superPosition*frule.getConsequence();
				superPositionSum+=superPosition;
			}else{
				String msg = "Rule is not valid for the given criteria!";
				throw new InvalidRuleBaseException(msg);
			}
		}
		return (decisionSum/superPositionSum)>0;
	}

}
