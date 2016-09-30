/*
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2013 by the
 *   CICESE Research Center and  
 *   CETYS University, Mexico
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

package mx.cicese.dcc.teikoku.information.broker;


import java.util.UUID;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.util.time.Instant;

/**
 * The Entity class is the root entity from which all the GLUE classes inherit (an exception is
 * made for the Extension class). The specialized classes will inherit both the associations and
 * the attributes of Extension class. The attributes CreationTime and Validity are metadata related
 * to the generation and life of the information. The Name attribute allows a human-readable name
 * to be provided for any object, usable for e.g. monitoring or diagnostic displays. The Name 
 * SHOULD NOT have any semantic interpretation.</p>
 * <p>
 * 
 * This implementation is based on GLUE Specification V. 2.0. The GLUE specification is an information 
 * model for Grid entities described using the natural language and UML Class Diagrams.
 * <p>
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 *  
 * @category Information system
 * 
 */

public abstract class Entity {
	
	/**
	 * Timestamp describing when the entity instance was
	 * generated.
	 * 
	 */
	private Instant creationTime;

	
	/**
	 * A global unique ID.
	 * 
	 */
	private UUID id;
	
	
	/**
	 * The entity type
	 */
	EntityType type;
	
	/**
	 * The duration after CreationTime that the information presented in the Entity SHOULD be
	 * considered relevant. After that period has elapsed, the information SHOULD NOT be 
	 * considered relevant
	 * 
	 */
	protected Instant validity;
	
	
	/**
	 * Class constructor
	 */
	public Entity() {
		this.creationTime = Clock.instance().now();
		this.id = UUID.randomUUID();
	}
	
	
	/**
	 * Class constructor
	 * 
	 * @param name, a human readable name for the entity
	 */
	public Entity(EntityType type) {
		this();
		this.type = type;
	}
	
	
	/**
	 * Gets the entity UUID
	 * 
	 * @return the entity UUID
	 */
	public UUID getUUID() {
		return this.id;
	}
	
	
	/**
	 * Gets the entity name
	 * 
	 * @return the entity name
	 */
	public String getName() {
		return this.type.toString();
	}
	
	
	/**
	 * Gets the entity creation time
	 * 
	 * @return the entity creation time
	 */
	public Instant getCreationTime() {
		return this.creationTime;
	}
	
	
	/**
	 * Gets the entity type
	 * 
	 * @return the entity type
	 */
	public EntityType getType() {
		return this.type;
	}
	
	
	/**
	 * Gets the entity validity
	 * 
	 * @returns the entity validity
	 */
	public Instant getValidity() {
		return this.validity;
	}
}
