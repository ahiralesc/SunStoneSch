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
package de.irf.it.rmg.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
final public class ConfigurationHelper {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(ConfigurationHelper.class);

	/**
	 * TODO: not yet commented
	 * 
	 * @param c
	 * @param prefix
	 * @param key
	 * @return
	 */
	public static String retrieveRelevantKey(Configuration c, String prefix,
			String key) {
		String result = null;

		String prefixSpecificKey = prefix + "." + key;
		if (c.containsKey(prefixSpecificKey)) {
			log.debug("found prefix-specific key: " + prefixSpecificKey);
			result = prefixSpecificKey;
		} // if
		else {
			if (c.containsKey(key)) {
				log.debug("found general key: " + key);
				result = key;
			} // if
		} // else

		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param c
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static String[] retrieveRelevantSubkeys(Configuration c,
			String prefix, String key) {
		String[] result = null;

		String prefixSpecificKey = prefix + "." + key;
		Iterator iter = c.getKeys(prefixSpecificKey);
		Collection<String> subkeys = new ArrayList<String>();

		if (iter.hasNext()) {
			log.debug("found prefix-specific subkeys for key: "
					+ prefixSpecificKey);
			while (iter.hasNext()) {
				String element = (String) iter.next();
				subkeys.add(element);
			} // while
			result = subkeys.toArray(new String[subkeys.size()]);
		} // if
		else {
			iter = c.getKeys(key);
			if (iter.hasNext()) {
				log.debug("found general subkeys for key: " + key);
				while (iter.hasNext()) {
					String element = (String) iter.next();
					subkeys.add(element);
				} // while
				result = subkeys.toArray(new String[subkeys.size()]);
			} // if
		} // else

		return result;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param c
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static String retrieveRelevantKey(Configuration c, Class clazz,
			String key) {
		return retrieveRelevantKey(c, clazz.getName(), key);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param c
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static String[] retrieveRelevantSubkeys(Configuration c,
			Class clazz, String key) {
		return retrieveRelevantSubkeys(c, clazz.getName(), key);
	}
}
