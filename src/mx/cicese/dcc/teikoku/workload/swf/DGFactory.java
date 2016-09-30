package mx.cicese.dcc.teikoku.workload.swf;

import java.util.Vector;
import edu.uci.ics.jung.graph.Graph;
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
public class DGFactory extends WorkflowFactory{
	/**
	 * The name of the composite job producer
	 */
	private final String name = "DG"; 
	
	
	/**
	 * 	(non-Javadoc)
	 * 	@see mx.cicese.dcc.teikoku.WorkflowFactory#createWorkflow(mx.cicese.dcc.teikoku.WorkflowType)
	 */
	Graph<SWFJob,Precedence> createWorkflow(Vector<SWFJob> jobs) {
		Graph<SWFJob,Precedence> structure = null;
		
		// To implement.
		
		return structure;
	}
	
	/**
	 * 	(non-Javadoc)
	 * 	@see mx.cicese.dcc.teikoku.WorkflowFactory#getName(mx.cicese.dcc.teikoku.WorkflowType)
	 */
	public String getName() {
		return this.name;
	}

}
