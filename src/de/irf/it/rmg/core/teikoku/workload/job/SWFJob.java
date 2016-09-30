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
package de.irf.it.rmg.core.teikoku.workload.job;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.irf.it.rmg.core.teikoku.job.Description;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.TimeFactory;

import java.util.Vector;
import java.util.UUID;

import mx.cicese.dcc.teikoku.workload.job.MemberState;
import mx.cicese.dcc.teikoku.workload.job.Vertex;

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
final public class SWFJob extends Job implements Vertex{

	/**
	 * The header of the workload this job is originating from.
	 * 
	 */
	final private SWFHeader swfHeader;

	/**
	 * The description data for this job.
	 * 
	 */
	final private Description swfDescription = new SWFDescription();

	/**
	 * The job number of this job, starting from <code>1</code>.
	 * 
	 */
	private long jobNumber;

	/**
	 * The submission time of this job, in seconds.
	 * 
	 * The earliest time the log refers to is zero, and is the submittal time
	 * the of the first job. The lines in the log are sorted by ascending
	 * submittal times. In makes sense for jobs to also be numbered in this
	 * order.
	 * 
	 */
	private long submitTime;
	
	/**
	 * The wait time of this job, in seconds.
	 * 
	 * Denotes the difference between the job's submit time and the time at
	 * which it actually began to run.
	 * 
	 */
	private long waitTime;

	/**
	 * The run time of this job, in seconds.
	 * 
	 * The wall clock time the job was running (end time minus start time). We
	 * decided to use "wait time" and "run time" instead of the equivalent
	 * "start time" and "end time" because they are directly attributable to the
	 * scheduler and application, and are more suitable for models where only
	 * the run time is relevant.
	 * 
	 * Note that when values are rounded to an integral number of seconds (as
	 * often happens in logs) a run time of 0 is possible and means the job ran
	 * for less than 0.5 seconds.
	 * 
	 */
	private long runTime;
	
	
	
	/**
	 * Since the run time might get modified during the simulation. This variable 
	 * stores the run time from read from the log. 
	 */
	private long originalRunTime;


	/**
	 * The number of allocated processors of this job.
	 * 
	 * In most cases this is also the number of processors the job uses; if the
	 * job does not use all of them, we typically don't know about it.
	 * 
	 */
	private int numberOfAllocatedProcessors;

	/**
	 * The average CPU time used of this job, both user and system, in seconds.
	 * 
	 * This is the average over all processors of the CPU time used, and may
	 * therefore be smaller than the wall clock runtime. If a log contains the
	 * total CPU time used by all the processors, it is divided by the number of
	 * allocated processors to derive the average.
	 * 
	 */
	private double averageCPUTimeUsed;

	/**
	 * The used memory of this job, in kilobytes.
	 * 
	 * This value is the average per processor.
	 * 
	 */
	private int usedMemory;

	/**
	 * The requested number of processors of this job.
	 * 
	 */
	private int requestedNumberOfProcessors;

	/**
	 * The requested time of this job.
	 * 
	 * This can be either runtime (measured in wallclock seconds), or average
	 * CPU time per processor (also in seconds) -- the exact meaning is
	 * determined by a header comment.
	 * 
	 * In many logs this field is used for the user runtime estimate (or upper
	 * bound) used in backfilling. If a log contains a request for total CPU
	 * time, it is divided by the number of requested processors.
	 * 
	 * @see SWFHeader#getNote(int)
	 * 
	 */
	private double requestedTime;

	/**
	 * The requested memory of this job, in kilobytes.
	 * 
	 * This value is the average per processor.
	 * 
	 */
	private int requestedMemory;

	/**
	 * The status of this job, after finishing.
	 * 
	 * For jobs that cannot be preempted or checkpointed, the following values
	 * are allowed:
	 * 
	 * <ul>
	 * <li><code>0</code>, if the job failed (e.g. segmentation fault);</li>
	 * <li><code>1</code>, if the job completed;</li>
	 * <li><code>5</code>, if the job aborted (e.g. user cancellation);</li>
	 * </ul>
	 * 
	 * For preemptible or checkpointable jobs, the following additional values
	 * are possible:
	 * 
	 * <ul>
	 * <li><code>2</code>, if this is a partial execution and will continue;</li>
	 * <li><code>3</code>, if this is the last partial execution and the job
	 * completed;</li>
	 * <li><code>4</code>, if this is the last partial execution and the job
	 * failed;</li>
	 * </ul>
	 * 
	 */
	private byte status;

