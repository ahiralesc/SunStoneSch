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
package de.irf.it.rmg.core.teikoku.workload.swf;

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
public final class SWFConstants {

	// ----- SWF header constants ----------------------------------------------

	final public static String VERSION = "2.2";

	final public static String COMMENT_DELIMITER = "%";

	final public static byte NUMBER_OF_JOB_FIELDS = 18;

	public static enum HeaderFields {

		VERSION("Version:", false),
		COMPUTER("Computer:", false),
		INSTALLATION("Installation:", false),
		ACKNOWLEDGE("Acknowledge:", true),
		INFORMATION("Information:", false),
		CONVERSION("Conversion:", false),
		MAX_JOBS("MaxJobs:", false),
		MAX_RECORDS("MaxRecords:", false),
		PREEMPTION("Preemption:", false),
		UNIX_START_TIME("UnixStartTime:", false),
		TIME_ZONE("TimeZone:", false),
		TIME_ZONE_STRING("TimeZoneString:", false),
		START_TIME("StartTime:", false),
		END_TIME("EndTime:", false),
		MAX_NODES("MaxNodes:", false),
		MAX_PROCS("MaxProcs:", false),
		MAX_RUNTIME("MaxRuntime:", false),
		MAX_MEMORY("MaxMemory:", false),
		ALLOW_OVERUSE("AllowOveruse:", false),
		MAX_QUEUES("MaxQueues:", false),
		QUEUES("Queues:", false),
		QUEUE("Queue:", true),
		MAX_PARTITIONS("MaxPartitions:", false),
		PARTITIONS("Partitions:", false),
		PARTITION("Partition:", true),
		NOTE("Note:", true);

		final public static String PREEMPTION_NO = "No";

		final public static String PREEMPTION_YES = "Yes";

		final public static String PREEMPTION_DOUBLE = "Double";

		final public static String PREEMPTION_TS = "TS";

		private String identifier;

		private boolean multiValue;

		private HeaderFields(String name, boolean multiValue) {
			this.identifier = name;
			this.multiValue = multiValue;
		}

		public String getIdentifier() {
			return this.identifier;
		}

		public boolean isMultiValue() {
			return this.multiValue;
		}
	}

	// ----- SWF job/roll constants --------------------------------------------

	final public static class JobFields {

		final public static String JOB_NUMBER = "jobNumber";

		final public static String SUBMIT_TIME = "submitTime";

		final public static String WAIT_TIME = "waitTime";

		final public static String RUN_TIME = "runTime";

		final public static String NUMBER_OF_ALLOCATED_PROCESSORS = "numberOfAllocatedProcessors";

		final public static String AVERAGE_CPU_TIME_USED = "averageCPUTimeUsed";

		final public static String USED_MEMORY = "usedMemory";

		final public static String REQUESTED_NUMBER_OF_PROCESSORS = "requestedNumberOfProcessors";

		final public static String REQUESTED_TIME = "requestedTime";

		final public static String REQUESTED_MEMORY = "requestedMemory";

		final public static String STATUS = "status";

		final public static byte STATUS_FAILED = 0;

		final public static byte STATUS_COMPLETED = 1;

		final public static byte STATUS_CHECKPOINTED_PENDING = 2;

		final public static byte STATUS_CHECKPOINTED_COMPLETED = 3;

		final public static byte STATUS_CHECKPOINTED_FAILED = 4;

		final public static byte STATUS_CANCELLED = 5;

		final public static String USER_ID = "userID";

		final public static String GROUP_ID = "groupID";

		final public static String EXECUTABLE_APPLICATION_NUMBER = "executableApplicationNumber";

		final public static String QUEUE_NUMBER = "queueNumber";

		final public static String PARTITION_NUMBER = "partitionNumber";

		final public static String PRECEDING_JOB_NUMBER = "precedingJobNumber";

		final public static String THINK_TIME_FROM_PRECEDING_JOB = "thinkTimeFromPrecedingJob";
	}
}
