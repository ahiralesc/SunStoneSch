package mx.cicese.dcc.teikoku.utilities;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 *	Topological sort of a <b> directed acyclic graph </b> G=(V,E)
 *	is a linear ordering of vertices such that if there exists an
 * 	edge (u,v), then u appears before v in the linear ordering. 
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class TopologicalSort<V,E> extends DFSDistanceLabeler<V,E>{
	
	public void sort( Hypergraph<V,E> g) {
		super.labelDistances(g);
	}
	
	
}// End TopologicalSort
