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

import java.net.URI;
import java.util.Date;
import java.util.TimeZone;

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
final public class SWFHeader {

	/**
	 * The version number of the format this workload is described in.
	 * 
	 */
	final private static String VERSION = "2.2";

	/**
	 * The brand and model of the described computer.
	 * 
	 */
	private String computer;

	/**
	 * The location of installation and the machine name.
	 * 
	 */
	private String installation;

	/**
	 * The name(s) of person(s) to acknowledge for creating and/or collecting
	 * the workload.
	 * 
	 */
	private String[] acknowledge;

	/**
	 * The website(s) or email address(es) from which more information about the
	 * workload or the installation can be obtained.
	 * 
	 */
	private URI[] information;

	/**
	 * The name and email address of whoever converted the log to the standard
	 * format.
	 * 
	 * Note that the syntax of this string must be
	 * <code>Name &lt;Email&gt;</code>.
	 * 
	 */
	private String conversion;

	/**
	 * The total number of jobs in this workload.
	 * 
	 */
	private int maxJobs;

	/**
	 * The total number of records in this workload file.
	 * 
	 * If no checkpointing/preemption information is included, there is one
	 * record per job, and this is equal to MaxJobs. For workloads with
	 * checkpointing/preemption support, there may be multiple records per job.
	 * 
	 */
	private int maxRecords;

	/**
	 * The preemption type of this workload.
	 * 
	 * @see Preemption
	 * 
	 */
	private Preemption preemption;

	/**
	 * The starting point of the workload, in *NIX time.
	 * 
	 */
	private long unixStartTime;

	/**
	 * @deprecated Use {@link #timeZoneString} instead.
	 * 
	 * The difference value to add to times given as seconds since the epoch.
	 * 
	 * The sum can then be used to get the correct date and hour of the day. The
	 * default is 0, and then gmtime can be used directly.
	 * 
	 * Note: the local time should not be used, as then the results will depend
	 * on the difference between the usage time zone and the installation time
	 * zone.
	 * 
	 */
	@Deprecated
	private String timeZone;

	/**
	 * The time zone in which the log was generated.
	 * 
	 * All times within this workload are in this time zone.
	 * 
	 */
	private TimeZone timeZoneString;

	/**
	 * The time at which the log starts.
	 * 
	 */
	private Date startTime;

	/**
	 * The time at which the log ends.
	 * 
	 * This indicates the point of the last job termination.
	 * 
	 */
	private Date endTime;

	/**
	 * The number of nodes in the computer.
	 * 
	 */
	private int maxNodes;

	/**
	 * The number of processors in the computer.
	 * 
	 * This is different from MaxNodes if each node is an SMP.
	 * 
	 */
	private int maxProcs;

	/**
	 * The maximum runtime that the system allowed, in seconds.
	 * 
	 * This may be larger than any specific job's runtime in the workload.
	 * 
	 */
	private int maxRuntime;

	/**
	 * The maximum memory that the system allowed, in kilobytes.
	 * 
	 */
	private int maxMemory;

	/**
	 * The overuse policy of this system.
	 * 
	 * <code>true</code>, if a job may use more resources than requested;
	 * <code>false</code>, otherwise.
	 * 
	 */
	private boolean allowOveruse;

	/**
	 * The number of queues used.
	 * 
	 */
	private int maxQueues;

	/**
	 * A verbal description of the system's queues.
	 * 
	 * This should include an explanation of the queue number field (if it has
	 * known values). As a minimum it should be denoted how to tell between a
	 * batch and interactive job.
	 * 
	 */
	private String queues;

	/**
	 * A description of a single queue, repeated for all available queues.
	 * 
	 * @see SegmentDescription
	 * 
	 */
	private SegmentDescription[] queue;

	/**
	 * The number of partitions used.
	 * 
	 */
	private int maxPartitions;

	/**
	 * A verbal description of the system's partitions.
	 * 
	 * This should include an explanation of the partition number field.
	 * 
	 * For example, partitions can be distinct parallel machines in a cluster,
	 * or sets of nodes with different attributes (memory configuration, number
	 * of CPUs, special attached devices), especially if this is known to the
	 * scheduler.
	 * 
	 */
	private String partitions;

