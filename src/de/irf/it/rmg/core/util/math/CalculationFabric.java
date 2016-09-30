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
/**
 * 
 */
package de.irf.it.rmg.core.util.math;

import java.util.Queue;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.TimeHelper;

/**
 * @author Administrator
 *
 */
public class CalculationFabric {

	public double calculateValue(String what,Job job,Site site){
		if (what.equals("rwp")){
			try{
				return getCurrentRWP(site);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if (what.equals("rjp")){
			try{
				return getRelativeJobParallelism(job,site);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if (what.equals("jSA")){
			try{
				return getJobSA(job);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		System.out.println("Theres calculationroutine for parameter: "+what);
		System.exit(-1);
		return -1;
	}
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * @return
	 */
	private double getCurrentRWP(Site site) {
		double rwp = 0;
		Queue<Job> currentJObs = site.getScheduler().getQueue();
		for (Job job : currentJObs) {
			rwp += job.getDescription().getNumberOfRequestedResources();
		} // for
		rwp/=(site.getSiteInformation().getNumberOfAvailableResources()*1.0);
		return rwp;
	}
	
	/**
	 * TODO: not yet commented
	 * 
	 * @param j
	 * @return
	 */
	private long getJobSA(Job j) {
		long p_bar = TimeHelper.toSeconds(j.getDescription()
				.getEstimatedRuntime());
		long m_j = j.getDescription().getNumberOfRequestedResources();
		long sa = p_bar * m_j;
		return sa;
	}
	
	/**
	 * TODO: not yet commented
	 * 
	 * @param j
	 * @return
	 */
	private double getRelativeJobParallelism(Job j,Site site) {
		double m_j = j.getDescription().getNumberOfRequestedResources()*1.0;
		return (m_j*100)/(site.getSiteInformation().getNumberOfAvailableResources()*1.0);
	}
	
}
