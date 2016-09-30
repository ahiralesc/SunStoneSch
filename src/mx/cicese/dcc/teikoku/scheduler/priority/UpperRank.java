package mx.cicese.dcc.teikoku.scheduler.priority;

import edu.uci.ics.jung.graph.Hypergraph;
import mx.cicese.dcc.teikoku.utilities.TopologicalSort;
import mx.cicese.dcc.teikoku.workload.job.Vertex;
import mx.cicese.dcc.teikoku.workload.job.Edge;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 *	Estimates the upper rank of a composite job
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class UpperRank<V extends Vertex,E extends Edge> implements Priority<V,E> {

	/**
	 * The topological sort algorithm instance.
	 */
	TopologicalSort<V,E> alg;
	
	/**
	 * The computed upper ranks
	 */
	Map<V,Number> rank;
	
	/**
	 * The priority name
	 */
	String name = "UpperRank";
	
    /**
	 * Class constructor
	 */
	public UpperRank(){
		alg = new TopologicalSort<V,E>();
		rank = new HashMap<V,Number>();
	}

	/**
	 *	Computes the upper rank 
	 */
	public void compute(Hypergraph<V,E> g) {
		alg.clear();
		alg.sort(g);
		List<V> sortedList = alg.getVerticesOrdByFinishTime(); /*alg.getTopologicalSort();*/
		
		//Initialize level information
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			rank.put(it.next(), new Integer(0));
		}//End for
		
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			V v = it.next();
			double max = 0;
			for(V p : g.getPredecessors(v)){
				E e = g.findEdge(p,v);
				double cost = 	(rank.get(p).intValue() + p.getCost() +	e.getCost());
				if( cost > max)
					max = cost;
			}//End for
			this.rank.put(v, max);	
		}// End for
	}//End compute
	
	/**
	 * Getter, returns the upper ranks
	 * 
	 * @return 	the ranking of all jobs stored in a Map<V,Number>
	 */
	public Map<V,Number> getRanking() {
		return this.rank;
	} //End getTopLevel
	
	
	 /**    
     *	Removes all mappings. 
     * 	
     */
    public void clear() {
    	this.rank.clear();
    } // clear
    
    
    /**
     * Gets the priority strategy name
     */
    public String getName(){
 	   return this.name;
    }
    
}// End UpperRank 
