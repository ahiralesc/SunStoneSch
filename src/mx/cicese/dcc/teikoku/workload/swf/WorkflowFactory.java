package mx.cicese.dcc.teikoku.workload.swf;

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
import java.util.Vector;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
* @author <a href="mailto:ahiralesc@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 *
 */
public abstract class WorkflowFactory {
	

	/**
	 * Returns an instance of a Workflow. The workflow is built using precedence 
	 * constraints and jobs specified in the jobs set. 
	 *  
	 * @param jobs
	 * 			A Vector of jobs that compose the workflow.
	 * 			jobs are of type WSWFJob
	 * 
	 * @return A workflow of type Workflow
	 * @see #mx.cicese.dcc.teikoku.workflow.job.Workflow
	 */
	public CompositeJob getCompositeJob(Vector<SWFJob> jobs) {
		/* Get the name of the composite job */
		SWFJob job = jobs.firstElement();
		CompositeJob compositeJob = new CompositeJob(
				new Integer(job.getCompositeJobId()).toString());
		compositeJob.setStructure(createWorkflow(jobs));
		return compositeJob;
	}
		
	
	/**
	 * Getter method that returns an instance of a Workflow of type:
	 * 	- DAG, TREE, DG, or CHAIN.
	 * It builds the Graph based on the precedence constrains specified 
	 * in the "jobs" set.  	
	 * 
	 * @param workflowType
	 * 		The worflow type of the workflow to create.
	 *  
	 * @see #mx.cicese.dcc.teikoku.workflow.job.ChainFactory
	 * @see #mx.cicese.dcc.teikoku.workflow.job.DAGFactory
	 * @see #mx.cicese.dcc.teikoku.workflow.job.DGFactory
	 * @see #mx.cicese.dcc.teikoku.workflow.job.TreeFactory
	 * 
	 * @return An instance of a Graph
	 */
	abstract Graph<SWFJob,Precedence> createWorkflow(Vector<SWFJob> jobs);
	
	
	/**
	 *  Gets the name of the composite job name producer
	 */
	
	abstract String getName();
	
	
	/**
	 * Getter method that returns an instance of a successor give
	 * a successor id and a set of succesors.   	
	 * 
	 * @param jobs
	 * 		A set of jobs 
	 * @param succesorID
	 * 		The id of a job
	 * 
	 * @return An instance of a job of type WSWFJob. 
	 */	
	protected SWFJob successorInstance(Vector<SWFJob> jobs, Number successorID) {
		SWFJob sucessor = null;
		
		for(SWFJob job : jobs ) {
			if ( job.getJobNumber() == successorID.longValue() ) {
				sucessor = job;
				break;
			}
		}
		return sucessor;
	}
	
}
