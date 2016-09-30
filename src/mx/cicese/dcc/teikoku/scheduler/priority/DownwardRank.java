package mx.cicese.dcc.teikoku.scheduler.priority;

import edu.uci.ics.jung.graph.Hypergraph;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import mx.cicese.dcc.teikoku.workload.job.Vertex;
import mx.cicese.dcc.teikoku.workload.job.Edge;
import mx.cicese.dcc.teikoku.utilities.TopologicalSort;

/**
 *	Estimates the downward rank of a composite job
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class DownwardRank<V extends Vertex,E extends Edge> implements Priority<V,E> {
	
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
	String name = "DownwardRank";
	
	
	/**
	 * Class constructor
	 */
	public DownwardRank(){
		alg = new TopologicalSort<V,E>();
		rank = new HashMap<V,Number>();
	} //DownwardRank

	/**
	 *	Computes the downward rank
	 */
	public void compute(Hypergraph<V,E> g) {
		alg.clear();
		alg.sort(g);
		List<V> sortedList = invertTopologicalOrder(alg.getVerticesOrdByFinishTime()); /*alg.getTopologicalSort()*/
		
		//Initialize level information
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			rank.put(it.next(), new Integer(0));
		}//End for
		
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			V v = it.next();
			double max = 0;
			for(V p : g.getSuccessors(v)){
				E e = g.findEdge(v,p);
				double cost = e.getCost() + rank.get(p).doubleValue();
				if( cost > max)
					max = cost;
			}//End for
			this.rank.put(v, max + v.getCost());	
		}// End for
	}//End compute
	
	/**
	 * Getter, returns the downward ranks
	 */
	public Map<V,Number> getRanking() {
		return this.rank;
	} //End getTopLevel
	
	/**
	 * Inverts a given topological sorted list
	 * 
	 * @param list		a topologically ordered list
	 * @return			the list ordered in inverse order
	 */
	private List<V> invertTopologicalOrder(List<V> list) {
		List<V> reverseList = new LinkedList<V>();
		for(Iterator<V> it = list.iterator(); it.hasNext();){
			reverseList.add(0, it.next());
		} //end for
		return reverseList;
	}// End invertTopologicalOrder
	
	
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

} //DownwardRank

