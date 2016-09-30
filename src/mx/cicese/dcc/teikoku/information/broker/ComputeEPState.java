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


/**
 * Contains a single machine (site) state information
 * 
 * @author <a href="mailto:ahirales@cicese.edu.mx">Adan Hirales Carbajal</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 *  @category Information system
 */

public class ComputeEPState extends Entity 
	implements EndPointData {
	
	/**
	 * The machine clock rate
	 */
	public float clockRate;

	
	/**
	 * An estimate of the average time flow time 	
	 *
	 *The flow time is defined as:
	 *	c_j - r_j
	 *  
	 *  with,
	 *  c_j, job j completion time
	 *  r_j, job j release time 
	 *  
	 */
	public double estAvgFlowTime;
	
	
	/**
	 * An estimate of the average processing time 	
	 *
	 * The processing time of a job is defined as p_j
	 *
	 */
	public double estAvgProcTime;
		

	/**
	 * An estimate of the average time a job will wait after submission until it starts to execute. The
	 * value SHOULD be reported as 0 if there are free slots and a new job will start immediately,
	 * even if it takes some finite time for a job to be started.	
	 *
	 *The average waiting time, defined as the sum 
	 *  c_j - p_j - r_j
	 *  
	 *  with,
	 *  c_j, job j completion time
	 *  p_j, job j processing time
	 *  r_j, job j release time 
	 *  
	 */
	public double estAvgWaitingTime;
	
	
	/**
	 * The number of slots which are currently unoccupied by jobs and are free for new jobs 
	 * in this Share to start immediately.
	 */
	public int freeSlots;


	/**
	 * A state representing the health of the endpoint in terms of its 
	 * capability of properly delivering the functionalities
	 * 
	 * 	 1, indicates the endpoint is processing jobs and it is capable of accepting new jobs
	 *   0, indicates the endpoint is no longer accepting new jobs. It will complete the execution 
	 *      of previously accepted jobs
	 *      
	 */
	public boolean healthState;


	/**
	 * The maximum allowed number of jobs that are in the Grid layer waiting to be passed to
	 * the underlying computing manager (i.e.,LRMS) for this machine (or share if the machine 
	 * is partitioned)
	 * 
	 */
	public int maxPreLRMSWaitingJobs;


	/**
	 * The maximum allowed number of jobs in the running state in this for this machine (or share if the machine 
	 * is partitioned)
	 * 	 
	 */
	public int maxRunningJobs;


	/**
	 * The maximum allowed number of jobs in this machine (or share if the machine 
	 * is partitioned)
	 * 
	 */
	public int maxTotalJobs;
	
	
	/**
	 * The maximum allowed number of jobs in the waiting state in thismachine (or share if the machine 
	 * is partitioned)
	 * 
	 */
	public int maxWaitingJobs;
	
	
	/**
	 * The length of longest executing job. 
	 * A value of zero is returned if no jobs are executing.
	 * A value grater than zero is returned, if
	 * - One or more jobs are running are running but have not completed. The job with 
	 *   the longest running time among the running job set is selected 
	 */
	public long maxLengthExecJob;
	
	
	
	/**
	 * The number of jobs which are currently running in an Execution Environment, but that were
	 * submitted via the machine local submission system
	 * 
	 */
	public int localRunningJobs;


	/**
	 * The number of jobs which have started their execution, but that were
	 * submitted via the machine local submission system..
	 * 
	 */
	public int localSuspendenJobs;


	/**
	 * The number of jobs which are currently waiting to start execution, but that were
	 * submitted via the machine local submission system.
	 * 
	 */
	public int localWaitingJobs;


	/**
	 * The longest executing job
	 * 
	 */
	public long p_max;


	/**
	 * The number of Grid jobs which are currently managed by the Grid software layer waiting to
	 * be passed to the underlying Computing Manager (LRMS), and hence are not yet candidates to start execution.
	 * 
	 */
	public int preLRMSWaitingJobs;


	/**
	 * The number of jobs which are currently running in an Execution Environment
	 * 
	 */
	public int runningJobs;
	
	
	/**
	 * The size of the machine
	 * 
	 */
	public int size;
	
	
	/**
	 * The sum of job flow times
	 */
	public long sumFlowTimes;
	
	
	/**
	 * The sum of job processing times
	 */
	public long sumProcTimes;
	
	
	/**
	 * The sum of job waiting times
	 */
	public long sumWaitingTimes;
	
	
	/**
	 * The total number of jobs currently waiting pre- or post-execution for files 
	 * to be staged which were submitted via this machine.
	 */
	public int stagingJobs;
	

	/**
	 * The number of jobs which have started their execution, but are currently suspended
	 * (e.g., having been preempted by another job).
	 * 
	 */
	public int suspendedJobs;


	/**
	 * The total number of jobs in the machine. Total jobs is the sum
	 * of running, waiting and and preLRMSW waiting jobs
	 * 
	 */
	public int totalJobs;
	
	
	/**
	 * The total number of jobs executed by the site.
	 * 
	 * Jobs that are counted include:
	 * - Jobs that complete execution
	 * - Jobs that abort execution 
	 */
	public long totalNumberExecutedJobs;
	
	
	/**
	 * The total number of used resources
	 * 
	 */
	public int usedSlots;
		

	/**
	 * The number of jobs which are currently waiting to start execution. Usually these will be
	 * queued in the underlying Computing Manager.
	 * 
	 */
	public int waitingJobs;
	
	
	/**
	 * The energy consumption of the machine
	 * 
	 */
	public double coreEnergyConsumption;
	
	
	/**
	 * The energy efficiency of the machine
	 */
	public double energyConsumpion;
	
	
	
	public ComputeEPState() {
		super(EntityType.STATUS);
		
		this.clockRate = 0;
		this.estAvgFlowTime = 0;
		this.estAvgProcTime = 0;
		this.estAvgWaitingTime = 0;
		this.freeSlots = 0;
		this.healthState = true;
		this.maxPreLRMSWaitingJobs = 0;
		this.maxRunningJobs = 0;
		this.maxTotalJobs = 0;
		this.maxWaitingJobs = 0;
		this.localRunningJobs = 0;
		this.localSuspendenJobs = 0;
		this.localWaitingJobs = 0;
		this.p_max = 0;
		this.preLRMSWaitingJobs = 0;
		this.runningJobs = 0;
		this.size = 0;
		this.stagingJobs = 0;
		this.suspendedJobs = 0;
		this.totalJobs = 0;
		this.totalNumberExecutedJobs = 0;
		this.usedSlots = 0;
		this.waitingJobs = 0;
		this.coreEnergyConsumption = 0;
		this.energyConsumpion = 0;
		this.maxLengthExecJob = 0;
	}
	
}
