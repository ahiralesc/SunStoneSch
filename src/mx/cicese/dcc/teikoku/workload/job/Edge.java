package mx.cicese.dcc.teikoku.workload.job;

/**
 * CompositeEdge is the base interface that all edge implementations must implement.
 * It allows association of cost to a given edge. 
 * <p>  
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public interface Edge {
	
	/** 
     * Sets the cost of the edge
     *
     * @param cost	the cost of the vertex
     */
	public void setCost(long cost);
	
	/** 
     * Gets the cost of the edge
     *
     * @return		the cost of the vertex
     */
	public long getCost();
	
}
