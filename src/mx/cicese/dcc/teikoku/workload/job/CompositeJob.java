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

import edu.uci.ics.jung.graph.Graph;
import de.irf.it.rmg.core.teikoku.job.Description;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class CompositeJob extends Job{
	
	/**
	 * Proprietor of this workflow.
	 * 
	 */
	private String proprietor;
	
	/**
	 * Budget assigned for the execution of the workflow.
	 * 
	 */
	private Float budget;
	
	/**
	 * Stores the nodes and vertices that compose the workflow.
	 * 
	 */
	private Graph<SWFJob,Precedence> structure;
	
	
	/**
	 * The state of the workflow. 
	 * 
	 * @see #mx.cicese.dcc.teikoku.workflow.WState
	 */
	private CompositeJobState state;
	
	/**
	 * The description data for this job.
	 * 
	 */
	final private Description workflowDescription = new WorkflowDescription();
	
	/**
	 * Total number of processors reqested for this workflow.
	 * 
	 */
	private int requestedNumberOfProcessors;
	
	
	/**
	 * The requested time of this job.
	 * 
	 * This can be either runtime (measured in wallclock seconds), or average
	 * CPU time per processor (also in seconds) -- the exact meaning is
	 * determined by a header comment.
	 * 
	 * In many logs this field is used for the user runtime estimate (or upper
	 * bound) used in backfilling. If a log contains a request for total CPU
	 * time, it is divided by the number of requested processors.
	 * 
	 * @see SWFHeader#getNote(int)
	 * 
	 */
	private double requestedTime;
	
	
	/**
	 * The submission time of this job, in seconds.
	 * 
	 * The earliest time the log refers to is zero, and is the submittal time
	 * the of the first job. The lines in the log are sorted by ascending
	 * submittal times. In makes sense for jobs to also be numbered in this
	 * order.
	 * 
	 */
	private long submitTime;
	
	/**
	 * TODO: not yet commented
	 * 
	 * Creates a new instance of this class. Defaults are specified for: 
	 * - uuid = 1
	 * - name = null
	 * - proprietor = null
	 * - budget = null
	 * - structure = null
	 * - state = INACTIVE, ence the workflow can not be scheduled.
	 */
	public CompositeJob(String name) {
		super(name);
		this.proprietor = null;
		this.budget = new Float(0);
		this.structure = null;
		this.state = CompositeJobState.INACTIVE;
	}
		
	/**
	 * Sets the proprietor of this workflow.
	 * 
	 * @param proprietor
	 *            The proprietor of this workflow.
	 * 
	 */
	final public void setProprietor(String proprietor) {
		this.proprietor = proprietor;
	}
	
	/**
	 * Getter method for the "proprietor" field of this type.
	 * 
	 * @return Returns the current contents of "this.proprietor".
	 */
	final public String getProprietor() {
		return this.proprietor;
	}
	
	/**
	 * Sets the budget of this workflow.
	 * 
	 * @param budget
	 *            The budget of this workflow.
	 * 
	 */
	final public void setBudget(Float budget) {
		this.budget = budget;
	}
	
	/**
	 * Getter method for the "budget" field of this type.
	 * 
	 * @return Returns the current contents of "this.uuid".
	 */
	final public Float getBudget() {
		return this.budget;
	}
	
	/**
	 * Setter method for the "structure" field of this type.
	 * 
	 * @param structure
	 *            The structure of this workflow. Each job in the structure 
	 *            is of type WSWFJob.   
	 */
	final public void setStructure(Graph<SWFJob,Precedence> structure) {
		this.structure = structure;
	}
	
	/**
	 * Getter method for the "structure" field of this type.
	 * 
	 * @return Returns the current contents of "this.uuid".
	 */
	final public Graph<SWFJob,Precedence> getStructure() {
		return this.structure;
	}
	
	/**
	 * Sets the state of this workflow.
	 * 
	 * @param state
	 *            The state of this workflow. Initial state must be 
	 *            <code>WState.INACTIVE</code>. For other state definitions see
	 * 
	 * @see #mx.cicese.dcc.teikoku.workflow.WState
	 */
	public void setState(CompositeJobState state) {
		this.state = state;
	}
	
	/**
	 * Returns the state of this workflow.
	 * 
	 * @return The state of this workflow.
	 */
	public CompositeJobState getState() {
		return this.state;
	}
	
	/**
	 * TODO 
	 * 
	 */
	final public void initializeState() {
		this.state.initialize(this.structure);
	} 
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.job.Job#getDescription()
	 */
	@Override
	public Description getDescription() {
		return this.workflowDescription;
	}

	/**
	 * Sets the requested number of processors of this job.
	 * 
	 * @param requestedNumberOfProcessors
	 *            The requested number of processors of this job.
	 * 
	 * @see #getRequestedNumberOfProcessors()
	 */
	final public void setRequestedNumberOfProcessors(int requestedNumberOfProcessors) {
		// TODO: not yet implemented
		this.requestedNumberOfProcessors = requestedNumberOfProcessors;
	}
	
	/**
	 * Returns the requested number of processors of this job.
	 * 
	 * @return The requested number of processors of this job.
	 *  
	 *  TODO: Es necesario contabilizar el numero de procesadores.
	 */
	final public int getRequestedNumberOfProcessors() {
		// TODO: not yet implemented
		return this.requestedNumberOfProcessors;
	}
	
	/**
	 * Returns the requested time of this job.
	 * 
	 * This can be either runtime (measured in wallclock seconds), or average
	 * CPU time per processor (also in seconds) -- the exact meaning is
	 * determined by a header comment.
	 * 
	 * In many logs this field is used for the user runtime estimate (or upper
	 * bound) used in backfilling. If a log contains a request for total CPU
	 * time, it is divided by the number of requested processors.
	 * 
	 * @return The requested time of this job.
	 */
	final public double getRequestedTime() {
		// TODO: not yet implemented
		return this.requestedTime;
	}

	/**
	 * Sets the requested time of this job.
	 * 
	 * @param requestedTime
	 *            The requested time of this job.
	 * 
	 * @see #getRequestedTime()
	 */
	final public void setRequestedTime(double requestedTime) {
		// TODO: not yet implemented
		this.requestedTime = requestedTime;
	}
	
	/**
	 * Returns the number of jobs in this workflow.
	 * 
	 * @return The number of jobs in this workflow. 
	 */
	final public int getNumberOfJobs() {
		// TODO: not yet implemented
		return this.structure.getVertexCount();
	}
	
	
	/**
	 * Returns the submission time of this job, in seconds.
	 * 
	 * The earliest time the log refers to is zero, and is the submittal time
	 * the of the first job. The lines in the log are sorted by ascending
	 * submittal times. In makes sense for jobs to also be numbered in this
	 * order.
	 * 
	 * @return The submission time of this job, in seconds.
	 */
	public long getSubmitTime() {
		// TODO: not yet implemented
		return this.submitTime;
	}

	/**
	 * Sets the submission time of this job, in seconds.
	 * 
	 * @param submitTime
	 *          The submission time of this job, in seconds.
	 * 
	 * @see #getSubmitTime()
	 */
	public long setSubmitTime() {
		long minSubmitTime = -1;
		boolean fistJobRetreived = true;
		
		for(SWFJob job :this.structure.getVertices()){
			// First job submit time is set as the minimum
			if( fistJobRetreived ) {
				fistJobRetreived = false;
				minSubmitTime = job.getSubmitTime();
			} 
			if( job.getSubmitTime() < minSubmitTime)
				minSubmitTime = job.getSubmitTime();
		}
				
		this.submitTime = minSubmitTime;
		return minSubmitTime;
	}
	
	/**
	 * TODO: not yet commented
	 * 
	 * @author <a href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
	 * @version $Version$, $Date$
	 * 
	 */
	private class WorkflowDescription
			implements Description {

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.teikoku.job.Description#getNumberOfRequestedResources()
		 */
		public int getNumberOfRequestedResources() {			
			return getRequestedNumberOfProcessors();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.teikoku.job.Description#getEstimatedRuntime()
		 */
		public Distance getEstimatedRuntime() {
			Distance result = null;

			long requestedTime = DateHelper
					.convertToMilliseconds(getRequestedTime());

			if (requestedTime == Distance.PERPETUAL) {
				result = TimeFactory.newPerpetual();
			} // if
			else {
				result = TimeFactory.newFinite(requestedTime);
			} // else

			return result;
		}
	}
}
