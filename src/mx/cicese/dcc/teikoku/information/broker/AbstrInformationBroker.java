/*
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2013 by the
 *   CICESE Research Center and  
 *   CETYS University, Mexico
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

package mx.cicese.dcc.teikoku.information.broker;

import java.util.Queue;
import java.util.UUID;

import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;

public abstract class AbstrInformationBroker implements InformationBroker {
	
	/**
	 * The information broker unique ID
	 */
	private UUID id;
	
	
	/**
	 * The machine in which the information broker is contained
	 */
	protected Site site;
	
	
	/**
	 * The type of information broker
	 * 
	 * @see InfBrokerType
	 */
	protected InfBrokerType type;
	
	
	/**
	 * Class constructor
	 */
	public AbstrInformationBroker() {
		this.id = UUID.randomUUID();
	}

	
	/**
	 * Gets the information broker unique ID
	 * 
	 * @return the information broker unique ID
	 */
	public UUID getID(){
		return this.id;
	}
	
	
	@Override
	public void setSite(Site site) {
		this.site = site;

	}

	
	@Override
	public Site getSite() {
		return this.site;
	}
	
	
	/**
	 * Gets the information broker type
	 * 
	 * @return the information broker type
	 */
	public InfBrokerType getType() {
		return this.type;
	}

	
	/**
	 * Sets the information broker type
	 * 
	 * @return the information broker type
	 */
	public void setType(InfBrokerType type) {
		this.type = type;
	}
	
	
	@Override
	public EndPointData getPublicInformation(EntityType type, Queue<Job> jobs, JobControlBlock jcb) {
		EndPointData msg = null;
		
		if(type.equals(EntityType.STATUS))
			msg = getStatus();
		
		if(type.equals(EntityType.ESTIMATE))
			msg = getJREstimate(jobs, jcb);
		
		if(type.equals(EntityType.STATISTIC)) 
			msg = getStatistic();
		
		
		if(type.equals(EntityType.EARLIEST_START_TIME)) {
			msg = this.getEST(jobs, jcb);
		}
		
		return msg;
	}
	
	protected abstract EndPointData getJREstimate(Queue<Job> jobs, JobControlBlock jcb);
		
	protected abstract EndPointData getStatus();
	
	protected abstract EndPointData getStatistic();
	
	protected EndPointData getEST(Queue<Job> jobs, JobControlBlock jcb) {
		return null;
	};

}
