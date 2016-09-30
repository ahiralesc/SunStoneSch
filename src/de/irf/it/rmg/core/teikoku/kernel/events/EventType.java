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
package de.irf.it.rmg.core.teikoku.kernel.events;




/**
 * TODO: not yet commented
 * 
 * @author <a href="alexander.foelling@udo.edu">Alexander Folling</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public enum EventType {
	
	/**
	 * 
	 * @category Schedule
	 */
	RETURN_OF_LENT_RESOURCES(),


	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	JOB_ABORTED(),

	
	/**
	 * 
	 * @category Schedule
	 * 
	 */
	RESERVATION_ENDED(),
	
	
	
	/**
	 * @category Energy
	 */
	WAITING_TIME_TIMEDOUT(),
	
	
	/**
	 * 
	 * @category Grid broker
	 * 
	 */
	ALLOC_QUEUED(),

	
	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	JOB_COMPLETED(),
	
	
	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	VOLTAGE_FREQUENCY_REGULATION(),
	
	
	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	JOB_COMPLETED_ON_FOREIGN_SITE(),
	
	
	/**
	 * 
	 * @category Workflow
	 * 
	 */
	WORKFLOW_COMPLETED(),

	
	/**
	 * @category Rigid job
	 * 
	 */
	JOB_STARTED(),

	
	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	JOB_QUEUED(),
	

	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	JOB_RELEASED(),


	
	/**
	 * 
	 * @category Rigid job
	 * 
	 */
	JOB_QUEUED_ON_FOREIGN_SITE(),
	
}
