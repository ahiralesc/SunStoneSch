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
package de.irf.it.rmg.sim.kuiga.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener;

/**
 * A label that tags event sinks with information on their requested
 * {@link NotificationTime}. It denotes the moment in time at which a given
 * event listener is to be notified about the processing of an event by the
 * {@link Kernel}.
 * 
 * The {@link NotificationTime#HANDLE} case denotes that the accepting event
 * sink is supposed to take action on the event which result in a manipulation
 * of the simulated system's state. The other cases,
 * {@link NotificationTime#BEFORE} and
 * {@link NotificationTime#AFTER}, denote the delivery of the event before
 * and after the <code>HANDLE</code> case.
 * 
 * @see Kernel
 * @see EventIncidenceListener
 * @see NotificationTime
 * 
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface MomentOfNotification {
	
	/**
	 * Denotes the relative notification time for the annotated element. Note
	 * that the value of the annotation must not be <code>null</code>.
	 * 
	 * @return The relative notification time for the annotated element.
	 */
	NotificationTime value() default NotificationTime.AFTER;
}
