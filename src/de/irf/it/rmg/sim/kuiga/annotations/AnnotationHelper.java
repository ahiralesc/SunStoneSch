package de.irf.it.rmg.sim.kuiga.annotations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.irf.it.rmg.sim.kuiga.Event;


/**
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander
 *         Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
public class AnnotationHelper {

	/**
	 * Checks whether a given type is compliant to the constraints specified in
	 * {@link EventSink}.
	 * 
	 * @param c
	 *            The type to be tested for compliance.
	 * @return <code>true</code>, iif all constraints are obeyed;
	 *         <code>false</code>, otherwise.
	 */
	public static boolean isEventSinkCompliant(Class<?> c) {
		return c.isAnnotationPresent(EventSink.class);
	}

	/**
	 * Checks whether a given method is compliant to the constraints specified
	 * in {@link AcceptedEventType}.
	 * 
	 * @param m
	 *            The method to be tested for compliance.
	 * @return <code>true</code>, iif all constraints are obeyed;
	 *         <code>false</code>, otherwise.
	 */
	public static boolean isAcceptedEventTypeCompliant(Method m) {
		boolean result = true;

		/*
		 * Check annotation presence (must have AcceptedEventType).
		 */
		if (!m.isAnnotationPresent(AcceptedEventType.class)) {
			result = false;
		} // if
		/*
		 * Check usability (must be public and not abstract).
		 */
		else if (!Modifier.isPublic(m.getModifiers())
				|| Modifier.isAbstract(m.getModifiers())) {
			result = false;
		} // else if
		/*
		 * Check number of arguments (must be one).
		 */
		else if (m.getParameterTypes().length != 1) {
			result = false;
		} // else if
		/*
		 * Check type of argument (must be Event or any subclass).
		 */
		else if (!Event.class.isAssignableFrom(m.getParameterTypes()[0])) {
			result = false;
		} // else if
		/*
		 * Check return type (must be void).
		 */
		else if (!m.getReturnType().equals(void.class)) {
			result = false;
		} // else if

		return result;
	}

}
