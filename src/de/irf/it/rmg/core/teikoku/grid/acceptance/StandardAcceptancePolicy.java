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
package de.irf.it.rmg.core.teikoku.grid.acceptance;

import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSource;
import de.irf.it.rmg.core.teikoku.job.ExecutableFitsSite;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.Preselector;

/**
 * This is the StandardAcceptancePolicy, which gots the following properties:
 * - it has a jobPreselector, which filters the jobs, which need more ressources the site has got
 * - The Acceptance-Behaviour is given by the remoteTransferPolicy
 * - It does not block locally submitted Jobs
 * - The scheduling for a locally submitted Job takes place in several phases:
 * 		1.)	Order the knownSites by the given locationPolicy
 * 		2.) Make the acceptance-Decision with regard to the remoteTransferPolicy and the ActivityDelegationTarget for each site
 * 		3a.) If the job should be scheduled locally, break the loop and accept the Job locally
 * 		3b.) if the Job shoul be scheduled remotely, ask the ActivityDelegationTarget, if it is willing to absorb the Job �bernehmen
 * 		4.) If no "Partner" wants to take the job it has to remain on the submission-site
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public class StandardAcceptancePolicy extends AbstractAcceptancePolicy {
	
	public StandardAcceptancePolicy(){
		super();
	}

	/**
	 * Submission Preselector to forbid Jobs with higher ressourcedemand 
	 * than existing ressources on the local site
	 */
	private Preselector jobPreselector;
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.acceptance.AcceptancePolicy#decideRemoteJobs(de.irf.it.rmg.core.teikoku.grid.DelegationSession)
	 */
	public boolean decideRemoteJob(Job job,ActivityDelegationSource creator) {
		if (jobPreselector==null){
			//Initialize the JobPreselector
			jobPreselector=new ExecutableFitsSite();
			jobPreselector.setSiteInformation(getActivityBroker().getSite().getSiteInformation());
		}
		
		if (jobPreselector.preselect(job) && super.getTransferPolicy().decide(job,creator)){
			return true;
		} else return false;
	}


	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.acceptance.AcceptancePolicy#decideLocalJobs(de.irf.it.rmg.core.teikoku.grid.DelegationSession)
	 */
	public boolean decideLocalJob(Job job) {
		//in this Implementation the Responsibility of any local Job is taken over
		super.getActivityBroker().getListOfManagedJobs().add(job);
		return true;
	}

}
