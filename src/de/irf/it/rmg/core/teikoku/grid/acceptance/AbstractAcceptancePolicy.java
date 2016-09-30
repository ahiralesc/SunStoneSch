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
package de.irf.it.rmg.core.teikoku.grid.acceptance;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;

/**
 * This class combined the common functionalities every AcceptancePolicy should have from initialization
 * to fields a standard-acceptancePolicy needs to do its functionality
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public abstract class AbstractAcceptancePolicy implements AcceptancePolicy {
	
	/**
	 * This Variable stores the Transferpolicy, which is used for remote Jobs
	 */
	private TransferPolicy transferPolicy;
	
	/**
	 * Standard logging-mechanism for some logs
	 */
	final private static Log log = LogFactory.getLog(AcceptancePolicy.class);
	
	/**
	 * Reference to the activityBroker, which is using this AcceptancePolicy
	 */
	private ActivityBroker activityBroker;
	
	/**
	 * Initialization of the class:
	 * The Following Components are loaded and initialized:
	 * - localTrasnferpolicy
	 * - remoteTransferPolicy (both could be the same Transferpolicy)
	 */
	public void initializeTransferPolicy() throws InstantiationException {
		//initialize TransferPolicy (ies)
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);

		String key = ConfigurationHelper.retrieveRelevantKey(c, activityBroker.getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_ACCEPTANCEPOLICY_TRANSFERPOLICY_CLASS);

		if (key == null) {
			String msg = "ResourceBroker entry ("
				+ Site.CONFIGURATION_SECTION + "[" + activityBroker.getSite().getName()
				+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_ACCEPTANCEPOLICY_TRANSFERPOLICY_CLASS
				+ ") not found in configuration";
			throw new InstantiationException(msg);
		} else{
			//A single Transferpolicy should be used
			String className = c.getString(key);
			TransferPolicy tp = ClassLoaderHelper.loadInterface(className,
			TransferPolicy.class);
			tp.setActivityBroker(activityBroker);
			this.transferPolicy = tp;
			tp.initialize(key.replace(".class", ""));
		}
	}

	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.acceptance.AcceptancePolicy#setActivityBroker(de.irf.it.rmg.core.teikoku.grid.Activity.ActivityBroker)
	 */
	public void setActivityBroker(ActivityBroker broker) {
		this.activityBroker=broker;
	}

	/**
	 * Standard-getter
	 * @return TransferPolicy
	 */
	public TransferPolicy getTransferPolicy() {
		return transferPolicy;
	}
	
	public void setTransferPolicy(TransferPolicy tp){
		this.transferPolicy=tp;
	}

	/**
	 * Standard-getter
	 * @return ResourceBroker
	 */
	protected ActivityBroker getActivityBroker() {
		return activityBroker;
	}

	/**
	 * Standard-getter
	 * @return Log
	 */
	protected Log getLog() {
		return log;
	}

}
