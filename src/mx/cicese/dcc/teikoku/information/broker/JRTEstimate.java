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

import java.util.Hashtable;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Instant;

/**
 * 	Contains job runtime estimates
 *  
 *  @author <a href="mailto:ahiralesc@gmail.com">Adan Hirales Carbajal</a>
 *         	(last edited by $Author$)
 * 	@version $Version$, $Date$
 * 
 *  @category Information system
 * 
 */

public class JRTEstimate extends Entity 
	implements EndPointData {
	
	/**
	 * Models the earliest start time when a given job 
	 * can start execution at the given site.
	 */
	public Hashtable<SWFJob,Instant> earliestStartTime;
	
		
	/**
	 * Models the earliest finishing time when a given 
	 * job can finish execution at the given site.
	 */
	public Hashtable<SWFJob,Instant> earliestFinishingTime;
	
	
	/**
	 * Represents the time the machine is available to schedule
	 * some job. It does not consider if it can schedule it. It
	 * only says that the machine can schedule something. 
	 */
	public Hashtable<SWFJob,Instant> earliestAvailTime;
	
	
	// public Vector<Instant> e;
	
	/**
	 * Class constructor
	 */
	public JRTEstimate() {
		super(EntityType.ESTIMATE);
		this.validity = null;
		this.earliestStartTime = new Hashtable<SWFJob,Instant>();
		this.earliestFinishingTime = new Hashtable<SWFJob,Instant>();
		this.earliestAvailTime = new Hashtable<SWFJob,Instant>();
	}
	
}
