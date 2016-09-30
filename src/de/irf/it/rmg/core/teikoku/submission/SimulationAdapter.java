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
package de.irf.it.rmg.core.teikoku.submission;

import mx.cicese.dcc.teikoku.information.broker.ComputeSiteInformation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.exceptions.SubmissionException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.util.time.TimeHelper;
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
final public class SimulationAdapter
		implements Executor {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(SimulationAdapter.class);

	private Site site;
	
	public SimulationAdapter(Site site){
		this.site=site;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.submission.Executor#submit(de.irf.it.rmg.core.teikoku.submission.SubmissionSource,
	 *      de.irf.it.rmg.core.teikoku.common.Job)
	 */
	public void submit(Job job)
			throws SubmissionException {
		SWFJob swfJob = ( SWFJob ) job;

		
		//Check whether estimated Endtime is before real endtime
		/*if (job.getDescription().getEstimatedRuntime().length()<swfJob.getRunTime()*1000){
			Instant endTime = TimeHelper.add(swfJob.getDuration().getAdvent(),
					TimeFactory.newFinite(DateHelper.convertToMilliseconds(job.getDescription().getEstimatedRuntime().length())));
			JobAbortedEvent jce = new JobAbortedEvent(endTime,job,this.site);
			Kernel.getInstance().dispatch(jce);
		} else{*/
		
		// DETERMINAR QUE UNIDAD DE TIEMPO CORRESPONDE AL TIEMPO DE EJECUCION DE LA TAREA
		
		// ACTUALIZAR CON EL TIEMPO DE EJECUCION DE ACUEDO A LA VELOCIDAD DEL RELOJ DEL SISTEMA
		double clockRate = (double) ((ComputeSiteInformation)site.getSiteInformation()).getClockRate();
		double rt = swfJob.getRunTime()/clockRate;
		swfJob.setRunTime((long) Math.ceil(rt));
		
		//Don't generate abortion-Events
			Instant endTime = TimeHelper.add(swfJob.getDuration().getAdvent(),
					TimeFactory.newFinite(DateHelper.convertToMilliseconds(swfJob
							.getRunTime())));
			JobCompletedEvent jce = new JobCompletedEvent(endTime, job,this.site);
			jce.getTags().put(job.getReleasedSite().getUUID().toString(), job.getReleasedSite());
			Kernel.getInstance().dispatch(jce);
		//}
		swfJob.getLifecycle().addEpisode(State.STARTED);
		swfJob.setWaitTime(TimeHelper.toSeconds(swfJob.getRuntimeInformation().getWaitTime()));
	}
}
