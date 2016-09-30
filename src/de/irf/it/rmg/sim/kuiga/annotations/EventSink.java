package de.irf.it.rmg.sim.kuiga.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener;

/**
 * A label that tags a class as a sink for simulation events. It denotes that
 * the kernel is to be aware of the type as a potential target for {@link Event}
 * dispatching.
 * 
 * This annotation must be used in conjunction with at least the
 * {@link AcceptedEventType} annotation in order to specify concrete delivery
 * endpoints for certain event types. Note that, by itself, the annotation does
 * not enable the delivery; it just specifies that a certain type has endpoints
 * that are further specified.
 * 
 * @see Kernel
 * @see Event
 * @see AcceptedEventType
 * @see EventIncidenceListener
 * 
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander
 *         Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventSink {
	
	// This type intentionally left blank.
	
}
