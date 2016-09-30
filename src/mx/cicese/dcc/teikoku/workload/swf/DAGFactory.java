package mx.cicese.dcc.teikoku.workload.swf;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.Vector;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import mx.cicese.dcc.teikoku.workload.job.Precedence;


/**
* Creates a Directed Graph (DG) container for Teikoku SWFJob
* 
* @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales Carbajal</a>
*         (last edited by $Author$)
* @version $Version$, $Date$
* 
*/
public class DAGFactory extends WorkflowFactory{
	/**
	 * The name of the composite job producer
	 */
	private final String name = "DAG"; 
	
	/**
	 *	(non-Javadoc)
	 *	@see mx.cicese.dcc.teikoku.WorkflowFactory#createWorkflow(mx.cicese.dcc.teikoku.WorkflowType)
	 */
	Graph<SWFJob,Precedence> createWorkflow(Vector<SWFJob> jobs) {
		DirectedGraph<SWFJob,Precedence> structure = new DirectedSparseMultigraph<SWFJob,Precedence>(); 
		
		for(SWFJob job : jobs)
			structure.addVertex(job);
				
		for(SWFJob job : jobs ) {
			for(Number succesorID : job.getSuccessors()) {
				SWFJob successor = successorInstance(jobs, succesorID);
				
				/* creates the precedence constraint, cost by default is
				 * set to cero upon the creation of the instance */
				Precedence p = new Precedence();
				p.setPredecessor(job);
				p.setSuccessor(successor);
				p.setCost(0);
				
				structure.addEdge(p, job, successor, EdgeType.DIRECTED);
			}
		}
		return structure;
	}
	
	
	/**
	 *  (non-Javadoc)
	 * 	@see mx.cicese.dcc.teikoku.WorkflowFactory#getName(mx.cicese.dcc.teikoku.WorkflowType)
	 */
	public String getName() {
		return this.name;
	}
}
