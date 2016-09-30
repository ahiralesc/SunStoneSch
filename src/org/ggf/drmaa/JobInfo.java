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

import java.util.Map;

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
public interface JobInfo {

	/**
	 * Returns the identifier of the completed job.
	 * 
	 * @return The identifier of the completed job.
	 * @throws DrmaaException
	 */
	String getJobId()
			throws DrmaaException;

	/**
	 * Returns the resource usage data of the completed job.
	 * 
	 * @return The resource usage data of the completed job. Note that the
	 *         format of the returned utilization statistics is
	 *         implementation-dependent.
	 * @throws DrmaaException
	 */
	Map getResourceUsage()
			throws DrmaaException;

	/**
	 * Denotes whether this job has completed normally.
	 * 
	 * @return <code>true</code>, if the job terminated normally (on POSIX
	 *         systems, with an exit code of <code>0</code>, that is);
	 *         <code>false</code>, otherwise.
	 * @throws DrmaaException
	 */
	boolean hasExited()
			throws DrmaaException;

	/**
	 * Returns the operating system exit code of the job.
	 * 
	 * @return The operating system exit code of the job.
	 * @throws DrmaaException,
	 *             which may be a {@link IllegalStateException} when no exit
	 *             state information is available.
	 */
	int getExitStatus()
			throws DrmaaException;

	/**
	 * Denotes whether the job has terminated due to the receipt of a signal.
	 * 
	 * @return <code>true</code>, if the job has terminated due to the
	 *         receipt of a signal; <code>false</code>, otherwise.
	 * @throws DrmaaException
	 */
	boolean hasSignaled()
			throws DrmaaException;

	/**
	 * Returns a POSIX string representation of the signal that caused the
	 * termination of the job.
	 * 
	 * @return A POSIX string representation of the signal that caused the
	 *         termination of the job.
	 * @throws DrmaaException
	 *             which may be a {@link IllegalStateException} when the job did
	 *             not terminate due to the receipt of a signal.
	 */
	String getTerminatingSignal()
			throws DrmaaException;

	/**
	 * Denotes whether during job termination a core image has been created.
	 * 
	 * @return <code>true</code>, if a core image has been created;
	 *         <code>false</code>, otherwise.
	 * @throws DrmaaException
	 *             which may be a {@link IllegalStateException} when the job did
	 *             not terminate due to the receipt of a signal.
	 */
	boolean hasCoreDump()
			throws DrmaaException;

	/**
	 * Denotes whether the job ended before entering the {@link DelegationSession#RUNNING}
	 * state.
	 * 
	 * @return <code>true</code>, if the job did not reach the
	 *         {@link DelegationSession#RUNNING} state before ending; <code>false</code>,
	 *         otherwise.
	 * @throws DrmaaException
	 */
	boolean wasAborted()
			throws DrmaaException;

}
