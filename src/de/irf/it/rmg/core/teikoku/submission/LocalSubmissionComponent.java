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
/**
 * 
 */
package de.irf.it.rmg.core.teikoku.submission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.cicese.dcc.teikoku.workload.job.JobType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.grid.delegation.DelegationFabric;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobReleasedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
 * This component represents the entrance for jobs into the system, a very simple DRMAA if you want.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public class LocalSubmissionComponent implements ActivityDelegationSource{

	/**
	 * Filed for the site, the LocalSubmissionComponent is connected with
	 */
	private Site site;
	
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(ActivityBroker.class);

	
	/**
	 * Standard-constructor
	 * @param site
	 */
	public LocalSubmissionComponent(Site site){
		this.site=site;
	}

	/**
	 * standard-getter
	 * @return Site
	 */
	public Site getSite() {
		return site;
	}
	
	/**
	 * standard-setter
	 * @param site
	 */
	public void setSite(Site site){
		this.site=site;
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.DelegationSource#getPublicInformation()
	 */
	public Map<String, Object> getPublicInformation() {
		//there is no public information stored
		return null;
	}


	
	/**
	 * This method serves for Initialization of the localSubmissionComponent
	 */
	public void initialize(){
	}

	public void delegate(Job job) throws AcceptanceVetoException {
		//nothing todo for Delegation
	}

	public void deliverReleaseEvent(JobReleasedEvent event) {
		// Eliminacion de tarea de la carga vacia
		if(event.getReleasedJob().getJobType().equals(JobType.INDEPENDENT)) {
			if(((SWFJob)event.getReleasedJob()).getJobNumber() == 0) 
				return;
		}
		
		List<Job> joblist=new ArrayList<Job>();
		joblist.add(event.getReleasedJob());
		//Create new Session for the released Job
		ActivityDelegationSession session=DelegationFabric.generateActivityDelegationSession(joblist,this);
		try {
			if( this.getSite().getActivityBroker() != null)
				this.getSite().getActivityBroker().offer(session);
			else
				this.getSite().getGridActivityBroker().offer(session);
		} catch (OfferingVetoException e) {
			e.printStackTrace();
		}
	} 
}
