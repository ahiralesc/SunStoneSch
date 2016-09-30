package mx.cicese.dcc.teikoku.workload.job;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.UUID;
import java.util.Iterator;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.utilities.JobIdComparator;
import edu.uci.ics.jung.graph.Hypergraph;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

/**
 *	Set of static functions used to perform operations on composite jobs.
 *	CompositeJob utils currently implement the following helper functions:
 *	<ul>
 *	<li> initialize		Analyzes a composite job and assigns member jobs a state
 *						according to the conditions defined in MemberState
 *						(see <a href="#MemberState">MemberState</a>)
 *	<li> getIndependentJobs
 *						Retrieves a set of independent jobs from the given 
 *						composite job.
 *	<li> updatePrecedenceConstrains
 *						Updates the precedence constrains of a composite job 
 *						given a member job in completed state
 *	</ul>
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public final class CompositeJobUtils{

	/**
	 *	Analyzes a composite job and assigns member jobs a state.
	 *	(see <a href="#MemberState">MemberState</a>)
     *
     * @param g 	a composite job.
     * @param id 	the id of the composite job container
     * @see Vertex and Graph.
     */
	public static <V extends Vertex, E extends Edge> 
	void initialize(Hypergraph<V,E> g, UUID id) {
		for(V job : g.getVertices()) {
			if(g.inDegree(job) == 0)
				job.setMemberState(MemberState.NOT_EXPLORED);
			else
				job.setMemberState(MemberState.NOT_AVAILABLE);
			((SWFJob)job).setJobContainerId(id);
		} // End for
	}// End initialize
	
	/**
	 *	Retrieves a set of independent jobs from the given composite job.
     *
     * @param g 	a composite job.
     * @return  	a <code>List</code> of independent member jobs
     */
	@SuppressWarnings("unchecked")
	public static <V extends Vertex, E extends Edge>
	List<V> getIndependentJobs(Hypergraph<V,E> g) {
		List<V> independentJobs = new LinkedList<V>();
		
		for(V job : g.getVertices()) {
			if( job.getMemberState().equals(MemberState.NOT_EXPLORED)) {
				job.setMemberState(MemberState.EXPLORED_NOT_COMPLETED);
				independentJobs.add(job);
			}// End if
		}// End for
		
		Collections.sort(independentJobs, new JobIdComparator());
		return independentJobs;
	}// End getIndependentJobs
	
	
	/**
	 *	Changes the state of a set a vertices to the specified state.
     *
     * @param g 	a composite job.
     * @return  	a <code>List</code> of independent member jobs
     */
	@SuppressWarnings(value = "all")
	public static <V extends Vertex, E extends Edge>
	void updateState(Hypergraph<V,E> g, List<V> jobs, MemberState target) {
		for(Iterator it=jobs.iterator(); it.hasNext();){
			V job = (V) it.next();
			job.setMemberState(MemberState.NOT_EXPLORED);
		} //End for
	}// End updateState
	
	
	
	
	/**
	 *	Updates the precedence constrains of a composite job 
	 *	given a member job in completed state.
     *
     * @param g 				a composite job.
     * @param completedJob 		a member job in MemberState.COMPLETED state
     * @see MemberState
     */
	public static <V extends Vertex, E extends Edge> 
	void updatePrecedenceConstrains(Hypergraph<V,E> g, V completedJob ) {
		for(V job : g.getVertices()) {
			if(job.equals(completedJob)) {
				job.setMemberState(MemberState.COMPLETED);
				break;
			}//End if
		} // End for
		
		// Search to determine if precedence constraints were broken.
		// If so, mark the job as NOT_EXPLORED.
		for(V successor : g.getSuccessors(completedJob)) {
			int numCompletedPredeccessors = 0;
			int numPredeccessors = g.getPredecessors(successor).size(); 
			for( V predeccessor : g.getPredecessors(successor)){
				if( predeccessor.getMemberState().equals(MemberState.COMPLETED) ) 
					numCompletedPredeccessors++;
			}//End for
			if(numCompletedPredeccessors == numPredeccessors)
				successor.setMemberState(MemberState.NOT_EXPLORED);
		}//End for
	}//End updatePrecedenceConstrains
	
	
	/**
	 *	Returns the maximum start time for a given node.
	 *  The estimate is based AFT of predecessor jobs 
	 *	
     *
     * @param g 				a composite job.
     * @param completedJob 		a member job in MemberState.COMPLETED state
     * @see MemberState
     */
	public static <V extends Vertex, E extends Edge> 
	long getMaximumStartTime(Hypergraph<V,E> g, V m, JobControlBlock plan){
		long max = 0; 
		
		for( V job : g.getPredecessors(m)) {
			long preJob = plan.getAFT((SWFJob)job);
			if( preJob > max )
				max = preJob;
		} //End for
		return max;
	} 
	
	
	/**
	 *	Returns the number of member jobs in Completed state.
     *
     * @param g 				a composite job.
     * @see MemberState
     */
	public static <V extends Vertex, E extends Edge> 
	int getNumberOfCompletedJobs(Hypergraph<V,E> g) {
		int numCompJobs = 0;
		for(V job : g.getVertices()) {
			if (job.getMemberState() == MemberState.COMPLETED) 
				numCompJobs++;
		} // End for
		return numCompJobs;
	}//End getNumberOfCompletedJobs
	
}
