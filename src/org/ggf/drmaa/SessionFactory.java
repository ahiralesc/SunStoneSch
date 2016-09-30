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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;

/**
 * Provides a factory class to allow DRMAA applications to retrieve a vendor
 * specific implementation of the {@link DelegationSession} interface and enables the
 * provision of different {@link DelegationSession} implementations depending on the
 * current need.
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
abstract public class SessionFactory {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(SessionFactory.class);

	/**
	 * Holds the property key for factory class name determination.
	 * 
	 */
	final private static String SESSION_FACTORY_KEY = "org.ggf.drmaa.SessionFactory";

	/**
	 * Holds the default file system properties file name.
	 * 
	 */
	final private static String DEFAULT_PROPERTIES_FILE_PATH = SystemUtils
			.getJavaHome()
			+ File.separator + "lib" + File.separator + "drmaa.properties";

	/**
	 * Holds the default class path resource properties file name.
	 * 
	 */
	final private static String CLASS_PATH_RESOURCE_PATH = "META-INF/services/org.ggf.drmaa.SessionFactory";

	/**
	 * Returns a {@link SessionFactory} instance appropriate to the DRM in use.
	 * 
	 * In order to determine the correct implementation, this method looks for
	 * the <code>org.ggf.drmaa.SessionFactory</code> property at the following
	 * places, in order:
	 * <ul>
	 * <li>{@link System#getProperties()};</li>
	 * <li>The property file <code>${java.home}/lib/drmaa.properties</code>,
	 * if existant (using ${java.home} from the system properties); and</li>
	 * <li>The classpath resource
	 * <code>META-INF/services/org.ggf.drmaa.SessionFactory.</code>
	 * 
	 * If neither contains the required property, an {@link Error} is thrown.
	 * 
	 * @return A {@link SessionFactory} instance appropriate for the DRM system
	 *         in use.
	 */
	public static SessionFactory getFactory() {
		SessionFactory result = null;

		/*
		 * look in system properties
		 */
		if ((result = getFactoryInstanceFromSystemProperties()) == null) {
			/*
			 * look in file system default properties
			 */
			if ((result = getFactoryInstanceFromDefaultPropertiesFile()) == null) {
				/*
				 * look in class path default resources
				 */
				if ((result = getFactoryInstanceFromClassPathResource()) == null) {
					/*
					 * fail eternally
					 */
					String msg = "fatal: could not find appropriate factory instance, aborting";
					log.fatal(msg);
					throw new Error(msg);
				} // if
			} // if
		} // if

		return result;
	}

	/**
	 * Tries to load an appropriate factory instance from the system properties
	 * (the first case in {@link SessionFactory#getFactory()}.
	 * 
	 * @return A correctly initialized {@link SessionFactory} instance, if
	 *         possible; <code>null</code>, otherwise.
	 */
	private static SessionFactory getFactoryInstanceFromSystemProperties() {
		SessionFactory result = null;

		String factoryClassName = System.getProperty(SESSION_FACTORY_KEY);
		if (factoryClassName != null) {
			try {
				result = ClassLoaderHelper.loadInterface(factoryClassName,
						SessionFactory.class);
			} // try
			catch (InstantiationException e) {
				log.warn("creation failed: could not instantiate \""
						+ factoryClassName + "\"");
			} // catch
		} // if

		return result;
	}

	/**
	 * Tries to load an appropriate factory instance from the default file
	 * system properties (the second case in {@link SessionFactory#getFactory()}.
	 * 
	 * @return A correctly initialized {@link SessionFactory} instance, if
	 *         possible; <code>null</code>, otherwise.
	 */
	private static SessionFactory getFactoryInstanceFromDefaultPropertiesFile() {
		SessionFactory result = null;

		File defaultPropertyFile = new File(SystemUtils.getJavaHome()
				+ File.separator + DEFAULT_PROPERTIES_FILE_PATH);

		InputStream is = null;
		try {
			is = new FileInputStream(defaultPropertyFile);
		} // try
		catch (FileNotFoundException e) {
			log.info("not found: default configuration file \""
					+ defaultPropertyFile + "\"", e);
			return null;
		} // catch

		Properties defaultProperties = new Properties();
		try {
			defaultProperties.load(is);
		} // try
		catch (IOException e) {
			log.info("cannot read (I/O problem): default configuration file \""
					+ defaultPropertyFile + "\"", e);
			return null;
		} // catch

		String factoryClassName = defaultProperties
				.getProperty(SESSION_FACTORY_KEY);
		if (factoryClassName != null) {
			try {
				result = ClassLoaderHelper.loadInterface(factoryClassName,
						SessionFactory.class);
			} // try
			catch (InstantiationException e) {
				log.warn("creation failed: could not instantiate \""
						+ factoryClassName + "\"");
			} // catch
		} // if

		return result;
	}

	/**
	 * Tries to load an appropriate factory instance from class path default
	 * resources (the third case in {@link SessionFactory#getFactory()}.
	 * 
	 * @return A correctly initialized {@link SessionFactory} instance, if
	 *         possible; <code>null</code>, otherwise.
	 */
	private static SessionFactory getFactoryInstanceFromClassPathResource() {
		SessionFactory result = null;

		InputStream is = ClassLoader
				.getSystemResourceAsStream(CLASS_PATH_RESOURCE_PATH);

		Properties classPathResourceFile = new Properties();
		try {
			classPathResourceFile.load(is);
		} // try
		catch (IOException e) {
			log.info("cannot read (I/O problem): class path resource \""
					+ CLASS_PATH_RESOURCE_PATH + "\"", e);
		} // catch

		String factoryClassName = classPathResourceFile
				.getProperty(SESSION_FACTORY_KEY);
		if (factoryClassName != null) {
			try {
				result = ClassLoaderHelper.loadInterface(factoryClassName,
						SessionFactory.class);
			} // try
			catch (InstantiationException e) {
				log.warn("creation failed: could not instantiate \""
						+ factoryClassName + "\"");
			} // catch
		} // if

		return result;
	}

	/**
	 * Returns a {@link DelegationSession} instance appropriate for the DRM system in use.
	 * 
	 * @return A {@link DelegationSession} instance appropriate for the DRM system in
	 *         use.S
	 */
	abstract public Session getSession();

}
