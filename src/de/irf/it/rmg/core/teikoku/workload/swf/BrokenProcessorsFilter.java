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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.ComputeSite;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.teikoku.workload.WorkloadFilter;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSink;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

import mx.cicese.dcc.teikoku.information.broker.SiteInformation;
import mx.cicese.dcc.teikoku.information.broker.SiteInformationBroker;
/**
 * Checks whether (a) the jobs resource requirements fit to the given site's
 * size and (b) the given job has same values for the
 * 'requestedNumberOfProcessors' field.
 * 
 * More formally, it is tested whether
 * 
 * <pre>
 *         Requested Number of Processors &amp;le [;MaxProcs|;MaxNodes] 
 * </pre>
 * 
 * is true (testing is done in order, whichever header field is available -- if
 * none is set, the site's {@link ComputeSite#getProvidedResources()}<code>.size()</code>
 * value is used).
 * 
 * Furthermore, since many of the public available workloads do not contain sane
 * values for the 'requestedNumberOfProcessors' field, it is checked whether
 * these fields match. If this is not the case and fixing is allowed,
 * 
 * <pre>
 *         Requested Number Of Processors := Number Of Allocated Processors
 * </pre>
 * 
 * is set. Otherwise, the job is dropped.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class BrokenProcessorsFilter
		implements WorkloadFilter {

	/**
	 * Holds the workload sink this filter refers to.
	 * 
	 * @see WorkloadSink
	 */
	private SiteInformation siteInformation;

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(BrokenProcessorsFilter.class);
	
	
	/**
	 * The largest machine in the Grid
	 */
	private long maxMachineSize;

			
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param siteInformation
	 *            The workload sink this filter refers to.
	 * 
	 * @see WorkloadSink
	 */
	public BrokenProcessorsFilter(SiteInformation siteInformation) {
			
		this.siteInformation = siteInformation;
		
		/* Gets the maximum machine size */
		maxMachineSize = -1;
		List<SiteInformationBroker> knownSites = new LinkedList<SiteInformationBroker>();
		
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for (Site s:sites.values()){
			int size = s.getSiteInformation().getNumberOfAvailableResources();
			if( size > maxMachineSize )
				maxMachineSize = size;
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.workload.WorkloadFilter#apply(de.irf.it.rmg.core.teikoku.site.Site,
	 *      de.irf.it.rmg.core.teikoku.Job, boolean)
	 */
	public boolean apply(Job job, boolean fix) {
		boolean result = false;

		if (job instanceof SWFJob) {
			SWFJob swfJob = (SWFJob) job;
			result = this.sanitizeRequestedAndAllocatedResources(swfJob, fix);
		} // if
		
		if (this.checkResourceRequirements(this.siteInformation, job)) {
			result = true;
		} // if
		else {
			result = false;
		} // else

		
		return result;
	}

	/**
	 * Checks whether the job's number of requested processors is larger than
	 * the site's number of available processors. If this is the case, mark this
	 * job as invalid.
	 * 
	 * It also checks if the number of requested resources exceed the maximum 
	 * machine size.
	 * 
	 * @param site
	 *            The site the job is supposed to run on.
	 * @param job
	 *            The job to be checked.
	 * @return <code>true</code>, iif the job's processor requirements do not
	 *         exceed the site's size; <code>false</code>, otherwise.
	 */
	private boolean checkResourceRequirements(SiteInformation siteInformation, Job job) {
		boolean result = true;

		
		int maxResources = siteInformation.getNumberOfAvailableResources();

		int requestedNumberOfProcessors = job.getDescription().getNumberOfRequestedResources();

		// This line discards all jobs that require more resources than the maximum machine size in the Grid.
		if(requestedNumberOfProcessors > maxMachineSize) {
			return false;
		}
		
		// This line has been added to use site 0 has the Grid broker. It has -1 resources
		if (maxResources == 1)
			return result;
				
		if (requestedNumberOfProcessors > maxResources) {
			String msg = "job \""
					+ job.getName()
					+ " ("
					+ job.getUUID()
					+ ")\" contains '"
					+ SWFConstants.JobFields.REQUESTED_NUMBER_OF_PROCESSORS
					+ "' field greater than the number of resources generally available to the referred workload sink; marking as invalid";
			//log.warn(msg);
			result = false;
		} // if

		return result;
	}

	/**
	 * Checks whether the number of initially requested resources is different
	 * from the number of finally allocated resources, and fixes it, if
	 * applicable.
	 * 
	 * 
	 * @param job
	 *            The job to be sanitized.
	 * 
	 * @param fix
	 *            A flag whether to fix or to drop the given job.
	 * 
	 * @return The given job, if it was valid or fixed; <code>null</code>
	 *         otherwise.
	 */
	private boolean sanitizeRequestedAndAllocatedResources(SWFJob job,
			boolean fix) {
		boolean result = true;

		int requestedNumberOfProcessors = job.getRequestedNumberOfProcessors();
		int allocatedNumberOfProcessors = job.getNumberOfAllocatedProcessors();

		if (requestedNumberOfProcessors != allocatedNumberOfProcessors) {
			String msg = "job \"" + job.getName() + " (" + job.getUUID()
					+ ")\" contains '"
					+ SWFConstants.JobFields.REQUESTED_NUMBER_OF_PROCESSORS
					+ "' field different from '"
					+ SWFConstants.JobFields.NUMBER_OF_ALLOCATED_PROCESSORS
					+ "'; ";
			if (fix) {

				if (allocatedNumberOfProcessors <= 0 && requestedNumberOfProcessors > 0) {
					allocatedNumberOfProcessors = requestedNumberOfProcessors;
					job.setNumberOfAllocatedProcessors(allocatedNumberOfProcessors);
					msg += "fixing with '"
							+ SWFConstants.JobFields.REQUESTED_NUMBER_OF_PROCESSORS
							+ "' field value";
				} else if (requestedNumberOfProcessors <= 0 && allocatedNumberOfProcessors > 0) {
					requestedNumberOfProcessors = allocatedNumberOfProcessors;
					job.setRequestedNumberOfProcessors(requestedNumberOfProcessors);
					msg += "fixing with '"
							+ SWFConstants.JobFields.NUMBER_OF_ALLOCATED_PROCESSORS
							+ "' field value";
				} else {
					requestedNumberOfProcessors = allocatedNumberOfProcessors;
					job.setRequestedNumberOfProcessors(requestedNumberOfProcessors);
					msg += "fixing with '"
							+ SWFConstants.JobFields.NUMBER_OF_ALLOCATED_PROCESSORS
							+ "' field value";
				} // else
				result = true;
			} else {
				msg += "marking as invalid";
				result = false;
			} // else

		} else if (allocatedNumberOfProcessors <= 0	&& requestedNumberOfProcessors <= 0) {
			String msg = "job \"" + job.getName() + " (" + job.getUUID()
					+ ")\" contains '"
					+ SWFConstants.JobFields.REQUESTED_NUMBER_OF_PROCESSORS
					+ "' field different from '"
					+ SWFConstants.JobFields.NUMBER_OF_ALLOCATED_PROCESSORS
					+ "'; ";
			msg += "marking as invalid";
			result = false;
		} // else if
		
		return result;
	}
}
