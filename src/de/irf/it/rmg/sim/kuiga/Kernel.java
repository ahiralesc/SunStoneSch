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
package de.irf.it.rmg.sim.kuiga;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.util.Pair;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.collections.SortedQueueBinarySearchImpl;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import de.irf.it.rmg.sim.kuiga.annotations.AnnotationHelper;
import de.irf.it.rmg.sim.kuiga.annotations.EventSink;
import de.irf.it.rmg.sim.kuiga.annotations.InvalidAnnotationException;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.sim.kuiga.annotations.NotificationTime;
import de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener;
import de.irf.it.rmg.sim.kuiga.listeners.TimeChangeListener;
import de.irf.it.rmg.sim.kuiga.listeners.TypeChangeListener;


/**
 * 
 * 
 * @author <a href="mailto:alexander.papaspyrou@tu-dortmund.de">Alexander
 *         Papaspyrou</a>
 * @since 0.2 (veto)
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
final public class Kernel {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging"
	 * API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(Kernel.class);

	/**
	 * Constant infix for the configuration section, which is used to find
	 * Kernel specific configuration parameters within the configuration
	 * document
	 * 
	 */
	final public static String CONFIGURATION_SECTION = "kernel";

	/**
	 * Instance of the Kernel singleton within the tGSF
	 * 
	 */
	private static Kernel instance = null;

	/**
	 * A sorted eventqueue that stores all events, that not yet happened first
	 * by time and then by eventType.
	 * 
	 * @see de.irf.it.rmg.core.teikoku.kernel.events.EventType
	 * 
	 */
	private SortedQueue<Event> eventQueue;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Map<Class<? extends Event>, Set<Pair<Method, Object>>> before;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Map<Class<? extends Event>, Set<Pair<Method, Object>>> handle;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Map<Class<? extends Event>, Set<Pair<Method, Object>>> after;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Set<EventIncidenceListener> eventIncidenceListeners;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Set<TimeChangeListener> timeChangeListeners;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Set<TypeChangeListener> typeChangeListeners;

	/**
	 * Returns the actual instance of the Kernel and instantiates it if
	 * necessary
	 * 
	 * @return Kernel
	 */
	public static Kernel getInstance() {
		if (instance == null) {
			instance = new Kernel();
		} // if
		return instance;
	}

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	private Kernel() {
		this.eventQueue = new SortedQueueBinarySearchImpl<Event>();
		this.eventQueue.setKeepOrdered(true);

		this.eventIncidenceListeners = new HashSet<EventIncidenceListener>();
		this.typeChangeListeners = new HashSet<TypeChangeListener>();
		this.timeChangeListeners = new HashSet<TimeChangeListener>();
		
		this.before = new HashMap<Class<? extends Event>, Set<Pair<Method, Object>>>();
		this.handle = new HashMap<Class<? extends Event>, Set<Pair<Method, Object>>>();
		this.after = new HashMap<Class<? extends Event>, Set<Pair<Method, Object>>>();
	}

	/**
	 * Registers an arbitrary object as a sink for the delivery of simulation
	 * {@link Event}s. After completion, the kernel starts to notify this object
	 * if an event occurs that matches the object's delivery requirements.
	 * 
	 * More specifically, the given object is registered as an event sink in the
	 * Kernel, if
	 * <ul>
	 * <li>its type is tagged with an {@link EventSink} annotation and</li>
	 * <li>at least one method is tagged with an {@link AcceptedEventType}
	 * annotation.</li>
	 * </ul>
	 * The method checks whether the constraints specified for both annotations
	 * are correctly fulfilled; otherwise an {@link InvalidAnnotationException}
	 * is thrown.
	 * 
	 * Objects that have been already registered are silently ignored. Objects
	 * that are missing either annotation are rejected.
	 * 
	 * @param sink
	 *            The object to be registered as a sink for {@link Event}
	 *            delivery by the kernel.
	 * @throws InvalidAnnotationException
	 *             if the specified object has no {@link EventSink} annotation
	 *             or lacks a valid {@link AcceptedEventType} annotation.
	 * 
	 * @see EventSink
	 * @see AcceptedEventType
	 * @see MomentOfNotification
	 */
	public void registerEventSink(final Object sink)
			throws InvalidAnnotationException {
		this.updateEventSinkRegistration(sink, true);
	}

	/**
	 * Unregisters an arbitrary object as a sink for the delivery of simulation
	 * {@link Event}s. After completion, the kernel stops to notify this object
	 * if an event occurs that matches the object's delivery requirements.
	 * 
	 * More specifically, the given object is unregistered as an event sink in
	 * the Kernel, if
	 * <ul>
	 * <li>its type is tagged with an {@link EventSink} annotation and</li>
	 * <li>at least one method is tagged with an {@link AcceptedEventType}
	 * annotation.</li>
	 * </ul>
	 * The method checks whether the constraints specified for both annotations
	 * are correctly fulfilled; otherwise an {@link InvalidAnnotationException}
	 * is thrown.
	 * 
	 * Objects that have been already unregistered are silently ignored. Objects
	 * that are missing either annotation are rejected.
	 * 
	 * @param sink
	 *            The object to be unregistered as a sink for {@link Event}
	 *            delivery by the kernel.
	 * @throws InvalidAnnotationException
	 *             if the specified object has no {@link EventSink} annotation
	 *             or lacks a valid {@link AcceptedEventType} annotation.
	 * 
	 * @see EventSink
	 * @see AcceptedEventType
	 * @see MomentOfNotification
	 */
	public void unregisterEventSink(final Object sink)
			throws InvalidAnnotationException {
		this.updateEventSinkRegistration(sink, false);
	}

	/**
	 * Updates the event sink management data structure for the given object.
	 * Flagging denotes addition, if <code>true</code>, and removal, if
	 * <code>false</code>.
	 * 
	 * @param sink
	 *            The object to be handled.
	 * @param flag
	 *            The mode of operation: addition, if <code>true</code>;
	 *            removal, if <code>false</code>.
	 * @throws InvalidAnnotationException
	 *             if the specified object has no {@link EventSink} annotation
	 *             or lacks a valid {@link AcceptedEventType} annotation.
	 * 
	 * @see #before
	 * @see #handle
	 * @see #after
	 */
	private void updateEventSinkRegistration(final Object sink,
			final boolean flag)
			throws InvalidAnnotationException {
		/*
		 * Check whether the given type is annotated correctly.
		 */
		if (AnnotationHelper.isEventSinkCompliant(sink.getClass())) {

			/*
			 * yes: Iterate over public methods.
			 */
			final Method[] methods = sink.getClass().getMethods();
			boolean found = false;

			for (final Method method : methods) {
				/*
				 * Check whether the given method is annotated correctly.
				 */
				if (AnnotationHelper.isAcceptedEventTypeCompliant(method)) {
					/*
					 * yes: Retrieve annotation.
					 */
					final AcceptedEventType aet = method
							.getAnnotation(AcceptedEventType.class);
					final MomentOfNotification mon = method
							.getAnnotation(MomentOfNotification.class);

					/*
					 * Check whether the (optional) MomentOfNotification is set
					 * and add to corresponding notification map.
					 */
					NotificationTime nt = null;
					if (mon != null) {
						nt = mon.value();
					} // if
					else {
						nt = NotificationTime.DEFAULT;
					} // else
					/*
					 * yes: Retrieve appropriate set from map.
					 */
					Set<Pair<Method, Object>> s = null;
					switch (nt) {
						case BEFORE:
							s = this.before.get(aet.value());
							if (s == null) {
								s = new HashSet<Pair<Method, Object>>();
								this.before.put(aet.value(), s);
							} // if
							break; // BEFORE
						case HANDLE:
							s = this.handle.get(aet.value());
							if (s == null) {
								s = new HashSet<Pair<Method, Object>>();
								this.handle.put(aet.value(), s);
							} // if
							break; // HANDLE
						case AFTER:
							s = this.after.get(aet.value());
							if (s == null) {
								s = new HashSet<Pair<Method, Object>>();
								this.after.put(aet.value(), s);
							} // if
							break; // AFTER
						default:
							final String message = "case illegal: unhandled NotificationType "
									+ nt + "detected: ";
							throw new IllegalStateException(message);
					} // switch

					/*
					 * Add/remove entry.
					 */
					if (flag) {
						s.add(new Pair<Method, Object>(method, sink));
					} // if
					else {
						s.remove(new Pair<Method, Object>(method, sink));
					} // else

					found = true;
				} // if
			} // for

			/*
			 * Check whether any valid method was found.
			 */
			if (!found) {
				/*
				 * no: Throw exception.
				 */
				final String message = "not annotated: no method found with "
						+ AcceptedEventType.class;
				throw new InvalidAnnotationException(message);
			} // else

		} // if
		else {
			/*
			 * no: Throw exception.
			 */
			final String message = "not annotated: object lacks "
					+ EventSink.class;
			throw new InvalidAnnotationException(message);
		} // else
	}

	/**
	 * Registers the given parameter as a listener for the occurrence of future
	 * events. After completion of the method, the kernel starts to notify the
	 * given {@link EventIncidenceListener} object on every event that is
	 * processed throughout the simulation.
	 * 
	 * @param listener
	 *            The object to be registered as a listener for occurring
	 *            events.
	 */
	public void registerEventIncidenceListener(
			final EventIncidenceListener listener) {
		this.eventIncidenceListeners.add(listener);
	}

	/**
	 * Unregisters the given parameter as a listener for the occurrence of
	 * future events. After completion of the method, the kernel stops to notify
	 * the given {@link EventIncidenceListener} object on every event that is
	 * processed throughout the simulation.
	 * 
	 * @param listener
	 *            The object to be unregistered as a listener for occurring
	 *            events.
	 */
	public void unregisterEventIncidenceListener(
			final EventIncidenceListener listener) {
		this.eventIncidenceListeners.remove(listener);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param listener
	 */
	public void registerTimeChangeListener(final TimeChangeListener listener) {
		this.timeChangeListeners.add(listener);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param listener
	 */
	public void unregisterTimeChangeListener(final TimeChangeListener listener) {
		this.timeChangeListeners.remove(listener);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param listener
	 */
	public void registerTypeChangeListener(final TypeChangeListener listener) {
		this.typeChangeListeners.add(listener);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param listener
	 */
	public void unregisterTypeChangeListener(final TypeChangeListener listener) {
		this.typeChangeListeners.remove(listener);
	}

	/**
	 * Wakes up the kernel and commences the handling of events pending with
	 * respect to the passed timestamp.
	 * 
	 * During the course of the method call, none, one, or more events might be
	 * processed and thus modify the simulation system state. In addition,
	 * events may be added or removed to/from the event queue.
	 * 
	 * After the handling of all events pending for the given timestamp, the
	 * next relevant timestamp, i.e. the timestamp of the next event in the
	 * queue, is determined and returned.
	 * 
	 * @param timestamp
	 *            The timestamp for which events in the queue are to be
	 *            processed.
	 * @return Instant The next relevant timestamp for the simulation kernel or
	 *         <code>null</code>, iif currently no event is pending.
	 */
	public Instant wakeup(final Instant timestamp) {
		Instant result = null;

		log.debug("woke up on " + timestamp);
		/*
		 * Fetch the event at the head of the queue.
		 */
		Event nextEvent = this.eventQueue.peek();
		
		//System.out.println(nextEvent.toString()); <-- probable problema

		/*
		 * Check whether there are events to handle and, if yes, delegate the
		 * handling for the current simulation timestamp.
		 */
		if (nextEvent != null) {
			nextEvent = this.handleEvents(Clock.instance().now());
		} // if

		/*
		 * Since none, one, or more events might have been processed (and thus
		 * removed from the queue), check whether a subsequent event exists.
		 */
		if (nextEvent != null) {
			/*
			 * yes: Determine the next interesting timestamp for the simulation.
			 */
			result = nextEvent.getTimestamp();
		} // if
		else {
			/*
			 * no: Return nothing.
			 */
			result = null;
		} // else

		return result;
	}

	/**
	 * Dispatches a new event to the kernel for future handling.
	 * 
	 * The kernel finds the correct position in the event queue and injects the
	 * event there. The actual ordering is determined according to
	 * {@link Event#compareTo(Event)}.
	 * 
	 * @param e
	 *            The event to be dispatched to the kernel for future handling.
	 */
	public void dispatch(final Event e) {
		log.debug("dispatch new event to queue: " + e);
		this.eventQueue.offer(e);
	}

	/**
	 * Handles the events that are pending at the given instant, and triggers
	 * notification of all sinks and listeners.
	 * 
	 * @param instant
	 *            The timestamp up to which events are to be handled.
	 * @return The next pending event after handling has completed or
	 *              <code>null</code>, if no event is left in the queue.
	 */
	private Event handleEvents(final Instant instant) {
		log.debug("handling events on " + instant);

		/*
		 * Look at the first event in the queue.
		 */
		Event nextEvent = this.eventQueue.peek();
		Event previousEvent = null;
		//System.out.println("Current time : " + Clock.instance().now());

		/*
		 * Check whether there is an event left to handle.
		 */
		if (nextEvent != null) {
			/*
			 * yes: Fetch and handle events from the queue until either no event
			 * is left (event == null) or the next event in the queue has a
			 * later timestamp than the given instant.
			 */
			while (!nextEvent.getTimestamp().after(instant)) {
				log.debug("fetching next event in queue: " + nextEvent);

				/*
				 * There is at least one event in the queue with a timestamp
				 * older than the given instant; fetch it.
				 */
				nextEvent = this.eventQueue.poll();

				/*
				 * Trigger notification to all event sinks.
				 */
				this.deliverTo(this.before.get(nextEvent.getClass()),nextEvent);
				this.deliverTo(this.handle.get(nextEvent.getClass()),nextEvent);
				this.deliverTo(this.after.get(nextEvent.getClass()), nextEvent);

				/*
				 * Store the event that has been fetched and handled, and look
				 * at the next event in the queue.
				 */
				previousEvent = nextEvent;
				nextEvent = this.eventQueue.peek();

				/*
				 * Check whether there is an event left to handle.
				 */
				if (nextEvent == null) {
					/*
					 * no: Trigger notification of all TypeChangeListeners,
					 * since the event type will change to null. Then, finish
					 * handling.
					 */
					for (final TypeChangeListener typeChangeListener : this.typeChangeListeners) {
						typeChangeListener
								.notifyTypeChange(previousEvent, null);
					} // for
					break;
				} // if
				else {
					/*
					 * yes: Check whether any listener needs to be notified.
					 */

					if (!nextEvent.getClass().equals(previousEvent.getClass())) {
						/*
						 * type change: trigger TypeChangeListener notification.
						 */
						for (final TypeChangeListener typeChangeListener : this.typeChangeListeners) {
							typeChangeListener.notifyTypeChange(previousEvent,
									nextEvent);
						} // for
					} // if

					/*
					 * Since in the meantime, the type change listener might
					 * have injected an event which needs handling, look again
					 * at the next event in the queue.
					 */
					nextEvent = this.eventQueue.peek();

					if (nextEvent.getTimestamp().after(instant)) {
						/*
						 * time change: trigger TimeChangeListener notification.
						 */
						for (final TimeChangeListener timeChangeListener : this.timeChangeListeners) {
							timeChangeListener.notifyTimeChange(previousEvent
									.getTimestamp(), nextEvent.getTimestamp());
						} // for
					} // if

					/*
					 * Since in the meantime, the time change listener might
					 * have injected an event which needs handling, look again
					 * at the next event in the queue.
					 */
					nextEvent = this.eventQueue.peek();

				} // else
			}// while
		} // if

		/*
		 * After handling all events for the given instant, return the next
		 * pending event to the caller.
		 */
		return this.eventQueue.peek();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param sinks
	 * @param event
	 */
	private void deliverTo(final Set<Pair<Method, Object>> sinks,
			final Event event) {
		if (sinks!=null){
			for (final Pair<Method, Object> sink : sinks) {
				
				final Method m = sink.getLeft();
				final Object o = sink.getRight();
				try {
					m.invoke(o, event);
				} // try
				catch (final IllegalArgumentException e) {
					// TODO: not yet handled.
					e.printStackTrace();
				} // catch IllegalArgumentException
				catch (final IllegalAccessException e) {
					// TODO: not yet handled.
					e.printStackTrace();
				} // catch IllegalAccessException
				catch (final InvocationTargetException e) {
					// TODO: not yet handled.
					e.printStackTrace();
				} // catch InvocationTargetException
			} // for
		}
	}
	
	public SortedQueue<Event> getEventQueue() {
		return this.eventQueue;
	}
	
}
