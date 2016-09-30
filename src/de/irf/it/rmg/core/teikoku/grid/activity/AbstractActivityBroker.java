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
package de.irf.it.rmg.core.teikoku.grid.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.AcceptanceVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.DequeuingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.NoJobRequestAllowedException;
import de.irf.it.rmg.core.teikoku.exceptions.OfferingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.SessionInvalidException;
import de.irf.it.rmg.core.teikoku.grid.acceptance.AcceptancePolicy;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationSession;
import de.irf.it.rmg.core.teikoku.grid.delegation.ActivityDelegationTarget;
import de.irf.it.rmg.core.teikoku.grid.delegation.DelegationFabric;
import de.irf.it.rmg.core.teikoku.grid.distribution.DistributionPolicy;
import de.irf.it.rmg.core.teikoku.grid.location.LocationPolicy;
import de.irf.it.rmg.core.teikoku.grid.transfer.TransferPolicy;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.submission.LocalSubmissionComponent;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;

import mx.cicese.dcc.teikoku.broker.ActivityBrokerRole;
import mx.cicese.dcc.teikoku.energy.EnergyBroker;
import mx.cicese.dcc.teikoku.scheduler.Dispatcher;


/**
 * This abstrat class is combining common functionalities of all Activitybroker like initialization of
 * necessary components like locationPolicy, acceptancePolicy and distributionPolicy and
 * provides some fields and associated getters and setters for references to necessary components 
 * 
* @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>,<a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a> and <a
 *         href="mailto:adan.hirales@cetys.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */

public abstract class AbstractActivityBroker implements ActivityBroker{

	/**
	 * Holds the necessary distributionPolicy for the distribution-behaviour of the
	 * Broker and for Generation of DelegationSessions
	 */
	private DistributionPolicy distributionPolicy;
	
	/**
	 * Holds the acceptancePolicy of the Broker, the most important policy, which decides about what jobs
	 * should be accepted or denied and respectively offered to other Broker
	 */
	private AcceptancePolicy acceptancePolicy;
	
	/**
	 * Holds the locationPolicy, which is important to generate an order of the known Broker, when a job should
	 * be offered to another site
	 */
	private LocationPolicy locationPolicy;
	
	/**
	 * Holds a reference to the site the broker is connected with
	 */
	private Site site;
	/**
	 * Holds a list of known broker
	 */
	private List<ActivityBroker> knownActivityBroker=new ArrayList<ActivityBroker>();
	/**
	 * Holds a List of valid Sessions, which can be used to accept remote Jobs
	 */
	private List<ActivityDelegationSession> activeSessions=new ArrayList<ActivityDelegationSession>();
	
	/**
	 * This is a list of all managedJobs, which is synchronized to implement, that in each moment each job is present
	 * on ONE site
	 */
	private List<Job> managedJobs=Collections.synchronizedList(new ArrayList<Job>());
	/**
	 * This Map holds the public Information of the site, for default its only the number of
	 * given resources
	 */
	private Map<String, Object> publicInformation=new HashMap<String, Object>();
	
	/**
	 * Differentiates the role of a site:
	 * - If set to true, the site role is grid broker.
	 * - If set to false, the site role is a compute or data site
	 * 
	 * The role is determined when this site is associated to a activity broker (compute or data site) 
	 * or a grid activity broker (grid broker).
	 */
	private ActivityBrokerRole role;