	/**
	 * The user ID of this job.
	 * 
	 * This value is between <code>1</code> and the number of different users.
	 * 
	 */
	private short userID;

	/**
	 * The group ID of this job.
	 * 
	 * This value is between <code>1</code> and the number of different
	 * groups.
	 * 
	 */
	private short groupID;

	/**
	 * The executable application's number of this job.
	 * 
	 * This value is between one and the number of different applications
	 * appearing in the workload.
	 * 
	 * In some logs, this might represent a script file used to run jobs rather
	 * than the executable directly; this is noted in a header comment.
	 * 
	 */
	private long executableApplicationNumber;

	/**
	 * The queue number of this job.
	 * 
	 * This value is between <code>1</code> and the number of different queues
	 * in the system. The nature of the system's queues should be explained in a
	 * header comment.
	 * 
	 * This field is where batch and interactive jobs should be differentiated:
	 * a value of <code>0</code> denotes interactive jobs.
	 * 
	 * @see SWFHeader#getQueue(int)
	 * 
	 */
	private byte queueNumber;

	/**
	 * The partition number of this job.
	 * 
	 * This value is between <code>1</code> and the number of different
	 * partitions in the systems. The nature of the system's partitions should
	 * be explained in a header comment.
	 * 
	 * @see SWFHeader#getPartition(int)
	 * 
	 */
	private byte partitionNumber;

	/**
	 * The number of the preceding job of this job.
	 * 
	 * It denotes a previous job in the workload, such that the current job can
	 * only start after the termination of this preceding job. Together with the
	 * {@link #thinkTimeFromPrecedingJob} field, this allows the workload to
	 * include feedback as described above.
	 * 
	 */
	private long precedingJobNumber;

	/**
	 * The think time from the preceding job of this job.
	 * 
	 * It denotes the number of seconds that should elapse between the
	 * termination of the preceding job and the submittal of this one.
	 * 
	 */
	private double thinkTimeFromPrecedingJob;

	/**
	 * The job id of the composite job this job is a member of.
	 * 
	 * If the job is independent then the default value is null.
	 *  
	 */
	private int compositeJobId;
	
	/**
	 * The job id that acts as a container for member jobs
	 * 
	 */
	private UUID jobContainerId;
	
	/**
	 * The predecessors of this job.
	 * 
	 * This field is used to store job identifiers when parsing a workload file
	 *  
	 */
	private Vector<Number> predecessors;
	
	/**
	 * The sucessors of this job.
	 * 
	 * This field is used to store job identifiers when parsing a workload file
	 *  
	 */
	private Vector<Number> successors;
	
