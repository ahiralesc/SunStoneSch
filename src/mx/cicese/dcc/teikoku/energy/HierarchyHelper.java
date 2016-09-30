package mx.cicese.dcc.teikoku.energy;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import de.irf.it.rmg.core.util.time.Instant;

import mx.cicese.dcc.teikoku.utilities.Color;

import edu.uci.ics.jung.graph.DirectedGraph;

public final class HierarchyHelper {
	
	
	/**
	 * Returns the set of processors that have off state cores
	 * 
	 * @param hierarchy, the object containing the energy model
	 * @return  a list of processors
	 */
	public static LinkedList<Component> getOffCoreProcessors(DirectedGraph<Component,Number> hierarchy) {
	
		LinkedList<Component> l = new LinkedList<Component>();
		
		Collection<Component> processors = hierarchy.getVertices();
		for(Component r : processors) {
			if( r.get(State.off) > 0 && hierarchy.outDegree(r) == 0)
				l.add(r);
		}
		return l;
	
	}
	
	
	
	/**
	 * Returns the set of processors that have idle state cores
	 * 
	 * @param hierarchy, the object containing the energy model
	 * @return a list of processors
	 */
	public static List<Component> getIdleCoreProcessors(DirectedGraph<Component,Number> hierarchy) {
		LinkedList<Component> l = new LinkedList<Component>();
		
		Collection<Component> processors = hierarchy.getVertices();
		for(Component r : processors) {
			if( r.get(State.idle)> 0 && hierarchy.outDegree(r) == 0)
				l.add(r);
		}
		return l;
		
	}
	
	
	
	/**
	 * Converts a list of resources to a list of reserved resources
	 * 
	 * @param processors, the list of resources
	 * @return, a list of reserved resources
	 */
	public static List<RsvCoreSet> getIdleCoreRsvProcessors(List<Component> processors) {
		LinkedList<RsvCoreSet> l = new LinkedList<RsvCoreSet>();
		
		for(Component r : processors) {
			if( r.get(State.idle)> 0) {
				RsvCoreSet rsv = new RsvCoreSet(r, r.get(State.idle), State.idle);
				l.add(rsv);
			}
		}
		return l;
		
	}

	
	
	/**
	 * Returns the set of processors whose sum of idle and off cores is greater than zero
	 *  
	 * @param hierarchy, the object containing the energy model
	 * @return a list of processors
	 */
	public static List<Component> getProcessors(DirectedGraph<Component,Number> hierarchy) {
	LinkedList<Component> l = new LinkedList<Component>();
		
		Collection<Component> processors = hierarchy.getVertices();
		for(Component r : processors) {
			if( (r.get(State.idle) + r.get(State.off)> 0) && hierarchy.outDegree(r) == 0)
				l.add(r);
		}
		return l;
	}
	
	
	
	/**
	 * Returns the root node of the rooted k-Tree 
	 * 
	 * @param hierarchy, the object containing the energy model
	 * @return the root node of the energy model tree
	 */
	public static Component getRoot(DirectedGraph<Component,Number> hierarchy) {
		Component root = null;
		
		Collection<Component> processors = hierarchy.getVertices();
		for(Component r: processors)
			if(hierarchy.inDegree(r) == 0)
				root = r;
		
		return root;
	}
	
	
	/**
	 * Estimates the maximum delay (in milliseconds) required to turn on
	 * a resource that has been allocated to a job
	 * 
	 * @param rcs, the set of processors assigned to the job
	 * @param rt, the instance of time the job arrived
	 * @return the maximum delay required to turn on a resource
	 */
	public static long getMaxActivationDelay(List<RsvCoreSet> rcs, Instant rt) {
		long maxDelay = -1;
		
		// Determine the maximum delay
		for(RsvCoreSet cs : rcs) 
			if( cs.processor.getColor().equals(Color.WHITE))
				maxDelay = visit(cs.processor, maxDelay);
		
		// Color resource white
		for(RsvCoreSet cs : rcs)
			color(cs.processor);
		
		return maxDelay;
	}
	
	
	/**
	 * Traverses the resources hierarchy from a leaf node (processor)
	 * to the root (site) and computes the maximum delay required to
	 * turn on a resource 
	 * 
	 * @param p, a leaf resource (processor)
	 * @param maxDelay, the maximum delay found
	 * @return the maximum delay required to turn on a resource 
	 */
	private static long visit(Component p, long maxDelay) {
		if( p == null)
			return maxDelay;
		
		if( maxDelay < p.getOnDelay()) {
			maxDelay = p.getOnDelay();
			p.setColor(Color.GRAY);
			maxDelay = visit(p.getParent(),maxDelay);
		}
		
		return maxDelay;
	}
	
	
	/**
	 * Used to restore the color of resources
	 * 
	 * @param p, a processor resource
	 */
	public static void color(Component p) {
		if( p != null ) 
			if(!p.getColor().equals(Color.WHITE)) {
				p.setColor(Color.WHITE);
				color(p.getParent());
			}
	}
	
	
	
}
