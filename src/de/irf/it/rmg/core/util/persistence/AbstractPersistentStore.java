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
package de.irf.it.rmg.core.util.persistence;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
abstract public class AbstractPersistentStore implements PersistentStore {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(AbstractPersistentStore.class);

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private URL url;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean allowOverwrite;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 *
	 */
	public AbstractPersistentStore() {
		this.initialized = false;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.util.persistence.PersistentStore
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.persistence.PersistentStore#getUrl()
	 */
	final public URL getUrl() {
		return this.url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.persistence.PersistentStore#setUrl(java.net.URL)
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.persistence.PersistentStore#isAllowOverwrite()
	 */
	final public boolean isAllowOverwrite() {
		return this.allowOverwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.persistence.PersistentStore#setAllowOverwrite(boolean)
	 */
	final public void setAllowOverwrite(boolean allowOverwrite) {
		this.allowOverwrite = allowOverwrite;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Initializable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#isInitialized()
	 */
	final public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param initialized
	 */
	final protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#initialize(java.lang.Object[])
	 */
	public void initialize() throws InitializationException {
		if (this.url == null) {
			String msg = "initialization failed: no url set";
			log.error(msg);
			throw new InitializationException(msg);
		} // if
	}
}
