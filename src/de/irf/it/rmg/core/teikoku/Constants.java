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
package de.irf.it.rmg.core.teikoku;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a> and <a
 *         href="mailto:ahiralesc@gmail.com">Adan Hirales</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
final public class Constants {

	// ----- configuration keys ------------------------------------------------

	/* general (component) keys */
	final public static String CONFIGURATION_ID_COMPONENT = "id";

	final public static String CONFIGURATION_REF_COMPONENT = "ref";

	final public static String CONFIGURATION_CLASS_COMPONENT = "class";

	/* runtime-specific keys */
	final public static String CONFIGURATION_RUNTIME_WORKINGDIRECTORY = "workingDirectory";

	final public static String CONFIGURATION_RUNTIME_LOGGINGLEVEL = "loggingLevel";

	final public static String CONFIGURATION_RUNTIME_SYSTEMMANAGER_CLASS = "systemManager."
			+ CONFIGURATION_CLASS_COMPONENT;

	/* site-specific keys */
	final public static String CONFIGURATION_SITES_NUMBEROFPROVIDEDRESOURCES = "numberOfProvidedResources";
	
	final public static String CONFIGURATION_SITES_CLOCKRATE = "machineClockRate";

	final public static String CONFIGURATION_SITES_LISTOFPROVIDEDRESOURCES = "listOfProvidedResources";

	final public static String CONFIGURATION_SITES_LOCALSUBMISSIONCOMPONENT_LIFETIME = "localsubmissioncomponent.sessionlifetime";
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_CLASS = "activitybroker."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_GRIDACTIVITYBROKER_CLASS = "gridActivityBroker."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_SESSIONLIFETIME = "activitybroker."
		+ "sessionlifetime";
	
	final public static String CONFIGURATION_SITES_TRANSFERPOLICY_CRITERIA = "criteria";
	
	final public static String CONFIGURATION_SITES_TRANSFERPOLICY_RULEBASE_CLASS = "rulebase."
	+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_DISTRIBUTIONPOLICY_CLASS = "activitybroker.distributionpolicy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_TRANSFERPOLICY_CLASS = "activitybroker.transferpolicy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_DISTRIBUTIONPOLICY_TRANSFERPOLICY_CLASS = "activitybroker.distributionpolicy.transferpolicy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_LOCATIONPOLICY_CLASS = "activitybroker.locationpolicy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_LOCATIONPOLICY_METRIC = "activitybroker.locationpolicy.metric";
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_PUBLIC_INFORMATION = "activitybroker.public";
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_ACCEPTANCEPOLICY_CLASS = "activitybroker.acceptancepolicy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_ACTIVITYBROKER_ACCEPTANCEPOLICY_TRANSFERPOLICY_CLASS = "activitybroker.acceptancepolicy.transferpolicy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_INFORMATIONBROKER_CLASS = "informationBroker."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_INFORMATIONBROKER_REFRESHRATE = "informationBroker.refreshRate";
	
	final public static String LOCATIONPOLICY ="locPol";
	
	final public static String LOCATIONPOLICY_ORDER ="locPolorder";
	
	final public static String CONFIGURATION_SITES_RESOURCEBROKER_CLASS = "resourcebroker."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_RESOURCEBROKER_PUBLIC_INFORMATION = "resourcebroker.public";
	
	final public static String CONFIGURATION_SITES_METABROKER_CLASS = "metabroker."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_SCHEDULER_CLASS = "scheduler."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_LOCALSTRATEGY_CLASS = "scheduler.localstrategy."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_JOBPOOLSTRATEGY_CLASS = "scheduler.jobpoolstrategy."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_LOCALPRESELECTOR_CLASS = "scheduler.localpreselector."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_POOLPRESELECTOR_CLASS = "scheduler.jobpoolpreselector."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_SCHEDULER_RIGID_ALLOCATION_CLASS = "gridActivityBroker.dss.strategy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_SCHEDULER_RIGID_PRIORITY_CLASS = "gridActivityBroker.dss.policy."
		+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_SCHEDULER_COMPOSITE_STRATEGY_CLASS = "gridActivityBroker.composite.strategy."
		+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_DECISIONMAKER_DISTRIBUTIONPOLICY_CLASS = "decisionmaker.distributionpolicy."
		+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_DECISIONMAKER_ACCEPTANCEPOLICY_CLASS = "decisionmaker.acceptancepolicy."
			+ CONFIGURATION_CLASS_COMPONENT;
	
	final public static String CONFIGURATION_SITES_DECISIONMAKER_REMOTE= "decisionmaker.remoteCriteria";
	
	final public static String CONFIGURATION_SITES_DECISIONMAKER_REMOTEFIELD= "decisionmaker.remoteField";
	
	final public static String CONFIGURATION_SITES_DECISIONMAKER_REMOTE_REFMETRICS=CONFIGURATION_SITES_DECISIONMAKER_REMOTE + "."  
			+ CONFIGURATION_REF_COMPONENT;
	
	final public static String CONFIGURATION_SITES_DECISIONMAKER_LOCAL="decisionmaker.localCriteria";
	
