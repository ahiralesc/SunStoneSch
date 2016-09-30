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
package de.irf.it.rmg.core.teikoku;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Level;

import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.runtime.RuntimeManager;
import de.irf.it.rmg.core.util.FileHelper;
import de.irf.it.rmg.core.util.Initializable;
import de.irf.it.rmg.core.util.Resettable;
import de.irf.it.rmg.core.util.reflection.ClassLoaderHelper;

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
final public class Bootstrap
		implements Initializable, Resettable, Daemon {

	/**
	 * Determine the name of the current application
	 * 
	 */
	final public static String APPLICATION_NAME = "Teikoku Scheduling Framework";

	/**
	 * Determine the version of the current application
	 * 
	 */
	final public static String APPLICATION_VERSION = "v0.1";
	
	
	public static String PROPERTY_VERSION;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private StopWatch uptimeCounter;
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	private static Bootstrap instance = null;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private RuntimeManager runtimeManager;

	public static String TESTNUMBER;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public Bootstrap() {
		this.uptimeCounter = new StopWatch();
		instance=this;
	}
	
	/**
	 * @return
	 */
	public static Bootstrap getInstance(){
		return instance;
	}

	public static void main(String[] args) {
		Bootstrap b = new Bootstrap();

		String syntax = "java -jar teikoku.jar <options> runtimeConfiguration";
		String header = "\n" + APPLICATION_NAME + " (" + APPLICATION_VERSION
				+ ")\n";
		String footer = "";

		/*
		 * parse command line
		 */
		try {
			b.parseCommandLine(args);
		}
		catch (ParseException e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(syntax, header, CommandLineOptions.options, footer);

			System.err.println("\nThe '" + e.getMessage()
					+ "' switch is required and must be set. Exiting...");
			System.exit(1);
		}

		/*
		 * run initialization
		 */
		try {
			b.initialize();
		} // try
		catch (InitializationException e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(syntax, header, CommandLineOptions.options, footer);

			System.err.println(e.getMessage());
			System.exit(1);
		} // catch

		/*
		 * start manager
		 */
		try {
			b.start();
		} // try
		catch (Exception e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(syntax, header, CommandLineOptions.options, footer);

			System.err.println("<" + e.getClass() + "> " + e.getMessage() + ": " + e.getCause());
			e.printStackTrace();
			System.exit(1);
		} // catch
		
		try {
			b.stop();
		} // try
		catch (Exception e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(syntax, header, CommandLineOptions.options, footer);

			System.err.println(e.getMessage());
			System.exit(1);
		} // catch
		
		b.destroy();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param args
	 */
	private void parseCommandLine(String[] args)
			throws ParseException {
		/*
		 * create options
		 */
		Options options = new Options();
		for (CommandLineOptions clo : CommandLineOptions.values()) {
			options.addOption(clo.createOption());
		} // for
		CommandLineOptions.options = options;

		/*
		 * process command line
		 */
		CommandLineParser clp = new GnuParser();
		CommandLineOptions.commandLine = clp.parse(options, args);
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public RuntimeManager configureRuntimeSystem(File workingDirectory,
			Level loggingLevel, File runtimeConfigurationFile,
			String runtimeManagerClassName)
			throws InitializationException {
		this.initialized = false;

		/*
		 * ensure that "workingDirectory" is available
		 */
		if (FileHelper.ensureWritableDirectory(workingDirectory)) {
			String canonicalPath = null;
			try {
				/*
				 * initialize runtime system logger before anything else to ensure
				 * logging capabilities for subsequent steps
				 */	
				canonicalPath = workingDirectory.getCanonicalPath();
				//String log4jFile = workingDirectory.getCanonicalPath() + "\\log4j.properties";
				//String log4jFile = "src\\main\\resources\\log4j.properties";
				//PropertyConfigurator.configure(log4jFile);		
			} // try
			catch (IOException e) {
				throw new InitializationException(e);
			} // catch
			Configuration c = RuntimeEnvironment.getInstance()
					.getConfiguration().subset(
							RuntimeEnvironment.CONFIGURATION_SECTION);
			c.addProperty(Constants.CONFIGURATION_RUNTIME_WORKINGDIRECTORY,
					canonicalPath);
		} // if
		else {
			String msg = "\"workingDirectory\" not available: creation failed, path is no directory or not writable";
			throw new InitializationException(msg);
		} // else

		/*
		 * load property system to ensure availability of sane defaults for the
		 * rest of the system
		 */
		try {
			PropertiesConfiguration pc = new PropertiesConfiguration(
					runtimeConfigurationFile);
			/*
			 * Parse the Property file name and delete all characters except numbers.
			 * This number that is contained in the property file is used as PROPERTY_VERSION.
			 * The identification is useful for multiple configurations executed in a batch.
			 */
			String[] helpStrings=runtimeConfigurationFile.toString().split("_");
			
			this.PROPERTY_VERSION = helpStrings[helpStrings.length-1].replaceAll(".properties", "");
			this.TESTNUMBER = helpStrings[helpStrings.length-2];
			RuntimeEnvironment.getInstance().setConfiguration(pc);
		} // try
		catch (ConfigurationException e) {
			throw new InitializationException(e);
		} // catch

		/*
		 * initialize runtime environment before other runtime components to
		 * enforce correct initialization of all system components
		 */
		RuntimeEnvironment rt = RuntimeEnvironment.getInstance();
		if (!rt.isInitialized()) {
			rt.initialize();
		} // if

		/*
		 * create and initialize runtime manager
		 */
		try {
			this.runtimeManager = ClassLoaderHelper.loadInterface(
					runtimeManagerClassName, RuntimeManager.class);
		} // try
		catch (InstantiationException e) {
			throw new InitializationException(e);
		} // catch
		this.runtimeManager.initialize();

		/*
		 * complete system initialization and return runtime manager
		 */
		this.initialized = true;
		return this.runtimeManager;
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	public long getUptime() {
		return this.uptimeCounter.getTime();
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Initializable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#isInitialized()
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Initializable#initialize()
	 */
	public void initialize()
			throws InitializationException {
		this.initialized = false;

		File workingDirectory = new File(CommandLineOptions.WORKING_DIRECTORY
				.getValue());
		Level loggingLevel = Level.toLevel(CommandLineOptions.LOGGING_LEVEL
				.getValue());
		File runtimeConfigurationFile = new File(workingDirectory,CommandLineOptions.commandLine
				.getArgs()[0]);
		String runtimeManagerClassName = CommandLineOptions.MANAGER_CLASS
				.getValue();
		this.configureRuntimeSystem(workingDirectory, loggingLevel,
				runtimeConfigurationFile, runtimeManagerClassName);
		this.initialized = true;
	}
	
	

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Resettable
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Resettable#reset()
	 */
	public void reset() {
		// TODO: not yet implemented.
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for org.apache.commons.daemon.Daemon
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.daemon.Daemon#init(org.apache.commons.daemon.DaemonContext)
	 */
	public void init(DaemonContext context)
			throws Exception {
		try {
			this.parseCommandLine(context.getArguments());
		} // try
		catch (ParseException e) {
			String msg = "";
			throw new InitializationException(msg, e);
		} // catch
		this.initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.daemon.Daemon#start()
	 */
	public void start()
			throws Exception {
		System.out.println("commencing runtime system on " + new Date()
				+ ", using \"" + this.runtimeManager.getClass().getName()
				+ "\" as runtime manager");
		
		this.uptimeCounter.start();
		this.runtimeManager.commence();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.daemon.Daemon#stop()
	 */
	public void stop()
			throws Exception {
		this.runtimeManager.terminate();
		this.uptimeCounter.stop();
		
		System.out.println("terminating runtime system on "
				+ new Date()
				+ ", with a total uptime of "
				+ DurationFormatUtils.formatDurationWords(this.uptimeCounter
						.getTime(), true, true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.daemon.Daemon#destroy()
	 */
	public void destroy() {
		RuntimeEnvironment.getInstance().terminate();
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize()
			throws Throwable {
		super.finalize();
		this.runtimeManager.terminate();
		RuntimeEnvironment.getInstance().terminate();
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>,
	 *         <a href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>,
	 *         and <a href="mailto:alexander.papaspyrou@udo.edu">Alexander
	 *         Papaspyrou</a> (last edited by $Author$)
	 * @version $Version$, $Date$
	 * 
	 */
	private enum CommandLineOptions {

		/**
		 * TODO: not yet commented
		 * 
		 */
		WORKING_DIRECTORY("d") {

			/**
			 * TODO: not yet commented
			 * 
			 * @return
			 */
			@Override
			public Option createOption() {
				Option o = new Option(
						super.key,
						"working-directory",
						true,
						"Use <directory> as the working directory for the application. This is the base for all input and output of the current run.");
				o.setArgName("directory");
				return o;
			}

			/**
			 * TODO: not yet commented
			 * 
			 * @return
			 */
			@Override
			public String getValue() {
				String result = super.getValue();
				if (result == null) {
					result = SystemUtils.getUserDir().getAbsolutePath();
				} // if
				return result;
			}
		},

		/**
		 * TODO: not yet commented
		 * 
		 */
		LOGGING_LEVEL("l") {

			/**
			 * TODO: not yet commented
			 * 
			 * @return
			 */
			@Override
			public Option createOption() {
				Option o = new Option(
						super.key,
						"logging-level",
						true,
						"Print only logging statements with a level higher that <loglevel>. See http://logging.apache.org/log4j/ for a list of allowed levels.");
				o.setArgName("loglevel");
				return o;
			}

			/**
			 * TODO: not yet commented
			 * 
			 * @return
			 */
			@Override
			public String getValue() {
				String result = super.getValue();
				if (result == null) {
					result = Level.ERROR.toString();
				} // if
				return result;
			}
		},

		/**
		 * TODO: not yet commented
		 * 
		 */
		MANAGER_CLASS("m") {

			/**
			 * TODO: not yet commented
			 * 
			 * @return
			 */
			@Override
			public Option createOption() {
				Option o = new Option(
						super.key,
						"manager-class",
						true,
						"Use the type specified by <fqcn> (the fully qualified class name) as the manager for the runtime execution. See the user guide for details.");
				o.setRequired(true);
				o.setArgName("fqcn");
				return o;
			}
		};

		/**
		 * TODO: not yet commented
		 * 
		 */
		public static Options options;;

		/**
		 * TODO: not yet commented
		 * 
		 */
		public static CommandLine commandLine;

		/**
		 * TODO: not yet commented
		 * 
		 */
		private String key;

		/**
		 * TODO: not yet commented
		 * 
		 * @param key
		 */
		private CommandLineOptions(String key) {
			this.key = key;
		}

		/**
		 * TODO: not yet commented
		 * 
		 * @return
		 */
		abstract public Option createOption();

		/**
		 * TODO: not yet commented
		 * 
		 * @return
		 */
		public String getValue() {
			String result = null;

			if (commandLine == null) {
				String msg = "";
				throw new UnsupportedOperationException(msg);
			} // if
			else {
				if (commandLine.hasOption(this.key)) {
					result = commandLine.getOptionValue(this.key);
				} // if
			} // else
			return result;
		}
	}

	public void terminateUngracefully(Exception e) {
		this.runtimeManager.terminateUngracefully(e);
		
	}
}
