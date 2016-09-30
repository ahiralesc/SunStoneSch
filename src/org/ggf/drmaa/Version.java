// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the
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
package org.ggf.drmaa;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Holds the major and minor version numbers of this DRMAA implementation as
 * returned by the {@link DelegationSession#getVersion()} method.
 * 
 * Note that this class is only a container object for the version number of a
 * certain implementation and not specific to this implementation's actual
 * version. Itself, it does not contain any references to a certain version
 * number of a certain implementation.
 * 
 * Instead, {@link DelegationSession#getVersion()} is expected to create and/or keep a
 * properly initialized instance of this type, holding the correct major and
 * minor version.
 * 
 * @see DelegationSession
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class Version
		implements Serializable, Cloneable, Comparable {

	/**
	 * The serial version unique identifier of this class.
	 * 
	 * @see Serializable
	 */
	private static final long serialVersionUID = -3794920563297126277L;

	/**
	 * Keeps the major version number of this DRMAA implementation.
	 * 
	 */
	private int major;

	/**
	 * Keeps the minor version number of this DRMAA implementation.
	 * 
	 */
	private int minor;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @param major
	 *            the major version number
	 * @param minor
	 *            the ninor version number
	 */
	public Version(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	/**
	 * Returns the major version number of this DRMAA implementation.
	 * 
	 * @return the major version number
	 */
	public int getMajor() {
		return this.major;
	}

	/**
	 * Returns the minor version number of this DRMAA implementation.
	 * 
	 * @return the minor verion number
	 */
	public int getMinor() {
		return this.minor;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Comparable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		return new CompareToBuilder().append(this.major,
				(( Version ) other).major).append(this.minor,
				(( Version ) other).minor).toComparison();
	}

}
