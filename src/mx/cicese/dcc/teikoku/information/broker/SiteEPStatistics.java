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

/**
 * Provides statistical information for the Site layer 
 * 
 * @author <a href="mailto:ahirales@cicese.edu.mx">Adan Hirales Carbajal</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 *  @category Information system
 */
public class SiteEPStatistics extends Entity 
implements EndPointData {
	
	/**
	 * The average flow time
	 */
	public double estAvgFlowTime;
	
	
	/**
	 * The average processing time
	 */
	public double estAvgProcTime;
	
	
	/**
	 * The average waiting time 
	 */
	public double estAvgWaitingTime;
	
	
	/**
	 * The utilization
	 */
	public double utilization;
	
	/**
	 * Class constructor
	 */
	public SiteEPStatistics() {
		super(EntityType.STATISTIC);
		this.estAvgFlowTime = 0;
		this.estAvgProcTime = 0;
		this.estAvgWaitingTime = 0;
	}

}
