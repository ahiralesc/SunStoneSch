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

package de.irf.it.rmg.core.teikoku.site.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Occupation;
import de.irf.it.rmg.core.teikoku.common.Reservation;
import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.ReservationEndEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.ReturnOfLentResourcesEvent;
import de.irf.it.rmg.core.teikoku.scheduler.Scheduler;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleBitVectorImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.collections.RemoveAllRetainAllSetDecorator;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

/**
 * The Schedule is a data structure that stores the state of one schedule for
 * parallel machines models. It contains knowledge about the resources
 * {@link Resource}, current scheduled jobs {@link Job} and reservations. The
 * internal function bases on schedule events {@link UtilizationChange} that
 * contain the information about the changes refering the load and occupation on
 * individual points in time. Using an external clock it is possible to use this
 * data structure in simulation and real time applications as well.
 * 
 * @version $Version$, $Date$
 * 
 */
public class Schedule {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(Schedule.class);

	/**
	 * Holds the scheduler this schedule belongs to.
	 * 
	 */
	private Scheduler scheduler;

	/**
	 * Holds the set of currently scheduled {@link Job}s.
	 */
	private Set<Job> scheduledJobs;

	/**
	 * Holds the set of currently scheduled {@link Reservation}s.
	 */
	private Set<Reservation> scheduledReservations;

	/**
	 * Holds a list containing all {@link UtilizationChange}s. This represents
	 * the current schedule as a set of start and end times of already scheduled
	 * jobs and reservations, and the changes in resource occupation. The list
	 * of current events has the following properties:
	 * 
	 * <ul>
	 * <li>Every event denotes a certain timestamp in the schedule.</li>
	 * <li>Events must not overlap, that is, at a given timestamp, there must
	 * be only one event.</li>
	 * <li>Every <code>OldOccupation</code> has a start and an end event.</li>
	 * <li>Every event marks <em>at least</em> the start and/or the end of
	 * one <code>OldOccupation</code>. I.e., a single event may denote the
	 * beginning and/or ending of several <code>OldOccupation</code>s.</li>
	 * <li>The list of events imposes a total ordering on the timestamps of the
	 * events, in an ascending manner.</li>
	 * <li>An <code>OldOccupation</code> may cover several events due to the
	 * fact that other <code>OldOccupation</code>s can start or end in
	 * between of the <code>OldOccupation</code>s duration.</li>
	 * </ul>
	 * 
	 * @see Occupation
	 */
	private List<UtilizationChange> managedUtilizationChanges;


	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public Schedule(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.scheduledJobs = RemoveAllRetainAllSetDecorator
				.decorate(new HashSet<Job>());
		this.scheduledReservations = RemoveAllRetainAllSetDecorator
				.decorate(new HashSet<Reservation>());
		this.managedUtilizationChanges = new ArrayList<UtilizationChange>();
	}
	
	/**
	 * Copy constructor
	 * 
	 * Note: Not all attributes are initiated.
	 * 
	 * @param another
	 */
	public Schedule(Schedule another) {
		// Must set a unique scheduler 
		this.scheduler = null;
		
		this.scheduledJobs = RemoveAllRetainAllSetDecorator.decorate(new HashSet<Job>());
		this.scheduledJobs.addAll(another.scheduledJobs);
		
		this.scheduledReservations = RemoveAllRetainAllSetDecorator.decorate(new HashSet<Reservation>());
		for(Reservation r : another.scheduledReservations)
			this.scheduledReservations.add(r.clone());
		
		this.managedUtilizationChanges = new ArrayList<UtilizationChange>();
		for(UtilizationChange u : another.managedUtilizationChanges)
			this.managedUtilizationChanges.add(u.clone());
	}
		
	
	/**
	 * Creates a clone copy of schedule
	 */
	public Schedule clone() {
		return new Schedule(this);
	}
	

	final public Set<Job> getScheduledJobs() {
		return this.scheduledJobs;
	}

	final public Set<Reservation> getScheduledReservations() {
		return this.scheduledReservations;
	}
	
	final public List<UtilizationChange> getManagedUtilizationChanges() {
		return this.managedUtilizationChanges;
	}

	// ----- add[OldJob|Reservation]() and helper methods
	// -------------------------

	/**
	 * Adds a job to the schedule.
	 * <p>
	 * Note that it is checked whether the addition of the job produces a valid
	 * schedule. If this is the case, the job is added normally; otherwise, an
	 * exception is thrown.
	 * 
	 * @param j
	 *            The job to be added to the schedule.
	 * 
	 * @throws IllegalScheduleException
	 *             if the addition of the job given as an argument would
	 *             invalidate the schedule.
	 * @throws IllegalOccupationException
	 * 
	 * @see Job
	 */
	public void addJob(Job j)
			throws IllegalScheduleException, InvalidTimestampException,
			IllegalOccupationException {

		this.addOccupation(j);
		log.debug("added new job " + j + " to the schedule");
		this.scheduledJobs.add(j);

	}

	/**
	 * Adds a job to the schedule, replacing the given reservation. When the job
	 * has been added successfully, the reservation is purged. The replacement
	 * is performed only if the following rules are obeyed:
	 * <ol>
	 * <li>{@link Job#getStartTime()} is greater than or equal to
	 * {@link Reservation#getStartTime()},</li>
	 * <li>{@link Job#getEndTime()} is less than or equal to
	 * {@link Reservation#getEndTime()},</li>
	 * <li>{@link Job#getOccupiedResources()} is a subset of
	 * {@link Reservation#getOccupiedResources()}.</li>
	 * </ol>
	 * Note that this method does not perform any checks if the reservation is
	 * valid or not.
	 * 
	 * @param j
	 *            The job to be added to the schedule.
	 * @param r
	 *            The reservation to be replaced.
	 * @throws IllegalOccupationException
	 * @throws IllegalScheduleException
	 *             if the given reservation does not meet the job�s
	 *             requirements.
	 * @see Job
	 * @see Reservation
	 */
	public void addJob(Job j, Reservation r)
			throws IllegalOccupationException, IllegalScheduleException {

		boolean reservationIsValid = true;

		// first we check, if the reservation is in the schedule:
		if (!this.scheduledReservations.contains(r)) {
			reservationIsValid = false;
			String msg = "no reservation for job " + j + " found: " + r
					+ " unknown";
			throw new IllegalOccupationException(msg);
		}
		// if the job's startTime is before the beginning of the reservation
		// (OldJob.getStartTime() is smaller than reservation.getStartTime()
		// or if the job's endTime is after the end of the reservation
		// (OldJob.getEndTime() is greater than reservation.getEndTime()
		else if (j.getDuration().getAdvent()
				.before(r.getDuration().getAdvent())
				|| j.getDuration().getCessation().after(
						r.getDuration().getCessation())) {
			reservationIsValid = false;
			String msg = "reservation inappropriate for job " + j
					+ ": duration of " + r + " unmatching";
			throw new IllegalScheduleException(msg);
		}
		// OldJob#getOccupiedResources()} is a subset of
		// Reservation#getOccupiedResources()
		else if (j.getResources().isSubSetOf(r.getResources(), false)) {
			reservationIsValid = false;
			String msg = "reservation inappropriate for job " + j
					+ ": resources of " + r + " insufficient";
			throw new IllegalScheduleException(msg);
		}

		// if the resources are available
		if (reservationIsValid) {
			// we may replace the reservation
			this.removeReservation(r);

			// we try to add the occupation
			this.addOccupation(j);
			this.scheduledJobs.add(j);
		}

	}

