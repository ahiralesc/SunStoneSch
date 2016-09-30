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
package de.irf.it.rmg.core.teikoku.grid.distribution;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;
import de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;

/**
 * This abstract class combines common functionalities of different distributionPolicies
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class StandardDistributionPolicy implements DistributionPolicy {

	/**
	 * Holds the reference to the activityBroker, which uses this distributionPolicy
	 */
	private ActivityBroker activityBroker;
	/**
	 * Filed for the transferPolicy, that is optionally used by this distributionPolicy
	 */
	private TransferPolicy transferPolicy;
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.DistributionPolicy#decide(de.irf.it.rmg.core.teikoku.job.Job, de.irf.it.rmg.core.teikoku.grid.DelegationTarget)
	 */
	public boolean decide(Job j, ActivityDelegationTarget dt){
		return transferPolicy.decide(j, dt);
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.DistributionPolicy#initialize()
	 */
	public void initializeTransferPolicy() throws InstantiationException {
		//initialize TransferPolicy
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, activityBroker.getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_DISTRIBUTIONPOLICY_TRANSFERPOLICY_CLASS);

		if (key == null) {
				//No TransferPolicy given
				String msg = "ResourceBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + activityBroker.getSite().getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_DISTRIBUTIONPOLICY_TRANSFERPOLICY_CLASS
					+ ") not found in configuration";
				
				throw new InstantiationException(msg);
		} else{
			String className = c.getString(key);
			TransferPolicy tp = ClassLoaderHelper.loadInterface(className,
			TransferPolicy.class);
			tp.setActivityBroker(activityBroker);
			this.transferPolicy = tp;
			tp.initialize(key.replace(".class", ""));
		}
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.DistributionPolicy#setActivityBroker(de.irf.it.rmg.core.teikoku.grid.ActivityBroker)
	 */
	public void setActivityBroker(ActivityBroker broker) {
		this.activityBroker=broker;

	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.distribution.DistributionPolicy#setTransferPolicy(de.irf.it.rmg.core.teikoku.grid.distribution.TransferPolicy)
	 */
	public void setTransferPolicy(TransferPolicy transferPolicy) {
		this.transferPolicy=transferPolicy;

	}
	
	/**
	 * standard-getter
	 * @return ResourceBroker
	 */
	public ActivityBroker getActivityBroker(){
		return activityBroker;
	}
	
}
