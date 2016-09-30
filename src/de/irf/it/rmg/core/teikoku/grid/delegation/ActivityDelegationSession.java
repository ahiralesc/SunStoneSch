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
package de.irf.it.rmg.core.teikoku.grid.delegation;

import java.util.List;

import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.SessionInvalidException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * the ActivityDelegationsession represents a handle for joboffers, so the receiving site/ activityBroker is able
 * to accept offered Jobs synchronized with other competitors
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public class ActivityDelegationSession extends DelegationSession{

	/**
	 *Holds the offered Jobs
	 */
	private List<Job> joblist;
	
	/**
	 * The Producer of this ActivityDelegationSession
	 */
	private ActivityDelegationSource creator;

	/**
	 * Constructor
	 * @param lifetime
	 */
	public ActivityDelegationSession(Instant expiration,ActivityDelegationSource creator,List<Job> joblist){
		super(expiration);
		this.joblist=joblist;
		this.creator=creator;
	}
	
	/**
	 * checks, whether the session is actually still valid
	 * @return boolean
	 */
	public boolean isValid(){
		return (super.getExpiration().isEternity() || Clock.instance().now().before(super.getExpiration()));
	}
	
	/**
	 * standard-getter for the List of Jobs managed in this session
	 * @return List<Job>
	 */
	public List<Job> getJoblist() {
		return joblist;
	}

	/**
	 * Standard-getter to get the creator of this session
	 * @return ActivityDelegationSource
	 */
	public ActivityDelegationSource getCreator() {
		return creator;
	}

	/**
	 * Method to accept a single job of that session. if that action fails for example if the job
	 * is not available anymore a AcceptanceVetoException will be thrown
	 * 
	 * @param job
	 * @throws AcceptanceVetoException
	 * @throws SessionInvalidException
	 */
	public void accept(Job job) throws AcceptanceVetoException, SessionInvalidException{
		if (!this.isValid()){
			throw new SessionInvalidException("The DelegationSession is expired");
		}
		try{
			//First remove this job from local jobList
			//joblist.remove(job);
			//try to remove Jobs from List of distributedJobs
			creator.delegate(job);
		}
		catch(Exception e){
			throw new AcceptanceVetoException("The Job could not be delegated");
		}
	}

}