	/**
	 * A description of a single partition, repeated for all available
	 * partitions.
	 * 
	 * @see SegmentDescription
	 * 
	 */
	private SegmentDescription[] partition;

	/**
	 * An array of verbal descriptions of special features of the log.
	 * 
	 * For example, "The runtime is until the last node was freed; jobs may have
	 * freed some of their nodes earlier".
	 * 
	 */
	private String[] note;

	/**
	 * Returns the version number of the format this workload is described in.
	 * 
	 * @return The version number of the format this workload is described in.
	 */
	public String getVersion() {
		return VERSION;
	}

	/**
	 * Returns the brand and model of the described computer.
	 * 
	 * @return The brand and model of the described computer.
	 */
	public String getComputer() {
		return this.computer;
	}

	/**
	 * Sets the brand and model of the described computer.
	 * 
	 * @param computer
	 *            The brand and model of the described computer.
	 * 
	 * @see #getComputer()
	 */
	public void setComputer(String computer) {
		this.computer = computer;
	}

	/**
	 * Returns the location of installation and the machine name.
	 * 
	 * @return The location of installation and the machine name.
	 */
	public String getInstallation() {
		return this.installation;
	}

	/**
	 * Sets the location of installation and the machine name.
	 * 
	 * @param installation
	 *            The location of installation and the machine name.
	 * 
	 * @see #getInstallation()
	 */
	public void setInstallation(String installation) {
		this.installation = installation;
	}

	/**
	 * Returns the name(s) of person(s) to acknowledge for creating and/or
	 * collecting the workload.
	 * 
	 * @return The name(s) of person(s) to acknowledge for creating and/or
	 *         collecting the workload.
	 * 
	 * @see #getAcknowledge(int)
	 */
	public String[] getAcknowledgeArray() {
		return this.acknowledge;
	}

	/**
	 * Returns the name of person to acknowledge for creating and/or collecting
	 * the workload, identified by the given index.
	 * 
	 * @param index
	 *            The index of the acknowledge to return.
	 * @return The name of person to acknowledge for creating and/or collecting
	 *         the workload identified by the given index.
	 */
	public String getAcknowledge(int index) {
		return this.acknowledge[index];
	}

	/**
	 * Sets the name(s) of person(s) to acknowledge for creating and/or
	 * collecting the workload.
	 * 
	 * @param acknowledgeArray
	 *            The name(s) of person(s) to acknowledge for creating and/or
	 *            collecting the workload.
	 * 
	 * @see #setAcknowledge(String, int)
	 */
	public void setAcknowledgeArray(String[] acknowledgeArray) {
		this.acknowledge = acknowledgeArray;
	}

	/**
	 * Sets the name of person to acknowledge for creating and/or collecting the
	 * workload, identified by the given index.
	 * 
	 * @param acknowledge
	 *            The name of person to acknowledge for creating and/or
	 *            collecting the workload.
	 * @param index
	 *            The index of the acknowledge to set.
	 */
	public void setAcknowledge(String acknowledge, int index) {
		this.acknowledge[index] = acknowledge;
	}

	/**
	 * Returns the website(s) or email address(es) from which more information
	 * about the workload or the installation can be obtained.
	 * 
	 * @return The website(s) or email address(es) from which more information
	 *         about the workload or the installation can be obtained.
	 * 
	 * @see #getInformation(int)
	 */
	public URI[] getInformationArray() {
		return this.information;
	}

	/**
	 * Returns the website or email address from which more information about
	 * the workload or the installation can be obtained, identified by the given
	 * index.
	 * 
	 * @param index
	 *            The index of the information to return.
	 * @return The website(s) or email address(es) from which more information
	 *         about the workload or the installation can be obtained,
	 *         identified by the given index.
	 */
	public URI getInformation(int index) {
		return this.information[index];
	}

