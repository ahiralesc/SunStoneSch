package mx.cicese.dcc.teikoku.workload.job;

import de.irf.it.rmg.core.teikoku.job.Job;
import mx.cicese.dcc.teikoku.workload.job.Edge;

/**
 *	Precedence models jobs precedence constrains present in composite jobs.
 *	Precedence constraints are set during parsing, which is where jobs are initiated, 
 *	for further details see <a href="CSWFParser#buildWorkflow">CSWFParser.</a>)
 *	<p>
 *
 *	Two jobs are link to a precedence association:
 *	<ul>
 *		<li> Predecessor (p).
 *		<li> And, a successor (s).
 *	</ul>
 * 	Such that p --> s.
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class Precedence implements Edge{
	/**
	 * Predecessor job
	 */
	private Job predecessor;
	
	/**
	 * Successor job
	 */
	private Job successor;
	
	/**
	 * The precedence associated cost, by default the cost is cero
	 * upon creation.
	 */
	private long cost;
	
	
	/**
	 * Class constructor
	 */
	public Precedence() {
		this.predecessor = null;
		this.successor = null;
		this.cost = 0;
	}
	
	/**
	 *	Setter, sets this precedence relation predecessor job.
	 *  
	 * @param predecessor		the predecessor job.
	 */
	public void setPredecessor(Job predecessor) {
		this.predecessor = predecessor;
	}//End setPredecessor

	/**
	 * Getter, gets the predecessor.
	 *  
	 * @return		this precedence predecessor job
	 */
	public Job getPredecessor() {
		return this.predecessor;
	} // End getPredecessor

	/**
	 *	Setter, sets this precedence relation succesror job. 
	 *  
	 * @param successor		the succesor job.
	 */
	public void setSuccessor(Job successor) {
		this.successor = successor;
	} // End setSuccessor
	
	/**
	 * Getter, gets the successor.
	 *  
	 * @return		this precedence successor job
	 */
	public Job getSuccessor() {
		return this.successor;
	} //End getSuccessor
	
	/**
	 *	Setter, associates a cost to the precedence constraint.  
	 *  
	 * @param cost		the succesor job.
	 */
	public void setCost(long cost) {
		this.cost = cost;
	}// End setCost
	
	/**
	 * Getter, gets the cost of the precedence constraint.
	 * 
	 * @return		the cost of the precedence.
	 */
	public long getCost() {
		return this.cost;
	}// End getCost
	
}// Precedence
