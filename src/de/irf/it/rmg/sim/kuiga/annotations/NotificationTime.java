package de.irf.it.rmg.sim.kuiga.annotations;

import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener;


/**
 * A description of moments in time during the notification lifecycle of an
 * event currently handled by the {@link Kernel}. It denotes the relative time
 * an {@link EventIncidenceListener} is being notified on the processing of a
 * given event.
 * 
 * The moments described by this enumeration specify the relative time at which
 * event listeners are informed about the event <i>currently under
 * inspection</i> by the kernel.
 * 
 * The semantics of which listeners are supposed to be notified at which point
 * in time is domain-specific, and has to be imposed by the model of the
 * simulated system. Classes in the simulation package are agnostic of the
 * meaning of "before", "handle", and "after"; they just ensure that events are
 * dispatched according to the specified ordering. Moreover, no check whatsoever
 * is being performed whether an event listener with "handling" semantics (see
 * above) really does modifications on the system.
 * 
 * The default behavior&mdash;i.e., when the annotation is completely
 * omitted&mdash;is {@link NotificationTime#AFTER}.
 * 
 * @see Kernel
 * @see EventIncidenceListener
 * @see MomentOfNotification
 * 
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander
 *         Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public enum NotificationTime {

	/**
	 * Denotes that the event listener is to be notified about the occurrence of
	 * a certain event <b>before</b> it is actually handled.
	 * 
	 */
	BEFORE,

	/**
	 * Denotes that the event listener is to be notified about the occurrence of
	 * a certain event, in order to <b>take action</b> on it.
	 */
	HANDLE,

	/**
	 * Denotes that the event listener is to be notified about the occurrence of
	 * a certain event <b>after</b> its handling is completed.
	 */
	AFTER;

	/**
	 * Stores the default value for this enumeration.
	 */
	public static final NotificationTime DEFAULT = NotificationTime.getDefault();
	
	/**
	 * Returns the default value for this enumeration.
	 * 
	 * @return The default value for this enumeration.
	 * 
	 * @see NotificationTime
	 */
	private static NotificationTime getDefault() {
		// TODO: load default NotificationTime value from configuration.
		return AFTER;
	}

}
