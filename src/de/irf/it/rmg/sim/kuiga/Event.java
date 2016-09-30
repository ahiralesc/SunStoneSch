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


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.irf.it.rmg.core.util.time.Instant;


/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
 *         Papaspyrou</a>
 * @since
 * @version $Revision$ (as of $Date$ by $Author$)
 * 
 */
abstract public class Event
		implements Comparable<Event> {

	/**
	 * Holds a static counter for the "id" field to ensure a new id value for
	 * every object.
	 * 
	 */
	private static int nextId = 0;

	/**
	 * Keeps the id of this event.
	 * 
	 */
	final private int id;

	/**
	 * Keeps the timestamp at which this event occurs.
	 * 
	 */
	final private Instant timestamp;

	/**
	 * Keeps the tags associated with this event.
	 * 
	 */
	private Map<String, Object> tags;

	{ // class initialization
		nextId++;
		this.id = nextId;
	} // class initialization

	/**
	 * TODO: not yet commented
	 * 
	 */
	public Event() {
		this.timestamp = Clock.instance().now();
		this.tags = new HashMap<String, Object>();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param tags
	 */
	public Event(final String tagKey, final Object tagValue) {
		this();
		this.tags.put(tagKey, tagValue);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param tagKeys
	 */
	public Event(final String... tagKeys) {
		this();
		for (final String tagKey : tagKeys) {
			this.tags.put(tagKey, null);
		} // for
	}

	/**
	 * TODO: not yet commented
	 * 
	 */
	public Event(final Instant timestamp) {
		this.timestamp = timestamp;
		this.tags = new HashMap<String, Object>();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @param tags
	 */
	public Event(final Instant timestamp, final String tagKey,
			final Object tagValue) {
		this(timestamp);
		this.tags.put(tagKey, tagValue);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param timestamp
	 * @param tagKeys
	 */
	public Event(final Instant timestamp, final String... tagKeys) {
		this(timestamp);
		for (final String tagKey : tagKeys) {
			this.tags.put(tagKey, null);
		} // for
	}

	/**
	 * Getter method for the "timestamp" field of this type.
	 * 
	 * @return Returns the current contents of "this.timestamp".
	 */
	final public Instant getTimestamp() {
		return this.timestamp;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	final public Map<String, Object> getTags() {
		return this.tags;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	abstract protected int getOrdinal();

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Event other) {
		int result = 0;
		final int byTimestamp = this.timestamp.compareTo(other.timestamp);
		if (byTimestamp != 0) {
			result = byTimestamp;
		} // if
		else {
			final int byOrdering;
			if (this.getOrdinal() < other.getOrdinal()) {
				byOrdering = -1;
			} // if
			else if (this.getOrdinal() > other.getOrdinal()) {
				byOrdering = 1;
			} // else if
			else {
				byOrdering = 0;
			} // else
			
			
			if (byOrdering != 0) {
				result = byOrdering;
			} // if
			else {
				final int byId;
				if (this.id < other.id) {
					byId = -1;
				} // if
				else if (this.id > other.id) {
					byId = 1;
				} // else if
				else {
					byId = 0;
				} // else
				result = byId;
			} // else
		} // else
		return result;
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
	public boolean equals(final Object candidate) {
		if (candidate == null) {
			return false;
		} // if
		if (this == candidate) {
			return true;
		} // if
		if (candidate instanceof Event) {
			final Event other = ( Event )candidate;
			return new EqualsBuilder().append(this.id, other.id).isEquals();
		} // if
		else {
			return false;
		} // else
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).append(
				"timestamp", this.timestamp).toString();
	}
}