	/**
	 * Adds a reservation to the schedule.
	 * <p>
	 * Note that it is checked whether the addition of the reservation produces
	 * a valid schedule and the reservation has not expired. If this is the
	 * case, the reservation is added normally; otherwise, an exception is
	 * thrown.
	 * 
	 * @param r
	 *            The reservation to be added to the schedule.
	 * @throws IllegalOccupationException
	 * @throws IllegalScheduleException
	 *             iif the addition of the reservation given as an argument
	 *             would invalidate the schedule.
	 * 
	 * @see Job
	 */
	public void addReservation(Reservation r)
			throws IllegalOccupationException, IllegalScheduleException {
		if (r.getExpirationTime().before(Clock.instance().now())) {
			String msg = "reservation invalid: " + r + " already expired";
			throw new IllegalScheduleException(msg);
		}
		else {
			this.addOccupation(r);
			this.scheduledReservations.add(r);
			Kernel.getInstance().dispatch(new ReservationEndEvent(r.getExpirationTime(),r,this.getScheduler().getSite()));
		}
	}

	/**
	 * An occupation may only be added if the following is true:
	 * <ol>
	 * <li>The occupied resources by the occupation are free for the whole
	 * duration of the occupation</li>
	 * <li>{@link Occupation#getStartTime()} is greater than the current time
	 * given by the clock</li>
	 * </ol>
	 * 
	 * To add a new occupation it is necessary to modify the events that occur
	 * during the time interval of o. The resources that are occupied by o are
	 * added to the occupied resources stored in the events. New events will be
	 * created if the timestamps of the start or end event are unequal to these
	 * stored in any event.
	 * 
	 * @param o
	 *            is the occupation we want to add.
	 */
	private void addOccupation(Occupation o)
			throws IllegalOccupationException {

		/*
		 * Check whether the requested occupation is valid at all.
		 */
		this.checkOccupationAdditionRequestValidity(o);

		// So far the occupation could be scheduled
		// if there is no scheduleEvent, we can schedule
		// else we have to check if the resources are free at the given time

		if (managedUtilizationChanges.isEmpty()) {

			UtilizationChange newStartEvent = new UtilizationChange(this);
			UtilizationChange newEndEvent = new UtilizationChange(this);

			newStartEvent.setTimestamp(o.getDuration().getAdvent());
			newEndEvent.setTimestamp(o.getDuration().getCessation());

			newStartEvent.incrementInvolvedOccupations();
			newEndEvent.incrementInvolvedOccupations();

			newStartEvent.addResources(o.getResources());
			managedUtilizationChanges.add(0, newStartEvent);
			managedUtilizationChanges.add(1, newEndEvent);
		}
		else {// we have to check, if there are free resources at the given
			// time.

			UtilizationChange testStartEvent = new UtilizationChange(this);
			testStartEvent.setTimestamp(o.getDuration().getAdvent());
			testStartEvent.addResources(o.getResources());

			UtilizationChange testEndEvent = new UtilizationChange(this);
			testEndEvent.setTimestamp(o.getDuration().getCessation());
			testEndEvent.addResources(o.getResources());

			int endIndex = Collections.binarySearch(
					this.managedUtilizationChanges, testEndEvent);

			// Does the end event exist?
			if (endIndex < 0) {
				// end event index does not exist. Therefore, a new
				// end event has to be added.
				endIndex = this.convertToInsertionIndex(endIndex);
				UtilizationChange newEnd = new UtilizationChange(this);
				newEnd.setTimestamp(o.getDuration().getCessation());
				// if the job is inserted before the first job in the schedule
				// the resources of the previous event don't need to be copied
				// the utilization will be updated later in the
				// updateUtilization method
				if (endIndex != 0) {
					newEnd.addResources(this.managedUtilizationChanges.get(
							endIndex - 1).getOccupiedResources());
				}
				newEnd.incrementInvolvedOccupations();
				this.managedUtilizationChanges.add(endIndex, newEnd);

			}
			else {
				this.managedUtilizationChanges.get(endIndex)
						.incrementInvolvedOccupations();
			}

			int startIndex = Collections.binarySearch(
					this.managedUtilizationChanges, testStartEvent);

			// Does the start event exist?
			if (startIndex < 0) {
				// If we try to insert a new start event this will be
				// in the position before the end event. Therefore, we
				// have to adjust the current end event index and increase
				// it by one.
				endIndex++;
				// start event index does not exist. Therefore, a new
				// start event has to be added.
				startIndex = this.convertToInsertionIndex(startIndex);
				UtilizationChange newStart = new UtilizationChange(this);
				newStart.setTimestamp(o.getDuration().getAdvent());
				// if the job is inserted before the first job in the schedule
				// the resources of the previous event don't need to be copied
				// the utilization will be updated later in the
				// updateUtilization method
				if (startIndex != 0) {
					newStart.addResources(this.managedUtilizationChanges.get(
							startIndex - 1).getOccupiedResources());
				}
				newStart.incrementInvolvedOccupations();
				this.managedUtilizationChanges.add(startIndex, newStart);
			}
			else {
				this.managedUtilizationChanges.get(startIndex)
						.incrementInvolvedOccupations();
			}

			this.updateUtilization(o.getResources(), startIndex, endIndex,true);
		}

	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param o
	 * @throws IllegalOccupationException
	 */
	private void checkOccupationAdditionRequestValidity(Occupation o)
			throws IllegalOccupationException {
		/*
		 * Check whether the requested search area begins in the "past"
		 * (relative to the systems current timestamp).
		 */
		if (o.getDuration().getAdvent().before(Clock.instance().now())) {
			String msg = "invalid search area: start time must not lie in the past.";
			throw new IllegalOccupationException(msg);
		} // if

		/*
		 * Check whether the number of requested resources can be satisfied at
		 * all.
		 */
		if (o.getResources().size() > this.scheduler.getSite()
				.getSiteInformation().getNumberOfAvailableResources()) {
			String msg = "invalid resource request: exceeding size of site.";
			throw new IllegalOccupationException(msg);
		} // if
	}

	// ----- modify[OldJob|Reservation]() and helper methods
	// ----------------------

	/**
	 * Deletes the given job from the schedule.
	 * 
	 * @param j
	 *            The job to be deleted from the schedule.
	 * @throws IllegalOccupationException
	 *             if the given job is not known to the schedule.
	 */
	public void removeJob(Job j)
			throws IllegalOccupationException {
		if (this.scheduledJobs.contains(j)) {
			this.removeOccupation(j);
			this.scheduledJobs.remove(j);
		} // if
		else {
			String msg = "removal failed: job " + j + " unknown.";
			throw new IllegalOccupationException(msg);
		} // else
	}

	/**
	 * Deletes the given reservation from the schedule.
	 * 
	 * @param r
	 *            The reservation to be deleted from the schedule.
	 * @throws IllegalOccupationException
	 *             iff the given reservation is not known to the schedule.
	 */
	public void removeReservation(Reservation r)
			throws IllegalOccupationException {
		if (this.scheduledReservations.contains(r)) {
			this.removeOccupation(r);
			this.scheduledReservations.remove(r);
		} // if
		else {
			String msg = "removal failed: reservation " + r + " unknown.";
			throw new IllegalOccupationException(msg);
		} // else
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param o
	 * @throws IllegalOccupationException
	 */
	private void removeOccupation(Occupation o)
			throws IllegalOccupationException {
		UtilizationChange occupationStartingUtilizationChange = new UtilizationChange(
				this, o.getDuration().getAdvent());
		UtilizationChange occupationEndingUtilizationChange = new UtilizationChange(
				this, o.getDuration().getCessation());

		int startIndex = Collections.<UtilizationChange> binarySearch(
				this.managedUtilizationChanges,
				occupationStartingUtilizationChange);
		int endIndex = Collections.<UtilizationChange> binarySearch(
				this.managedUtilizationChanges,
				occupationEndingUtilizationChange);

		if (endIndex==-1*this.managedUtilizationChanges.size()){
			endIndex=this.managedUtilizationChanges.size()-1;
		}
		
		
		if (startIndex < 0 || endIndex < 0) {
			String msg = "removal failed: utilization change not found.";
			throw new IllegalOccupationException(msg);
		}

		this.managedUtilizationChanges.get(startIndex)
				.decrementInvolvedOccupations();
		this.managedUtilizationChanges.get(endIndex)
				.decrementInvolvedOccupations();

		this.updateUtilization(o.getResources(), startIndex, endIndex, false);

		// the order is important, because of the index changing
		this.removeUninvolvedUtilizationChanges(endIndex);
		this.removeUninvolvedUtilizationChanges(startIndex);

	}

	// ----- modify[OldJob|Reservation]() and helper methods
	// ----------------------

	/**
	 * TODO: not yet commented
	 * 
	 * @param j
	 * @param newDuration
	 */
	public void modifyJob(Job j, Period newDuration) {
		// TODO: not yet implemented.
		this.modifyOccupation(j, newDuration);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param j
	 * @param newOccupiedResources
	 */
	public void modifyJob(Job j, SortedSet<Resource> newOccupiedResources) {
		// TODO: not yet implemented.
		this.modifyOccupation(j, newOccupiedResources);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param r
	 * @param newDuration
	 */
	public void modifyReservation(Reservation r, Period newDuration) {
		// TODO: not yet implemented.
		this.modifyOccupation(r, newDuration);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param r
	 * @param newOccupiedResources
	 */
	public void modifyReservation(Reservation r,
			SortedSet<Resource> newOccupiedResources) {
		// TODO: not yet implemented.
		this.modifyOccupation(r, newOccupiedResources);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param o
	 * @param newDuration
	 */
	private void modifyOccupation(Occupation o, Period newDuration) {
		// TODO: not yet implemented.
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param o
	 * @param newOccupiedResources
	 */
	private void modifyOccupation(Occupation o,
			SortedSet<Resource> newOccupiedResources) {
		// TODO: not yet implemented.
	}

	// ----- findNextFreeSlot() and helper methods -----------------------------

	/**
	 * @param occupationLength
	 * @param requiredResources
	 * @param searchArea
	 * @return
	 * 
	 * This method returns the first possible time slot inside the searchArea,
	 * that is exactly of the size determined by occupationLength. During the
	 * TimeInterval given by the Slot the requiredResources are not occupied. If
	 * that is not possible, then the returned Slot is null.
	 * 
	 * @throws InvalidTimestampException
	 * @throws IllegalOccupationException
	 * 
	 */
	public Slot findNextFreeSlot(Distance occupationLength,
			int requiredResources, Period searchArea)
			throws InvalidTimestampException, IllegalOccupationException {

		Slot result = null;

		this.checkSlotRequestValidity(searchArea, occupationLength,
				requiredResources);

		if (this.managedUtilizationChanges.isEmpty()) {
			/*
			 * Since no occupations are currently scheduled, the slot request
			 * can always be satisfied; therefore, a new slot beginning at the
			 * search area start and ending at the (search area start +
			 * duration) is returned.
			 */
			result = new Slot(searchArea.getAdvent(), occupationLength);
			result.setResources(new ResourceBundleBitVectorImpl(this.scheduler.getSite().getSiteInformation(), this.scheduler.getSite().getSiteInformation().getProvidedResources().toArray()));
			//List<Resource> tmp = this.scheduler.getSite().getSiteInformation().getProvidedResources().subList(0, requiredResources);
			//result.setResources(new ResourceBundleBitVectorImpl(this.scheduler.getSite().getSiteInformation(), tmp.toArray(new Resource[0]) ));
			//System.out.println(tmp);
		} // if
		else {
			/*
			 * Find the index of the occupation change from which this search
			 * should start.
			 */
			int relevantIndexOfUtilizationChange = this
					.determineRelevantUtilizationChange(searchArea.getAdvent());
			if (relevantIndexOfUtilizationChange < 0) {
				/*
				 * For the requested timestamp, there currently exists no
				 * OccupationChange; therefore, use the next one before the
				 * given timestamp.
				 */
				relevantIndexOfUtilizationChange = this
						.convertToInsertionIndex(relevantIndexOfUtilizationChange) - 1;
			} // if

			// Set the start index to the relevant index
			// If the relevant index is negative this indicates that we are
			// searching before
			// the first inserted event. The relevant index however will remain
			// Negative and
			// only the start and end indices will be adjusted.
			int slotCandidateStartIndex = relevantIndexOfUtilizationChange;
			// If the slotCandidateStartIndex is negative set it to zero
			slotCandidateStartIndex = slotCandidateStartIndex < 0 ? 0 : slotCandidateStartIndex;
			int slotCandidateEndIndex = slotCandidateStartIndex;

			// If the relevant index is negative this indicates that we are
			// searching before
			// the first inserted event. In this case, we have to insert a dummy
			// UtilizationChange with no occupied resources at position 0 which
			// has a timestamp equal to the searchArea startTime.
			if (relevantIndexOfUtilizationChange < 0) {
				UtilizationChange newUtil = new UtilizationChange(this);
				newUtil.setTimestamp(searchArea.getAdvent());
				this.managedUtilizationChanges.add(0, newUtil);
			}

			/*
			 * Calculate the potential offset value between the timestamp at the
			 * determined start index and the start time of the given search
			 * area.
			 */
			Distance offsetFromStartIndex = this.calculateOffsetFromIndex(
					slotCandidateStartIndex, searchArea.getAdvent());

			ResourceBundle lastGoodSetOfAvailableResources = this
					.determineAvailableSetOfResources(slotCandidateStartIndex);
			/*
			 * From the last determined start index, try to enlarge the slot
			 * until the slot length is sufficient for the requested
			 * occupationLength while ensuring that, over the whole slot length,
			 * a satisfying set of resources has a sustained availability.
			 * 
			 * Otherwise, the end index of the last (failed) enlargement is used
			 * as the start index for the next (potentially successful)
			 * enlargement.
			 * 
			 * This process is repeated until either a valid slot is found or
			 * the specified search area is exceeded.
			 */
			while (!(this.managedUtilizationChanges.get(slotCandidateEndIndex)
					.getTimestamp().after(searchArea.getCessation()))) {
				/*
				 * The end of the specified search area has not been exceeded.
				 * Therefore, the resource availability for the current slot
				 * candidate has to be checked.
				 */

				if (this.checkResourceAvailability(requiredResources,
						lastGoodSetOfAvailableResources, slotCandidateEndIndex)) {
					/*
					 * For the current slot candidate, the set of available
					 * resources is valid. As such, check whether the slot
					 * candidate length is already sufficient.
					 */
					if (this.isSlotLengthSufficient(occupationLength,
							slotCandidateStartIndex, slotCandidateEndIndex + 1,
							offsetFromStartIndex)) {

						/*
						 * Recalculate the potential offset value between the
						 * timestamp at the determined start index and the start
						 * time of the given search area.
						 */
						Distance offSetFromStartIndex = this
								.calculateOffsetFromIndex(
										slotCandidateStartIndex, searchArea
												.getAdvent());
						// The offset and the current starttime of the schedule
						// events
						// result in the actual starttime of this potential
						// slot.
						Instant startTime = TimeHelper.add( this.managedUtilizationChanges.get(slotCandidateStartIndex)
								.getTimestamp(),offsetFromStartIndex);
						Instant endTime = TimeHelper.add(startTime, occupationLength);
						// Nevertheless, we have to check if the slot ends
						// before the search area. It is only created if the
						// slot is within the area. Otherwise, null is returned

						if (!endTime.after(searchArea.getCessation())) {
							/*
							 * The length of the current slot candidate is
							 * sufficient: create a new slot and exit the loop.
							 */
							result = this.createSlot(occupationLength,
									lastGoodSetOfAvailableResources,
									slotCandidateStartIndex,
									offSetFromStartIndex);
						}
						break;
					} // if
					else {
						/*
						 * The current slot candidate is not long enough, but
						 * still valid (in terms of sufficient resources). As
						 * such, try to enlarge it and repeat the loop.
						 */
						slotCandidateEndIndex++;
						lastGoodSetOfAvailableResources = lastGoodSetOfAvailableResources
								.intersect(this
										.determineAvailableSetOfResources(slotCandidateEndIndex));
					} // else
				} // if
				else {
					/*
					 * For the current slot candidate, the set of available
					 * resources is not valid anymore. Therefore, start over
					 * from the end index of the last (failed) enlargement,
					 * resetting the offset value and resource set.
					 */
					if (slotCandidateStartIndex == slotCandidateEndIndex) {
						/*
						 * The last unsuccessful slot had a length of 1 (thus
						 * including a single OccupationChange only). Therefore,
						 * both indices have to be moved one step further to
						 * initiate a new try.
						 */
						slotCandidateStartIndex++;
						slotCandidateEndIndex++;
					} // if
					// Adjust the indices.
					if (slotCandidateStartIndex < slotCandidateEndIndex) {
						slotCandidateStartIndex++;
					}

					slotCandidateEndIndex = slotCandidateStartIndex;
					offsetFromStartIndex = this.calculateOffsetFromIndex(
							slotCandidateStartIndex, searchArea.getAdvent());
					lastGoodSetOfAvailableResources = this
							.determineAvailableSetOfResources(slotCandidateStartIndex);
				} // else
			} // while

			// If we have inserted a dummy UtilizationChange due to a negative
			// relevant index we have to remove it after the search. This method
			// should not add UtilizationChanges to the schedule permanently.
			if ((relevantIndexOfUtilizationChange < 0)) {
				this.managedUtilizationChanges.remove(0);
			}

		} // else

		return result;
	}
	
	/**
	 * @param occupationLength
	 * @param requiredResources
	 * @param searchArea
	 * @return
	 * 
	 * This method returns the first possible period inside the searchArea,
	 * where the amount of requiredResources is not occupied. If
	 * that is not possible, then the returned period is null.
	 * 
	 * @throws InvalidTimestampException
	 * @throws IllegalOccupationException
	 * 
	 */
	public Period findNextFreePeriod(int requiredResources, Period searchArea)
			throws InvalidTimestampException, IllegalOccupationException {

		Period result = null;

		/*
		 * Check whether the number of requested resources is positive and can
		 * be satisfied at all.
		 */
		if (requiredResources <= 0) {
			String msg = "invalid resource request: number of required resources must be positive.";
			throw new IllegalOccupationException(msg);
		} // if
		else if (requiredResources > this.scheduler.getSite()
				.getSiteInformation().getNumberOfAvailableResources()) {
			String msg = "invalid resource request: exceeding size of site.";
			throw new IllegalOccupationException(msg);
		} // if
		 
		
		if (this.managedUtilizationChanges.isEmpty()) {
			/*
			 * Since no occupations are currently scheduled, the Period is an eternal period
			 * starting by the present time
			 */
			result = searchArea;
		} // if
		else {
			/*
			 * Find the index of the occupation change from which this search
			 * should start.
			 */
			int relevantIndexOfUtilizationChange = this
					.determineRelevantUtilizationChange(searchArea.getAdvent());
			if (relevantIndexOfUtilizationChange < 0) {
				/*
				 * For the requested timestamp, there currently exists no
				 * OccupationChange; therefore, use the next one before the
				 * given timestamp.
				 */
				relevantIndexOfUtilizationChange = this
						.convertToInsertionIndex(relevantIndexOfUtilizationChange) - 1;
			} // if

			// Set the start index to the relevant index
			// If the relevant index is negative this indicates that we are
			// searching before
			// the first inserted event. The relevant index however will remain
			// negative and
			// only the start and end indices will be adjusted.
			int periodCandidateStartIndex = relevantIndexOfUtilizationChange;
			// If the periodCandidateStartIndex is negative set it to zero
			periodCandidateStartIndex = periodCandidateStartIndex < 0 ? 0 : periodCandidateStartIndex;
			int periodCandidateEndIndex = periodCandidateStartIndex;

			// If the relevant index is negative this indicates that we are
			// searching before
			// the first inserted event. In this case, we have to insert a dummy
			// UtilizationChange with no occupied resources at position 0 which
			// has a timestamp equal to the searchArea startTime.
			if (relevantIndexOfUtilizationChange < 0) {
				UtilizationChange newUtil = new UtilizationChange(this);
				newUtil.setTimestamp(searchArea.getAdvent());
				this.managedUtilizationChanges.add(0, newUtil);
			}

			/*
			 * Calculate the potential offset value between the timestamp at the
			 * determined start index and the start time of the given search
			 * area.
			 */
			Distance offsetFromStartIndex = this.calculateOffsetFromIndex(
					periodCandidateStartIndex, searchArea.getAdvent());

			ResourceBundle lastGoodSetOfAvailableResources = this
					.determineAvailableSetOfResources(periodCandidateStartIndex);
			/*
			 * From the last determined start index, try to enlarge the period
			 * as much as possible
			 * 
			 * 
			 * This process is repeated until either the resourcedemands are 'nt fullfilled
			 * anymore or the specified search area is exceeded.
			 */
			while (!(this.managedUtilizationChanges.get(periodCandidateEndIndex)
					.getTimestamp().after(searchArea.getCessation()))) {
				/*
				 * The end of the specified search area has not been exceeded.
				 * Therefore, the resource availability for the current utilizationchange
				 * candidate has to be checked.
				 */

				if (this.checkResourceAvailability(requiredResources,
						lastGoodSetOfAvailableResources, periodCandidateEndIndex)) {
					
					
					/*
				    * The current utilizationchange is still valid (in terms of sufficient resources). As
					* such, try to enlarge the period and repeat the loop. If its not expandable, then the given Period is
					* the whole to be found
					*/
					if (periodCandidateEndIndex==this.managedUtilizationChanges.size()-1){
						Instant startTime = TimeHelper.add(this.managedUtilizationChanges.get(
								periodCandidateStartIndex).getTimestamp(), offsetFromStartIndex);
						result=new Period(startTime,searchArea.getCessation());
						return result;
					} else{
						periodCandidateEndIndex++;
						lastGoodSetOfAvailableResources = lastGoodSetOfAvailableResources
								.intersect(this
										.determineAvailableSetOfResources(periodCandidateEndIndex));
					}
					
					
				} // if
				else {
					/*
					 * For the current utilizationchange, the set of available
					 * resources is not valid anymore. Therefore, create the result-Period, if 
					 * we have at least one valid period of time, where the resourcedemands are fullfilled
					 */
					if (periodCandidateEndIndex!=periodCandidateStartIndex){
						Instant startTime = TimeHelper.add(this.managedUtilizationChanges.get(
								periodCandidateStartIndex).getTimestamp(), offsetFromStartIndex);
						result=new Period(startTime,this.managedUtilizationChanges.get(periodCandidateEndIndex)
								.getTimestamp());
						return result;
					} else{
						//we have to search at a later time
						periodCandidateStartIndex++;
						periodCandidateEndIndex=periodCandidateStartIndex;
						
						offsetFromStartIndex = TimeFactory.newFinite(0L);

						lastGoodSetOfAvailableResources = this
						.determineAvailableSetOfResources(periodCandidateStartIndex);
					}
				} // else
			} // while

			// If we have inserted a dummy UtilizationChange due to a negative
			// relevant index we have to remove it after the search. This method
			// should not add UtilizationChanges to the schedule permanently.
			if ((relevantIndexOfUtilizationChange < 0)) {
				this.managedUtilizationChanges.remove(0);
			}
			
			if (this.managedUtilizationChanges.get(periodCandidateEndIndex)
					.getTimestamp().isEternity() && 
					lastGoodSetOfAvailableResources.size()>=requiredResources){
				Instant startTime = TimeHelper.add(this.managedUtilizationChanges.get(
						periodCandidateStartIndex).getTimestamp(), offsetFromStartIndex);
				result=new Period(startTime,searchArea.getCessation());
				return result;
			}

		} // else

		return result;
	}
	
	

	/**
	 * TODO: not yet commented
	 * 
	 * @param searchArea
	 * @throws InvalidTimestampException
	 * @throws IllegalOccupationException
	 */
	private void checkSlotRequestValidity(Period searchArea,
			Distance occupationLength, int requestedResources)
			throws InvalidTimestampException, IllegalOccupationException {
		/*
		 * Check whether the requested search area begins in the "past"
		 * (relative to the systems current timestamp).
		 */
		if (searchArea.getAdvent().before(Clock.instance().now())) {
			String msg = "invalid search area: start time must not lie in the past.";
			throw new InvalidTimestampException(msg);
		} // if

		/*
		 * Check whether the length of the given search area is sufficient for
		 * the given occupation length.
		 */
		if (searchArea.distance().shorter(occupationLength)) {
			String msg = "invalid search area: too short for given occupation length.";
			throw new InvalidTimestampException(msg);
		} // if

		/*
		 * Check whether the number of requested resources is positive and can
		 * be satisfied at all.
		 */
		if (requestedResources <= 0) {
			String msg = "invalid resource request: number of requested resources must be positive.";
			throw new IllegalOccupationException(msg);
		} // if
		else if (requestedResources > this.scheduler.getSite()
				.getSiteInformation().getNumberOfAvailableResources()) {
			String msg = "invalid resource request: exceeding size of site.";
			throw new IllegalOccupationException(msg);
		} // if

	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param occupationLength
	 * @param freeResources
	 * @param startIndex
	 * @param offSetFromStartIndex
	 * @return
	 */
	private Slot createSlot(Distance occupationLength,
			ResourceBundle resources, int startIndex,
			Distance offSetFromStartIndex) {
		Slot result = null;

		Instant startTime = TimeHelper.add(this.managedUtilizationChanges.get(
				startIndex).getTimestamp(), offSetFromStartIndex);
		result = new Slot(startTime, occupationLength);
		result.setResources(resources);

		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param requiredResources
	 * @param lastGoodSetOfResources
	 * @param indexPositionToCheck
	 * @return
	 */
	private boolean checkResourceAvailability(int requiredResources,
			ResourceBundle lastGoodSetOfResources, int indexPositionToCheck) {
		boolean result = false;
		if (this.determineAvailableNumberOfResources(indexPositionToCheck) < requiredResources) {
			result = false;
		} // if
		else {
			ResourceBundle candidateResources = lastGoodSetOfResources
					.intersect(this
							.determineAvailableSetOfResources(indexPositionToCheck));
			result = !(candidateResources.size() < requiredResources);
		} // else
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param indexOfUtilizationChange
	 * @return
	 */
	private ResourceBundle determineAvailableSetOfResources(
			int indexOfUtilizationChange) {
		ResourceBundle result = null;
		if (indexOfUtilizationChange >= this.managedUtilizationChanges.size() - 1) {
			/*
			 * The requested position lies at or behind the end of the list of
			 * known UtilizationChange entries. Therefore, all resources are
			 * available.
			 */
			result = new ResourceBundleBitVectorImpl(this.scheduler.getSite()
					.getSiteInformation(), this.scheduler.getSite()
					.getSiteInformation().getProvidedResources().toArray());
			result.add(this.getScheduler().getSite().getSiteInformation().getLentResources());
		} // if
		else {
			result = this.managedUtilizationChanges.get(
					indexOfUtilizationChange).getFreeResources();
		} // else
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param indexOfUtilizationChange
	 * @return
	 */
	private int determineAvailableNumberOfResources(int indexOfUtilizationChange) {
		int result = 0;
		if (indexOfUtilizationChange >= this.managedUtilizationChanges.size() - 1) {
			/*
			 * The requested position lies at or behind the end of the list of
			 * known UtilizationChange entries. Therefore, all resources are
			 * available.
			 */
			result = this.scheduler.getSite().getSiteInformation()
					.getNumberOfAvailableResources();
		} // if
		else {
			result = this.managedUtilizationChanges.get(
					indexOfUtilizationChange).getFreeness();
		} // else
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param occupationLength
	 * @param startIndexOfUtilizationChange
	 * @param endIndexOfUtilizationChange
	 * @param offsetFromStartIndex
	 * @return
	 */
	private boolean isSlotLengthSufficient(Distance occupationLength,
			int startIndexOfUtilizationChange, int endIndexOfUtilizationChange,
			Distance offsetFromStartIndex) {
		boolean result = false;

		if (endIndexOfUtilizationChange >= this.managedUtilizationChanges
				.size() - 1) {
			/*
			 * The slot candidate ends somewhere at or after the last known
			 * OccupationChange; therefore, it can be extended infinitely and
			 * is, by definition, always long enough.
			 */
			result = true;
		} // if
		else {
			Instant startIndexTimestamp = this.managedUtilizationChanges.get(
					startIndexOfUtilizationChange).getTimestamp();
			Instant endIndexTimestamp = this.managedUtilizationChanges.get(
					endIndexOfUtilizationChange).getTimestamp();
			/*
			 * Calculate the length of the slot, considering that there might be
			 * an offset from the timestamp at the UtilizationChange to the
			 * earliest start time of the slot (due to the search area
			 * restriction).
			 */
			Distance resultAreaLength = TimeHelper.distance(endIndexTimestamp,
					startIndexTimestamp);

			Distance slotLength = TimeHelper.subtract(resultAreaLength,
					offsetFromStartIndex);

			if (occupationLength.longer(slotLength)) {
				result = false;
			} // if
			else {
				result = true;
			} // else
		}
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param index
	 * @param timestamp
	 * @return
	 */
	private Distance calculateOffsetFromIndex(int index, Instant timestamp) {
		Distance result = TimeFactory.newFinite(0);

		if (index >= 0) {
			Instant indexTimestamp = this.managedUtilizationChanges.get(index)
					.getTimestamp();
			if (indexTimestamp.before(timestamp)) {
				result = TimeHelper.distance(indexTimestamp, timestamp);
			} // if
		} // if

		return result;
	}

	/**
	 * Returns the index of the latest {@link UtilizationChange} before the
	 * given timestamp or (if existent) the <code>UtilizationChange</code>
	 * with the given timestamp, from the list of utilization changes
	 * representing this schedule.
	 * 
	 * Note that this method does not differentiate between existing and
	 * non-existing elements; this should be done using the
	 * {@link Collection#contains(Object)} method.
	 * 
	 * @param timestamp
	 *            The reference timestamp to be searched for.
	 * @return The <code>UtilizationChange</code> element with the given
	 *         timestamp, or the latest before it.
	 * 
	 * @see Collections#binarySearch(List, Object)
	 */
	private int determineRelevantUtilizationChange(Instant timestamp) {
		int result = 0;
		UtilizationChange uc = new UtilizationChange(this);
		uc.setTimestamp(timestamp);
		Collections.sort(this.managedUtilizationChanges); // MODIFICACION
		result = Collections.binarySearch(this.managedUtilizationChanges, uc);
		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param searchResultIndex
	 * @return
	 */
	private int convertToInsertionIndex(int searchResultIndex) {
		return (searchResultIndex < 0 ? Math.abs(searchResultIndex) - 1 : searchResultIndex);
	}

	/**
	 * This method updates all UtilizationChanges form the start index
	 * (inclusive) ot the end index (exclusive) with the given set of resources.
	 * Thereby, the resources are added if an occupation has been added or
	 * removed correspondigly.
	 * 
	 * @param resources
	 *            The set of resources that is added or removed
	 * @param start
	 *            the start index for the update loop (inclusive)
	 * @param end
	 *            the end index for the update loop (exclustive)
	 * @param add
	 *            if true the resources are adde or removed otherwise.
	 */
	private void updateUtilization(ResourceBundle resources, int start,
			int end, boolean add) {
		/*
		 * Determine whether to add or to remove occupied resources.
		 */
		if (add) {
			/*
			 * Increase the utilization for all jobs between - the starting
			 * (included) and - the ending (excluded) utilization change
			 */
			for (int i = start; i < end; i++) {
				this.managedUtilizationChanges.get(i).addResources(resources);
			} // for
		} // if
		else {
			/*
			 * decrease the utilization for all jobs between - the starting
			 * (included) and - the ending (excluded) utilization change
			 */
			for (int i = start; i < end; i++) {
				this.managedUtilizationChanges.get(i)
						.removeResources(resources);
			} // for
		} // if
	}

	/**
	 * 
	 * This method removed the UtilizationChange element at the given index if
	 * the involvedOccupation counter is equal to zero. This indicates that this
	 * element is not needen anymore.
	 * 
	 * @param index
	 *            The index of an {@link UtilizationChange} element within the
	 *            currentSCheduleEvents that is checked. If the
	 *            involvenOccupation count is equal to zero the elemet is
	 *            removed.
	 * @return true if it was possible to remove the tested element ans false
	 *         otherwise.
	 * 
	 */
	private boolean removeUninvolvedUtilizationChanges(int index) {
		boolean removed = false;
		if (this.managedUtilizationChanges.get(index).getInvolvedOccupations() <= 0) {
			this.managedUtilizationChanges.remove(index);
			removed = true;
		}

		return removed;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @return
	 */
	public int getUtilizationAt(Instant timestamp) {
		int result = 0;

		int index = this.convertToInsertionIndex(this
				.determineRelevantUtilizationChange(timestamp));
		if (this.managedUtilizationChanges.size() > 0) {
			result = this.managedUtilizationChanges.get(index).getUtilization();
		}
		else {
			result = 0;
		}

		return result;
	}

	public int getFreenessAt(Instant timestamp) {
		int result = 0;

		int index = this.convertToInsertionIndex(this
				.determineRelevantUtilizationChange(timestamp));
		if (this.managedUtilizationChanges.isEmpty()
				|| (index > (this.managedUtilizationChanges.size() - 1))) {
			result = this.scheduler.getSite().getSiteInformation()
					.getNumberOfAvailableResources();
		} // if
		else {
			result = this.managedUtilizationChanges.get(index).getFreeness();
		} // else
		return result;
	}
	
	public int getActualFreeness(){
		int result=this.scheduler.getSite().getSiteInformation()
		.getNumberOfAvailableResources();
		for (Job j:this.scheduledJobs){
			result-=j.getDescription().getNumberOfRequestedResources();
		}
		return result;
	}
	
	public int getNumberOfActualUtilizedProcessors(){
		int result=0;
		for (Job j:this.scheduledJobs){
			result+=j.getDescription().getNumberOfRequestedResources();
		}
		return result;
	}

	@Deprecated
	public Job shortenJobDuration(Job j, Period newDuration)
			throws IllegalScheduleException, InvalidTimestampException {
		return ( Job ) this.shortenOccupation(j, newDuration);
	}

	/**
	 * 
	 * The beginning of the new TimeInterval must be equal to the old one. The
	 * new end must not be after the old end.
	 * 
	 * @param o
	 * @param newDuration
	 * @throws InvalidTimestampException
	 * @throws FailedOccupationModificationException
	 */
	@Deprecated
	private Occupation shortenOccupation(Occupation currentOccupation,
			Period newDuration)
			throws IllegalScheduleException, InvalidTimestampException {
		Instant oldStartTime = currentOccupation.getDuration().getAdvent();
		Instant oldEndTime = currentOccupation.getDuration().getCessation();
		Instant newStartTime = newDuration.getAdvent();
		Instant newEndTime = newDuration.getCessation();

		if (!(newStartTime.equals(oldStartTime))) {
			String msg = "given duration start times do not match: original timestamp \""
					+ currentOccupation.getDuration().getAdvent()
					+ "\" != new timestamp \"" + newDuration.getAdvent() + "\"";
			throw new InvalidTimestampException(msg);
		}
		else if (newEndTime.after(oldEndTime)) {
			//CHANGE: Allow later endtimes after planning phase
			/*String msg = "given duration end times invalid: original timestamp \""
					+ currentOccupation.getDuration().getCessation()

					+ "\" !> new timestamp \"" + newDuration.getCessation() + "\"";
			throw new InvalidTimestampException(msg);*/
		}
		else if (newEndTime.equals(oldEndTime)) {
			// Nothing to do here!!!
		}
		else if (!this.scheduledJobs.contains(currentOccupation)) {
			String msg = "requested occupation not found";
			throw new IllegalScheduleException(msg);
		}
		else {
			// Determine start index of current occupation
			UtilizationChange testStartEvent = new UtilizationChange(this);
			testStartEvent.setTimestamp(currentOccupation.getDuration()
					.getAdvent());

			// Determine end index of current occupation
			UtilizationChange testEndEvent = new UtilizationChange(this);
			testEndEvent.setTimestamp(currentOccupation.getDuration()
					.getCessation());

			// Find the end index of the old schedule event
			int oldEndIndex = Collections.<UtilizationChange> binarySearch(
					this.managedUtilizationChanges, testEndEvent);

			if (oldEndIndex < 0) {
				String msg = "shortening failed: ending utilization change not found";
				throw new IllegalScheduleException(msg);
			}

			// Removed the assigned job from the end event by
			// decreasing the corresponding counter.
			this.managedUtilizationChanges.get(oldEndIndex)
					.decrementInvolvedOccupations();

			UtilizationChange newEndEvent = new UtilizationChange(this);
			newEndEvent.setTimestamp(newDuration.getCessation());

			int newEndIndex = Collections.binarySearch(
					this.managedUtilizationChanges, newEndEvent);

			if (newEndIndex < 0) {
				log
						.debug("adding new utilization change for shortened occupation "
								+ currentOccupation);
				newEndIndex = Math.abs(newEndIndex) - 1;
				newEndEvent.addResources(this.managedUtilizationChanges.get(
						newEndIndex - 1).getOccupiedResources());
				newEndEvent.removeResources(currentOccupation.getResources());
				newEndEvent.incrementInvolvedOccupations();
				this.managedUtilizationChanges.add(newEndIndex, newEndEvent);
				oldEndIndex++;
				this.updateUtilization(currentOccupation.getResources(),
						newEndIndex, oldEndIndex, false);
			}
			// new end index already exists
			else {
				this.managedUtilizationChanges.get(newEndIndex)
						.incrementInvolvedOccupations();
				this.updateUtilization(currentOccupation.getResources(),
						newEndIndex, oldEndIndex, false);

			}
			// remove the old event if it is not neede anymore
			this.removeUninvolvedUtilizationChanges(oldEndIndex);

			// Set the new duration of this occupation
			this.scheduledJobs.remove(( Job ) currentOccupation);
			currentOccupation.setDuration(newDuration);
			this.scheduledJobs.add(( Job ) currentOccupation);
		}
		return currentOccupation;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} // if
		if (this == other) {
			return true;
		} // if
		if (!(other instanceof Schedule)) {
			return false;
		} // if
		Schedule candidate = ( Schedule ) other;
		return new EqualsBuilder().append(this.scheduledJobs,
				candidate.scheduledJobs).append(this.scheduledReservations,
				candidate.scheduledReservations).append(
				this.managedUtilizationChanges,
				candidate.managedUtilizationChanges).isEquals();
	}

	Scheduler getScheduler() {
		return this.scheduler;
	}

	public void addResources(ResourceBundle rb, Period timespan, Site origin) {
		//Create the Resource-onjects and add them to the lentResources of the site
		int newAmount=rb.size();
		int oldAmount= this.scheduler.getSite().getSiteInformation().getProvidedResources().size()
			+ this.scheduler.getSite().getSiteInformation().getLentResources().size();		
		String praefix=origin.getName()+"-";
		Resource r;
		List<Resource> rl=new ArrayList<Resource>();
		for (int i=oldAmount+1;i<oldAmount+newAmount+1;i++){
			UUID id=UUID.randomUUID();
			r=new Resource(praefix+id.toString());
			r.setOrdinal(i);
			r.setID(id);
			r.setSite(origin);
			rl.add(r);
		}
		this.getScheduler().getSite().getSiteInformation().addLentResources(rl);
		ResourceBundle rbsite=this.getScheduler().getSite().getSiteInformation().getLentResources().createSubSetWith(rl.size(),
				this.getScheduler().getSite().getSiteInformation().getLentResources().size()-rl.size());
		
		
		//We just can add the new utilization change for the new resources
		UtilizationChange newStartEvent = new UtilizationChange(this);
		newStartEvent.setTimestamp(timespan.getAdvent());
		
		if (managedUtilizationChanges.isEmpty()) {
			managedUtilizationChanges.add(0, newStartEvent);	
		}
		else {// we have to check, if there are free resources at the given
			// time.

			int startIndex = Collections.binarySearch(
					this.managedUtilizationChanges, newStartEvent);

			// Does the start event exist?
			if (startIndex < 0) {
				// start event index does not exist. Therefore, a new
				// start event has to be added.
				startIndex = this.convertToInsertionIndex(startIndex);
				// if the job is inserted before the first job in the schedule
				// the resources of the previous event don't need to be copied
				// the utilization will be updated later in the
				// updateUtilization method
				if (startIndex != 0) {
					newStartEvent.addResources(this.managedUtilizationChanges.get(
							startIndex - 1).getOccupiedResources());
				}
				this.managedUtilizationChanges.add(startIndex, newStartEvent);
			}
			else {
				this.managedUtilizationChanges.get(startIndex)
						.addNewResources(rbsite);
			}

			this
					.updateUtilization(rbsite, startIndex, this.managedUtilizationChanges.size(),
							false);
		}
		
		if (!(timespan.getCessation().isEternity())){
			//We have to insert a reservation for the lent resources to realize
			//the planned return of them
			Reservation newR=new Reservation(timespan.getCessation(),TimeFactory.newEternity(),rbsite);
			try {
				this.addReservation(newR);
				//Set A Timer for the end of the reservation
				Event e=new ReturnOfLentResourcesEvent(newR.getDuration().getAdvent(),newR,this.scheduler.getSite()); 
				Kernel.getInstance().dispatch(e);
			} catch (IllegalOccupationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalScheduleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	
	public void removeLentResources(ResourceBundle resources) {
		
		UtilizationChange newStartEvent = new UtilizationChange(this);
		newStartEvent.setTimestamp(this.scheduler.getSite().getSiteInformation().getTimeOfLoan(resources.get(0)));
		int startIndex = Collections.binarySearch(
				this.managedUtilizationChanges, newStartEvent);
		int index=startIndex;
		while (index<this.managedUtilizationChanges.size()){
			this.managedUtilizationChanges.get(index).removeLentResources(resources);
			index++;
		}
	}
	
	
	public void setScheduler(Scheduler another) {
		this.scheduler = another;
	}
}
