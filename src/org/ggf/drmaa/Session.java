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
package org.ggf.drmaa;

import java.util.List;

/**
 * Provides the main entry point into a DRMAA-managed system.
 * 
 * A session encapsulates a single user session for utilization of the
 * underlying resource manager implementation. Within one session, users can
 * create and manipulate job templates, submit (run) jobs based on these
 * templates, and control their status.
 * 
 * Only one DRMAA session per client is allowed at any time. That is, another
 * session can be opened only after the current one is closed. Furthermore,
 * nesting of sessions is not recommended.
 * 
 * OldJob IDs remain valid over session boundaries, and the
 * {@link #control(String, int)} method will work on these, provided that they
 * can be resolved. 
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public interface Session {

	// /////////////////////////////////////////////////////////////////////////
	// CONTROL COMMAND CONSTANTS

	/**
	 * Indicates that a given job should be suspended.
	 * 
	 * @see #control(String, int)
	 */
	final static int SUSPEND = 0;

	/**
	 * Indicates that a given job should be resumed.
	 * 
	 * @see #control(String, int)
	 */
	final static int RESUME = 1;

	/**
	 * Indicates that a given job should be placed into a hold state.
	 * 
	 * @see #control(String, int)
	 */
	final static int HOLD = 2;

	/**
	 * Indicates that a given job should be released from its hold state.
	 * 
	 * @see #control(String, int)
	 */
	final static int RELEASE = 3;

	/**
	 * Indicates that a given job should be terminated.
	 * 
	 * @see #control(String, int)
	 */
	final static int TERMINATE = 4;

	// /////////////////////////////////////////////////////////////////////////
	// ACTION SCOPE AND TIMING CONSTANTS

	/**
	 * Indicates that an action should be performed on all jobs in the session
	 * at submission time, minus any jobs that go out of scope during the run
	 * time of the action.
	 * 
	 * @see #control(String, int)
	 * @see #synchronize(List, long, boolean)
	 * 
	 */
	final static String JOB_IDS_SESSION_ALL = "DRMAA_JOB_IDS_SESSION_ALL";

	/**
	 * Indicates that an action should be performed on any jobs currently in the
	 * {@link #RUNNING} state in the session.
	 * 
	 * @see #wait(String, long)
	 */
	final static String JOB_IDS_SESSION_ANY = "DRMAA_JOB_IDS_SESSION_ANY";

	/**
	 * Indicates that a method call should not return until the given job(s) has
	 * entered the {@link #DONE} or {@link #FAILED} state.
	 * 
	 * @see #wait(String, long)
	 * @see #synchronize(List, long, boolean)
	 */
	final static long TIMEOUT_WAIT_FOREVER = -1L;

	/**
	 * Indicates that a method call should return immediately if the given
	 * job(s) has not yet entered the {@link #DONE} or {@link #FAILED} state.
	 * 
	 * @see #wait(String, long)
	 * @see #synchronize(List, long, boolean)
	 */
	final static long TIMEOUT_NO_WAIT = 0L;

	// /////////////////////////////////////////////////////////////////////////
	// JOB STATE CONSTANTS

	/**
	 * Indicates that a job's current state cannot be determined.
	 * 
	 * @see #getJobProgramStatus(String)
	 */
	final static int UNDETERMINED = 0x00;

	/**
	 * Indicates that a job is queued and waiting to be scheduled.
	 * 
	 * @see #getJobProgramStatus(String)
	 */
	final static int QUEUED_ACTIVE = 0x10;

	/**
	 * Indicates that a job has been placed on hold by the system or
	 * administrator.
	 * 
	 * @see #getJobProgramStatus(String)
	 */
	final static int SYSTEM_ON_HOLD = 0x11;

	/**
	 * Indicates that a job has been placed on hold by the user.
	 * 
	 * @see #getJobProgramStatus(String)
	 */
	final static int USER_ON_HOLD = 0x12;

	/**
	 * Indicates that a job has been placed on hold by both the system or
	 * administrator and by the user.
	 * 
	 * @see #getJobProgramStatus(String)
	 */
	final static int USER_SYSTEM_ON_HOLD = 0x13;

	/**
	 * Indicates that a job has been scheduled and is running.
	 * 
	 */
	final static int RUNNING = 0x20;

	/**
	 * Indicates that a job has been suspended by the system or administrator.
	 * 
	 * @see #getJobProgramStatus(String)
	 * 
	 */
	final static int SYSTEM_SUSPENDED = 0x21;

	/**
	 * Indicates that a job has been suspended by the user.
	 * 
	 * @see #getJobProgramStatus(String)
	 * 
	 */
	final static int USER_SUSPENDED = 0x22;

	/**
	 * Indicates that a job has been suspended by both the system or
	 * administrator and by the user.
	 * 
	 * @see #getJobProgramStatus(String)
	 * 
	 */
	final static int USER_SYSTEM_SUSPENDED = 0x23;

	/**
	 * Indicates that a job has finished normally.
	 * 
	 * @see #getJobProgramStatus(String)
	 * 
	 */
	final static int DONE = 0x30;

	/**
	 * Indicates that a job exited abnormally before finishing.
	 * 
	 * @see #getJobProgramStatus(String)
	 * 
	 */
	final static int FAILED = 0x40;

	/**
	 * Returns a string denoting a specific type of a DRM system.
	 * 
	 * If called <em>before</em> {@link #init(String)}, a comma delimited
	 * string of DRM identifiers, one element per DRM implementation provided,
	 * is returned.
	 * 
	 * If called <em>after</em> {@link #init(String)}, the selected DRM
	 * system identifier is returned.
	 * 
	 * @return The DRMAA system identifier information. This string is
	 *         implementation dependent and should not be interpreted by the
	 *         caller.
	 * @throws DrmaaException
	 */
	String getDrmsInfo()
			throws DrmaaException;

	/**
	 * Returns a string denoting a specific version of a DRM system.
	 * 
	 * If called <em>before</em> {@link #init(String)}, a comma delimited
	 * string of DRMAA implementations, one element for each DRMAA
	 * implementation provided, is returned.
	 * 
	 * If called <em>after</em> {@link #init(String)}, the selected DRMAA
	 * implementation is returned.
	 * 
	 * @return The DRMAA implementation information. This string is
	 *         implementation dependent and should not be interpreted by the
	 *         caller.
	 * @throws DrmaaException
	 */
	String getDrmaaImplementation()
			throws DrmaaException;

	/**
	 * Returns a string denoting a specific installation of a specific DRM
	 * system.
	 * 
	 * If called <em>before</em> {@link #init(String)}, a comma delimited
	 * string of default DRMAA implementation contact strings, one element per
	 * DRM system available, is returned.
	 * 
	 * If called <em>after</em> {@link #init(String)}, the contact string of
	 * the DRM system the session is attached to is returned.
	 * 
	 * @return The DRMAA system contact information. This string is
	 *         implementation dependent and should not be interpreted by the
	 *         caller.
	 * @throws DrmaaException
	 */
	String getContact()
			throws DrmaaException;

	/**
	 * Initializes a DRMAA session for use.
	 * 
	 * This method must be called before any other DRMAA calls, except for the
	 * {@link #getDrmsInfo()}, {@link #getDrmaaImplementation()} and
	 * {@link #getContact()} methods of this interface.
	 * 
	 * Furthermore, only a single call to the {@link #init(String)} method is
	 * allowed (from a single source), until a subsequent call to
	 * {@link #exit()}.
	 * 
	 * @param contactString
	 *            An implementation-dependent string to specify the DRM system
	 *            to use. If <code>null</code>, the default DRM system is
	 *            used, provided there is only one DRMS available.
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link DrmsInitException} when session initialization
	 *             failed;</li>
	 *             <li>{@link InvalidContactStringException} when the
	 *             <code>contact</code> parameter is invalid;</li>
	 *             <li>{@link AlreadyActiveSessionException} when the session
	 *             has already been initialized;</li>
	 *             <li>{@link DefaultContactStringException} when the
	 *             <code>contact</code> parameter is <code>null</code> and
	 *             the default contact string could not be used to connect to
	 *             the DRMS; or</li>
	 *             <li>{@link NoDefaultContactStringSelectedException} when the
	 *             <code>contact</code> parameter is <code>null</code> and
	 *             more than one DRMS is available.</li>
	 *             </ul>
	 */
	void init(String contactString)
			throws DrmaaException;

	/**
	 * Returns a new {@link JobTemplate} instance, which is used to set the
	 * defining characteristics for jobs to be submitted.
	 * 
	 * @return A blank {@link JobTemplate} object.
	 * @throws DrmaaException,
	 *             which may be a {@link NoActiveSessionException} when the
	 *             session has not been initialized.
	 * 
	 * @see JobTemplate
	 */
	JobTemplate createJobTemplate()
			throws DrmaaException;

	/**
	 * Deallocates and removes the given {@link JobTemplate} from this session,
	 * if applicable, without having any effect on running jobs.
	 * 
	 * @param jobTemplate
	 *            The job template to delete.
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized; or</li>
	 *             <li>{@link InvalidJobTemplateException} when the given job
	 *             template was not created with {@link #createJobTemplate()} or
	 *             has already been deleted. </li>
	 *             </ul>
	 * 
	 * @see #createJobTemplate()
	 */
	void deleteJobTemplate(JobTemplate jobTemplate)
			throws DrmaaException;

	/**
	 * Submits a job with attributes defined in the job template given as a
	 * parameter.
	 * 
	 * @param jobTemplate
	 *            The job template to be used to create the job.
	 * @return A job identifier string identical to that returned from the
	 *         underlying DRM system.
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized;</li>
	 *             <li>{@link InvalidJobTemplateException} when the given job
	 *             template was not created with {@link #createJobTemplate()} or
	 *             has already been deleted;</li>
	 *             <li>{@link TryLaterException} when the request could not be
	 *             processed due to excessive system load; or</li>
	 *             <li>{@link DeniedByDrmException} when the DRMS eternally
	 *             rejected the given job.
	 *             </ul>
	 * 
	 * @see #createJobTemplate()
	 * @see #runBulkJobs(JobTemplate, int, int, int)
	 * @see JobTemplate
	 */
	String runJob(JobTemplate jobTemplate)
			throws DrmaaException;

	/**
	 * Submits a set of parametric jobs, dependent on the implied loop index,
	 * each with attributes defined in the given job template.
	 * 
	 * Each job in this set is identical except for its index.
	 * 
	 * @param jobTemplate
	 *            The job template to be used to create the jobs.
	 * @param beginIndex
	 *            The starting value for the submission loop, which must be
	 *            greater that <code>0</code>.
	 * @param endIndex
	 *            The terminating value for the submission loop, which must be
	 *            equal or greater than <code>beginIndex</code>.
	 * @param step
	 *            The per-iteration loop index increment.
	 * @return A list of job identifier strings identical to those returned from
	 *         the underlying DRM system.
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized;</li>
	 *             <li>{@link InvalidJobTemplateException} when the given job
	 *             template was not created with {@link #createJobTemplate()} or
	 *             has already been deleted;</li>
	 *             <li>{@link TryLaterException} when the request could not be
	 *             processed due to excessive system load; or</li>
	 *             <li>{@link DeniedByDrmException} when the DRMS eternally
	 *             rejected one of the given jobs.
	 *             </ul>
	 * 
	 * @see {@link #createJobTemplate()}
	 * @see #runJob(JobTemplate)
	 * @see JobTemplate#PARAMETRIC_INDEX
	 */
	List runBulkJobs(JobTemplate jobTemplate, int beginIndex, int endIndex,
			int step)
			throws DrmaaException;

	/**
	 * Holds, releases, suspends, resumes or kills the job identified by
	 * <code>jobName</code>. If <code>jobName</code> is
	 * {@link #JOB_IDS_SESSION_ALL}, then this method acts on all jobs
	 * <em>submitted during this DRMAA session</em> up to the moment
	 * {@link #control(String, int)} is called.
	 * 
	 * To avoid thread races in multi-threaded applications, the caller should
	 * explicitly synchronize this call with any other job submission calls or
	 * {@link #control(String, int)} calls that may change the number of remote
	 * jobs.
	 * 
	 * Note that this method returns once the action has been acknowledged by
	 * the DRM system.
	 * 
	 * @param jobName
	 *            The id of the job to control.
	 * @param operation
	 *            The control action to be taken. Legal values for this
	 *            parameter are
	 *            <ul>
	 *            <li>{@link #SUSPEND}, which stops the job;</li>
	 *            <li>{@link #RESUME}, which (re-)starts the job;</li>
	 *            <li>{@link #HOLD}, which puts the job on-hold;</li>
	 *            <li>{@link #RELEASE}, which releases the hold on the job;
	 *            and</li>
	 *            <li>{@link #TERMINATE}, which kills the job.</li>
	 *            </ul>
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized;</li>
	 *             <li>{@link InvalidJobException} when the job id does not
	 *             represent a valid job;</li>
	 *             <li>{@link ResumeInconsistentStateException} when the job is
	 *             not in a state from which it can be resumed;</li>
	 *             <li>{@link SuspendInconsistentStateException} when the job
	 *             is not in a state from which it can be suspended;</li>
	 *             <li>{@link HoldInconsistentStateException} when the job is
	 *             not in a state from which it can be held; or</li>
	 *             <li>{@link ReleaseInconsistentStateException} when the job
	 *             is not in a state from which it can be released.</li>
	 *             </ul>
	 */
	void control(String jobName, int operation)
			throws DrmaaException;

	/**
	 * Waits until all jobs specified in the job list have finished execution.
	 * To prevent blocking indefinitely in this method, the caller may use a
	 * timeout specifying how many seconds to block.
	 * 
	 * To avoid thread races in multi-threaded applications, the caller should
	 * explicitly synchronize this call with any other job submission calls or
	 * {@link #synchronize(List, long, boolean)} calls that may change the
	 * number of remote jobs.
	 * 
	 * If the call exits before the timeout has been elapsed, then
	 * <ul>
	 * <li>either all jobs have been waited on</lI>
	 * <li>or there was an interrupt.</li>
	 * </ul>
	 * 
	 * If no active jobs are availabe, the call will return immediately.
	 * 
	 * @param jobList
	 *            The ids of the jobs to synchronize. If this parameter contains
	 *            the {@link #JOB_IDS_SESSION_ALL} constant, then all jobs that
	 *            have been submitted since session initialization are
	 *            synchronized.
	 * @param timeout
	 *            The maximum number of seconds to wait. The
	 *            {@link #TIMEOUT_WAIT_FOREVER} constant may be used for
	 *            indefinite waiting, while the {@link #TIMEOUT_NO_WAIT}
	 *            constant may be used for immediate returning. Alternatively, a
	 *            number of seconds may be specified.
	 * @param dispose
	 *            <code>true</code>, if the underlying DRM should dispose of (<em>reap</em>)
	 *            the job's internal data (i.e. statistical information such as
	 *            system resource consumption). <code>false</code>, if the
	 *            data record should be left for future access via
	 *            {@link #wait(String, long)}
	 * @throws DrmaaException
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized;</li>
	 *             <li>{@link InvalidJobException} when the job id does not
	 *             represent a valid job; or</li>
	 *             <li>{@link ExitTimeoutException} when the call was
	 *             interrupted before all given jobs finished.</li>
	 *             </ul>
	 * 
	 * @see #wait(String, long)
	 */
	void synchronize(List jobList, long timeout, boolean dispose)
			throws DrmaaException;

	/**
	 * Waits for a job with <code>jobName</code> to finish execution or fail
	 * and disposes of (<em>reaps</em>) the job's internal data (i.e.
	 * statistical information such as system resource consumption).
	 * 
	 * Generally, this method may be called once per <code>jobName</code>,
	 * except if the call timed out before the corresponding job finished or
	 * failed.
	 * 
	 * @param jobName
	 *            The id of the job for which to wait. If
	 *            {@link #JOB_IDS_SESSION_ANY} is provided as the
	 *            <code>jobName</code>, this method will wait for any job
	 *            submitted during this DRMAA session.
	 * @param timeout
	 *            The maximum number of seconds to wait. The
	 *            {@link #TIMEOUT_WAIT_FOREVER} constant may be used for
	 *            indefinite waiting, while the {@link #TIMEOUT_NO_WAIT}
	 *            constant may be used for immediate returning. Alternatively, a
	 *            number of seconds may be specified.
	 * @return The resource usage and status information as a {@link JobInfo}
	 *         instance.
	 * @throws DrmaaException
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized;</li>
	 *             <li>{@link InvalidJobException} when the job id does not
	 *             represent a valid job; or</li>
	 *             <li>{@link ExitTimeoutException} when the call was
	 *             interrupted before the given job finished or failed.</li>
	 *             </ul>
	 * 
	 * @see #synchronize(List, long, boolean)
	 * @see JobInfo
	 */
	JobInfo wait(String jobName, long timeout)
			throws DrmaaException;

	/**
	 * Returns the program status of the job identified by <code>jobName</code>.
	 * 
	 * @param jobName
	 *            The id of the job whose status is to be retrieved.
	 * @return The program's status, which may be one of
	 *         <ul>
	 *         <li>{@link #UNDETERMINED}</li>
	 *         <li>{@link #QUEUED_ACTIVE}</li>
	 *         <li>{@link #SYSTEM_ON_HOLD}</li>
	 *         <li>{@link #USER_ON_HOLD}</li>
	 *         <li>{@link #USER_SYSTEM_ON_HOLD}</li>
	 *         <li>{@link #RUNNING}</li>
	 *         <li>{@link #SYSTEM_SUSPENDED}</li>
	 *         <li>{@link #USER_SUSPENDED}</li>
	 *         <li>{@link #USER_SYSTEM_SUSPENDED}</li>
	 *         <li>{@link #DONE}</li>
	 *         <li>{@link #FAILED}</li>
	 *         </ul>
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized;</li>
	 *             <li>{@link InvalidJobException} when the job id does not
	 *             represent a valid job; or</li>
	 */
	int getJobProgramStatus(String jobName)
			throws DrmaaException;

	/**
	 * Disengages from the DRM system(s) and perfoms necessary internal cleanup.
	 * 
	 * This routine ends the current DRMAA session, but does not affect any jobs
	 * (i.e. the current job's state remains unchanged). Moreover, any
	 * {@link JobTemplate} instances which have not yet been deleted become
	 * invalid.
	 * 
	 * Furthermore, only a single call to the {@link #exit()} method is allowed
	 * (from a single source), until a subsequent call of {@link #init(String)}.
	 * 
	 * @throws DrmaaException,
	 *             which may be one of the following:
	 *             <ul>
	 *             <li>{@link DrmsExitException} when session exiting failed;
	 *             or</li>
	 *             <li>{@link NoActiveSessionException} when the session has
	 *             not been initialized or {@link #exit()} has already been
	 *             called.</li>
	 *             </ul>
	 * 
	 * @see #init(String)
	 */
	void exit()
			throws DrmaaException;

	/**
	 * Returns a {@link Version} instance containing the major and minor version
	 * numbers of the DRMAA library.
	 * 
	 * @return The version number as a {@link Version} object.
	 * @throws DrmaaException
	 *             which may be a {@link NoActiveSessionException} when the
	 *             session has not been initialized or {@link #exit()} has
	 *             already been called.
	 * 
	 * @see Version
	 */
	Version getVersion()
			throws DrmaaException;

}
