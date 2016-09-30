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
package de.irf.it.rmg.core.teikoku.exceptions;


/**
 * This Exception is thrown, when a invalid/ incorrect Job should be submitted
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>, and <a
 *         href="mailto:alexander.foelling@udo.edu">Alexander F�lling</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class SubmissionException extends Exception {
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private static final long serialVersionUID = -1852416703109845948L;

	/**
	 * TODO: not yet commented
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	public SubmissionException() {
		super();
	}

	/**
	 * TODO: not yet commented
	 * Creates a new instance of this class, using the given parameters.
	 *
	 * @param message
	 */
	public SubmissionException(String message) {
		super(message);
	}

	/**
	 * TODO: not yet commented
	 * Creates a new instance of this class, using the given parameters.
	 *
	 * @param cause
	 */
	public SubmissionException(Throwable cause) {
		super(cause);
	}

	/**
	 * TODO: not yet commented
	 * Creates a new instance of this class, using the given parameters.
	 *
	 * @param message
	 * @param cause
	 */
	public SubmissionException(String message, Throwable cause) {
		super(message, cause);
	}
}
