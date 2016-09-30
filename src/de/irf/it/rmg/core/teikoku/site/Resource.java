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
package de.irf.it.rmg.core.teikoku.site;

import java.util.UUID;

import mx.cicese.dcc.teikoku.energy.Component;
import mx.cicese.dcc.teikoku.energy.State;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;

/**
 * Represents a resource that can be occupied by a job, identified by its
 * (non-guaranteed) unique name. Note that the type of the resource is
 * arbitrary; equality is considered on an instance basis (see
 * {@link java.lang.Object}). For concrete resources, this type should be
 * subclassed.
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class Resource
		implements Comparable<Resource> {

	/**
	 * The name of the resource. This serves as a default name field without
	 * guarantees for uniqueness. If identity must be guaranteed, subclasses
	 * should add an appropriate field using a primary-key aware type (such as
	 * {@link java.util.UUID}).
	 */
	private String name;

	/**
	 * Keeps the next ordinal for object creation to ensure an increment for
	 * every created object.
	 */
	private static int nextOrdinal = 0;

	/**
	 * <p>
	 * Holds an integer representing the creation order of this resource. Here,
	 * smaller numbers indicate an earlier object creation while larger numbers
	 * indicate a later creation.
	 * </p>
	 * <p>
	 * Note that the ordinal does not guarantee system-wide uniqueness of
	 * resources.
	 * </p>
	 * 
	 * @see #compareTo(Resource)
	 */
	private int ordinal;

	
	/**
	 * The site that holds the resource
	 */
	private Site site;

	
	
	/**
	 * The processor that contains this core
	 */
	private Component component;
	
	/*
	 * Holds the uuid of the object
	 */
	private UUID uuid;
	
	
	/**
	 * The time instance the core is freed. It corresponds to the time instance
	 * when the core transitioned from active to idle state.
	 */
	private Instant instance;
	
	
	/**
	 * The state of the resource
	 */
	private State state;
	
	// -------------------------------------------------------------------------
	// Class initialization
	// -------------------------------------------------------------------------
	{
		/*
		 * Increase the ordinal counter by one and use the current value as this
		 * resource instance's ordinal.
		 */
		nextOrdinal++;
		this.ordinal = nextOrdinal;
	}

	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param name
	 *            The resource's name, used as a default identifier.
	 */
	public Resource(String name) {
		this.name = name;
		this.component = null;
		this.state = State.off;
		this.instance = null;
		
	}
	
	
	/**
	 * Copy constructor
	 * 
	 * @param another resource
	 */
	public Resource(Resource another) {
		this.name = another.name;
		this.ordinal = another.ordinal;
		this.site = another.site;
		this.uuid = another.uuid;
	}
	
	
	/**
	 * Creates a clone deep copy of resource
	 */
	public Resource clone() {
		return new Resource(this); 
	}
	
	
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------
	
	/**
	 * Determines the relative ordering of two events. Herefor, an internal
	 * counter representing the creation order of two <code>Resource</code>
	 * objects is used. As such, a <code>r1.compareTo(r2);</code> call would
	 * behave as follows:
	 * 
	 * <ul>
	 * <li><code>r1 &lt; r2</code>, iif <code>r1</code> has been created
	 * before<code>r2</code></li>
	 * <li><code>r1 &gt; r2</code>, iif <code>r1</code> has been created
	 * after<code>r2</code></li>
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Resource other) {
		return new CompareToBuilder().append(this.ordinal, other.ordinal)
				.toComparison();
	}

	/**
	 * Sets a time instance at which the core transitioned to idle state.
	 * 
	 */
	public void setInstance() {
		this.instance = Clock.instance().now();
	}
	
	
	/**
	 * Gets the instance 
	 * @return
	 */
	public Instant getInstance(){
		return this.instance;
	}
	

	/**
	 * Returns the processor that contains
	 * @return
	 */
	public Component getComponent() {
		return this.component;
	}
	
	
	
	/**
	 * Returns the name of this resource.
	 * 
	 * @return The name of this resource.
	 */
	final public String getName() {
		return this.name;
	}


	/**
	 * <p>
	 * Returns the ordinal of this resource, representing a relative ordering
	 * regarding the site this resource belongs to. The imposed relation
	 * describes the creation order of resources: those with a smaller ordinal
	 * have been created earlier than those with a larger ordinal.
	 * </p>
	 * <p>
	 * Note that, for system-wide handling, this ordinal is not guaranteed to be
	 * unique.
	 * </p>
	 * 
	 * @return The ordinal of this resource.
	 * 
	 * @see #compareTo(Resource)
	 */
	final public int getOrdinal() {
		return this.ordinal;
	}


	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	
	/**
	 * Gets the resource state 
	 * 
	 * @param state
	 */
	public State getState( ) {
		return this.state;
	}
	
	
	/**
	 * Sets the state of the core
	 * 
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}

	
	
	public UUID getUUID(){
		return this.uuid;
	}


	/**
	 * Creates a new group of anonymous resources with relative ordering between
	 * them.
	 * 
	 * The naming is done with ascending integers (starting at 1), which are
	 * stored as strings with padded zeroes. For example, with an
	 * <code>amount</code> value of <code>100</code>, naming would start
	 * with <code>001, 002, ...</code>
	 * 
	 * The resources are created in the same order as the ascending numbers.
	 * 
	 * @param amount
	 *            The number of requested resources.
	 * @return A new group of resources, relative ordered.
	 * 
	 * @see #getOrdinal()
	 */
	public static Resource[] newGroupOfAnonymousResources(int amount) {
		resetOrdinal();
		Resource[] result = new Resource[amount];
	
		String format = "%1$0" + ( int ) Math.ceil(Math.log10(amount) + 1)
				+ "d";
		for (int i = 0; i < amount; i++) {
			String name = String.format(format, i + 1);
			result[i] = new Resource(name);
		} // for
	
		return result;
	}


	/**
	 * Creates a new group of resources with relative ordering between them.
	 * 
	 * The resources are created in the same order as the given list of names;
	 * thus, if name ordering is also important, the list of names should be in
	 * the correct order when calling.
	 * 
	 * @param names
	 *            The requested names for the resources, in order.
	 * @return A new group of resources, relative ordered.
	 * 
	 * @see #getOrdinal()
	 */
	public static Resource[] newGroupOfResources(String[] names) {
		resetOrdinal();
		Resource[] result = new Resource[names.length];
	
		for (int i = 0; i < result.length; i++) {
			result[i] = new Resource(names[i]);
		} // for
	
		return result;
	}


	/**
	 * <p>
	 * Resets the "created after" relationship between resources. More formally,
	 * the resource ordinal counter to zero (0). This method should be called
	 * from {@link ComputeSite} during the initialization of the site's resources to
	 * ensure a correct resource count for multi-site setups.
	 * </p>
	 * <p>
	 * Note that, for each site, this method should be called <b>once</b> at
	 * creation time and afterwards never again. Otherwise, the relative order
	 * of resources will get mixed up, possibly impairing functionality of other
	 * system parts (such as {@link Schedule}, depending on it's
	 * implementation).
	 * </p>
	 * 
	 * @see #compareTo(Resource)
	 */
	private static void resetOrdinal() {
		nextOrdinal = 0;
	}

	
	/**
	 * Sets the processor that contains this core
	 * @param cmp
	 */
	public void setComponent(Component cmp) {
		this.component = cmp;
	}

	
	
	public void setID(UUID id) {
		this.uuid=id;
	}


	/**
	 * Sets the name of this resource.
	 * 
	 * @param name
	 *            The new value of the name of this resource, overwriting the
	 *            current one.
	 */
	final public void setName(String name) {
		this.name = name;
	}


	/**
	 * Set the ordinal of this object
	 * 
	 * @param i
	 */
	public void setOrdinal(int i) {
		this.ordinal=i;
	}
	
	

	

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/**
	 * @param site the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.Occupation#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", this.name).appendSuper(
				super.toString()).toString();
	}
	
}
