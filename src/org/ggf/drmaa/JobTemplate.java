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
import java.util.Map;
import java.util.Set;

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
public interface JobTemplate {

	/**
	 * TODO: not yet commented
	 * 
	 */
	final static int HOLD_STATE = 0;

	/**
	 * TODO: not yet commented
	 * 
	 */
	final static int ACTIVE_STATE = 1;

	/**
	 * TODO: not yet commented
	 * 
	 */
	final static String HOME_DIRECTORY = "$drmaa_hd_ph$";

	/**
	 * TODO: not yet commented
	 * 
	 */
	final static String WORKING_DIRECTORY = "$drmaa_wd_ph$";

	/**
	 * TODO: not yet commented
	 * 
	 */
	final static String PARAMETRIC_INDEX = "$drmaa_incr_ph$";

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getRemoteCommand()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param command
	 * @throws DrmaaException
	 */
	void setRemoteCommand(String command)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	List getArgs()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param args
	 * @throws DrmaaException
	 */
	void setArgs(List args)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	int getJobSubmissionState()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param state
	 * @throws DrmaaException
	 */
	void setJobSubmissionState(int state)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	Map getJobEnvironment()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param env
	 * @throws DrmaaException
	 */
	void setJobEnvironment(Map env)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getWorkingDirectory()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param wd
	 * @throws DrmaaException
	 */
	void setWorkingDirectory(String wd)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getJobCategory()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param category
	 * @throws DrmaaException
	 */
	void setJobCategory(String category)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getNativeSpecification()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param spec
	 * @throws DrmaaException
	 */
	void setNativeSpecification(String spec)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	Set getEmail()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param email
	 * @throws DrmaaException
	 */
	void setEmail(Set email)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	boolean getBlockEmail()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param blockEmail
	 * @throws DrmaaException
	 */
	void setBlockEmail(boolean blockEmail)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	PartialTimestamp getStartTime()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param startTime
	 * @throws DrmaaException
	 */
	void setStartTime(PartialTimestamp startTime)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getJobName()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param name
	 * @throws DrmaaException
	 */
	void setJobName(String name)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getInputPath()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param inputPath
	 * @throws DrmaaException
	 */
	void setInputPath(String inputPath)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getOutputPath()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param outputPath
	 * @throws DrmaaException
	 */
	void setOutputPath(String outputPath)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	String getErrorPath()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param errorPath
	 * @throws DrmaaException
	 */
	void setErrorPath(String errorPath)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	boolean getJoinFiles()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param joinFiles
	 * @throws DrmaaException
	 */
	void setJoinFiles(boolean joinFiles)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	FileTransferMode getTransferFiles()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param mode
	 * @throws DrmaaException
	 */
	void setTransferFiles(FileTransferMode mode)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	PartialTimestamp getDeadlineTime()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param deadline
	 * @throws DrmaaException
	 */
	void setDeadlineTime(PartialTimestamp deadline)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	long getHardWallclockTimeLimit()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param limit
	 * @throws DrmaaException
	 */
	void setHardWallclockTimeLimit(long limit)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	long getSoftWallClockTimeLimit()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param limit
	 * @throws DrmaaException
	 */
	void setSoftWallClockTimeLimit(long limit)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	long getHardRunDurationLimit()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param limit
	 * @throws DrmaaException
	 */
	void setHardRunDurationLimit(long limit)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	long getSoftRunDurationLimit()
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @param limit
	 * @throws DrmaaException
	 */
	void setSoftRunDurationLimit(long limit)
			throws DrmaaException;

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 * @throws DrmaaException
	 */
	Set getAttributeNames()
			throws DrmaaException;

}
