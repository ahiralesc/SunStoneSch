package mx.cicese.dcc.teikoku.utilities;


/*
* JUNG -- Java Universal Network/Graph Framework 
*
* Copyright (c) 2003-2013 by 
* CICESE Research Center, Computer Science Department and 
* CETYS University, Center for Applied Engineering, Mexico
*  
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License as published by the Free Software
* Foundation; either version 2 of the License. 
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the
*
*   Free Software Foundation, Inc.,
*   51 Franklin St, Fifth Floor,
*   Boston, MA 02110, USA
*/

import java.util.HashMap;
import java.util.Map;
import edu.uci.ics.jung.graph.Hypergraph;


/**
 * The lowest Common Ancestor (LCA) problem in trees (T) is formally defined as follows:
 * 
 * Given vertices v and u in T, query LCA_T(v,u) returns the lowest common ancestor of v 
 * and u in T, that is, it returns the node farthest from the root that is an ancestor of both v and u.
 * 
 * The heuristics consists of two phases:
 * <UL>
 *		<LI> Pre-processing phase. The partition partition heuristic splits T in sqrt(h) sections. 
 *			 T is labeled level based. The root level is assumed 0. The height h of T is the longest 
 *			 critical path. Section i contains vertices with labels i*sqrt(h) to (i-1)*sqrt(h). </LI>
 *		<LI> Query phase. Finds the ancestor that is situated on the last level of the upper next section </LI>
 *</UL>
 *			  
 * Asymptotic complexity is <O(n),O(sqrt(n))>, with n = |V(T)|, V(T) the vertex set.
 * 
 * @see Bender, M. A. & Farach-Colton, M. The LCA Problem Revisited Proceedings of the 4th Latin American Symposium on Theoretical Informatics, Springer-Verlag, 2000, 88-94 
 * @see Bender, M. A.; Farach-Colton, M.; Pemmasani, G.; Skiena, S. & Sumazin, P. Lowest common ancestors in trees and directed acyclic graphs Journal of Algorithms , 2005, 57, 75 - 94
 * 
 * @author <a href="mailto:ahiralesc@gmail.com">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */

public class TreePartition<V,E> {

	/**
	 * The level labeler
	 */
	private Level<V,E> labeler;
	
	
	/**
	 * The sections in the rooted tree 
	 */
	private Map<V,V> section;
	
	
	/**
	 * The root of the rooted tree
	 */
	private V root;
	
	
	/**
	 * The number of sections in which the rooted tree is partition
	 */
	private int numSections;
	
	
	/**
	 * Class constructor
	 */
	public TreePartition(){
		labeler = new Level<V,E>();
		section = new HashMap<V,V>();
	}
	
	
	/**
	 * Partitions the rooted tree into sections. In the first call v is equal to 
	 * the root of the tree
	 * 
	 * @param g, a rooted tree 
	 * @param v, a vertex in the rooted tree. 
	 */
	private void dfs(Hypergraph<V,E> g, V v) {
		if(labeler.getLevel(v)<numSections)
			section.put(v, root);
		else {
				if(!((labeler.getLevel(v) % numSections)==0))
					section.put(v, g.getPredecessors(v).iterator().next());
				else
					section.put(v, section.get(g.getPredecessors(v).iterator().next()));
		}
		
		for(V u : g.getSuccessors(v))
			dfs(g, u);
	}
	
	
	/**
	 * Gets the Lowest Common Ancestor (LCA) of vertexes v and u
	 * 
	 * @param g, a rooted tree
	 * @param v, a vertex in the tree
	 * @param u, a vertex in the tree
	 * @return the LCA 
	 */
	public V lca(Hypergraph<V,E> g, V v, V u) {
		while(section.get(v) != section.get(u) )
			if(labeler.getLevel(v)> labeler.getLevel(u))
				v = section.get(v);
			else
				u = section.get(u);
		
		while(v != u)
			if(labeler.getLevel(v)> labeler.getLevel(u))
				v = g.getPredecessors(v).iterator().next();
			else
				u = g.getPredecessors(u).iterator().next();
		return v;
	}
	
	
	/**
	 * Computes all LCA between vertices in the rooted tree
	 * 
	 * @param g, the rooted tree
	 * @param root, the root of the tree
	 */
	public void process(Hypergraph<V,E> g, V root) {
		// Labels the tree
		labeler.label(g);
		this.root = root;
		// Determine the number of section to partition the tree
		numSections = (int)Math.ceil(labeler.getHeight());
		// computes all LCA
		dfs(g,root);
	}
	
}
