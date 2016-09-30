package mx.cicese.dcc.teikoku.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 *	Conducts level based labeling of a rooted tree. 
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@gmail.com">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */
public class Level <V,E> {

	/**
	 * The topological sort heuristic.
	 */
	private TopologicalSort<V,E> alg;
	
	/**
	 * The computed levels
	 */
	private Map<V,Number> level;
	
	/**
	 * The height of the tree
	 */
	private int height;
	
	
	/**
	 * Class constructor
	 */
	public Level(){
		alg = new TopologicalSort<V,E>();
		level = new HashMap<V,Number>();
		height = -1;
	} //Level

	
	/**
	 *	Computes the level
	 */
	public void label(Hypergraph<V,E> g) {
		alg.clear();
		alg.sort(g);
		
		List<V> tpsSet = alg.getVerticesOrdByFinishTime();
		
		//Initialize levels Does it works for Directed and Undirected????
		for(V v : tpsSet){
			level.put(v, 0);
		}//End for
		
		for(V v : tpsSet){
			int max = 0;
			for(V p : g.getPredecessors(v)){
				int cost = 1 + level.get(p).intValue(); 
				if( cost > max)
					max = cost;
			}//End for
			
			if( max > height)
				height = max;
			
			this.level.put(v, max);	
		}// End for
	}//End compute

	
	/**
	 * Returns the level in G of the given vertex
	 * 
	 * @param v, a vertex
	 * @return the level of v in G
	 */
	public int getLevel(V v) {
		return level.get(v).intValue();
	}
	
	
	/**
	 * Returns the tree height
	 * 
	 * @return the tree height
	 */
	public int getHeight() {
		return this.height;
	}
}



