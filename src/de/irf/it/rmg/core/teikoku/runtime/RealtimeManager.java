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
package de.irf.it.rmg.core.teikoku.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.exceptions.InitializationException;
import de.irf.it.rmg.core.teikoku.exceptions.WorkloadException;
import de.irf.it.rmg.core.teikoku.grid.SinkToSourceAdapter;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.teikoku.workload.WorkloadFilter;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSink;
import de.irf.it.rmg.core.teikoku.workload.WorkloadSource;
import de.irf.it.rmg.core.teikoku.workload.swf.BrokenProcessorsFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.BufferedWorkloadSource;
import de.irf.it.rmg.core.teikoku.workload.swf.IllegalEstimateFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.IllegalRuntimeFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.RuntimeOveruseFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.SWFParser;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Kernel;

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
public class RealtimeManager
		implements RuntimeManager, Runnable {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(RealtimeManager.class);

	/**
	 * Holds the mapping from workload sources to workload sinks.
	 * 
	 */
	private Map<WorkloadSource, WorkloadSink> workloadMap;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean initialized = false;

	/**
	 * TODO: not yet commented
	 * 
	 */
	private boolean finished = true;

	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public RealtimeManager() {
		this.workloadMap = new HashMap<WorkloadSource, WorkloadSink>();
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
		/*
		 * setup Object#toString() output style
		 */
		ToStringBuilder.setDefaultStyle(new RealtimeToStringStyle());

		/*
		 * load workload source-to-sink mappings
		 */
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
				.subset(RuntimeManager.CONFIGURATION_SECTION);
		String[] workloadSourceNames = c.getStringArray("workloadSource.id");

		if (workloadSourceNames.length == 0) {
			String msg = "configuration error: no workload sources defined";
			log.fatal(msg);
			throw new InitializationException(msg);
		} // if

		/*
		 * iterate over specified sources and sinks and configure them properly
		 */
		SiteContainer sc = RuntimeEnvironment.getInstance().getSiteContainer();
		for (String workloadSourceName : workloadSourceNames) {
			/*
			 * find associated site and fetch from SiteContainer, if possible
			 */
			String associatedSiteName = c.getString(workloadSourceName + "."
					+ "associatedSite.ref");

			// TODO: find a better solution than using SiteContainer
			Site site = sc.getSite(associatedSiteName);
			// XXX: this doesn't work
			WorkloadSink workloadSink = new SinkToSourceAdapter(sc
					.getSite(associatedSiteName).getLocalSubmissionComponent());
			
			if (workloadSink == null) {
				String msg = "configuration error: \"associatedSiteName\" not set or not matching any defined site for workload source \""
						+ workloadSourceName + "\"";
				log.fatal(msg);
				throw new InitializationException(msg);
			} // if

			/*
			 * find source url for workload source
			 */
			String url = c.getString(workloadSourceName + "." + "url");
			if (url == null) {
				String msg = "configuration error: no url defined for workload source \""
						+ workloadSourceName + "\"";
				log.fatal(msg);
				throw new InitializationException(msg);
			} // if
			URL u = null;
			try {
				u = new URL(url);
			} // try
			catch (MalformedURLException e) {
				String msg = "configuration error: " + e.getMessage();
				log.fatal(msg);
				throw new InitializationException(msg);
			} // catch

			/*
			 * generate and initialize workload source with found url and
			 * filters
			 * 
			 * TODO: generalize this somehow
			 */
			WorkloadSource workloadSource = null;
			try {
				workloadSource = new SWFParser(u);
			} // try
			catch (IOException e) {
				throw new InitializationException(e);
			} // catch
			workloadSource = new BufferedWorkloadSource(workloadSource);
			WorkloadFilter[] wf = new WorkloadFilter[]
				{ new BrokenProcessorsFilter(site.getSiteInformation()),
						new IllegalRuntimeFilter(),
						new RuntimeOveruseFilter(false),
						new IllegalEstimateFilter() };
			workloadSource.initialize(wf);

			/*
			 * register workload source and site (as workload sink) in map
			 */
			this.workloadMap.put(workloadSource, workloadSink);
		} // for

		this.initialized = true;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for de.irf.it.rmg.core.util.Ephemeral
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#commence()
	 */
	synchronized public void commence() {
		if (this.finished = true) {

			Thread t = new Thread(this);
			t.start();

			try {
				this.wait();
			} // try
			catch (InterruptedException e) {
				// TODO: not yet handled.
				e.printStackTrace();
			} // catch
		} // if
		else {
			String msg = "already running: " + this.getClass().getSimpleName()
					+ " has commenced before";
			throw new RuntimeException(msg);
		} // else
	}

	/**
	 * TODO: not yet commented
	 * 
	 * @param e
	 */
	public void terminateUngracefully(Exception e) {
		String msg = "ungraceful termination ";
		log.fatal(msg, e);
		this.finished = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#terminate()
	 */
	public void terminate() {
		this.finished = true;
	}

	// -------------------------------------------------------------------------
	// Implementation/Overrides for java.lang.Runnable
	// -------------------------------------------------------------------------

	public void run() {
		this.finished = false;
		Instant nextTimestamp = Clock.instance().advent();

		log.info("starting real time simulation");
		/*
		 * while any of the workload sources still has jobs to provide *or* the
		 * kernel still has events to handle, do the following
		 */
		while (!this.finished) {
			/*
			 * check for all workloads whether there are jobs to be released
			 * _before_ the next known event in the kernel is to occur
			 */
			for (Iterator<WorkloadSource> iter = workloadMap.keySet().iterator(); iter
					.hasNext();) {
				WorkloadSource source = iter.next();
				WorkloadSink sink = this.workloadMap.get(source);
				Job j = null;
				do {
					try {
						j = source.fetchNextJob();
					} // try
					catch (WorkloadException e) {
						this.terminateUngracefully(e);
					} // catch
					if (j != null) {
						String msg = "source " + source + ": releasing " + j
								+ " to " + sink;
						log.debug(msg);
						sink.putNextJob(j);
					} // if
				} // do
				while (j != null);
			} // for

			Kernel.getInstance().wakeup(nextTimestamp);
			Clock.instance().advance();

			try {
				Thread.sleep(Clock.instance().progression().length());
			} // try
			catch (Exception e) {
				// TODO: handle exception
			} // catch
		} // while
		this.notifyAll();
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
	final private class RealtimeToStringStyle extends ToStringStyle {

		/**
		 * TODO: not yet commented
		 * 
		 */
		private static final long serialVersionUID = 6369128501675265691L;

		/**
		 * Creates a new instance of this class, using the given parameters.
		 * 
		 */
		public RealtimeToStringStyle() {
			super();
			this.setUseShortClassName(true);
			this.setUseIdentityHashCode(false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.lang.builder.ToStringStyle#appendDetail(java.lang.StringBuffer,
		 *      java.lang.String, java.lang.Object)
		 */
		@Override
		protected void appendDetail(StringBuffer buffer, String fieldName,
				Object value) {
			Object valueToAppend = value;
			if (value instanceof Date) {
				valueToAppend = (( Date ) value).getTime();
			} // if
			super.appendDetail(buffer, fieldName, valueToAppend);
		}
	}
}
