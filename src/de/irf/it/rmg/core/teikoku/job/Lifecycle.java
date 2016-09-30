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
 * Copyright (c) 2007 by the
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
package de.irf.it.rmg.core.teikoku.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.util.time.AbstractHistory;
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
final public class Lifecycle extends AbstractHistory<State> {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(Provenance.class);

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public Lifecycle() {
		super();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param state
	 */
	public Lifecycle(State state) {
		super(state);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @return
	 */
	public State[] findStatesAt(Instant timestamp) {
		List<State> result = super.getConditionsAt(timestamp);
		return result.toArray(new State[result.size()]);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param state
	 * @return
	 */
	public Period[] findPeriodsFor(State state) {
		Period[] period = null;
		List<Period> result = super.getPeriodsFor(state);
		if (result != null) {
			period = result.toArray(new Period[result.size()]);
		} // if
		return period;
	}

	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.time.AbstractHistory
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.time.AbstractHistory#addEpisode(java.lang.Object)
	 */
	@Override
	public void addEpisode(State state)
			throws IllegalArgumentException {
		boolean changeValid = state.validateStateChange(super
				.getLastCondition());
		if (changeValid) {
			if (log.isDebugEnabled()) {
				String msg = "added episode: new state is " + state + " (was: "
						+ super.getLastCondition() + ")";
				log.debug(msg);
			} // if
			super.addEpisode(state);
		} // if
		else {
			String msg = "invalid change: " + super.getLastCondition() + "->"
					+ state;
			throw new IllegalArgumentException(msg);
		} // else
	}
}
