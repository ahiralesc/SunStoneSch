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
package de.irf.it.rmg.core.teikoku.exceptions;

/**
 * 
 * 
 */
final public class InvalidRuleBaseException extends Exception {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private static final long serialVersionUID = -6881912089842070294L;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	public InvalidRuleBaseException() {
		super();
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param message
	 *            The message of this exception.
	 */
	public InvalidRuleBaseException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param cause
	 *            The nested throwable this exception was caused by.
	 */
	public InvalidRuleBaseException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param message
	 *            The message of this exception.
	 * @param cause
	 *            The nested throwable this exception was caused by.
	 */
	public InvalidRuleBaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
