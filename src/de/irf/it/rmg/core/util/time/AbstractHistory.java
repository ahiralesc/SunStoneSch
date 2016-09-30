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
package de.irf.it.rmg.core.util.time;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.sim.kuiga.Clock;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>
 * @since 0.1 (mini)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 * @param <T>
 */
abstract public class AbstractHistory<T> {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging"
	 * API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(AbstractHistory.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Stack<Instant> history;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Map<Instant, List<T>> instantToConditionMapping;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private T lastCondition;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Map<T, List<Period>> conditionToPeriodMapping;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Period lastPeriod;

	/**
	 * TODO: not yet commented
	 * 
	 */
	public AbstractHistory() {
		this.history = new Stack<Instant>();
		this.instantToConditionMapping = new HashMap<Instant, List<T>>();
		this.conditionToPeriodMapping = new HashMap<T, List<Period>>();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param condition
	 */
	public AbstractHistory(final T condition) {
		this();
		/*
		 * add initial episode
		 */
		this.addEpisode(condition);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public List<Episode<T>> getEpisodes() {
		final List<Episode<T>> result = new ArrayList<Episode<T>>();

		for (final Instant instant : this.history) {
			final List<? extends T> conditionsAtInstant = this.instantToConditionMapping
					.get(instant);
			for (final T condition : conditionsAtInstant) {
				result.add(new Episode<T>(instant, condition));
			} // for
		} // for

		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final public T getLastCondition() {
		return this.lastCondition;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final public Period getLastPeriod() {
		return this.lastPeriod;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final protected List<T> getCurrentConditions() {
		return this.instantToConditionMapping.get(this.history.peek());
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @return
	 */
	protected List<T> getConditionsAt(final Instant timestamp) {
		List<T> result = null;

		/*
		 * find the episode in history before the given timestamp
		 */
		int index = Collections.binarySearch(this.history, timestamp);
		index = index < 0 ? Math.abs(index) - 2 : index;

		if (index < 0) {
			/*
			 * the given instant is before the lifecycle's start
			 */
			result = null;
			if (log.isTraceEnabled()) {
				final String msg = "not found: condition at " + timestamp;
				log.trace(msg);
			} // if
		} // if
		else {
			final Instant episode = this.history.get(index);
			result = this.instantToConditionMapping.get(episode);
		} // else

		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param condition
	 * @return
	 */
	protected List<Period> getPeriodsFor(final T condition) {
		return this.conditionToPeriodMapping.get(condition);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param condition
	 */
	protected void addEpisode(final T condition) {
		final Instant now = Clock.instance().now();

		/*
		 * check whether any episodes exist at all
		 */
		if (this.history.isEmpty()) {
			/*
			 * no: add initial episode to history
			 */
			this.history.push(now);
			final List<T> newConditions = new ArrayList<T>();
			newConditions.add(condition);
			this.instantToConditionMapping.put(now, newConditions);
		} // if
		else {
			/*
			 * yes: check whether time has advanced since the last episode
			 */
			final Instant lastEpisode = this.history.peek();
			if (lastEpisode.equals(now)) {

				/*
				 * no: add new condition to existing episode
				 */
				final List<T> lastConditions = this.instantToConditionMapping
						.get(lastEpisode);
				lastConditions.add(condition);

				/*
				 * update last period cessation to now
				 */
				this.lastPeriod.setCessation(now);

			} // if
			else {

				/*
				 * yes: add new episode to history
				 */
				this.history.push(now);
				final List<T> newConditions = new ArrayList<T>();
				newConditions.add(condition);
				this.instantToConditionMapping.put(now, newConditions);

				/*
				 * update last period cessation to now - 1
				 */
				final Instant nowMinusOne = TimeHelper.subtract(now,
						TimeFactory.newFinite(1));
				this.lastPeriod.setCessation(nowMinusOne);

			} // else
		} // else

		/*
		 * add new period to condition table
		 */
		final Period period = new Period(now);

		/*
		 * check whether the condition already exists
		 */
		if (this.conditionToPeriodMapping.containsKey(condition)) {
			/*
			 * yes: add new period to existing list
			 */
			final List<Period> periodsForCondition = this.conditionToPeriodMapping
					.get(condition);
			periodsForCondition.add(period);
		} // if
		else {
			/*
			 * no: create table entry with new period
			 */
			final List<Period> periodsForCondition = new ArrayList<Period>();
			periodsForCondition.add(period);
			this.conditionToPeriodMapping.put(condition, periodsForCondition);
		} // else

		/*
		 * record current condition and period as last
		 */
		this.lastCondition = condition;
		this.lastPeriod = period;
	}
}
