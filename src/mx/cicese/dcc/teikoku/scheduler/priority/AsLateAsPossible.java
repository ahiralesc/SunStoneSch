package mx.cicese.dcc.teikoku.scheduler.priority;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import mx.cicese.dcc.teikoku.utilities.TopologicalSort;
import mx.cicese.dcc.teikoku.utilities.CriticalPath;
import mx.cicese.dcc.teikoku.workload.job.Vertex;
import mx.cicese.dcc.teikoku.workload.job.Edge;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 *	Estimates the As Late as Possible Ranking of a composite job.
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class AsLateAsPossible<V extends Vertex,E extends Edge> implements Priority<V,E> {
	/**
	 * The topological sort
	 */
	private TopologicalSort<V,E> tpAlg;
	
	/**
	 * The critical path
	 */
	private CriticalPath<V,E> cpAlg;
	
	/**
	 * The computed As Late As Possible ranking
	 */
	private Map<V,Number> rank;
	
	/**
	 * The critical path length
	 */
	private double criticalPathLength;
	
	/**
	 * The priority name
	 */
	String name = "AsLateAsPossible";
	
	
	/**
	 * Class constructor
	 */
	public AsLateAsPossible(){
		tpAlg = new TopologicalSort<V,E>();
		cpAlg = new CriticalPath<V,E>();
		rank = new HashMap<V,Number>();
	}

	/**
	 *	Computes the As Late As Possible ranking
	 */
	public void compute(Hypergraph<V,E> g) {
		this.tpAlg.clear();
		this.cpAlg.clear();
		
		tpAlg.sort(g);
		List<V> sortedList = invertTopologicalOrder(tpAlg.getVerticesOrdByFinishTime()); /*tpAlg.getTopologicalSort() */
		cpAlg.compute(g);
		criticalPathLength = cpAlg.getCriticalPathLength(); 
		
		//Initialize level information
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			rank.put(it.next(), new Integer(0));
		}//End for
		
		for(Iterator<V> it = sortedList.iterator(); it.hasNext();){
			V v = it.next();
			double minFt = this.criticalPathLength;
			for(V p : g.getSuccessors(v)){
				E e = g.findEdge(v,p);
				double cost = rank.get(p).doubleValue() - e.getCost();
				if(cost < minFt)
					minFt = cost;
			}//End for
			this.rank.put(v, minFt - v.getCost()); 	
		}// End for
	}//End compute
	
	/**
	 * Getter, returns the As Late As Possible ranking
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
		}
		return reverseList;
	}// End invertTopologicalOrder	
	
	 /**    
     *	Removes all mappings. 
     * 	
     */
    public void clear() {
    	//Initialize 
    	this.criticalPathLength = 0;
    	this.rank.clear();
    } // clear
    
    
    /**
     * Gets the priority strategy name
     */
    public String getName(){
 	   return this.name;
    }
	
} // AsLateAsPossible
