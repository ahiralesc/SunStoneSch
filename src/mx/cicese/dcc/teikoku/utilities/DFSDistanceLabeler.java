package mx.cicese.dcc.teikoku.utilities;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 *	Labels each node in the graph according to the DFS distance. 
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class DFSDistanceLabeler<V,E> {
	/**
	 * Vertex predecessors (Map<V,V>)
	 */
    private Map<V,V> predecessor;
    
    /**
	 * Vertex colors (Map<V,V>)
	 */
    private Map<V,Color> color;
    
    /**
     * Discovery times (Map<V,number>)
     */
    private Map<V,Number> discoveryTime;
    
    /**
     * Finishing times (Map<V,Number>)
     */
    private Map<V,Number> finishingTime;
    
    /**
     * List of vertices sorted by finishing times
     */
    private List<V> ordListByFinishingTime;
    
    /**
     * Used to determine if the graph is directed or undirected
     */
    private boolean isDirected;
    
    /**
     * Global time variable used by DFS
     */
    int time;
    
	
    /**
	 * Class constructor
	 */
    public DFSDistanceLabeler() {
    	predecessor = new HashMap<V,V>();
    	color = new HashMap<V,Color>();
    	discoveryTime = new HashMap<V,Number>();
    	finishingTime = new HashMap<V,Number>();
    	ordListByFinishingTime = new LinkedList<V>();
    }// End DFSDistanceLabeler
    
    
    /**
     *	Getter method that returns a map<V,V> of predecessors estimated
     * 	by DFS
     * 
     * 	@return 		the predecessors of all vertices in the given graph
     */
    public Map<V,V> getPredecessors() {
        return predecessor;
    }// End getPredecessors

    /**
     * Computes the distances to all vertices.
     * 
     * @param g		a directed or undirected graph (Hypergraph<V,E> g) 
     */
    public void labelDistances(Hypergraph<V,E> g) {
    	determineTypeOfGraph(g);
    	
        for(V v : g.getVertices()) { 
        	predecessor.put(v,null);
        	color.put(v, Color.WHITE);
        }// End for
        
        time = 0;
        
        for(V v : g.getVertices()) 
        	if(color.get(v).equals(Color.WHITE))
        		DFS_Visit(g, v);
    }// End labelDistances

    /**
     * Helper function used by the standard DFS algorithm
     * 
     * @param g		the directed or indirected graph
     * @param u		a vertex
     */
    private void DFS_Visit(Hypergraph<V,E> g, V u) {
    	color.put(u, Color.GRAY);
    	time++;
    	discoveryTime.put(u, new Integer(time));
    	
    	Collection<V> set = (this.isDirected)? g.getSuccessors(u): g.getNeighbors(u);
    	for(V v : set) {
    		if(color.get(v).equals(Color.WHITE)) {
    			predecessor.put(v, u);
    			DFS_Visit(g,v);
    		}//End if
    	}// End for
    	color.put(u, Color.BLACK);
    	time++;
    	this.finishingTime.put(u,new Integer(time));
    	this.ordListByFinishingTime.add(0,u);
    }//End DFS_Visit
 

    /**
     * Returns a list of vertices ordered by finishing time
     * 
     * @return		the list of vertices ordered by this.finishingTime
     */
    public List<V> getVerticesOrdByFinishTime() {
    	return this.ordListByFinishingTime;
    }// getVerticesOrdByFinishTime
    
    /**
     * Getter, returns the discovery times of all vertices.
     * 
     * @return		a map (Map<V,Number>) holding the discovery 
     * 				times of all vertices.
     */
    public Map<V,Number> getDiscoveryTimes() {
    	return this.discoveryTime;
    }// getDiscoveryTimes

    /**
     * Getter, returns the finishing times of all vertices.
     * 
     * @return		a map (Map<V,Number>) holding the finishing 
     * 				times of all vertices.
     */
    public Map<V,Number> getFinishingTimes() {
    	return this.finishingTime;
    }// getFinishingTimes
    
    /**    
     * 	Helper method, Determines if the given graph is directed 
     * 	or undirected 
     * 	
     * 	@param g		a directed or undirected graph.
     */
    private void determineTypeOfGraph(Hypergraph<V,E> g) {
    	/*
    	 * If one edge is EdgeType.DIRECTED then set the isDirected variable to true
    	 * The flag is used determine if predecessors or neigborhs of a given vertex
    	 * should be queried during this algorithm execution.
    	 */
    	for( E e : g.getEdges()){
    		if(g.getEdgeType(e).equals(EdgeType.DIRECTED)){
    			this.isDirected = true;
    			return;
    		}// End if
    	}//End for
    	this.isDirected = false;
    }// End determineTypeOfGraph

    /**    
     *	Removes all mappings. 
     * 	
     */
    public void clear() {
    	this.color.clear();
    	this.discoveryTime.clear();
    	this.finishingTime.clear();
    	this.ordListByFinishingTime.clear();
    	this.predecessor.clear();
    } // clear
    
}// End DFSDistanceLabeler
