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
 * Copyright (c) 2006 by the
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
package de.irf.it.rmg.core.teikoku.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.AbortionNotHandledException;
import de.irf.it.rmg.core.teikoku.exceptions.DequeuingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class ParallelMachineScheduler extends AbstractScheduler
		implements Scheduler, StrategyListener {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(ParallelMachineScheduler.class);

	/**
	 * TODO: not yet commented Creates a new instance of this class, using the
	 * given parameters.
	 * 
	 */
	public ParallelMachineScheduler()
			throws InstantiationException {
		// super(site);
	}
	
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public ParallelMachineScheduler(ParallelMachineScheduler another) {
		super(another);
	}

	/**
	 * Creates a copy 
	 */
	@Override
	public Scheduler clone() {
		return new ParallelMachineScheduler(this);
	}
	

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.Scheduler
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#activate()
	 */
	public void activate() {
		super.getLocalStrategy().decide(super.getQueue(), super.getSchedule());
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener#advise(de.irf.it.rmg.core.teikoku.common.Job,
	 *      de.irf.it.rmg.core.teikoku.common.Job)
	 */
	public void advise(Job jobToSchedule, Slot possibleSlot)
			throws DecisionVetoException {
		if (possibleSlot != null) {
			try {
				//try to dequeue Job
				super.dequeue(this, jobToSchedule);
			} catch (DequeuingVetoException e) {
				//Check whether job was aborted on this site
				Period[] periods=jobToSchedule.getLifecycle().findPeriodsFor(State.ABORTED);
				boolean cond=false;
				if (periods!=null){
					Site[] sites=jobToSchedule.getProvenance().findSitesAt(periods[periods.length-1].getAdvent());
					for (Site site: sites){
						if (site==this.getSite()){
							cond=true;
							getQueue().remove(jobToSchedule);
							break;
						}
					}
				}
				if (!cond){
					e.printStackTrace();
				}	
			}
			try{
				/*
				 * adapt job to slot
				 */
				Instant startTime = possibleSlot.getAdvent();
				Period duration = new Period(startTime, possibleSlot.getCessation());
				jobToSchedule.setDuration(duration);
				
				//Gets the resources selected for the job (QUITAR DE AQUI)
			//	ResourceBundle rb = possibleSlot.getResources().createSubSetWith(
				//		jobToSchedule.getDescription().getNumberOfRequestedResources());
				ResourceBundle rb = possibleSlot.getResources();
				
				//Sets the resources for the job
				jobToSchedule.setResources(rb);
				
				//Estimate the frequency of reallocated idle cores and its fragmentation
				setAllocationProperties(jobToSchedule, rb);
				
				
				//TODO: ENERGY MANAGEMENT START
				//if(super.getSite().getComponents().get("EnergyManager")) {
				//	super.getSite().getEnergyManager().handleRequest((SWFJob)jobToSchedule);
				//}
				//TODO: ENERGY MANAGEMENT END
				
				/*
				 * add job to schedule
				 */
				super.getSchedule().addJob(jobToSchedule);
				jobToSchedule.getLifecycle().addEpisode(State.SCHEDULED);
				super.createEvents(jobToSchedule,State.STARTED);
				
			} // try
			catch (InvalidTimestampException e) {
				e.printStackTrace();
			} // catch
			catch (IllegalOccupationException e) {
				e.printStackTrace();
			} // catch
			catch (IllegalScheduleException e) {
				e.printStackTrace();
			} // catch
		} // if
	}

	@Override
	public void handleAbortion(Job job) throws AbortionNotHandledException{
		//resubmit the job
		this.putNextJob(job);
	}
	
	@Override
	public void handleCompletion(Job job){
		getLocalStrategy().handleCompletion(job);
	}
}
