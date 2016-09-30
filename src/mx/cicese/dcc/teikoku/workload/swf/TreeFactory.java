package mx.cicese.dcc.teikoku.workload.swf;

import java.util.Vector;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.util.EdgeType;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import mx.cicese.dcc.teikoku.workload.job.Precedence;


/**
 * Creates a Tree container for Teikoku SWFJob
 * 
 * @author <a href="mailto:ahiralesc@hotmail.com">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */

public class TreeFactory extends WorkflowFactory {
	private final String name = "TREE"; 
	
	/**
	 * 	(non-Javadoc)
	 * 	@see mx.cicese.dcc.teikoku.WorkflowFactory#createWorkflow(mx.cicese.dcc.teikoku.WorkflowType)
	 */
	Graph<SWFJob,Precedence> createWorkflow(Vector<SWFJob> jobs) {
		Forest<SWFJob,Precedence> structure = new DelegateForest<SWFJob,Precedence>(); 
		
		SWFJob firstJob = jobs.firstElement();
		structure.addVertex(firstJob);
				
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
	 * 	(non-Javadoc)
	 *	@see mx.cicese.dcc.teikoku.WorkflowFactory#getName(mx.cicese.dcc.teikoku.WorkflowType) 
	 */
	public String getName() {
		return this.name;
	}

}