	/**
	 * This method is used to make an offer of Jobs to this Broker by giving
	 * it
	 * @param ActivityDelegationSession session
	 */
	public void offer(ActivityDelegationSession session) throws OfferingVetoException{
		//Add the offered session to the list of activeSessions, maybe for later use
		getActiveSessions().add(session);
		
		/** 
		 * The job was forward via the Grid layer dispatcher
		 * - No load balancing is conducted 
		 */
		if (session.getCreator() instanceof Dispatcher) {
			//Must create the jobReleaseEvent
			for (Job j:session.getJoblist()){
				//if (acceptancePolicy.decideRemoteJob(j,session.getCreator())){
					try {
						session.accept(j);
						manageNewJob(j);
					} catch (AcceptanceVetoException e) {
						//Maybe another site accepted the Job first
						//or the job owner itself decides to execute the Job
					} catch (SessionInvalidException e) {
						//The session is to old, so it has to be removed from the list of activeSessions
						//In other implementations, the acceptancePolicy could make a query to another site
						//to get a new valid session for their Jobs
						getActiveSessions().remove(session);
					}
				//} //End if acceptance policy
			} // end for 
			return;
		}// end if instance of GridactivityBroker
		
		
		if (session.getCreator() instanceof ActivityBroker){
			//The session has been offered by another activityBroker and must be handled as a remote Job
			List<Job> jobs=session.getJoblist();
			for (Job j:jobs){
				if (acceptancePolicy.decideRemoteJob(j,session.getCreator())){
					try {
						
						//Try to accept the Job
						session.accept(j);
						//if job is accepted it can be scheduled locally
						//In this Implementation every overtaken job is directly given to the LRMS
						//There is no distribution of accepted jobs to further sites, which is possible in principle
						manageNewJob(j);
					} catch (AcceptanceVetoException e) {
						//Maybe another site accepted the Job first
						//or the jobowner itself decides to execute the Job
					} catch (SessionInvalidException e) {
						//The session is to old, so it has to be removed from the list of activeSessions
						//In other implementations, the acceptancePolicy could make a query to another site
						//to get a new valid session for theire Jobs
						getActiveSessions().remove(session);
					}
				}
			}
			
		} else {	
			if (session.getCreator() instanceof LocalSubmissionComponent) {
				//Local submission of Jobs
				for (Job j:session.getJoblist()){
					j.setReleasedSite(getSite());
					j.getLifecycle().addEpisode(State.RELEASED);
					j.getProvenance().addEpisode(getSite());
					if (!acceptancePolicy.decideLocalJob(j)){
						//A local job was declined
						throw new OfferingVetoException("Local Job was declined");
					}
					//in this Implementation use the DistributionPolicy for locally submitted
					//jobs to decide, whether the Job should be executed locally or beeing delegated to other
					//sites
					List<ActivityBroker> activityBrokerToAsk=locationPolicy.getOrder(getKnownActivityBroker());
					boolean accepted=false;
					
					
					
					for (ActivityBroker ab:activityBrokerToAsk){
						//Check for one Partner after another
						if ((distributionPolicy.decide(j,ab))){
							//Check, if the Job is still scheduable locally or maybe taken over by a remote site
							if (getListOfManagedJobs().contains(j)){
								
								//the job is still scheduable
								site.getScheduler().putNextJob(j);
								accepted=true;
							}
							//The Job is scheduled, so the loop can be breaked
							break;
						} else{
							try {
								//The Job should be scheduled remotely
								ActivityDelegationSession remoteSession=DelegationFabric.generateActivityDelegationSession(j,this);
								ab.offer(remoteSession);
							} catch (OfferingVetoException e) {
								e.printStackTrace();
							}
						}
					}//end for activityBroker
					
					//Check, if no other Broker has accepted the Job
					//In this Implementation a job, that has not been taken directly is not been accepted
					//may be different in other Implementations e.g. using webservices with time for Communication
					if (!accepted && getListOfManagedJobs().contains(j)){
						//The job must be scheduled locally
						site.getScheduler().putNextJob(j);
					}
				}// end for job
				
			} 
		}
	}
	
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getListOfManagedJobs()
	 */
	public List<Job> getListOfManagedJobs(){
		return managedJobs;
	}
	

	/**
	 * This method is used to query the Broker for Jobs to delegate
	 * 
	 * @return DelegationsSession session
	 */
	public ActivityDelegationSession query(ActivityDelegationTarget dt) throws NoJobRequestAllowedException{
		//in this standard-implementation there's no query of Jobs allowed
		throw new NoJobRequestAllowedException("JobRequest is not allowed");
		//return null;
	}

