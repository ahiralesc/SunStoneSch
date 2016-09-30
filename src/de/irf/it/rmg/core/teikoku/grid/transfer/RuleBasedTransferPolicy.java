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
package de.irf.it.rmg.core.teikoku.grid.transfer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Bootstrap;
import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidRuleBaseException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;
import de.irf.it.rmg.core.teikoku.grid.rule.AbstractRuleBase;
import de.irf.it.rmg.core.teikoku.grid.rule.RuleBase;
import de.irf.it.rmg.core.teikoku.grid.rule.RuleNameFilter;
import de.irf.it.rmg.core.teikoku.grid.rule.SizeRuleBaseFilter;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;

/**
 * This TransferPolicy decides jobacceptance with use of simple ruleBases
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public class RuleBasedTransferPolicy implements TransferPolicy {

	/**
	 * Filed for the activityBroker, this transferPolica is used for
	 */
	private ActivityBroker activityBroker;
	
	/**
	 * RuleBaseFilter, which serves for defining the propper ruleBase for a specific exchangePartner
	 * by they "size"-difference between the actual partner and the site a rulebase was trained with
	 */
	private SizeRuleBaseFilter sizeRuleBaseFilter=new SizeRuleBaseFilter();
	
	/**
	 * This field is for saving the used criteria (e.g. metrics to make the decision)
	 */
	private List<String> criteria=new ArrayList<String>();
	
	/**
	 * Contains all RuleBases, which are involved in the decisionProcess
	 */
	private List<RuleBase> ruleBases=new ArrayList<RuleBase>();
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.TransferPolicy#decide(de.irf.it.rmg.core.teikoku.job.Job)
	 */
	public boolean decide(Job job,ActivityDelegationSource ds) {
		//First get the right Rulebase
		RuleBase rb=sizeRuleBaseFilter.getPropperRuleBase(ruleBases, ds);
		//The ruleBase has to decide, if the job will be accepted or not
		try {
			return rb.decide(job);
		} catch (InvalidRuleBaseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}
	

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy#decide(de.irf.it.rmg.core.teikoku.job.Job, de.irf.it.rmg.core.teikoku.grid.DelegationTarget)
	 */
	public boolean decide(Job job, ActivityDelegationTarget dt) {
//		First get the right Rulebase
		RuleBase rb=sizeRuleBaseFilter.getPropperRuleBase(ruleBases, dt);
		//The ruleBase has to decide, if the job will be accepted or not
		try {
			return rb.decide(job);
		} catch (InvalidRuleBaseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy#setActivityBroker(de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker)
	 */
	public void setActivityBroker(ActivityBroker activityBroker) {
		this.activityBroker=activityBroker;
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy#initialize(java.lang.String)
	 */
	public void initialize(String prefix) throws InstantiationException{
		// Initialize RuleMetrics

		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, activityBroker.getSite().getName(),
				prefix+ "." +Constants.CONFIGURATION_SITES_TRANSFERPOLICY_CRITERIA);

		if (key == null) {
				String msg = "Transferpolicy entry ("
					+ Site.CONFIGURATION_SECTION + "[" + activityBroker.getSite().getName()
					+ "]" + prefix + "." +Constants.CONFIGURATION_SITES_TRANSFERPOLICY_CRITERIA
					+ ") not found in configuration";
				throw new InstantiationException(msg);
		} // if
		this.criteria=c.getList(key);
		//Initialize RuleBases
		instantiateRuleBase(prefix);	
	}
	
	/**
	 * @param prefix
	 */
	private void instantiateRuleBase(String prefix) {
		try{	
			Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
			.subset(Site.CONFIGURATION_SECTION);
			String key = ConfigurationHelper.retrieveRelevantKey(c, activityBroker.getSite().getName(),
					prefix+ "." +Constants.CONFIGURATION_SITES_TRANSFERPOLICY_RULEBASE_CLASS);
		
			if (key == null) {
				String msg = "TransferPolicy rulebase entry ("
						+ Site.CONFIGURATION_SECTION + "[" + activityBroker.getSite().getName()
						+ "]" + prefix+ Constants.CONFIGURATION_SITES_TRANSFERPOLICY_RULEBASE_CLASS
						+ ") not found in configuration";
				throw new InstantiationException(msg);
			} // if

			String className = c.getString(key);

			//get a list of all rulefiles
			File dir = new File("DMConf");
			Bootstrap bs=Bootstrap.getInstance();
			//Aply the RulenameFilter to leave only the rulebaseFiles for this site
			RuleNameFilter rnf=new RuleNameFilter(activityBroker.getSite().getName()+"_rules_"+bs.TESTNUMBER+"_"+bs.PROPERTY_VERSION);
			String[] children = dir.list(rnf);
			
			for (int i=0;i<children.length;i++){
				RuleBase tmp=ClassLoaderHelper.loadInterface(className,
						RuleBase.class);
				((AbstractRuleBase) tmp).setSite(activityBroker.getSite());
				((AbstractRuleBase) tmp).setCriteria(this.criteria);
				((AbstractRuleBase) tmp).createRuleBaseFromFile("DMConf/"+children[i]);
				ruleBases.add(tmp);
			}
			
		} catch (InstantiationException e){
			e.printStackTrace();
			System.out.println("Instantiation of ruleBase not sucessfull");
			System.exit(1);
		}
	}
	
	public List<String> getCriteria(){
		return this.criteria;
	}

}
