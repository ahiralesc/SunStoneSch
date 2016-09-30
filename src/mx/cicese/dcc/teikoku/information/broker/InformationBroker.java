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

import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;

/**
 * Scheduling strategies may require accounting data or job execution estimates for 
 * decision-making purposes. In Teikoku, a grid information component provides services for 
 * consulting resource availability, state, statistical, and architectural characteristics. 
 * Its design is based on the Grid laboratory Uniform Environment (GLUE) schema 
 * <p>
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 *  
 * @category Information system
 * 
 */

public interface InformationBroker {
	
	/**
	 * Set the information broker containing machine 
	 * 
	 * @param site a machine
	 */
	public void setSite(Site site);
	
	
	/**
	 * Gets the information broker containing machine
	 * 
	 * @return a machine
	 */
	public Site getSite();

	
	/**
	 * Instruments state, estimate, or statistical data from the compute end-point entity
	 * 
	 * @see EntityTpe for different types of data types.
	 *  
	 * @param infoType, the type of data to instrument
	 * @param jobs, if the EntityType is of type ESTIMATE, then for each job in jobs a runtime estimate is
	 * 				computed
	 * @param jcb,	workflow scheduling runtime estimates are stored in the workflow job control block (JCB)
	 * 
	 * @return the requested end-point data
	 */
	public EndPointData getPublicInformation(EntityType type, Queue<Job> jobs, JobControlBlock jcb);
	
	
	/**
	 * Set the information broker type 
	 * 
	 * @param the information broker type
	 */
	public void setType(InfBrokerType type);
}