	/**
	 * Sets the website(s) or email address(es) from which more information
	 * about the workload or the installation can be obtained.
	 * 
	 * @param informationArray
	 *            The website(s) or email address(es) from which more
	 *            information about the workload or the installation can be
	 *            obtained.
	 * 
	 * @see #setInformation(String, int)
	 */
	public void setInformationArray(URI[] informationArray) {
		this.information = informationArray;
	}

	/**
	 * Sets the website(s) or email address(es) from which more information
	 * about the workload or the installation can be obtained, identified by the
	 * given index.
	 * 
	 * @param information
	 *            The website or email address from which more information about
	 *            the workload or the installation can be obtained.
	 * @param index
	 *            The index of the information to set.
	 */
	public void setInformation(URI information, int index) {
		this.information[index] = information;
	}

	/**
	 * Returns the name and email address of whoever converted the log to the
	 * standard format.
	 * 
	 * Note that the syntax of this string must be
	 * <code>Name &lt;Email&gt;</code>.
	 * 
	 * @return The name and email address of whoever converted the log to the
	 *         standard format.
	 */
	public String getConversion() {
		return this.conversion;
	}

	/**
	 * Sets the name and email address of whoever converted the log to the
	 * standard format.
	 * 
	 * @param conversion
	 *            The name and email address of whoever converted the log to the
	 *            standard format.
	 * 
	 * @see #getConversion()
	 */
	public void setConversion(String conversion) {
		this.conversion = conversion;
	}

	/**
	 * Returns the total number of jobs in this workload.
	 * 
	 * @return The total number of jobs in this workload.
	 */
	public int getMaxJobs() {
		return this.maxJobs;
	}

	/**
	 * Sets the total number of jobs in this workload.
	 * 
	 * @param maxJobs
	 *            The total number of jobs in this workload.
	 * 
	 * @see #getMaxJobs()
	 */
	public void setMaxJobs(int maxJobs) {
		this.maxJobs = maxJobs;
	}

	/**
	 * Returns the total number of records in this workload file.
	 * 
	 * If no checkpointing/preemption information is included, there is one
	 * record per job, and this is equal to MaxJobs. For workloads with
	 * checkpointing/preemption support, there may be multiple records per job.
	 * 
	 * @return The total number of records in this workload file.
	 */
	public int getMaxRecords() {
		return this.maxRecords;
	}

