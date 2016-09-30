package mx.cicese.dcc.teikoku.scheduler.machine.strategy;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import mx.cicese.dcc.teikoku.energy.Component;
import mx.cicese.dcc.teikoku.energy.RsvCoreSet;
import mx.cicese.dcc.teikoku.energy.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import edu.uci.ics.jung.graph.DirectedGraph;

public class Increasing extends AbstractStrategy{
	
	boolean sorted;
	List<Component> sortedResources;
	
	
	public Increasing() {
		sorted = false;
		sortedResources = new LinkedList<Component>();
	}
	
	
	@Override
	public List<RsvCoreSet> select(
				SWFJob job,  DirectedGraph<Component,Number> hierarchy, 
				List<Component> resources, State state) {
		
		List<RsvCoreSet> cs = new LinkedList<RsvCoreSet>();
		
		//Get the job size
		int reqNumCores = job.getRequestedNumberOfProcessors();
				
		if(!sorted) {
			sortedResources.addAll(resources);
			Collections.sort(sortedResources, new SortResourceByLabel());
			sorted = true;
		}
		
		
		for(Component processor : sortedResources){
			int numAvailCores = processor.get(state);
			int cores = 0;
			
			if(numAvailCores > 0 && reqNumCores > 0) {
				
				// The job need less cores than those available. Do not request more.
				if( (numAvailCores - reqNumCores) > 0){
					cores = reqNumCores;
					reqNumCores = 0;
				} else {
				// Select all the available cores. Keep on requesting cores from other resources. 
					cores = numAvailCores;
					reqNumCores = reqNumCores - numAvailCores;
				}
				
				RsvCoreSet rsvCS = new RsvCoreSet(processor, cores, state);
				cs.add(rsvCS);
				
				/*
				 * Each time a resource is selected, the number of available and state of the resources
				 * must be updated. 
				 */
				processor.add(cores, state,0);
			}
			
			if( reqNumCores == 0 )
				break;
		}
		
		return cs;
	}
	
	
	public class SortResourceByLabel implements Comparator<Component> {
		public int compare(Component r1, Component r2) {
			
			int l1 = r1.getLabel(); 
			int l2 = r2.getLabel(); 
			int result = (l1 < l2 ? -1 : ((l1 == l2) ? 0 : 1));
			
			return result;
		}
	} 
}
