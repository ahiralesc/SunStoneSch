package de.irf.it.rmg.sim.kuiga.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener;

/**
 * A label that tags a delivery endpoint for the acceptance of certain
 * {@link Event} types. It denotes that a given method is capable of handling
 * the annotated types in a sensible manner.
 * 
 * The annotation determines whether the {@link Kernel} delivers an event to a
 * certain event sink by comparing the type of the event that is to be delivered
 * with the list of event types specified in the annotation. In case of a match,
 * the event is delivered to the sink.
 * 
 * For every annotated method, the <code>Kernel</code> will check whether it
 * fulfills <b>all</b> of the following constraints:
 * <ul>
 * <li>the method has <code>public</code> visibility,</li>
 * <li>accepts a single argument,</li>
 * <li>the argument type is {@link Event} or any subclass, and</li>
 * <li>the return type of the method is <code>void</code>.</li>
 * </ul>
 * Given that all constraints apply, the <code>Kernel</code> will deliver the
 * event.
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
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface AcceptedEventType {

	/**
	 * Denotes the event types that can be handled by the annotated method. Note
	 * that the value of the annotation must not be <code>null</code>.
	 * 
	 * @return The event types that can be handled by the annotated method.
	 * 
	 * @see Event
	 */
	Class<? extends Event> value();
}
