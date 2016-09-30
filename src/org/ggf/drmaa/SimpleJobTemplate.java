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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The {@link SimpleJobTemplate} provides concrete implementations for all
 * abstract methods defined in the {@link JobTemplate} interface. The setters
 * for all required properties store copies of the property values in the
 * appropriate member variables, and the getters for all required properties
 * provide copies of the stored poperty values. In the case of properties of
 * type, {@link Map}, the associated getter returs a reference to the
 * associated member variable wrapped in a call to
 * {@link Collections#unmodifiableMap(Map)}. In the case of properties of type
 * {@link List} the associated getter return a reference to the associated
 * member variable wrapped in a call to
 * {@link Collections#unmodifiableList(List)}. In the case of properties of
 * type {@link Set} the associated getter return a reference to the associated
 * member variabel wrapped in a call to {@link Collections#unmodifiableSet(Set)}.
 * The setters and getters for all optional attributed throw an
 * {@link UnsupportedAttributeException}. The {@link #getAttributeNames()}
 * method returns a list that consists of the names of all required properties
 * and the names returned by calling the {@link #getOptionalAttributeNames()}
 * method.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class SimpleJobTemplate
		implements JobTemplate {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(SimpleJobTemplate.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String remoteCommand;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected List args;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected int jobSubmissionState;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected Map jobEnvironment;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String workingDirectory;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String jobCategory;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String nativeSpecification;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected Set email;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected boolean blockEmail;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected PartialTimestamp startTime;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String jobName;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String inputPath;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String outputPath;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected String errorPath;

	/**
	 * TODO: not yet commented
	 * 
	 */
	protected boolean joinFiles;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean modified;

	/**
	 * The no-args constructor creates a new {@link SimpleJobTemplate} instance
	 * with all property member variabels set to default values.
	 * 
	 * Unless otherwise specified, the default values for a property member
	 * variable is <code> 0</code>, <code>false</code>, or
	 * <code>null</code>, depending on its type.
	 * 
	 */
	public SimpleJobTemplate() {
		this.args = null;
		this.blockEmail = false;
		this.email = null;
		this.errorPath = null;
		this.inputPath = null;
		this.jobCategory = null;
		this.jobEnvironment = null;
		this.jobName = null;
		// XXX: use constant here
		this.jobSubmissionState = 0;
		this.joinFiles = false;
		this.modified = false;
		this.nativeSpecification = null;
		this.outputPath = null;
		this.remoteCommand = null;
		// XXX: not sure whether this should default to current time
		this.startTime = null;
		this.workingDirectory = null;
	}

	/**
	 * This method marks the job template to indicate that its properties have
	 * been modified, causing the next call to the {@link #toString()} method to
	 * recalculate its return value.
	 * 
	 */
	public void modified() {
		this.modified = true;
	}

	/**
	 * This method returns an empty list
	 * 
	 * @return an empty list
	 */
	public Set getOptionalAttributeNames() {
		Set<String> optionalAttributeNames = new HashSet<String>();
		// TODO: not yet implemented.
		return optionalAttributeNames;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for org.ggf.drmaa.JobTemplate
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getRemoteCommand()
	 */
	public String getRemoteCommand()
			throws DrmaaException {
		return this.remoteCommand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setRemoteCommand(java.lang.String)
	 */
	public void setRemoteCommand(String command)
			throws DrmaaException {
		this.remoteCommand = command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getArgs()
	 */
	public List getArgs()
			throws DrmaaException {
		return Collections.unmodifiableList(this.args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setArgs(java.util.List)
	 */
	public void setArgs(List args)
			throws DrmaaException {
		this.args = new ArrayList(args);
		;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getJobSubmissionState()
	 */
	public int getJobSubmissionState()
			throws DrmaaException {
		return this.jobSubmissionState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setJobSubmissionState(int)
	 */
	public void setJobSubmissionState(int state)
			throws DrmaaException {
		this.jobSubmissionState = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getJobEnvironment()
	 */
	public Map getJobEnvironment()
			throws DrmaaException {
		return Collections.unmodifiableMap(this.jobEnvironment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setJobEnvironment(java.util.Map)
	 */
	public void setJobEnvironment(Map env)
			throws DrmaaException {
		this.jobEnvironment = new HashMap(env);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getWorkingDirectory()
	 */
	public String getWorkingDirectory()
			throws DrmaaException {
		return this.workingDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setWorkingDirectory(java.lang.String)
	 */
	public void setWorkingDirectory(String wd)
			throws DrmaaException {
		this.workingDirectory = wd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getJobCategory()
	 */
	public String getJobCategory()
			throws DrmaaException {
		return this.jobCategory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setJobCategory(java.lang.String)
	 */
	public void setJobCategory(String category)
			throws DrmaaException {
		this.jobCategory = category;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getNativeSpecification()
	 */
	public String getNativeSpecification()
			throws DrmaaException {
		return this.nativeSpecification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setNativeSpecification(java.lang.String)
	 */
	public void setNativeSpecification(String spec)
			throws DrmaaException {
		this.nativeSpecification = spec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getEmail()
	 */
	public Set getEmail()
			throws DrmaaException {
		return Collections.unmodifiableSet(this.email);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setEmail(java.util.Set)
	 */
	public void setEmail(Set email)
			throws DrmaaException {
		this.email = new HashSet(email);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getBlockEmail()
	 */
	public boolean getBlockEmail()
			throws DrmaaException {
		return this.blockEmail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setBlockEmail(boolean)
	 */
	public void setBlockEmail(boolean blockEmail)
			throws DrmaaException {
		this.blockEmail = blockEmail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getStartTime()
	 */
	public PartialTimestamp getStartTime()
			throws DrmaaException {
		return this.startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setStartTime(org.ggf.drmaa.PartialTimestamp)
	 */
	public void setStartTime(PartialTimestamp startTime)
			throws DrmaaException {
		this.startTime = startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getJobName()
	 */
	public String getJobName()
			throws DrmaaException {
		return this.jobName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setJobName(java.lang.String)
	 */
	public void setJobName(String name)
			throws DrmaaException {
		this.jobName = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getInputPath()
	 */
	public String getInputPath()
			throws DrmaaException {
		return this.inputPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setInputPath(java.lang.String)
	 */
	public void setInputPath(String inputPath)
			throws DrmaaException {
		this.inputPath = inputPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getOutputPath()
	 */
	public String getOutputPath()
			throws DrmaaException {
		return this.outputPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setOutputPath(java.lang.String)
	 */
	public void setOutputPath(String outputPath)
			throws DrmaaException {
		this.outputPath = outputPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getErrorPath()
	 */
	public String getErrorPath()
			throws DrmaaException {
		return this.errorPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setErrorPath(java.lang.String)
	 */
	public void setErrorPath(String errorPath)
			throws DrmaaException {
		this.errorPath = errorPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getJoinFiles()
	 */
	public boolean getJoinFiles()
			throws DrmaaException {
		return this.joinFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setJoinFiles(boolean)
	 */
	public void setJoinFiles(boolean joinFiles)
			throws DrmaaException {
		this.joinFiles = joinFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getTransferFiles()
	 */
	public FileTransferMode getTransferFiles()
			throws DrmaaException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setTransferFiles(org.ggf.drmaa.FileTransferMode)
	 */
	public void setTransferFiles(FileTransferMode mode)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getDeadlineTime()
	 */
	public PartialTimestamp getDeadlineTime()
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setDeadlineTime(org.ggf.drmaa.PartialTimestamp)
	 */
	public void setDeadlineTime(PartialTimestamp deadline)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getHardWallclockTimeLimit()
	 */
	public long getHardWallclockTimeLimit()
			throws DrmaaException {
		// TODO: not yet implemented.
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setHardWallclockTimeLimit(long)
	 */
	public void setHardWallclockTimeLimit(long limit)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getSoftWallClockTimeLimit()
	 */
	public long getSoftWallClockTimeLimit()
			throws DrmaaException {
		// TODO: not yet implemented.
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setSoftWallClockTimeLimit(long)
	 */
	public void setSoftWallClockTimeLimit(long limit)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getHardRunDurationLimit()
	 */
	public long getHardRunDurationLimit()
			throws DrmaaException {
		// TODO: not yet implemented.
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setHardRunDurationLimit(long)
	 */
	public void setHardRunDurationLimit(long limit)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getSoftRunDurationLimit()
	 */
	public long getSoftRunDurationLimit()
			throws DrmaaException {
		// TODO: not yet implemented.
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#setSoftRunDurationLimit(long)
	 */
	public void setSoftRunDurationLimit(long limit)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ggf.drmaa.JobTemplate#getAttributeNames()
	 */
	public Set getAttributeNames()
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/**
	 * This method returns a string representation of the job template instance
	 * which includes the values for all properties. (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// XXX: what about optionals?
		return new ToStringBuilder(this).append("args", this.args).append(
				"remoteCommand", this.remoteCommand).append(
				"jobSumbissionState", this.jobSubmissionState).append(
				"jobEnvironment", this.jobEnvironment).append(
				"workingDirectory", this.workingDirectory).append(
				"jobCategory", this.jobCategory).append("nativeSpecification",
				this.nativeSpecification).append("email", this.email).append(
				"blockEmail", this.blockEmail).append("startTime",
				this.startTime).append("jobName", this.jobName).append(
				"inputPath", this.inputPath).append("outputPath",
				this.outputPath).append("errorPath", this.errorPath).append(
				"joinFiles", this.joinFiles).appendSuper(super.toString())
				.toString();
	}

}
