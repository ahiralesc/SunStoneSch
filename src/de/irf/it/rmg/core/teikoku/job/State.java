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
public enum State {

	/**
	 * TODO: not yet commented
	 * 
	 */
	RELEASED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	QUEUED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	SCHEDULED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	STARTED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	SUSPENDED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	RESUMED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	ABORTED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	COMPLETED(),

	/**
	 * TODO: not yet commented
	 * 
	 */
	FAILED();

	/**
	 * TODO: not yet commented
	 *
	 * @param source
	 * @return
	 */
	public boolean validateStateChange(State source) {
		boolean result;
		
		switch (this) {
		
			case RELEASED:
				result = source == null ? true : false;
				break;
			case QUEUED:
				result = RELEASED.equals(source) || QUEUED.equals(source) || ABORTED.equals(source);
				break;
			case SCHEDULED:
				result = QUEUED.equals(source);
				break;
			case STARTED:
				result = SCHEDULED.equals(source);
				break;
			case RESUMED:
				result = STARTED.equals(source)
						|| SUSPENDED.equals(source);
				break;
			case SUSPENDED:
				result = STARTED.equals(source) 
						||RESUMED.equals(source);
				break;
			case ABORTED:
				result = RELEASED.equals(source)
						|| QUEUED.equals(source)
						|| SCHEDULED.equals(source)
						|| STARTED.equals(source)
						|| RESUMED.equals(source)
						|| SUSPENDED.equals(source);
				break;
			case COMPLETED:
				result = STARTED.equals(source)
						|| RESUMED.equals(source);
				break;
			case FAILED:
				result = RELEASED.equals(source)
						|| QUEUED.equals(source)
						|| SCHEDULED.equals(source)
						|| STARTED.equals(source)
						|| RESUMED.equals(source)
						|| SUSPENDED.equals(source);
				break;
			default:
				result = false;
				break;
		} // switch
		
		return result;
	}
}