	/**
	 * Sets the total number of records in this workload file.
	 * 
	 * @param maxRecords
	 *            The total number of records in this workload file.
	 * 
	 * @see #getMaxRecords()
	 */
	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}

	/**
	 * Returns the preemption type of this workload.
	 * 
	 * @return The preemption type of this workload.
	 * 
	 * @see Preemption
	 */
	public Preemption getPreemption() {
		return this.preemption;
	}

	/**
	 * Sets the preemption type of this workload.
	 * 
	 * @param preemption
	 *            The preemption type of this workload.
	 * 
	 * @see #getPreemption()
	 */
	public void setPreemption(Preemption preemption) {
		this.preemption = preemption;
	}

	/**
	 * Returns the starting point of the workload, in *NIX time.
	 * 
	 * @return The starting point of the workload, in *NIX time.
	 */
	public long getUnixStartTime() {
		return this.unixStartTime;
	}

	/**
	 * Sets the starting point of the workload, in *NIX time.
	 * 
	 * @param unixStartTime
	 *            The starting point of the workload, in *NIX time.
	 * 
	 * @see #getUnixStartTime()
	 */
	public void setUnixStartTime(long unixStartTime) {
		this.unixStartTime = unixStartTime;
	}

	/**
	 * @deprecated Use {@link #getTimeZoneString()} instead.
	 * 
	 * Returns the difference value to add to times given as seconds since the
	 * epoch.
	 * 
	 * The sum can then be used to get the correct date and hour of the day. The
	 * default is 0, and then gmtime can be used directly.
	 * 
	 * Note: the local time should not be used, as then the results will depend
	 * on the difference between the usage time zone and the installation time
	 * zone.
	 * 
	 * @return The difference value to add to times given as seconds since the
	 *         epoch.
	 */
	@Deprecated
	public String getTimeZone() {
		return this.timeZone;
	}

	/**
	 * @deprecated Use {@link #setTimeZoneString(TimeZone)} instead.
	 * 
	 * Sets the difference value to add to times given as seconds since the
	 * epoch.
	 * 
	 * @param timeZone
	 *            The difference value to add to times given as seconds since
	 *            the epoch.
	 * 
	 * @see #getTimeZone()
	 */
	@Deprecated
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Returns the time zone in which the log was generated.
	 * 
	 * All times within this workload are in this time zone.
	 * 
	 * @return The time zone in which the log was generated.
	 */
	public TimeZone getTimeZoneString() {
		return this.timeZoneString;
	}

	/**
	 * Sets the time zone in which the log was generated.
	 * 
	 * @param timeZoneString
	 *            The time zone in which the log was generated.
	 * 
	 * @see #getTimeZoneString()
	 */
	public void setTimeZoneString(TimeZone timeZoneString) {
		this.timeZoneString = timeZoneString;
	}

	/**
	 * Returns the time at which the log starts.
	 * 
	 * @return The time at which the log starts.
	 */
	public Date getStartTime() {
		return this.startTime;
	}

	/**
	 * Sets the time at which the log starts.
	 * 
	 * @param startTime
	 *            The time at which the log starts.
	 * 
	 * @see #getStartTime()
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * Returns the time at which the log ends.
	 * 
	 * This indicates the point of the last job termination.
	 * 
	 * @return The time at which the log ends.
	 */
	public Date getEndTime() {
		return this.endTime;
	}

	/**
	 * Sets the time at which the log ends.
	 * 
	 * @param endTime
	 *            The time at which the log ends.
	 * 
	 * @see #getEndTime()
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Returns the number of nodes in the computer.
	 * 
	 * @return The number of nodes in the computer.
	 */
	public int getMaxNodes() {
		return this.maxNodes;
	}

	/**
	 * Sets the number of nodes in the computer.
	 * 
	 * @param maxNodes
	 *            The number of nodes in the computer.
	 * 
	 * @see #getMaxNodes()
	 */
	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	/**
	 * Returns the number of processors in the computer.
	 * 
	 * This is different from MaxNodes if each node is an SMP.
	 * 
	 * @return The number of processors in the computer.
	 */
	public int getMaxProcs() {
		return this.maxProcs;
	}

	/**
	 * Sets the number of processors in the computer.
	 * 
	 * @param maxProcs
	 *            The number of processors in the computer.
	 * 
	 * @see #getMaxProcs()
	 */
	public void setMaxProcs(int maxProcs) {
		this.maxProcs = maxProcs;
	}

	/**
	 * Returns the maximum runtime that the system allowed, in seconds.
	 * 
	 * This may be larger than any specific job's runtime in the workload.
	 * 
	 * @return The maximum runtime that the system allowed, in seconds.
	 */
	public int getMaxRuntime() {
		return this.maxRuntime;
	}

	/**
	 * Sets the maximum runtime that the system allowed, in seconds.
	 * 
	 * @param maxRuntime
	 *            The maximum runtime that the system allowed, in seconds.
	 * 
	 * @see #getMaxRuntime()
	 */
	public void setMaxRuntime(int maxRuntime) {
		this.maxRuntime = maxRuntime;
	}

	/**
	 * Returns the maximum memory that the system allowed, in kilobytes.
	 * 
	 * @return The maximum memory that the system allowed, in kilobytes.
	 */
	public int getMaxMemory() {
		return this.maxMemory;
	}

	/**
	 * Sets the maximum memory that the system allowed, in kilobytes.
	 * 
	 * @param maxMemory
	 *            The maximum memory that the system allowed, in kilobytes.
	 * 
	 * @see #getMaxMemory()
	 */
	public void setMaxMemory(int maxMemory) {
		this.maxMemory = maxMemory;
	}

	/**
	 * Returns the overuse policy of this system.
	 * 
	 * @return <code>true</code>, if a job may use more resources than
	 *         requested; <code>false</code>, otherwise.
	 * 
	 */
	public boolean getAllowOveruse() {
		return this.allowOveruse;
	}

	/**
	 * Sets the overuse policy of this system.
	 * 
	 * @param allowOveruse
	 *            The overuse policy of this system.
	 * 
	 * @see #getAllowOveruse()
	 */
	public void setAllowOveruse(boolean allowOveruse) {
		this.allowOveruse = allowOveruse;
	}

	/**
	 * Returns the number of queues used.
	 * 
	 * @return The number of queues used.
	 */
	public int getMaxQueues() {
		return this.maxQueues;
	}

	/**
	 * Sets the number of queues used.
	 * 
	 * @param maxQueues
	 *            The number of queues used.
	 * 
	 * @see #getMaxQueues()
	 */
	public void setMaxQueues(int maxQueues) {
		this.maxQueues = maxQueues;
	}

	/**
	 * Returns a verbal description of the system's queues.
	 * 
	 * This should include an explanation of the queue number field (if it has
	 * known values). As a minimum it should be denoted how to tell between a
	 * batch and interactive job.
	 * 
	 * @return T
	 */
	public String getQueues() {
		return this.queues;
	}

	/**
	 * Sets a verbal description of the system's queues.
	 * 
	 * @param queues
	 *            A verbal description of the system's queues.
	 * 
	 * @see #getQueues()
	 */
	public void setQueues(String queues) {
		this.queues = queues;
	}

	/**
	 * Returns the description(s) of the single queue(s).
	 * 
	 * @return The description(s) of the single queue(s).
	 * 
	 * @see #getQueue(int)
	 */
	public SegmentDescription[] getQueueArray() {
		return this.queue;
	}

	/**
	 * Returns the description of a single queue, identified by the given index.
	 * 
	 * @param index
	 *            The index of the queue description to return.
	 * @return The description of a single queue, identified by the given index.
	 * 
	 * @see SegmentDescription
	 */
	public SegmentDescription getQueue(int index) {
		return this.queue[index];
	}

	/**
	 * Sets the description(s) of the single queue(s).
	 * 
	 * @param queueArray
	 *            The description(s) of the single queue(s).
	 * 
	 * @see #setQueue(de.irf.it.rmg.core.teikoku.workload.job.SWFHeader.SegmentDescription,
	 *      int)
	 */
	public void setQueueArray(SegmentDescription[] queueArray) {
		this.queue = queueArray;
	}

	/**
	 * Sets the description of a single queue, identified by the given index.
	 * 
	 * @param queue
	 *            The description of a single queue.
	 * @param index
	 *            The index of the queue description to set.
	 * 
	 * @see #getQueue(int)
	 */
	public void setQueue(SegmentDescription queue, int index) {
		this.queue[index] = queue;
	}

	/**
	 * Returns the number of partitions used.
	 * 
	 * @return The number of partitions used.
	 */
	public int getMaxPartitions() {
		return this.maxPartitions;
	}

	/**
	 * Sets the number of partitions used.
	 * 
	 * @param maxPartitions
	 *            The number of partitions used.
	 * 
	 * @see #getMaxPartitions()
	 */
	public void setMaxPartitions(int maxPartitions) {
		this.maxPartitions = maxPartitions;
	}

	/**
	 * Returns a verbal description of the system's partitions.
	 * 
	 * This should include an explanation of the partition number field.
	 * 
	 * For example, partitions can be distinct parallel machines in a cluster,
	 * or sets of nodes with different attributes (memory configuration, number
	 * of CPUs, special attached devices), especially if this is known to the
	 * scheduler.
	 * 
	 * @return A verbal description of the system's partitions.
	 */
	public String getPartitions() {
		return this.partitions;
	}

	/**
	 * Sets a verbal description of the system's partitions.
	 * 
	 * @param partitions
	 *            A verbal description of the system's partitions.
	 * 
	 * @see #getPartitions()
	 */
	public void setPartitions(String partitions) {
		this.partitions = partitions;
	}

	/**
	 * Returns the description(s) of the single partition(s).
	 * 
	 * @return The description(s) of the single partition(s).
	 * 
	 * @see #getPartition(int)
	 */
	public SegmentDescription[] getPartitionArray() {
		return this.partition;
	}

	/**
	 * Returns the description of a single partition, identified by the given
	 * index.
	 * 
	 * @param index
	 *            The index of the partition description to return.
	 * @return The description of a single partition, identified by the given
	 *         index.
	 * 
	 * @see SegmentDescription
	 */
	public SegmentDescription getPartition(int index) {
		return this.partition[index];
	}

	/**
	 * Sets the description(s) of the single partition(s).
	 * 
	 * @param partitionArray
	 *            The description(s) of the single partition(s).
	 * 
	 * @see #setPartition(de.irf.it.rmg.core.teikoku.workload.job.SWFHeader.SegmentDescription,
	 *      int)
	 */
	public void setPartitionArray(SegmentDescription[] partitionArray) {
		this.partition = partitionArray;
	}

	/**
	 * Sets the description of a single partition, identified by the given
	 * index.
	 * 
	 * @param partition
	 *            The description of a single queue.
	 * @param index
	 *            The index of the partition description to set.
	 */
	public void setPartition(SegmentDescription partition, int index) {
		this.partition[index] = partition;
	}

	/**
	 * Returns the verbal descriptions of special features of the log.
	 * 
	 * @return The verbal descriptions of special features of the log.
	 * 
	 * @see #getNote(int)
	 */
	public String[] getNoteArray() {
		return this.note;
	}

	/**
	 * Returns a verbal description of special features of the log, identified
	 * by the given index.
	 * 
	 * For example, "The runtime is until the last node was freed; jobs may have
	 * freed some of their nodes earlier".
	 * 
	 * @param index
	 *            The index of the note to return.
	 * @return A verbal descriptions of special features of the log, identified
	 *         by the given index.
	 */
	public String getNote(int index) {
		return this.note[index];
	}

	/**
	 * Sets the verbal descriptions of special features of the log.
	 * 
	 * @param noteArray
	 *            The verbal descriptions of special features of the log.
	 * 
	 * @see #setNote(String, int)
	 */
	public void setNoteArray(String[] noteArray) {
		this.note = noteArray;
	}

	/**
	 * Sets a verbal description of special features of the log, identified by
	 * the given index.
	 * 
	 * @param note
	 *            A verbal description of special features of the log.
	 * @param index
	 *            The index of the note to set.
	 */
	public void setNote(String note, int index) {
		this.note[index] = note;
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
	public enum Preemption {

		/**
		 * Denotes that jobs run to completion and are represented by a single
		 * entry in the workload.
		 * 
		 */
		NO(),

		/**
		 * Denotes that the execution of a job may be split into several parts,
		 * and each is represented by a separate entry in the workload.
		 * 
		 */
		YES(),

		/**
		 * Denotes that jobs may be split, and their information appears twice
		 * in the workload: once as a single summary entry, and again as a
		 * sequence of entries representing the parts.
		 * 
		 * @see #YES
		 * 
		 */
		DOUBLE(),

		/**
		 * Denotes that time slicing is used, but no details are available.
		 * 
		 */
		TS();
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
	final public class SegmentDescription {

		/**
		 * The number of the segment.
		 * 
		 */
		private int number;

		/**
		 * The name of the segment.
		 * 
		 */
		private String name;

		/**
		 * The description of the segment.
		 * 
		 */
		private String description;

		/**
		 * Returns the number of the segment.
		 * 
		 * @return The number of the segment.
		 */
		public int getNumber() {
			return this.number;
		}

		/**
		 * Sets the number of the segment.
		 * 
		 * @param number
		 *            The number of the segment.
		 */
		public void setNumber(int number) {
			this.number = number;
		}

		/**
		 * Returns the name of the segment.
		 * 
		 * @return The name of the segment.
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Sets the name of the segment.
		 * 
		 * @param name
		 *            The name of the segment.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Returns the description of the segment.
		 * 
		 * @return The description of the segment.
		 */
		public String getDescription() {
			return this.description;
		}

		/**
		 * Sets the description of the segment.
		 * 
		 * @param description
		 *            The description of the segment.
		 */
		public void setDescription(String description) {
			this.description = description;
		}
	}
}
