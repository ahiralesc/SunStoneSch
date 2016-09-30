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
package de.irf.it.rmg.core.teikoku.site.schedule;

import java.util.Iterator;
import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleBitVectorImpl;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;

/**
 * The UtilizationChange stores the information about the current occupation of
 * resources at the given timestamp. These Events are connected to each other.
 * The result is a list of ScheduleEvents.
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */
public class UtilizationChange
		implements Comparable<UtilizationChange> {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Schedule schedule;

	/**
	 * Holds the set of resources occupied at this change.
	 * 
	 */
	private ResourceBundle occupiedResources;

	/**
	 * Holds the set of resources free at this change.
	 * 
	 */
	private ResourceBundle freeResources;

	/**
	 * Holds the timestamp of this changes occurrence.
	 * 
	 */
	private Instant timestamp;

	/**
	 * Holds the number of occupations that start or end at this change.
	 * 
	 */
	private int involvedOccupations;

	/**
	 * TODO: not yet commented Creates a new instance of this class, using the
	 * given parameters.
	 * 
	 * @param schedule
	 */
	public UtilizationChange(Schedule schedule) {
		this(schedule, TimeFactory.newMoment(0));
	}

	/**
	 * TODO: not yet commented Creates a new instance of this class, using the
	 * given parameters.
	 * 
	 * @param schedule
	 * @param timestamp
	 */
	public UtilizationChange(Schedule schedule, Instant timestamp) {
		this.schedule = schedule;
		this.timestamp = timestamp;
		this.occupiedResources = new ResourceBundleBitVectorImpl(this.schedule
				.getScheduler().getSite().getSiteInformation());
		this.freeResources = new ResourceBundleBitVectorImpl(this.schedule
				.getScheduler().getSite().getSiteInformation(), this.schedule
				.getScheduler().getSite().getSiteInformation()
				.getProvidedResources().toArray());
		this.freeResources.add(schedule.getScheduler().getSite().getSiteInformation().getLentResources());
		this.involvedOccupations = 0;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public UtilizationChange( UtilizationChange another) {
		this.schedule = null;
		
		// Clone the occupied resources
		ComputeSiteInformation cs = ((ComputeSiteInformation) another.schedule.
									getScheduler().getSite().getSiteInformation()).clone();
		
		this.occupiedResources = new ResourceBundleBitVectorImpl(cs);
		for(Iterator<Resource> it=another.occupiedResources.iterator(); it.hasNext();)
			this.occupiedResources.add(it.next().clone());
			
		// Clone the free resources
		this.freeResources = new ResourceBundleBitVectorImpl(cs);
		for(Iterator<Resource> it=another.freeResources.iterator(); it.hasNext();)
			this.freeResources.add(it.next().clone());
		
		// for(this.freeResources	
		this.timestamp = another.timestamp.clone();
		
		this.involvedOccupations = another.involvedOccupations;
	}
	
	
	/**
	 * Creates a clone copy
	 */
	public UtilizationChange clone() {
		return new UtilizationChange(this);
	}
	

	/**
	 * This method returns the amount of occupied resources at the current
	 * timestamp.
	 * 
	 * @return The cardinality of the set of the occupied resources.
	 */
	public int getUtilization() {
		return this.occupiedResources.size();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public int getFreeness() {
		return this.freeResources.size();
	}

	/**
	 * Adds the given resources to the occupiedResources.
	 * 
	 * @param resources
	 * @throws UnsupportedOperationException
	 */
	public void addResources(ResourceBundle resources) {
		this.occupiedResources.add(resources);
		this.freeResources.remove(resources);
	}

	/**
	 * Removes the given resources from the occupiedResources.
	 * 
	 * @param resources
	 * @throws UnsupportedOperationException
	 */
	public void removeResources(ResourceBundle resources) {
		this.occupiedResources.remove(resources);
		this.freeResources.add(resources);
	}
	
	public void addNewResources(ResourceBundle rb) {
		freeResources.addAll(rb);
	}
	
	public void removeLentResources(ResourceBundle rb){
		freeResources.shiftRemove(rb);
		occupiedResources.shiftRemove(rb);
	}

	/**
	 * Getter method for the occupied resources during this event.
	 * 
	 * @return occupiedResources
	 */
	public ResourceBundle getOccupiedResources() {
		return this.occupiedResources;
	}

	/**
	 * Getter method or the free resources during this event.
	 * 
	 * @return freeResources
	 */
	public ResourceBundle getFreeResources() {
		return this.freeResources;
	}

	/**
	 * Getter method for the timestamp.
	 * 
	 * @return The time of this event.
	 */
	public Instant getTimestamp() {
		return timestamp;
	}

	/**
	 * Setter method for the current timeStamp.
	 * 
	 * @param timeStamp
	 * @throws UnsupportedOperationException
	 */
	public void setTimestamp(Instant timeStamp)
			throws UnsupportedOperationException {
		this.timestamp = timeStamp;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public int incrementInvolvedOccupations() {
		this.involvedOccupations++;
		return this.involvedOccupations;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public int decrementInvolvedOccupations() {
		this.involvedOccupations--;
		return this.involvedOccupations;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public int getInvolvedOccupations() {
		return involvedOccupations;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(UtilizationChange other) {
		return this.timestamp.compareTo(other.timestamp);
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} // if
		if (this == other) {
			return true;
		} // if
		if (!(other instanceof UtilizationChange)) {
			return false;
		} // if
		UtilizationChange candidate = ( UtilizationChange ) other;
		return new EqualsBuilder().append(this.timestamp, candidate.timestamp)
				.isEquals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.timestamp).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = new ToStringBuilder(this).append("timestamp",
				this.timestamp).append("utilization", this.getUtilization())
				.toString();
		return result;
	}
	
	/* ADDED */
	public void setSchedule(Schedule newCopy) {
			this.schedule = newCopy;
	}
	
	public void setFreeResources(ResourceBundle f) {
		this.freeResources = f;
	}
}
