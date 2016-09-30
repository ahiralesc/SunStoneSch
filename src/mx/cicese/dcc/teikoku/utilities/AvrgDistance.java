package mx.cicese.dcc.teikoku.utilities;

import java.util.HashMap;
import java.util.Set;


import edu.uci.ics.jung.graph.Hypergraph;

public class AvrgDistance<V,E> {
	
	/**
	 * The current height of T
	 */
	private int height;
	
	
	/**
	 * The level labeler
	 */
	private Level<V,E> level;
	
	
	/**
	 * The average distance
	 */
	private HashMap<V,Float> distance;

	
	/**
	 * The LCA heuristic
	 */
	private TreePartition<V,E> lca;
	
	
	/**
	 * Class constructor
	 */
	public AvrgDistance(Hypergraph<V,E> g, V root){
		
		distance = new HashMap<V,Float>();
		
		level = 	new Level<V,E>();
		level.label(g);
		
		lca = new TreePartition<V,E>();
		lca.process(g, root);
	}
	
		
	private float avrgLCA(Hypergraph<V,E> g, Set<V> A, V v)   {
		
		float dist = 0;
		
		int size = (A.contains(v))? A.size()- 1 : A.size(); 
		
		for(V u : A) { 
			if( u != v)
				dist += (height - level.getLevel(lca.lca(g, v, u)));
		}
		
		dist = dist/size;		
		
		return dist;
	}
	
	
	private float avrgNormalizedLCA(Hypergraph<V,E> g, Set<V> A, V v)   {
		
		float dist = 0;
		
		int size = (A.contains(v))? A.size()- 1 : A.size(); 
		
		for(V u : A) { 
			if( u != v) {
				float x = (height - level.getLevel(lca.lca(g, v, u)));
				float distance = (float)(1 - x/height);
				dist += distance;
			}
		}
		
		dist = dist/size;		
		
		return dist;
	}
	
	public void compute(Hypergraph<V,E> g, Set<V> A) {
		this.height = level.getHeight();
		for(V v : A)
			distance.put(v, avrgLCA(g,A,v));
		
	}
	
	public void computeNormalized(Hypergraph<V,E> g, Set<V> A) {
		this.height = level.getHeight();
		for(V v : A)
			distance.put(v, avrgNormalizedLCA(g,A,v));
	}
	
	public Float getAvrgDistance(V v){
		return this.distance.get(v);
	}

}