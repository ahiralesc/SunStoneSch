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

import java.util.List;

import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;


/**
 * 
 * This filter-class is for finding the best fitting ruleBase in regard of the number of available ressources
 * (e.g. if a job shoul be given to a site with 100 CPUs, you want to use a ruleBase for the decision, which was trained against a site
 * with maybe 128 CPUs rather than one trainied against a site with 1664 CPUs)
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class SizeRuleBaseFilter {
	
		/**
		 * returns the best fitting RuleBase with regard to the given ActivityDelegationTarget
		 * 
		 * @param rbs
		 * @param dt
		 * @return RuleBase
		 */
		public RuleBase getPropperRuleBase(List<RuleBase> rbs,ActivityDelegationTarget dt){
			RuleBase result=null;
			int difference=Integer.MAX_VALUE;
			try{
				int tmpSize;
				Object tmp=dt.getPublicInformation().get("size");
				if (tmp==null){
					throw new Exception("Size of the Partner is not given. please Check public Information of each Site");
				}
				int remoteSize=(Integer) tmp;
				for (RuleBase rb:rbs){
					//check the difference of size-information between the ruleBase and the remote ActivityDelegationTarget for each ruleBase
					tmpSize=((AbstractRuleBase) rb).getSize();
					/*if (tmpSize==0){
						throw new Exception("There is no size of Partner given, the Rulebase was trained with");
					}*/
					if (java.lang.Math.abs(tmpSize-remoteSize)<difference){
						difference=java.lang.Math.abs(tmpSize-remoteSize);
						result=rb;
					}
				}
			} catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			return result;
		}
		
		/**
		 * returns the best fitting RuleBase with regard to the given ActivityDelegationSource
		 * 
		 * @param rbs
		 * @param ds
		 * @return RuleBase
		 */
		public RuleBase getPropperRuleBase(List<RuleBase> rbs,ActivityDelegationSource ds){
			RuleBase result=null;
			int difference=Integer.MAX_VALUE;
			try{
				int tmpSize;
				Object tmp=ds.getPublicInformation().get("size");
				if (tmp==null){
					throw new Exception("Size of the Partner is not given. please Check public Information of each Site");
				}
				int remoteSize=(Integer) tmp;
				for (RuleBase rb:rbs){
//					check the difference of size-information between the ruleBase and the remote ActivityDelegationSource for each ruleBase
					tmpSize=((AbstractRuleBase) rb).getSize();
					/*if (tmpSize==0){
						throw new Exception("There is no size of Partner given, the Rulebase was trained with");
					}*/
					if (java.lang.Math.abs(tmpSize-remoteSize)<difference){
						difference=java.lang.Math.abs(tmpSize-remoteSize);
						result=rb;
					}
				}
			} catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			return result;
		}
	
}
