package mx.cicese.dcc.teikoku.scheduler.priority;

import java.util.Map;
import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.workload.job.Edge;
import mx.cicese.dcc.teikoku.workload.job.Vertex;

public interface Priority<V extends Vertex,E extends Edge> {

	/**
	 * Computes the priority, overloaded by implementation
	 * 
	 * @param g		the composite job structure
	 */
	public void compute(Hypergraph<V,E> g);
	
	/**
	 * Gets the computed ranking
	 * 
	 * @return		a map with the computed priorities
	 */
	public Map<V,Number> getRanking();
	
	
	/**
	 * Gets the priority name
	 */
	public String getName();
	
	
	/**    
    *	Removes all mappings. 
    * 	
    */
   	public void clear();
	
} // End Priority


