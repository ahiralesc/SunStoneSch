package mx.cicese.dcc.teikoku.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.workload.job.Vertex;
import mx.cicese.dcc.teikoku.workload.job.Edge;


/**
 *	In graph theory the longest path is a simple path of maximum length in a given graph.
 *	This problem is NP-complete which means that an optimal solution cannot be found in 
 *	polynomial time.
 *	<p>
 *	The longest path problem has an efficient for directed acyclic graphs by inverting 
 *	the weights, traversing the nodes in topological order and using the Bellman-Ford 
 *	algorithm. This solution was implemented in this version. 
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class CriticalPath<V extends Vertex,E extends Edge> {
	/**
	 * The topological sort algorithm instance.
	 */
	TopologicalSort<V,E> alg;
	
	/**
	 * The distance to each vertex (Map<V,Number>).
	 */
	private Map<V,Number> distance;
	
	/**
	 * The predecessor of each vertex (Map<V,Number>).
	 */
	private Map<V,V> predecessor;
	
	/**
	 * TODO:
	 */
	final long infinity = Long.MIN_VALUE;
	
	/**
	 * The critical path (List<V>) 
	 */
	List<V> criticalPath = null;
		
	/**
	 * The critical path length
	 */
	long criticalPathLength;


	/**
	 * Class constructor
	 */
	public CriticalPath() {
		alg = new TopologicalSort<V,E>();
		distance = new HashMap<V,Number>();
		predecessor = new HashMap<V,V>();
		criticalPath = new LinkedList<V>();
		criticalPathLength = Long.MAX_VALUE;
	}
	
	/**
	 *	Computes the critical path.
     *
     * @param g 	a DAG graph of type Hypergraph<V,E>.
     */
	public void compute(Hypergraph<V,E> g) {
		alg.clear();
		
		alg.sort(g);
		List<V> sortedList = alg.getVerticesOrdByFinishTime(); /*alg.getTopologicalSort(); */
		this.initialize(g,sortedList.get(0));
				
		/* For each vertex taken in topological order */ 
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			V u = it.next();
			for(V v : g.getSuccessors(u)) {
				this.relax(u,v,g);
			}//End for
		}//End for
		
		this.buildCriticalPath(g);
	}//End compute


	/**
	 *	Getter method retrieves a list with the critical path.
     *
     * @return 		a list with the critical path (List<V>).
     */
	public List<V> getCriticalPath(){
		return this.criticalPath;
	}
	
	/**
	 *	Getter method retrieves the length of the critical path.
     *
     * @return 		the lenght of the critical path (double).
     */
	public double getCriticalPathLength() {
		return this.criticalPathLength;
	}
	
	
	/**
	 * Relaxes the weight of the destination vertex 
	 * 
	 * @param u 	the source vertex
	 * @param v 	the destination vertex
	 * @param g 	the graph 
	 */
	private void relax(V u, V v, Hypergraph<V,E> g) {
		E e = g.findEdge(u, v);
		long distU = distance.get(v).longValue();
		long distV =  distance.get(u).longValue() + e.getCost() + u.getCost();
		if(distU < distV){
			distance.put( v, distV );
			predecessor.put(v,u);
		}//if
	} //End relax
	
	
	/**
	 * Initializes the predecessor and color of all vertices
	 * 
	 * @param g 	the graph
	 * @param s		a source vertex
	 */
	private void initialize(Hypergraph<V,E> g, V s) {
		for(V v : g.getVertices()){
			distance.put(v,this.infinity);
			predecessor.put(v,null);
			// Graphs that have more than one start node must also be initialized to zero
			if(g.inDegree(v)==0)
				distance.put(v,0);
		}//End for
		
		distance.put(s,0);
	} // initialize

	
	
	/**
	 * 	Traverses the predecessor structure to build the critical 
	 * 	path, initiating at the vertex with minimum value.
	 *
	 *	The critical path is stored in this.criticalPath
	 */
	private void buildCriticalPath(Hypergraph<V,E> g){
		V last = null;
		V v = null;
		
		/* If all distances are equal to zero, then min vertex 
			must be the one with in-degree equal to zero */
		if( !checkDistances() ) 
			v = this.getVWithMinValue(g);
		else {
			v = this.getMaxVertex(g);
			this.criticalPathLength = 0;
		}
		last = v;
		do{	V p = this.predecessor.get(v);
			criticalPath.add(v);
			if( p == null )
				break;
			else
				v = p;
		}while(true);
		this.criticalPathLength =  (this.criticalPathLength + last.getCost());
	}// End buildCriticalPath
	
	
	/**
	 *	Retrieves the vertex with minimum cost given
	 *  this.distance set.
	 * 
	 * @return		the vertex with minimum cost
	 */
	private V getVWithMinValue(Hypergraph<V,E> g) {
		V maxV = null;
		double maxDistance = Double.MIN_VALUE;
		Vector<V> exitNodes = new Vector<V>();
		
		// Search for all exit nodes
		for(V v : distance.keySet()) {
			if(g.outDegree(v)==0)
				exitNodes.add(v);
		}
		// Select the exit nodes that maximizes the critical path cost 
		for(V v : exitNodes) {
			long pathCost = distance.get(v).longValue() + v.getCost();
			if (pathCost > maxDistance) {
				maxDistance = pathCost;
				maxV = v;
			}//End if
		}//End for
		
		this.criticalPathLength = distance.get(maxV).longValue();
		return maxV;
	}// End getVWithMinValue
	
	
	
	
	 /**    
     *	Removes all mappings. 
     * 	
     */
    public void clear() {
    	this.distance.clear();
    	this.predecessor.clear();
    	this.criticalPath.clear();
    	this.criticalPathLength = 0;
    } // clear
    
    
    private boolean checkDistances() {
    	boolean allDistancesZero = true;
    	
    	for(V v : this.distance.keySet()){
    		if(this.distance.get(v).longValue() != 0) {
    			allDistancesZero = false;
    			break;
    		}
    	}
    	return allDistancesZero;
    }
    
    
    private V getMaxVertex(Hypergraph<V,E> g) {
    	V max_v = null;
    	double max = Double.MIN_VALUE;
    	    	
    	for(V v : g.getVertices()) {
    		long c = v.getCost();
    		if( c > max ) { 
    			max_v = v;
    			max = c;
    		}
    	}
    	return max_v;
    }

}// CriticalPath
