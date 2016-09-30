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
package de.irf.it.rmg.core.teikoku.workload;

import de.irf.it.rmg.core.teikoku.job.Job;

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
public interface WorkloadFilter {

	/**
	 * Applies this workload filter to the given job.
	 * 
	 * In detail, the {@link Job} object provided as the first parameter is
	 * checked against the filter's specification. If the filter would match
	 * this job, and thus the job would be filtered out, this method returns
	 * <code>false</code>. Otherwise, i.e. the filter would not match this
	 * job <em>or</em> the filter could "fix" the job, it returns
	 * <code>true</code>.
	 * 
	 * The process of "fixing" allows the filter to manipulate in such a way
	 * that it's specification would let the manipulated job pass, although the
	 * original job would have been filtered out. This allows imposing certain
	 * "check-and-repair" functionality within the filter.
	 * 
	 * The implementation (on the filter side) and usage (on the client side) of
	 * fixing functionality is optional.
	 * 
	 * @param job
	 *            The job that is to be checked against the filter's
	 *            specification. If fixing is allowed, the given {@link Job}
	 *            object may be manipulated by the filter.
	 * @param fix
	 *            Denotes whether, for this filter applicance, fixing is
	 *            allowed. If <code>true</code>, the filter <strong>may</strong>
	 *            try to fix jobs that would have been filtered out normally and
	 *            let them pass through. Otherwise, jobs matching the filter's
	 *            specification are always filtered out.
	 * @return <code>true</code>, if the job passed this filter;
	 *         <code>false</code>, otherwise.
	 */
	boolean apply(Job job, boolean fix);
	

}
