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
package de.irf.it.rmg.core.teikoku.job;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Occupation;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;

import mx.cicese.dcc.teikoku.workload.job.JobType;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
abstract public class Job extends Occupation {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(Job.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	private UUID uuid;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private String name;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private Site releasedSite = null;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private RuntimeInformation runtimeInformation;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Lifecycle lifecycle;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Provenance provenance;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Instant releaseTime;
	
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private Instant completionTime;
	

	/**
	 * TODO: not yet commented
	 * 
	 */
	private JobType jobType;
	
	/**
	 * The job priority
	 */
	private long priority;
	
	
	/**
	 * The job fragmentation rate. Computed as the sum of distances between adjacent cores. 
	 * Cores are adjacent if their id's are continuous. Adjacency is computed as core[k+1].id - core[k].id.
	 * If all cores are adjacent and n cores are allocated to the job, then the sum results in (n-1). 
	 * Fragmentation is subsequently computed as, fragmentation = (n-1)/(total number of cores allocated to the job - 1)
	 *
	 * A ratio of 1 indicates no fragmentation. A value greater than 1 indicates fragmentation.
	 */
	private float fragmentation;
	
	
	/**
	 * The ration of allocated idle cores.
	 */
	private float ratioAllocatedIdleCores;
	
	
	/**
	 * Average idle core reallocation length 
	 */
	private float avrgReallocLength; 
	

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public Job() {
		this.uuid = UUID.randomUUID();
		this.name = this.uuid.toString();
		this.runtimeInformation = new RuntimeInformationImpl();
		this.releaseTime = null;
		this.lifecycle = new Lifecycle();
		this.provenance = new Provenance();
		this.priority = 0;
		this.fragmentation = 0;
		this.ratioAllocatedIdleCores = 0;
		this.avrgReallocLength = 0;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param name
	 */
	public Job(String name) {
		this();
		this.name = name;		
	}

	/**
	 * Getter method for the "uuid" field of this type.
	 * 
	 * @return Returns the current contents of "this.uuid".
	 */
	final public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Getter method for the "name" field of this type.
	 * 
	 * @return Returns the current contents of "this.name".
	 */
	public String getName() {
		return this.name;
	}
	

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	public RuntimeInformation getRuntimeInformation() {
		return this.runtimeInformation;
	}

	/**
	 * Getter method for the "lifecycle" field of this type.
	 * 
	 * @return Returns the current contents of "this.lifecycle".
	 */
	final public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	/**
	 * Getter method for the "provenance" field of this type.
	 * 
	 * @return Returns the current contents of "this.provenance".
	 */
	final public Provenance getProvenance() {
		return this.provenance;
	}

	/**
	 * Getter method for the "description" field of this type.
	 * 
	 * @return Returns the current contents of "this.description".
	 */
	abstract public Description getDescription();

	/**
	 * This method determines the release time of the actual job using the
	 * information from the job's lifecycle.
	 * 
	 * @return Instant, the instant at whicht the job war released.
	 */
	public Instant getReleaseTime() {
		Instant result = null;

		/*
		 * If the member variable is already set, there will be no more change
		 * to it. Thus, just return the member variable's value.
		 */
		if (this.releaseTime != null) {
			result = this.releaseTime;
		}// if
		else {
			/*
			 * Otherwise, determine the release time from the lifecycle of the
			 * job.
			 */
			if (this.lifecycle != null) {
				Period[] p = this.lifecycle.findPeriodsFor(State.RELEASED);
				if (p != null && p.length > 0) {
					result = p[0].getAdvent();
					this.releaseTime = result;
				}// if
			}// if
		}// else
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>,
	 *         <a href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>,
	 *         and <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
	 *         Papaspyrou</a> (last edited by $Author$)
	 * @version $Version$, $Date$
	 * 
	 */
	private class RuntimeInformationImpl
			implements RuntimeInformation {

		private Instant startTime;

		private Instant releaseTime;
		
		
		private Instant retrieveStartTime() {
			Instant result = null;
			if (this.startTime != null) {
				result = this.startTime;
			}// if
			else {
				if (lifecycle != null) {

					Period[] startPeriods = lifecycle
							.findPeriodsFor(State.STARTED);

					if (startPeriods != null && startPeriods.length > 0) {
						result = startPeriods[0].getAdvent();
						this.startTime = result;
					}// if
				}// if
			}// else
			return result;

		}

		private Instant retrieveReleaseTime() {
			Instant result = null;
			if (this.releaseTime != null) {
				result = this.releaseTime;
			}// if
			else {
				if (lifecycle != null) {

					Period[] releasePeriods = lifecycle
							.findPeriodsFor(State.RELEASED);

					if (releasePeriods != null && releasePeriods.length > 0) {
						result = releasePeriods[0].getAdvent();
						this.releaseTime = result;
					}// if
				}// if
			}// else
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.RuntimeInformation#getWaitTime()
		 */
		public Distance getWaitTime() {
			Distance result = null;

			if (retrieveStartTime() != null) {
				if (retrieveReleaseTime() != null) {
					/*
					 * Job has been released and started. So calculation of wait
					 * time is the difference of both values.
					 */
					result = TimeHelper.distance(this.releaseTime,
							this.startTime);
				}// if
				else {
					/*
					 * Job has not been started but is already released.
					 * Calculate the wait time upto now.
					 */
					Instant now = Clock.instance().now();
					result = TimeHelper.distance(this.releaseTime, now);
				}
			}// if
			else{
				result = TimeFactory.newFinite(0); 
			}

			return result;
		}

		
		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.RuntimeInformation#getElapsedWaitTime()
		 */
		public Distance getElapsedWaitTime() {
			Distance result = null;

			if (retrieveReleaseTime() != null) {
				Instant now = Clock.instance().now();
				
				if (retrieveStartTime() != null){
					if (now.after(this.startTime)){
						/*
						 * the current time is after the start time. Thus the job has already been started and the
						 * elapsed wait time corresponds to the total wait time. 
						 */
						result = TimeHelper.distance(this.releaseTime, this.startTime);
					}//if
					else{
						if (now.before(this.releaseTime)){
							/*
							 * the current time is before the release time. Thus the job has not waited yet.
							 */
							result = TimeFactory.newFinite(0);
						}//if
						else{
							/*
							 * calculate the elapsed wait time upto the current time.
							 */
							result = TimeHelper.distance(this.releaseTime, now);
						}//else
					}//else
				}//if
				else{
					if (now.before(this.releaseTime)){
						/*
						 * the current time is before the release time. Thus the job has not waited yet.
						 */
						result = TimeFactory.newFinite(0);
					}//if
					else{
						/*
						 * if no start time is given, calculate depending on the current time 
						 */
						result = TimeHelper.distance(this.releaseTime, now);
					}
				}//else
			}//if
			else{
				result = TimeFactory.newFinite(0);
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.RuntimeInformation#getRemainingWaitTime()
		 */
		public Distance getRemainingWaitTime() {
			
			Distance totalWaitTime = getWaitTime();
			Distance elapsedWaitTime = getElapsedWaitTime();
			
			return TimeHelper.subtract(totalWaitTime, elapsedWaitTime);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.RuntimeInformation#getRunTime()
		 */
		public Distance getRunTime() {			
			Distance result;
			
//			Period duration = getDuration();
//			if (duration != null) {
//				result = duration.distance();
//			}// if
//			else{
//				result = TimeFactory.newFinite(0);
//			}
			
			result = getDescription().getEstimatedRuntime();

			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.RuntimeInformation#getElapsedRunTime()
		 */
		public Distance getElapsedRunTime() {
			Distance result;

			Period duration = getDuration();
			
			if (duration != null) {
				Instant now = Clock.instance().now();
				if (now.before(duration.getAdvent())) {
					/* 
					 * job has not been started yet, thus runtime is 0.
					 */
					result = TimeFactory.newFinite(0);
				}// if
				else {
					if (now.after(duration.getCessation())) {
						/*
						 * job has already finished processing, thus the elapsed runtime corresponds to the total runtime. 
						 */
						result = getRunTime();
					}// if
					else {
						/*
						 * job is still running.
						 */ 
						result = TimeHelper.distance(now, duration.getAdvent());
					}// else
				}// else
			}// if
			else{
				result = TimeFactory.newFinite(0);
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.RuntimeInformation#getRemainingRunTime()
		 */
		public Distance getRemainingRunTime() {
			
			Distance totalRuntime = getRunTime();
			Distance elapsedRuntime = getElapsedRunTime();
			Distance d = TimeHelper.subtract(totalRuntime, elapsedRuntime);
			return d;
		}
	}

	public float getIdleCoreReallocLength() {
		return this.avrgReallocLength;
	}
	
	public float getRatioIdleCores() {
		return this.ratioAllocatedIdleCores;
	}
	
	public Site getReleasedSite() {
		return this.releasedSite;
	}

	
	public void setReleasedSite(Site releasedSiteName) {
		this.releasedSite = releasedSiteName;
	}
	
	public Instant getCompletionTime() {
		return this.completionTime;
	}
	
	public void setFragmentation(float fragmentation) {
		this.fragmentation = fragmentation;
	}

	public void setIdleCoreReallocLength(float length){
		this.avrgReallocLength = length;
	}
	
	public void setcompletionTime(Instant completionTime) {
		this.completionTime = completionTime;
	}
	
	
	public float getFragmentation() {
		return this.fragmentation;
	}
	
	/**
	 * This method gets this job type.
	 * 
	 */
	public JobType getJobType() {
		return this.jobType;
	}
	
	/**
	 * This method sets the job type.
	 * 
	 */
	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}
	
	/**
	 * This method sets the job type.
	 * 
	 */
	public void setJobType(String jobType) {
	
		if(jobType.equals(JobType.INDEPENDENT.toString()))
				this.jobType = JobType.INDEPENDENT;
		if(jobType.equals(JobType.DG.toString()))
				this.jobType = JobType.DG;
		if(jobType.equals(JobType.DAG.toString())) 
			this.jobType = JobType.DAG;
		if(jobType.equals(JobType.CHAIN.toString())) 
			this.jobType = JobType.CHAIN;
		if(jobType.equals(JobType.TREE.toString())) 
			this.jobType = JobType.TREE;
	}

	
	/**
	 * Sets the job priority
	 */
	public void setPriority(long priority) {
		this.priority = priority;
	}
	
	public void setRatioAllocatedCores(float ratioAllocatedIdleCores) {
		this.ratioAllocatedIdleCores = ratioAllocatedIdleCores;
	}
	
	/**
	 * Gets the job priority
	 */
	public long getPriority() {
		return this.priority;
	}

}