	final public static String CONFIGURATION_SITES_DECISIONMAKER_LOCALFIELD= "decisionmaker.localField";
	
	final public static String CONFIGURATION_SITES_DECISIONMAKER_LOCAL_REFMETRICS=CONFIGURATION_SITES_DECISIONMAKER_LOCAL + "." +   
		CONFIGURATION_REF_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_LOCALQUEUECOMPARATOR_CLASS = "scheduler.localqueuecomparator."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_JOBPOOLCOMPARATOR_CLASS = "scheduler.jobpoolcomparator."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_SCHEDULER_LOCALQUEUECOMPARATORORDERING_CLASS = "scheduler.localqueuecomparator.ordering";

	final public static String CONFIGURATION_SITES_SCHEDULER_JOBPOOLCOMPARATORORDERING_CLASS = "scheduler.jobpoolcomparator.ordering";
	
	final public static String CONFIGURATION_SITES_GRID_STRATEGY_RIGID_CLASS = "gridStrategyRigid."
		+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_SITES_REGISTEREDMETRIC = "registeredMetric";
	
	final public static String CONFIGURATION_SITES_REGISTEREDMETRIC_IDENTIFIER = CONFIGURATION_SITES_REGISTEREDMETRIC +"."
			+ CONFIGURATION_REF_COMPONENT;
	
	final public static String CONFIGURATION_GLOBALMETRICS = "globalMetric." + CONFIGURATION_REF_COMPONENT;

	/* metrics-specific keys */
	final public static String CONFIGURATION_METRICS_WRITER_CLASS = "writer."
			+ CONFIGURATION_CLASS_COMPONENT;

	final public static String CONFIGURATION_METRICS_WRITER_OUTPUTFILE = "writer.outputfile";
	
	final public static String CONFIGURATION_METRICS_WRITER_OUTPUTFILE_SUBDIRECTORY = "results";

	final public static String CONFIGURATION_METRICS_WRITER_MAYOVERWRITE = "writer.mayOverwrite";
	
	
	/* PFFP constants BEGIN */

	final public static String PFFP_HURST = "pffp.hurst";
	final public static String PFFP_BETA = "pffp.beta";
	final public static String PFFP_SM = "pffp.Sm";
	final public static String PFFP_MU = "pffp.mu";
	final public static String PFFP_PHI = "pffp.phi";
	final public static String PFFP_PSI = "pffp.psi";
	
	/* PFFP constants END */
	
	/* ENERGY SECTION BEGIN */
	
	/* Used to activate/deactivate the energy manager component. 
	 * - ON, turns on energy management
	 * - OFF, turns off energy management
	*/
	final public static String CONFIGURATION_ENERGY_COMPONENT = "energyManagement";
	
	/* Site specks */
	final public static String CONFIGURATION_SITE_CORES = "energy.site.cores";
	final public static String CONFIGURATION_SITE_ON_CORES = "energy.site.on.cores";
	final public static String CONFIGURATION_SITE_FAIR = "energy.site.fair";
			
	/* Cabinet specks */
	final public static String CONFIGURATION_CABINETS = "energy.cabinets";
	final public static String CONFIGURATION_CABINET_POWER_ON = "energy.cabinet.powerOn";
	final public static String CONFIGURATION_CABINET_POWER_STDBY = "energy.cabinet.powerStdby";
	final public static String CONFIGURATION_CABINET_ON_DELAY = "energy.cabinet.onDelay";
	final public static String CONFIGURATION_CABINET_OFF_DELAY = "energy.cabinet.offDelay";
	
	/* Board specks */
	final public static String CONFIGURATION_BOARDS = "energy.boards";
	final public static String CONFIGURATION_BOARD_POWER_ON = "energy.board.powerOn";
	final public static String CONFIGURATION_BOARD_POWER_STDBY = "energy.board.powerStdby";
	final public static String CONFIGURATION_BOARD_ON_DELAY = "energy.board.onDelay";
	final public static String CONFIGURATION_BOARD_OFF_DELAY = "energy.board.offDelay";
		
	/* Processor specks */
	final public static String CONFIGURATION_PROCESSORS = "energy.processors";
	final public static String CONFIGURATION_PROCESSOR_POWER_ON = "energy.processor.powerOn";
	final public static String CONFIGURATION_PROCESSOR_POWER_STDBY = "energy.processor.powerStdby";
	final public static String CONFIGURATION_PROCESSOR_ON_DELAY = "energy.processor.onDelay";
	final public static String CONFIGURATION_PROCESSOR_OFF_DELAY = "energy.processor.offDelay";
	final public static String CONFIGURATION_PROCESSOR_CORES = "energy.processor.cores";
	
	/* Cores specks */
	final public static String CONFIGURATION_CORE_POWER_ACTIVE = "energy.core.powerActive";
	final public static String CONFIGURATION_CORE_POWER_IDLE = "energy.core.powerIdle";
	final public static String CONFIGURATION_CORE_POWER_OFF = "energy.core.powerOff";
	final public static String CONFIGURATION_CORE_TIMEOUT = "energy.core.timeOut";

}
