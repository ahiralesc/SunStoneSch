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
package de.irf.it.rmg.core.teikoku.site.drmaa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.InvalidJobTemplateException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.Version;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.util.Initializable;


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
public final class SessionImpl
		implements Initializable, Session {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(SessionImpl.class);
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	final private static int VERSION_MAJOR = 1;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	final private static int VERSION_MINOR = 0;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private Set<JobTemplate> managedJobTemplates;
	
	/**
	 * TODO: not yet commented
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	public SessionImpl() {
		this.initialized = false;
		this.managedJobTemplates = new HashSet<JobTemplate>();
	}
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Initializable
	// -------------------------------------------------------------------------

	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.util.Initializable#isInitialized()
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see de.irf.it.rmg.core.util.Initializable#initialize()
	 */
	public void initialize()
			throws InitializationException {
		// TODO: not yet implemented.
		
	}
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for org.ggf.drmaa.Session 
	// -------------------------------------------------------------------------

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#control(java.lang.String, int)
	 */
	public void control(String jobName, int operation)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#createJobTemplate()
	 */
	public JobTemplate createJobTemplate()
			throws DrmaaException {
		JobTemplate result = new TeikokuJobTemplate();
		this.managedJobTemplates.add(result);
		return result;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#deleteJobTemplate(org.ggf.drmaa.JobTemplate)
	 */
	public void deleteJobTemplate(JobTemplate jobTemplate)
			throws DrmaaException {
		boolean removed = this.managedJobTemplates.remove(jobTemplate);
		if (!removed) {
			String msg = "";
			throw new InvalidJobTemplateException(msg);
		}
		// TODO: not yet implemented.

	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#exit()
	 */
	public void exit()
			throws DrmaaException {
		// TODO: not yet implemented.
		this.initialized = false;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#getContact()
	 */
	public String getContact()
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#getDrmaaImplementation()
	 */
	public String getDrmaaImplementation()
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#getDrmsInfo()
	 */
	public String getDrmsInfo()
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#getJobProgramStatus(java.lang.String)
	 */
	public int getJobProgramStatus(String jobName)
			throws DrmaaException {
		// TODO: not yet implemented.
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#getVersion()
	 */
	public Version getVersion()
			throws DrmaaException {
		return new Version(VERSION_MAJOR, VERSION_MINOR);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#init(java.lang.String)
	 */
	public void init(String contactString)
			throws DrmaaException {
		// TODO: not yet implemented.
		this.initialized = true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#runBulkJobs(org.ggf.drmaa.JobTemplate, int, int, int)
	 */
	public List runBulkJobs(JobTemplate jobTemplate, int beginIndex,
			int endIndex, int step)
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#runJob(org.ggf.drmaa.JobTemplate)
	 */
	public String runJob(JobTemplate jobTemplate)
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#synchronize(java.util.List, long, boolean)
	 */
	public void synchronize(List jobList, long timeout, boolean dispose)
			throws DrmaaException {
		// TODO: not yet implemented.

	}

	/* 
	 * (non-Javadoc)
	 * @see org.ggf.drmaa.Session#wait(java.lang.String, long)
	 */
	public JobInfo wait(String jobName, long timeout)
			throws DrmaaException {
		// TODO: not yet implemented.
		return null;
	}
}
