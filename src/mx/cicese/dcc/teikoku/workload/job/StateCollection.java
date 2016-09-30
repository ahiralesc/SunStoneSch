package mx.cicese.dcc.teikoku.workload.job;

/*
 * "Teikoku Workflow Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the:
 *   Departamento de Ciencias Computacionales,
 *   Centro de Investigacon Cientifica y de Educacion Superior de Ensenada (CICESE),
 *   Mexico
 *    
 *   Facultad de Ciencias, 
 *   Ciencias Computacionales,
 *   Universidad Autonoma de Baja California (UABC), 
 *   Mexico
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class StateCollection {
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	Set<SWFJob> released;
	Set<SWFJob> queued;
	Set<SWFJob> scheduled;
	Set<SWFJob> started;
	Set<SWFJob> resumed;
	Set<SWFJob> aborted;
	Set<SWFJob> completed;
	Set<SWFJob> failed;
	
	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 */
	public StateCollection(){
		released = new HashSet<SWFJob>();
		queued = new HashSet<SWFJob>();
		scheduled = new HashSet<SWFJob>();
		started = new HashSet<SWFJob>();
		resumed = new HashSet<SWFJob>();
		aborted = new HashSet<SWFJob>();
		completed = new HashSet<SWFJob>();
		failed = new HashSet<SWFJob>();
	}
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * It iterates over the job graph identifying independent jobs.
	 * It assings independent jobs to a set. 
	 */
	public void initialize(Graph<SWFJob,Precedence> structure){
		for(SWFJob vertex : structure.getVertices()){
			if( structure.inDegree(vertex) == 0)
				released.add(vertex);
			else
				queued.add(vertex);
		}
	}
	
	
	
	/**
	 * TODO: not yet commented
	 * 
	 * It iterates over the job graph identifying independent jobs.
	 * It assigns independent jobs to a set. 
	 */
	public void updateIndependentJobs(Graph<SWFJob,Precedence> structure, SWFJob job){
		Collection<SWFJob> inJobs = structure.getSuccessors(job);
		for(Iterator<SWFJob> it=inJobs.iterator(); it.hasNext();) {
			released.add(it.next());
		}
	}
	
	
	
	
	/**
	 * Getter method for the "released" field of this type.
	 * 
	 * @return Returns the current contents of "released" of type Set.
	 */
	public Set<SWFJob> getReleasedJobs(){
		return released;
	}
	
	
	/**
	 * Getter method for the "started" field of this type.
	 * 
	 * @return Returns the current contents of "started" of type Set.
	 */
	public Set<SWFJob> getStartedJobs(){
		return started;
	}
	
	
	/**
	 * Getter method for the "completed" field of this type.
	 * 
	 * @return Returns the current contents of "completed" of type Set.
	 */
	public Set<SWFJob> getCompletedJobs(){
		return completed;
	}
	
	
	/**
	 * Setter method.
	 * 
	 */
	public void updateState(State state, SWFJob job) {
		switch (state) {
		
		case RELEASED:
			//Check if job is in realsed set, then update sets.
			if(released.contains(job)){
				released.remove(job);
				started.add(job);
			}
			break;
		case QUEUED:
			// Does nothing.
			break;
		case SCHEDULED:
			// Does nothing.
			break;
		case STARTED:
			if(started.contains(job)){
				started.remove(job);
				completed.add(job);
			}
			break;
		case SUSPENDED:
			// Does nothing. 
			break;
		case RESUMED:
			// Does nothing.
			break;
		case ABORTED:
			// Does nothing.
			break;
		case COMPLETED:
			// Does nothing.
			break;	
		case FAILED:
			// Does nothing.
			break;	
		default:
			// The simulation crashes!!!! 
			break;
	} // switch
	
	}
}
