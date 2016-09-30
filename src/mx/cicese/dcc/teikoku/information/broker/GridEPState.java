/*
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2013 by the
 *   CICESE Research Center and  
 *   CETYS University, Mexico
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

package mx.cicese.dcc.teikoku.information.broker;


import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;


/**
 * Contains the Grid state information
 *  
 *  @author <a href="mailto:ahiralesc@gmail.com">Adan Hirales Carbajal</a>
 *         	(last edited by $Author$)
 * 	@version $Version$, $Date$
 * 
 *  @category Information system
 * 
 */


public class GridEPState extends Entity implements EndPointData{
	
	
	/**
	 * The maximum number of allowed Grid jobs which can be managed by the Grid software waiting to be passed to the 
	 * underlying Computing Manager (LRMS), and hence are not yet candidates to start execution.
	 * 
	 */
	public long maxPreLRMSWaitingJobs;


	/**
	 * The maximum number of running jobs allowed by the machine
	 * 
	 */
	public long maxRunningJobs;


	/**
	 * The maximum number of jobs allowed by the machine
	 * 
	 */
	public long maxTotalJobs;


	/**
	 * The maximum number of waiting jobs. If the quota is exceeded, arriving  
	 * jobs are dropped
	 * 
	 */
	public long maxWaitingJobs;


	/**
	 * The number of Grid jobs which are currently managed by the Grid software layer waiting to
	 * be passed to the underlying Computing Manager (LRMS), and hence are not yet candidates to start execution.
	 * 
	 */
	public long preLRMSWaitingJobs;


	/**
	 * The number of jobs which are currently running in an Execution Environment
	 * 
	 */
	public long runningJobs;


	/**
	 * The of the Grid machines
	 * 
	 */
	public int size;
	
	
	/**
	 * The total number of jobs currently waiting pre- or post-execution for files 
	 * to be staged which were submitted via this machine.
	 */
	public long stagingJobs;


	/**
	 * The state of the machines
	 * 
	 */
	public Map<UUID,Boolean> state;


	/**
	 * The number of jobs which have started their execution, but are currently suspended
	 * (e.g., having been preempted by another job).
	 * 
	 */
	public long suspendedJobs;


	/**
	 * The total number of jobs in the machine. Total jobs is the sum
	 * of running, waiting and and preLRMSW waiting jobs
	 * 
	 */
	public long totalJobs;
	
	
	/**
	 * The number of jobs which are currently waiting to start execution. Usually these will be
	 * queued in the underlying Computing Manager.
	 * 
	 */
	public long waitingJobs;


	/**
	 * Class constructor
	 */
	GridEPState(EntityType type){
		super(type);
		
		this.validity = null;
		this.size = 0;
		this.state = new Hashtable<UUID,Boolean>();
		this.maxTotalJobs = 0;
		this.maxRunningJobs = 0;
		this.maxWaitingJobs = 0;
		this.maxPreLRMSWaitingJobs = 0;
		this.totalJobs = 0;
		this.runningJobs = 0;
		this.stagingJobs = 0;
		this.waitingJobs = 0;
		this.suspendedJobs = 0;
		this.preLRMSWaitingJobs = 0;
	}

}
