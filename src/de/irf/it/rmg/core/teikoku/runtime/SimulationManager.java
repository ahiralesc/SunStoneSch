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
package de.irf.it.rmg.core.teikoku.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.JobType;
import mx.cicese.dcc.teikoku.workload.swf.CSWFParser;

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
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.teikoku.workload.swf.BrokenProcessorsFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.IllegalEstimateFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.IllegalRuntimeFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.IllegalStatusFilter;
import de.irf.it.rmg.core.teikoku.workload.swf.RuntimeOveruseFilter;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         href="mailto:ahirales@uabc.mx">Adan Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
public class SimulationManager
		implements RuntimeManager {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */
	final private static Log log = LogFactory.getLog(SimulationManager.class);

	/**
	 * Holds the mapping from workload sources to workload sinks.
	 * 
	 */
	private Map<WorkloadSource, WorkloadSink> workloadMap;

	/**
	 * Indicated a finished initialization of the component
	 * 
	 */
	private boolean initialized;

	/**
	 * Indicates that the simulation is finished
	 * 
	 */
	private boolean finished;
	
	/**
	 * Termination condition for failures 
	 */
	private int numContinuosEqualEvents = 0; 
	private Event lastEvent = null;
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public SimulationManager() {
		this.workloadMap = new HashMap<WorkloadSource, WorkloadSink>();
		this.initialized = false;
		this.finished = false;
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
		ToStringBuilder.setDefaultStyle(new SimulationToStringStyle());

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

			/*
			 * Create SinkToSourceAdapter to submit jobs only via the
			 * DecisionMaker of the site. In this implementation that is the LocalSubmissionComponent of
			 * the site.
			 */
			Site site = sc.getSite(associatedSiteName);
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
			 * filters to filter out invalid not simulateable jobs
			 * 
			 * TODO: generalize this somehow
			 */
			WorkloadSource workloadSource = null;
			try {
				workloadSource = new CSWFParser(u);
				//workloadSource = new SWFParser(u);
			} // try
			catch (IOException e) {
				throw new InitializationException(e);
			} // catch
			WorkloadFilter[] wf = new WorkloadFilter[] {
					new BrokenProcessorsFilter(site.getSiteInformation()),
					new IllegalRuntimeFilter(),
					new RuntimeOveruseFilter(false),
					new IllegalEstimateFilter(),
					new IllegalStatusFilter()};
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
	/**
	 * This is the main method of the simulation manager
	 * It is executed until the end of the whole simulation when all
	 * jobs in all input workloads are executed completely
	 */
	public void commence() {
		/*
		 * initially load the first jobs from each workload source and dispatch
		 * them to their corresponding workload sink, keeping the earliest
		 * timestamp
		 */
		Instant nextRelevantTimestamp = null;
		for (WorkloadSource source : workloadMap.keySet()) {
			Job j = null;
			long submitTime = -1;
			try {
				j = source.fetchNextJob();
			} // try
			catch (WorkloadException e) {
				this.terminateUngracefully(e);
			} // catch
			
			/* The following code was inserted so that the site initiate
			 * even if no job was submitted.
			 */
			if( j != null ) {
				if (j.getJobType().equals(JobType.INDEPENDENT))
					submitTime = ((SWFJob)j).getSubmitTime();
				else
					submitTime = ((CompositeJob)j).getSubmitTime(); 
			
				Instant releaseTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(submitTime));
			
				if (nextRelevantTimestamp == null || nextRelevantTimestamp.after(releaseTime)) {
					nextRelevantTimestamp = releaseTime;
				}
				workloadMap.get(source).putNextJob(j);
			}//End if
		} // for
		
		
		log.info("starting simulation");
		/*
		 * while any of the workload sources still has jobs to provide *or* the
		 * kernel still has events to handle, do the following
		 */
		while (!this.finished) {
			/*
			 * Advance the simulation clock to the next relevant time-stamp and
			 * notify the kernel to handle all events which are to occur then
			 */
			Clock.instance().advance(nextRelevantTimestamp);
			nextRelevantTimestamp = Kernel.getInstance().wakeup(
					nextRelevantTimestamp);
			
			
			Event nextEvent = Kernel.getInstance().getEventQueue().peek();
			
				
			lastEvent = nextEvent;
			
			/*
			 * check and update the outer loop condition
			 */
			if (nextRelevantTimestamp == null) { //  && workloadMap.isEmpty()
				/*
				 * the kernel has no more events to handle *and* there are no
				 * more jobs in any of the workload sources; therefore, set the
				 * simulation's state to finished
				 */
				this.finished = true;

			} // if
			else {
				/*
				 * check for all workloads whether there are jobs to be released
				 * _before_ the next known event in the kernel is to occur
				 */
				Instant newNextRelevantTimestamp = nextRelevantTimestamp;

				for (Iterator<WorkloadSource> iter = workloadMap.keySet().iterator(); iter.hasNext();) {
					WorkloadSource source = iter.next();
					WorkloadSink sink = this.workloadMap.get(source);
					boolean reachedNextRelevantTimestamp = false;
					/*
					 * release new jobs from the current workload source to its
					 * corresponding workload sink until their release times
					 * pass the timestamp of the next occuring kernel event
					 */
					Job j = null; //SWFJob j = null;
					while (!reachedNextRelevantTimestamp) {
						try {
							j = source.fetchNextJob();
						} // try
						catch (WorkloadException e) {
							this.terminateUngracefully(e);
						} // catch
						/*
						 * check whether this source still produces new jobs
						 */
						if (j == null) {
							/*
							 * no: remove this source from the map of sources
							 * (along with the corresponding sink) and exit the
							 * loop
							 */
							String msg = "release complete: no more jobs from "
									+ source + " to " + sink;
							log.info(msg);
							iter.remove();
							reachedNextRelevantTimestamp = true;
						} // if
						else {
							long submitTime = -1;
							
							/*
							 * check whether the release time of the current job
							 * from the source exceeds the next kernel event's
							 * timestamp
							 */
							if (j.getJobType().equals(JobType.INDEPENDENT))
								submitTime = ((SWFJob)j).getSubmitTime();
							else
								submitTime = ((CompositeJob)j).getSubmitTime(); 
							
							/*
							 * check whether the release time of the current job
							 * from the source exceeds the next kernel event's
							 * timestamp
							 */
							Instant releaseTime = TimeFactory.newMoment(DateHelper.convertToMilliseconds(submitTime));
							/*
							 * memorize earliest releaseTime for possible udate
							 * of NextRelevantTimestamp
							 */
							if (newNextRelevantTimestamp.after(releaseTime)) {
								newNextRelevantTimestamp = releaseTime;
							}

							if (nextRelevantTimestamp != null && releaseTime.after(nextRelevantTimestamp)) {
								/*
								 * yes: exit the loop
								 */
								reachedNextRelevantTimestamp = true;
							} // if
							/*
							 * Here, new submitted jobs from any WorkloadSource
							 * interface are send to the WorkloadSink which is
							 * realized as an SinkToSourceAdapter. In the
							 * current implementation the Adapter guarantees
							 * that all jobs are submitted over the
							 * kernel.
							 */
							String msg = "source " + source + ": releasing "
									+ j + " to " + sink;
							log.debug(msg);
							sink.putNextJob(j);
						} // else
					} // while
				} // for
				// adjust nextRelevantTimestamp
				// the clock has to be set to the first releasetime of any job
				// on any site
				nextRelevantTimestamp = newNextRelevantTimestamp;
				
			} // else
		} // while
	}

	/**
	 * This method is executed whenever the simulation is terminated because of
	 * any non-regular reason
	 * 
	 * @param e
	 */
	public void terminateUngracefully(Exception e) {
		String msg = "ungraceful termination ";
		log.fatal(msg, e);
		System.err.println(this.getClass().getSimpleName() + ": " + msg + "("
				+ e.getClass().getSimpleName() + " message was \""
				+ e.getMessage() + "\")");
		System.exit(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.util.Ephemeral#terminate()
	 */
	public void terminate() {
		this.finished = true;
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
	final private class SimulationToStringStyle extends ToStringStyle {

		/**
		 * TODO: not yet commented
		 * 
		 */
		private static final long serialVersionUID = 6369128501675265691L;

		/**
		 * Creates a new instance of this class, using the given parameters.
		 * 
		 */
		public SimulationToStringStyle() {
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
				valueToAppend = ((Date) value).getTime();
			} // if
			super.appendDetail(buffer, fieldName, valueToAppend);
		}
	}
}
