package mx.cicese.dcc.teikoku.workload.swf;

import java.util.Vector;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
/**
* Creates a Directed Graph (DG) container for Teikoku SWFJob
* 
* @author <a href="mailto:ahiralesc@uabc.mx">Adan Hirales Carbajal</a>
*         (last edited by $Author$)
* @version $Version$, $Date$
* 
*/

public class ChainFactory extends WorkflowFactory {
	
	/**
	 * Name of the composite job producer
	 */
	private final String name = "CHAIN"; 
	
	
	/**
	 *  (non-Javadoc)
	 * 	@see mx.cicese.dcc.teikoku.WorkflowFactory#createWorkflow(mx.cicese.dcc.teikoku.WorkflowType)
	 */
	Graph<SWFJob,Precedence> createWorkflow(Vector<SWFJob> jobs) {
		Graph<SWFJob,Precedence> structure = new DirectedSparseGraph<SWFJob,Precedence>(); 
		
		for(SWFJob job : jobs)
			structure.addVertex(job);
				
		for(SWFJob job : jobs ) {
			for(Number sucessorID : job.getSuccessors()) {
				SWFJob successor = successorInstance(jobs, sucessorID);
				
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
	 * (non-Javadoc)
	 * @see mx.cicese.dcc.teikoku.WorkflowFactory#getName (mx.cicese.dcc.teikoku.WorkflowType)
	 */
	public String getName() {
		return this.name;
	}
	
}