	/**
	 * This method is used for delegation of a Job, e.g. when the job
	 * is delegated to another site
	 */
	public void delegate(Job job) throws AcceptanceVetoException{
		//try to remove the Job from Local SchedulingSystem
		if (job.getLifecycle().getLastCondition()==State.QUEUED){
			try {
				getSite().getScheduler().dequeue(this,job);
			} catch (DequeuingVetoException e) {
				throw new AcceptanceVetoException("The job cannot be dequeued from local Scheduling-System");
			}
		}
		//Try to give up the responsibility for the Job
		if (!managedJobs.remove(job)) throw new AcceptanceVetoException("The Job is no longer available for acception");
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getSite()
	 */
	public Site getSite(){
		return site;
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#setSite(de.irf.it.rmg.core.teikoku.site.Site)
	 */
	public void setSite(Site site){
		this.site=site;
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getAcceptancePolicy()
	 */
	public AcceptancePolicy getAcceptancePolicy() {
		return acceptancePolicy;
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getDistributionPolicy()
	 */
	public DistributionPolicy getDistributionPolicy() {
		return distributionPolicy;
	}

	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getLocationPolicy()
	 */
	public LocationPolicy getLocationPolicy() {
		return locationPolicy;
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getKnownActivityBroker()
	 */
	public List<ActivityBroker> getKnownActivityBroker(){
		if (knownActivityBroker.size()==0){
			initializeKnownActivityBroker();
		}
		return knownActivityBroker;
	}
	
	/**
	 * Must be implemented by subclass
	 */
	public abstract void initializeKnownActivityBroker();
	
	/**
	 * Method for adding knownActivityBroker
	 * @param ab
	 */
	public void addKnownActivityBroker(ActivityBroker ab){
		this.knownActivityBroker.add(ab);
	}
	
	/**
	 * Initialize ResourceBroker and all subPolicys
	 */
	public void initialize() throws InstantiationException{
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
		.subset(Site.CONFIGURATION_SECTION);
		//check, if only one Transferpolicy should be used
		TransferPolicy tp=null;
		String key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_TRANSFERPOLICY_CLASS);
		if (key != null) {
			String className = c.getString(key);
			tp = ClassLoaderHelper.loadInterface(className,
			TransferPolicy.class);
			tp.setActivityBroker(this);
			tp.initialize(key.replace(".class", ""));	
		}
		
		//Initialize DistributionPolicy
		key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_DISTRIBUTIONPOLICY_CLASS);
		if (key == null) {
			String msg = "ResourceBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + getSite().getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_DISTRIBUTIONPOLICY_CLASS
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if
		String className = c.getString(key);
		DistributionPolicy dp = ClassLoaderHelper.loadInterface(className,
				DistributionPolicy.class);
		dp.setActivityBroker(this);
		distributionPolicy=dp;
		dp.setActivityBroker(this);
		
		if (tp==null){
			dp.initializeTransferPolicy();
		} else{
			dp.setTransferPolicy(tp);
		}
		
		//initialize AcceptancePolicy
		key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_ACCEPTANCEPOLICY_CLASS);
		if (key == null) {
			String msg = "ResourceBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + getSite().getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_ACCEPTANCEPOLICY_CLASS
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if
		className = c.getString(key);
		AcceptancePolicy ap = ClassLoaderHelper.loadInterface(className,
				AcceptancePolicy.class);
		ap.setActivityBroker(this);
		acceptancePolicy=ap;
		ap.setActivityBroker(this);
		if (tp==null){
			ap.initializeTransferPolicy();
		} else{
			ap.setTransferPolicy(tp);
		}
		
		//initialize LocationPolicy
		key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_LOCATIONPOLICY_CLASS);
		if (key == null) {
			String msg = "ResourceBroker entry ("
					+ Site.CONFIGURATION_SECTION + "[" + getSite().getName()
					+ "]" + Constants.CONFIGURATION_SITES_ACTIVITYBROKER_LOCATIONPOLICY_CLASS
					+ ") not found in configuration";
			throw new InstantiationException(msg);
		} // if
		className = c.getString(key);
		LocationPolicy lp = ClassLoaderHelper.loadInterface(className,
				LocationPolicy.class);
		lp.setActivityBroker(this);
		locationPolicy=lp;
		lp.setActivityBroker(this);
		lp.initialize();
		
		//Initialize public Information here
		key = ConfigurationHelper.retrieveRelevantKey(c, getSite().getName(),
				Constants.CONFIGURATION_SITES_ACTIVITYBROKER_PUBLIC_INFORMATION);
		if (key != null) {
			List<String> infos=c.getList(key);
			
			//Fill Public Information with Standard parts
			if (infos.contains("size")){
				publicInformation.put("size", this.getSite().getSiteInformation().getProvidedResources().size());
			}
			
		} // if	
	}
	
	/**
	 * This method is called by the acceptancePolicy of the site, if a remote Job is taken over and
	 * should be given to the LRMS
	 */
	public void manageNewJob(Job j){
		//save the exchange in the Job-Object
		j.getProvenance().addEpisode(this.site);
		//The job is managed by this site now
		getListOfManagedJobs().add(j);
		this.getSite().getScheduler().putNextJob(j);
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.activity.ActivityBroker#getActiveSessions()
	 */
	public List<ActivityDelegationSession> getActiveSessions(){
		//First clean the list of inactive sessions
		int index=0;
		while(index<activeSessions.size()){
			if (!activeSessions.get(index).isValid()){
				activeSessions.remove(index);
			} else{
				index++;
			}
		}
		return activeSessions;
	}
	

	/**
	 * Standard-setter for the acceptancePolicy
	 * @param ap
	 */
	public void setAcceptancePolicy(AcceptancePolicy ap) {
		acceptancePolicy=ap;
	}

	/**
	 * Standard-setter for the distributionPolicy
	 * @param dp
	 */
	public void setDistributionPolicy(DistributionPolicy dp) {
		distributionPolicy=dp;
	}
	
	/**
	 * Standard-setter for the locationPolicy
	 * @param lp
	 */
	public void setLocationPolicy(LocationPolicy lp) {
		locationPolicy=lp;
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.core.teikoku.grid.DelegationSource#getPublicInformation()
	 */
	public Map<String,Object> getPublicInformation(){
		return this.publicInformation;
	}
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.teikoku.grid.activity.ActivityBroker#getRole()
	 */
	public ActivityBrokerRole getRole() {
		return this.role;
	}
	
	
	/* (Kein Javadoc)
	 * @see de.irf.it.rmg.teikoku.grid.activity.ActivityBroker#getRole()
	 */
	public void setRole(ActivityBrokerRole role) {
		this.role = role;
	}
}
