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
package de.irf.it.rmg.core.teikoku.scheduler.strategy;

import java.util.Queue;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;

/**
 * This class serves as an template for the actual strategy implementations.
 * 
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */
public abstract class AbstractStrategy
		implements Strategy {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(AbstractStrategy.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	private StrategyListener strategyListener;

	private Site site;
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */ 
	public AbstractStrategy(Strategy another) {
		this();
		this.strategyListener = null;
	}
	
	/**
	 * Simple constructor
	 * 
	 * @param another
	 */
	 public AbstractStrategy() {	}
	
	/**
	 * Clone
	 */
	public Strategy clone() {return null;}
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final protected StrategyListener getStrategyListener() {
		return this.strategyListener;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param strategyListener
	 */
	final protected void setStrategyListener(StrategyListener strategyListener) {
		this.strategyListener = strategyListener;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.teikoku.scheduler.Strategy
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Strategy#register(de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener)
	 */
	public void register(StrategyListener strategyListener) {
		this.setStrategyListener(strategyListener);
	}
	
	public void handleCompletion(Job job){
		//Do nothing here for default
	}
	
	public void setSite(Site site){
		this.site=site;
	}
	
	public Site getSite(){
		return this.site;
	}
	
	 public JobControlBlock getNextDecision(final Queue<Job> queue, final Schedule schedule){
			/* TODO: implement */
			return null;
	 }
	    
	 public JobControlBlock getNextDecision(final Queue<Job> queue, final TemporalSchedule schedule){
			/* TODO: implement */
			return null;
	 }
}
