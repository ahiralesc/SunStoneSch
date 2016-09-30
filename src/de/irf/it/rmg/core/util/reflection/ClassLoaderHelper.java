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
package de.irf.it.rmg.core.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last modified by: $Author$)
 * @version $Version$, $Date$
 * 
 */
final public class ClassLoaderHelper {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(ClassLoaderHelper.class);

	/**
	 * Dynamically loads an instance of a given class, returning it as the
	 * requested interface type. This method requires that the requested class
	 * is non-abstract and provides a default constructor with public
	 * visibility.
	 * 
	 * @param className
	 *            The class to be loaded dynamically, represented by its fully
	 *            qualified classname (e.g. <code>java.lang.String</code>).
	 * @param interfaceType
	 *            The interface the loaded class should be cast to before
	 *            returning it as an <code>Object</code>. This allows
	 *            typesafe casts in the calling method to the requested class.
	 * @return An instance of the requested class, with guaranteed type safety
	 *         for the requested type from the "interfaceType" parameter.
	 * @throws InstantiationException
	 *             iif the instantiation of the requested class failed due to
	 *             non-loadable or abstract classes, missing or non-accessible
	 *             constructors, unavailable interface implementations or
	 *             because of other issues.
	 */
	public static <T> T loadInterface(String className, Class<T> interfaceType)
			throws InstantiationException {
		/* try to load the requested class */
		Class classType = null;
		try {
			classType = Class.forName(className);
		} // try
		catch (ClassNotFoundException e) {
			String msg = "not found: requested type (" + className
					+ ") does not exist on the classpath.";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		return loadInterface(classType, interfaceType);
	}

	/**
	 * Dynamically loads an instance of a given class (using the provided
	 * constructor signature), returning it as the requested interface type. It
	 * is expected that the requested class is non-abstract, and that the
	 * selected constructor has public visibility.
	 * 
	 * @param className
	 *            The class to be loaded dynamically, represented by its fully
	 *            qualified classname from {@link Class#getName()}.
	 * @param interfaceType
	 *            The interface the loaded class should be cast to before
	 *            returning it as an <code>Object</code>. This allows
	 *            typesafe casts in the calling method to the requested class.
	 * @param constructorName
	 *            The name of the requested constructor, using the notation
	 *            given by the {@link Constructor#toString()} implementation.
	 * @param constructorParameters
	 *            The arguments for instance construction, matching the method
	 *            signature from the "constructorName" parameter of this method.
	 *            Note that, depending on the JVM implementation, it is
	 *            mandatory to wrap primitive types into their object
	 *            representations (see {@link Constructor#newInstance(Object[])}).
	 * @return An instance of the requested class, with guaranteed type safety
	 *         for the requested type from the "interfaceType" parameter.
	 * @throws InstantiationException
	 *             iif the instantiation of the requested class failed due to
	 *             non-loadable or abstract classes, missing or non-accessible
	 *             constructors, invalid instantiation argument lists,
	 *             unavailable interface implementations or because of other
	 *             issues.
	 */
	public static <T> T loadInterface(String className, Class<T> interfaceType,
			String constructorName, Object... constructorParameters)
			throws InstantiationException {
		/* try to load the requested class */
		Class classType = null;
		try {
			classType = Class.forName(className);
		} // try
		catch (ClassNotFoundException e) {
			String msg = "not found: requested type (" + className
					+ ") does not exist on the classpath.";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		return loadInterface(classType, interfaceType, constructorName,
				constructorParameters);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 * @throws InstantiationException
	 */
	public static <T> T loadInterface(Class clazz, Class<T> interfaceType)
			throws InstantiationException {
		Object o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e) {
			String msg = "instantiation failed: requested type must be a non-abstract class with a default (public visibility) constructor.";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		catch (IllegalAccessException e) {
			String msg = "access denied: requested type must be a non-abstract class with a default (public visibility) constructor.";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		return castToRequestedInterface(interfaceType, o);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param classType
	 * @param name
	 * @return
	 * @throws InstantiationException
	 */
	public static <T> T loadInterface(Class classType, Class<T> interfaceType,
			String constructorName, Object... constructorParameters)
			throws InstantiationException {
		/* try to find the requested constructor for instantiation */
		Constructor c = findRequestedConstructor(classType, constructorName);

		/*
		 * try to create a new instance of this class, using the found
		 * constructor
		 */
		Object o = null;
		try {
			o = c.newInstance(constructorParameters);
		} // try
		catch (IllegalArgumentException e) {
			String msg = "creation failed: provided argument list does not match constructor signature ("
					+ constructorName + ").";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		catch (InstantiationException e) {
			String msg = "instantiation failed: requested type must be a non-abstract class with a default (public visibility) constructor.";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		catch (IllegalAccessException e) {
			String msg = "access denied: requested type must be a non-abstract class with a default (public visibility) constructor.";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		catch (InvocationTargetException e) {
			String msg = "exception during construction occured: "
					+ e.getMessage();
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch

		return castToRequestedInterface(interfaceType, o);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param classType
	 * @param constructorName
	 * @return
	 * @throws InstantiationException
	 */
	private static Constructor findRequestedConstructor(Class classType,
			String constructorName) throws InstantiationException {
		Constructor constructor = null;
		for (Constructor candidate : classType.getConstructors()) {
			String candidateName = candidate.toString();
			log.debug("checking constructor candidate: " + candidateName);
			if (candidateName.equals(constructorName)) {
				log.debug("found requested constructor");
				constructor = candidate;
				break;
			} // if
		} // for
		if (constructor == null) {
			String msg = "not found: requested constructor (" + constructorName
					+ ") does not exist on the classpath.";
			log.fatal(msg);
			throw new InstantiationException(msg);
		} // if
		return constructor;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param <T>
	 * @param interfaceType
	 * @param result
	 * @param o
	 * @return
	 * @throws InstantiationException
	 */
	private static <T> T castToRequestedInterface(Class<T> interfaceType,
			Object o) throws InstantiationException {
		T result = null;
		/* determine whether loaded class implements requested interface */
		try {
			result = (T) interfaceType.cast(o);
		} // try
		catch (ClassCastException e) {
			String msg = "type cast failed: requested type must implement requested interface ("
					+ interfaceType.getName() + ").";
			log.fatal(msg, e);
			throw new InstantiationException(msg);
		} // catch
		return result;
	}
}
