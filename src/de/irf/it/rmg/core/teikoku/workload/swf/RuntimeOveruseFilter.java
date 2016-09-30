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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.WorkloadFilter;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;

/**
 * Checks whether the requested runtime of this job is smaller than the actual
 * runtime <em>and</em> the site forbids temporal resource overuse. More
 * formally, it is checked whether
 * 
 * <pre>
 *    Requested Time &lt; Run Time | ;AllowOverUse: False
 * </pre>
 * 
 * is true. If fixation is allowed, then the actual runtime is set to the user's
 * requested time, that is
 * 
 * <pre>
 *    Run Time := Requested Time
 * </pre>
 * 
 * is performed. Otherwise, the job is purged and the filter returns
 * <code>null</code>.
 * 
 * The rationale behind this filter is that most overuse-forbidding systems kill
 * jobs which run longer than requested.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class RuntimeOveruseFilter
		implements WorkloadFilter {

	/**
	 * Stores whether this filter allows the overuse of run time for jobs.
	 * 
	 */
	private boolean allowOveruse = false;

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(RuntimeOveruseFilter.class);

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param allowOveruse
	 *            Determines whether the overuse of run time is allowed
	 */
	public RuntimeOveruseFilter(boolean allowOveruse) {
		this.allowOveruse = allowOveruse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadFilter#apply(de.irf.it.rmg.core.teikoku.site.Site,
	 *      de.irf.it.rmg.core.teikoku.Job, boolean)
	 */
	public boolean apply(Job job, boolean fix) {
		boolean result = true;

		if (job instanceof SWFJob) {
			SWFJob swfJob = ( SWFJob ) job;
			if (!this.allowOveruse) {
				long requestedTime = DateHelper.convertToMilliseconds(swfJob
						.getRequestedTime());
				long runTime = DateHelper.convertToMilliseconds(swfJob

				.getRunTime());
				if (requestedTime < runTime) {
					String msg = "job \"" + swfJob.getName() + " ("
							+ swfJob.getUUID() + ")\""
							+ " contains invalid 'runTime' field for "
							+ "'allowOveruse=false' header";
					if (fix) {
						msg += "; fixing with 'requestedTime' field value";
						swfJob.setRunTime(DateHelper.convertToSeconds(requestedTime));
						result = true;
					} // if
					else {
						msg += "; skipping";
						result = false;
					} // else
					//log.warn(msg);
				} // if
			} // if
		} // if
		else {
			result = true;
		} // else

		return result;
	}
}
