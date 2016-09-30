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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.irf.it.rmg.core.teikoku.exceptions.InvalidRuleBaseException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.Pair;

/**
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class AbsoluteValuesRuleBase extends AbstractRuleBase {
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * @param dm
	 */
	public AbsoluteValuesRuleBase() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.grid.rule.RuleBase#decide(java.util.Map,
	 *      de.irf.it.rmg.core.teikoku.job.Job)
	 */
	public boolean decide(Job j)
			throws InvalidRuleBaseException {
		RidgidRule matchingRule = null;
		Map<String,Double> inputVector=super.createInputVector(j);
		
		double consequence=0;
		double consequencetmp=0;
		for (Rule rule : super.rules) {
			RidgidRule rrule=(RidgidRule) rule;
			if (!rule.isValidRule(inputVector.keySet())) {
				String msg = "No condition found in rule!";
				throw new InvalidRuleBaseException(msg);
			} // if
			consequencetmp=rrule.getConsequence(inputVector);
			if ((consequencetmp>=-1) && (consequencetmp<=1)) {
				//it is matching, check multiple rule matching for rigid rules
				if (matchingRule != null) {
					System.err
							.println("Multiple rule matching: error in rule base!");
				} // if
				matchingRule = rrule;
				consequence=consequencetmp;
			} // if
		} // for

		if (matchingRule == null) {
			String msg = "Invalid rule base: No matching rule found for values "
					+ inputVector + ".";
			throw new InvalidRuleBaseException(msg);
		} // if
		
		return consequence>0;
	}
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	public void createRuleBaseFromFile(String Filename) {
		Properties properties = new Properties();
		List<RidgidRule> rtmp = new ArrayList<RidgidRule>();
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
        String consequences[] = properties.getProperty("consequence").split("\\s");
        if(numberOfFeatures != featureNames.length)
            System.err.println((new StringBuilder("Number of features does not match the feature descriptions in ")));
        List<List<Double>> intervalMap = new ArrayList<List<Double>>();
        List<String> names = new ArrayList<String>();
        int numberOfRequiredRules = 1;
        for(int i = 0; i < numberOfFeatures; i++)
        {
            String featureName = featureNames[i];
            String featureKey = properties.getProperty((new StringBuilder(String.valueOf(featureName))).append(".featurekey").toString());
            String featureIntervalls[] = properties.getProperty((new StringBuilder(String.valueOf(featureName))).append(".intervals").toString()).split("\\s");
            List<Double> intervalValues = new ArrayList<Double>();
            for(int k = 0; k < featureIntervalls.length; k++)
            {
                Double val = new Double(featureIntervalls[k]);
                intervalValues.add(val);
            }

            numberOfRequiredRules *= intervalValues.size() - 1;
            intervalMap.add(intervalValues);
            names.add(featureKey);
        }

        if(numberOfRequiredRules != consequences.length)
            System.err.println((new StringBuilder("Number of defined consequences doesnot match the number of implicitely defined rules in ")));
        ArrayList intervals = new ArrayList(intervalMap);
        ArrayList featureKeys = new ArrayList(names);
        int consequence_counter = 0;
        for(int i = 0; i < ((ArrayList)intervals.get(1)).size() - 1; i++)
        {
            for(int j = 0; j < ((ArrayList)intervals.get(0)).size() - 1; j++)
            {
                Pair pair0 = new Pair((Double)((ArrayList)intervals.get(0)).get(j), (Double)((ArrayList)intervals.get(0)).get(j + 1));
                Pair pair1 = new Pair((Double)((ArrayList)intervals.get(1)).get(i), (Double)((ArrayList)intervals.get(1)).get(i + 1));
                RidgidRule rule = new RidgidRule();
                rule.setParametersOfCondition((String)featureKeys.get(1), pair1);
                rule.setParametersOfCondition((String)featureKeys.get(0), pair0);
                rule.setConsequence(new Double(consequences[consequence_counter++]));
                rtmp.add(rule);
            }

        }
        //Compression of RuleBase for Performancetuning
        rtmp = combine(rtmp, (String)featureKeys.get(1), (String)featureKeys.get(0));
        rtmp = combine(rtmp, (String)featureKeys.get(0), (String)featureKeys.get(1));
        rules.addAll(rtmp);
	}
	
    private List combine(List old, String featureKey1, String featureKey2)
    {
        for(int index = 0; index < old.size() - 1;)
        {
            RidgidRule rcompare = null;
            RidgidRule r = (RidgidRule)old.get(index);
            for(int j = index + 1; j < old.size(); j++)
            {
            	//Maybe erroneous
                if(r.getParametersOfCondition(featureKey1).getRight() != ((RidgidRule)old.get(j)).getParametersOfCondition(featureKey1).getLeft() || r.getParametersOfCondition(featureKey2).getRight() != ((RidgidRule)old.get(j)).getParametersOfCondition(featureKey2).getLeft() || r.getConsequence() != ((RidgidRule)old.get(j)).getConsequence())
                    continue;
                rcompare = (RidgidRule)old.get(j);
                break;
            }

            if(rcompare == null)
            {
                index++;
            } else
            {
                Pair pairtmp = r.getParametersOfCondition(featureKey2);
                pairtmp.setRight((Double)rcompare.getParametersOfCondition(featureKey2).getRight());
                r.setParametersOfCondition(featureKey2, pairtmp);
                old.remove(rcompare);
            }
        }

        return old;
    }
	
}