	/**
	 * The sucessors of this job.
	 * 
	 * This field is used to store job identifiers when parsing a workload file
	 *  
	 */
	private MemberState memberState; 
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public SWFJob(SWFHeader swfHeader) {
		super();
		this.swfHeader = swfHeader;
		this.memberState= MemberState.NOT_EXPLORED;
		this.predecessors = new Vector<Number>();
		this.successors = new Vector<Number>();
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param swfHeader
	 *            A reference to the header of the workload this job is
	 *            originating from.
	 */
	public SWFJob(String name, SWFHeader swfHeader) {
		super(name);
		this.swfHeader = swfHeader;
		this.memberState= MemberState.NOT_EXPLORED;
		this.predecessors = new Vector<Number>();
		this.successors = new Vector<Number>();
	}

	/**
	 * Returns the header of the workload this job is originating from.
	 * 
	 * @return The header of the workload this job is originating from.
	 */
	public SWFHeader getSWFHeader() {
		return this.swfHeader;
	}

	/**
	 * Returns the job number of this job.
	 * 
	 * @return The job number of this job.
	 */
	public long getJobNumber() {
		// TODO: not yet implemented
		return this.jobNumber;
	}

	/**
	 * Sets the job number of this job.
	 * 
	 * @param jobNumber
	 *            The job number of this job. This smallest value within a
	 *            single workload must start at <code>1</code>.
	 * 
	 * @see #getJobNumber()
	 */
	public void setJobNumber(long jobNumber) {
		// TODO: not yet implemented
		this.jobNumber = jobNumber;
	}

	/**
	 * Returns the submission time of this job, in seconds.
	 * 
	 * The earliest time the log refers to is zero, and is the submittal time
	 * the of the first job. The lines in the log are sorted by ascending
	 * submittal times. In makes sense for jobs to also be numbered in this
	 * order.
	 * 
	 * @return The submission time of this job, in seconds.
	 */
	public long getSubmitTime() {
		// TODO: not yet implemented
		return this.submitTime;
	}

	/**
	 * Sets the submission time of this job, in seconds.
	 * 
	 * @param submitTime
	 *            The submission time of this job, in seconds.
	 * 
	 * @see #getSubmitTime()
	 */
	public void setSubmitTime(long submitTime) {
		// TODO: not yet implemented
		this.submitTime = submitTime;
	}

	/**
	 * Returns the wait time of this job, in seconds.
	 * 
	 * Denotes the difference between the job's submit time and the time at
	 * which it actually began to run.
	 * 
	 * @return The wait time of this job, in seconds.
	 */
	public long getWaitTime() {
		// TODO: not yet implemented
		return this.waitTime;
	}

	/**
	 * Sets the wait time of this job, in seconds.
	 * 
	 * @param waitTime
	 *            The wait time of this job, in seconds.
	 * 
	 * @see #getWaitTime()
	 */
	public void setWaitTime(long waitTime) {
		// TODO: not yet implemented
		this.waitTime = waitTime;
	}

	/**
	 * Returns the run time of this job, in seconds.
	 * 
	 * The wall clock time the job was running (end time minus start time). We
	 * decided to use "wait time" and "run time" instead of the equivalent
	 * "start time" and "end time" because they are directly attributable to the
	 * scheduler and application, and are more suitable for models where only
	 * the run time is relevant.
	 * 
	 * Note that when values are rounded to an integral number of seconds (as
	 * often happens in logs) a run time of 0 is possible and means the job ran
	 * for less than 0.5 seconds.
	 * 
	 * @return The run time of this job, in seconds.
	 */
	public long getRunTime() {
		// TODO: not yet implemented
		return this.runTime;
	}

	/**
	 * Sets the run time of this job, in seconds.
	 * 
	 * @param runTime
	 *            The run time of this job, in seconds.
	 * 
	 * @see #getRunTime()
	 */
	public void setRunTime(long runTime) {
		// TODO: not yet implemented
		this.runTime = runTime;
	}
	
	
	
	/**
	 * Sets the run time of this job, in seconds.
	 * The runtime is not modified during simulation.
	 *  
	 * @param runTime
	 *            The run time of this job, in seconds.
	 * 
	 * @see #getOriginalRunTime()
	 */
	public void setOriginalRunTime(long rt) {
		this.originalRunTime = rt;
	}
	
	
	/**
	 * Gets the run time of this job, in seconds.
	 * The runtime is not modified during simulation.
	 *  
	 * @param runTime
	 *            The run time of this job, in seconds.
	 * 
	 * @see #setOriginalRunTime()
	 */
	public long getOriginalRunTime() {
		return this.originalRunTime;
	}

	
	/**
	 * Returns the number of allocated processors of this job.
	 * 
	 * In most cases this is also the number of processors the job uses; if the
	 * job does not use all of them, we typically don't know about it.
	 * 
	 * @return The number of allocated processors of this job.
	 */
	public int getNumberOfAllocatedProcessors() {
		// TODO: not yet implemented
		return this.numberOfAllocatedProcessors;
	}

	/**
	 * Sets the number of allocated processors of this job.
	 * 
	 * @param numberOfAllocatedProcessors
	 *            The number of allocated processors of this job.
	 * 
	 * @see #getNumberOfAllocatedProcessors()
	 */
	public void setNumberOfAllocatedProcessors(int numberOfAllocatedProcessors) {
		// TODO: not yet implemented
		this.numberOfAllocatedProcessors = numberOfAllocatedProcessors;
	}

	/**
	 * Returns the average CPU time used of this job, both user and system, in
	 * seconds.
	 * 
	 * This is the average over all processors of the CPU time used, and may
	 * therefore be smaller than the wall clock runtime. If a log contains the
	 * total CPU time used by all the processors, it is divided by the number of
	 * allocated processors to derive the average.
	 * 
	 * @return The average CPU time used of this job, both user and system, in
	 *         seconds.
	 */
	public double getAverageCPUTimeUsed() {
		// TODO: not yet implemented
		return this.averageCPUTimeUsed;
	}

	/**
	 * Sets the average CPU time used of this job, both user and system, in
	 * seconds.
	 * 
	 * @param averageCPUTimeUsed
	 *            The average CPU time used of this job, both user and system,
	 *            in seconds.
	 * 
	 * @see #getAverageCPUTimeUsed()
	 */
	public void setAverageCPUTimeUsed(double averageCPUTimeUsed) {
		// TODO: not yet implemented
		this.averageCPUTimeUsed = averageCPUTimeUsed;
	}

	/**
	 * Returns the used memory of this job, in kilobytes.
	 * 
	 * This value is the average per processor.
	 * 
	 * @return The used memory of this job, in kilobytes.
	 */
	public int getUsedMemory() {
		// TODO: not yet implemented
		return this.usedMemory;
	}

	/**
	 * Sets the used memory of this job, in kilobytes.
	 * 
	 * @param usedMemory
	 *            The used memory of this job, in kilobytes.
	 * 
	 * @see #getUsedMemory()
	 */
	public void setUsedMemory(int usedMemory) {
		// TODO: not yet implemented
		this.usedMemory = usedMemory;
	}

	/**
	 * Returns the requested number of processors of this job.
	 * 
	 * @return The requested number of processors of this job.
	 */
	public int getRequestedNumberOfProcessors() {
		// TODO: not yet implemented
		return this.requestedNumberOfProcessors;
	}

	/**
	 * Sets the requested number of processors of this job.
	 * 
	 * @param requestedNumberOfProcessors
	 *            The requested number of processors of this job.
	 * 
	 * @see #getRequestedNumberOfProcessors()
	 */
	public void setRequestedNumberOfProcessors(int requestedNumberOfProcessors) {
		// TODO: not yet implemented
		this.requestedNumberOfProcessors = requestedNumberOfProcessors;
	}

	/**
	 * Returns the requested time of this job.
	 * 
	 * This can be either runtime (measured in wallclock seconds), or average
	 * CPU time per processor (also in seconds) -- the exact meaning is
	 * determined by a header comment.
	 * 
	 * In many logs this field is used for the user runtime estimate (or upper
	 * bound) used in backfilling. If a log contains a request for total CPU
	 * time, it is divided by the number of requested processors.
	 * 
	 * @return The requested time of this job.
	 */
	public double getRequestedTime() {
		// TODO: not yet implemented
		return this.requestedTime;
	}

	/**
	 * Sets the requested time of this job.
	 * 
	 * @param requestedTime
	 *            The requested time of this job.
	 * 
	 * @see #getRequestedTime()
	 */
	public void setRequestedTime(double requestedTime) {
		// TODO: not yet implemented
		this.requestedTime = requestedTime;
	}

	/**
	 * Returns the requested memory of this job, in kilobytes.
	 * 
	 * This value is the average per processor.
	 * 
	 * @return The requested memory of this job, in kilobytes.
	 */
	public int getRequestedMemory() {
		// TODO: not yet implemented
		return this.requestedMemory;
	}

	/**
	 * Sets the requested memory of this job, in kilobytes.
	 * 
	 * @param requestedMemory
	 *            The requested memory of this job, in kilobytes.
	 * 
	 * @see #getRequestedMemory()
	 */
	public void setRequestedMemory(int requestedMemory) {
		// TODO: not yet implemented
		this.requestedMemory = requestedMemory;
	}

	/**
	 * Returns the status of this job, after finishing.
	 * 
	 * For jobs that cannot be preempted or checkpointed, the following values
	 * are allowed:
	 * 
	 * <ul>
	 * <li><code>0</code>, if the job failed (e.g. segmentation fault);</li>
	 * <li><code>1</code>, if the job completed;</li>
	 * <li><code>5</code>, if the job aborted (e.g. user cancellation);</li>
	 * </ul>
	 * 
	 * For preemptible or checkpointable jobs, the following additional values
	 * are possible:
	 * 
	 * <ul>
	 * <li><code>2</code>, if this is a partial execution and will continue;</li>
	 * <li><code>3</code>, if this is the last partial execution and the job
	 * completed;</li>
	 * <li><code>4</code>, if this is the last partial execution and the job
	 * failed;</li>
	 * </ul>
	 * 
	 * 1 if the job was completed, 0 if it failed, and 5 if cancelled. If
	 * information about chekcpointing or swapping is included, other values are
	 * also possible. See usage note below. This field is meaningless for
	 * models, so would be -1.
	 * 
	 * @return The status of this job, after finishing.
	 */
	public byte getStatus() {
		// TODO: not yet implemented
		return this.status;
	}

	/**
	 * Sets the status of this job, after finishing.
	 * 
	 * @param status
	 *            The status of this job, after finishing.
	 * 
	 * @see #getStatus()
	 */
	public void setStatus(byte status) {
		// TODO: not yet implemented
		this.status = status;
	}

	/**
	 * Returns the user ID of this job.
	 * 
	 * This value is between <code>1</code> and the number of different users.
	 * 
	 * @return The user ID of this job.
	 */
	public short getUserID() {
		// TODO: not yet implemented
		return this.userID;
	}

	/**
	 * Sets the user ID of this job.
	 * 
	 * @param userID
	 *            The user ID of this job.
	 * 
	 * @see #setUserID(short)
	 */
	public void setUserID(short userID) {
		// TODO: not yet implemented
		this.userID = userID;
	}

	/**
	 * Returns the group ID of this job.
	 * 
	 * This value is between <code>1</code> and the number of different
	 * groups.
	 * 
	 * @return The group ID of this job.
	 */
	public short getGroupID() {
		// TODO: not yet implemented
		return this.groupID;
	}

	/**
	 * Sets the group ID of this job.
	 * 
	 * @param groupID
	 *            The group ID of this job.
	 * 
	 * @see #getGroupID()
	 */
	public void setGroupID(short groupID) {
		// TODO: not yet implemented
		this.groupID = groupID;
	}

	/**
	 * Returns the executable application's number of this job.
	 * 
	 * This value is between one and the number of different applications
	 * appearing in the workload.
	 * 
	 * In some logs, this might represent a script file used to run jobs rather
	 * than the executable directly; this is noted in a header comment.
	 * 
	 * @return The executable application's number of this job.
	 */
	public long getExecutableApplicationNumber() {
		// TODO: not yet implemented
		return this.executableApplicationNumber;
	}

	/**
	 * Sets the executable application's number of this job.
	 * 
	 * @param executableApplicationNumber
	 *            The executable application's number of this job.
	 * 
	 * @see #getExecutableApplicationNumber()
	 */
	public void setExecutableApplicationNumber(long executableApplicationNumber) {
		// TODO: not yet implemented
		this.executableApplicationNumber = executableApplicationNumber;
	}

	/**
	 * Returns the queue number of this job.
	 * 
	 * This value is between <code>1</code> and the number of different queues
	 * in the system. The nature of the system's queues should be explained in a
	 * header comment.
	 * 
	 * This field is where batch and interactive jobs should be differentiated:
	 * a value of <code>0</code> denotes interactive jobs.
	 * 
	 * @return The queue number of this job.
	 */
	public byte getQueueNumber() {
		// TODO: not yet implemented
		return this.queueNumber;
	}

	/**
	 * Sets the queue number of this job.
	 * 
	 * @param queueNumber
	 *            The queue number of this job.
	 * 
	 * @see #getQueueNumber()
	 */
	public void setQueueNumber(byte queueNumber) {
		// TODO: not yet implemented
		this.queueNumber = queueNumber;
	}

	/**
	 * The partition number of this job.
	 * 
	 * This value is between <code>1</code> and the number of different
	 * partitions in the systems. The nature of the system's partitions should
	 * be explained in a header comment.
	 * 
	 * @return The partition number of this job.
	 */
	public byte getPartitionNumber() {
		// TODO: not yet implemented
		return this.partitionNumber;
	}

	/**
	 * Sets he partition number of this job.
	 * 
	 * @param partitionNumber
	 *            The partition number of this job.
	 * 
	 * @see #getPartitionNumber()
	 */
	public void setPartitionNumber(byte partitionNumber) {
		// TODO: not yet implemented
		this.partitionNumber = partitionNumber;
	}

	/**
	 * Returns the number of the preceding job of this job.
	 * 
	 * It denotes a previous job in the workload, such that the current job can
	 * only start after the termination of this preceding job. Together with the
	 * next field, this allows the workload to include feedback as described
	 * above.
	 * 
	 * @return The number of the preceding job of this job.
	 */
	public long getPrecedingJobNumber() {
		// TODO: not yet implemented
		return this.precedingJobNumber;
	}

	/**
	 * Sets the number of the preceding job of this job.
	 * 
	 * @param precedingJobNumber
	 *            The number of the preceding job of this job.
	 * 
	 * @see #getPrecedingJobNumber()
	 */
	public void setPrecedingJobNumber(long precedingJobNumber) {
		// TODO: not yet implemented
		this.precedingJobNumber = precedingJobNumber;
	}

	/**
	 * Returns the think time from the preceding job of this job.
	 * 
	 * It denotes the number of seconds that should elapse between the
	 * termination of the preceding job and the submittal of this one.
	 * 
	 * @return The think time from the preceding job of this job.
	 */
	public double getThinkTimeFromPrecedingJob() {
		// TODO: not yet implemented
		return this.thinkTimeFromPrecedingJob;
	}

	/**
	 * Sets the think time from the preceding job of this job.
	 * 
	 * @param thinkTimeFromPrecedingJob
	 *            The think time from the preceding job of this job.
	 * 
	 * @see #getThinkTimeFromPrecedingJob()
	 */
	public void setThinkTimeFromPrecedingJob(double thinkTimeFromPrecedingJob) {
		// TODO: not yet implemented
		this.thinkTimeFromPrecedingJob = thinkTimeFromPrecedingJob;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.job.Job
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.job.Job#getName()
	 */
	@Override
	public String getName() {
		return Long.toString(this.jobNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.job.Job#getDescription()
	 */
	@Override
	public Description getDescription() {
		return this.swfDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("number", this.getJobNumber())
				.append("relaseTime", this.getReleaseTime()).append("runTime",
						this.getRunTime()).append("processors",
						this.getRequestedNumberOfProcessors()).append(
						"duration", this.getDuration()).toString();
	}

	/**
	 * Sets the identifier of the composite job. This id is the
	 * one specified in the SWF formatted workload file.
	 * 
	 * @param composoteJobId	the id (int) value of the composite job
	 * 
	 */
	public void setCompositeJobId(int compositeJobId) {
		this.compositeJobId = compositeJobId;
	}
	
	/**
	 * Gets the identifier of the composite job. This id is the
	 * one specified in the SWF formatted workload file.
	 * 
	 * @return 	the id (int) value of the composite job
	 */
	public int getCompositeJobId() {
		return this.compositeJobId; 
	}
	
	/**
	 * Sets the UUID of the composite 
	 * 
	 * @param jobContainerId	the UUID of the composite job
	 */
	public void setJobContainerId(UUID jobContainerId) {
		this.jobContainerId = jobContainerId;
	}
	
	/**
	 * Returns the UUID of the composite job
	 * 
	 * @return 		the UUID of the composite job
	 */
	public UUID getJobContainerId() {
		return this.jobContainerId;
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 */
	public void setPredecessor(Number predecessor) {
		this.predecessors.add(predecessor); 
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 */
	public Vector<Number> getPredecessors() {
		return this.predecessors; 
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 */
	public void setSuccessor(Number sucessor) {
		this.successors.add(sucessor); 
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 */
	public Vector<Number> getSuccessors() {
		return this.successors; 
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 */
	public MemberState getMemberState() {
		return this.memberState;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 */
	public void setMemberState(MemberState state) {
		this.memberState= state; 
	}
	
	/**
	 * (non-Javadoc)
	 * @see mx.cicese.dcc.teikoku.workload.job.Vertex#setCost()
	 */
	public void setCost(long cost) {
		this.setRunTime(cost);
	}
	
	/**
	 * (non-Javadoc)
	 * @see mx.cicese.dcc.teikoku.workload.job.Vertex#getCost()
	 */
	public long getCost(){
		return this.getRunTime();
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
	private class SWFDescription
			implements Description {

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.Description#getNumberOfRequestedResources()
		 */
		public int getNumberOfRequestedResources() {			
			return getRequestedNumberOfProcessors();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.job.Description#getEstimatedRuntime()
		 */
		public Distance getEstimatedRuntime() {
			Distance result = null;

			long requestedTime = DateHelper
					.convertToMilliseconds(getRequestedTime());

			if (requestedTime == Distance.PERPETUAL) {
				result = TimeFactory.newPerpetual();
			} // if
			else {
				result = TimeFactory.newFinite(requestedTime);
			} // else

			return result;
		}

	}
}
