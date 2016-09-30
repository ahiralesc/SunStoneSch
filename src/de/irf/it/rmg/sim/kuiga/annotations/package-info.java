/**
 * Provides types for conveniently annotating custom system models for use
 * within the abstract simulation kernel.
 * 
 * This package contains various annotation types for easy usage in derived
 * system models to interact with the simulation system. It allows a declarative
 * specification of model requirements with respect to event notification,
 * simulation state changes, and other aspects of the simulation runtime.
 * 
 * In the most common case, implementors will use the
 * {@link EventSink} and {@link AcceptedEventType} annotations to determine
 * whether an external class provides delivery endpoints for events and, if so,
 * which kinds of events are considered interesting for which handling method.
 * 
 * Using annotations is the recommended way for specifying the event
 * delivery information and connecting event sources (i.e. the
 * {@link de.irf.it.rmg.sim.kuiga.Kernel}) to event sinks.
 * 
 * While {@link de.irf.it.rmg.sim.kuiga.Event}s can also be consumed by
 * implementing the
 * {@link de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener} interface,
 * this method offers less flexibility with respect to wiring
 * together sources and sinks, and might impair performance due to the lack of
 * pre-filtering capabilities. In general, the interfaces in the
 * <code>listeners</code> are tailored to monitoring and tracing purposes of
 * the event kernel itself, while this package should be generally used for
 * all business logic-related eventing issues.
 * 
 * @see EventSink
 * @see AcceptedEventType
 * @see de.irf.it.rmg.sim.kuiga.listeners.EventIncidenceListener
 * @see de.irf.it.rmg.sim.kuiga.Kernel
 */
package de.irf.it.rmg.sim.kuiga.annotations;