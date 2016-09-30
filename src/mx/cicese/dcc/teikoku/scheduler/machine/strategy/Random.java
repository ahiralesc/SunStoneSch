package mx.cicese.dcc.teikoku.scheduler.machine.strategy;

import java.util.LinkedList;
import java.util.List;

import mx.cicese.dcc.teikoku.energy.Component;
import mx.cicese.dcc.teikoku.energy.RsvCoreSet;
import mx.cicese.dcc.teikoku.energy.State;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

import edu.uci.ics.jung.graph.DirectedGraph;


public class Random extends AbstractStrategy{

	
	@Override
	public List<RsvCoreSet> select(
				SWFJob job,  DirectedGraph<Component,Number> hierarchy, 
				List<Component> resources, State state) {
		
		List<RsvCoreSet> cs = new LinkedList<RsvCoreSet>();
		//List<Number> elements = new LinkedList<Number>(); // Non selected core set
			
		//Get the job size
		int reqNumCores = job.getRequestedNumberOfProcessors();
		
		
		/* 
		 * Randomly select a subset of resources. It selects either off
		 * or idle cores. First it selects all idle cores, and then it
		 * selects all off cores.
		 */
		do{
			int i = (int) (Math.random() * resources.size());
			Component processor = resources.get(i);
			resources.remove(i);
						
			// Select cores that have been not selected previously
			//if( !scs.contains(processor) ) {
				int numAvailCores = processor.get(state);
				int cores = 0;
				
				if(numAvailCores > 0 && reqNumCores > 0) {
					// If the number of available cores in the resource is less than 
					// the amount of cores required
					if( reqNumCores - numAvailCores > 0){
						cores = numAvailCores;
						reqNumCores = reqNumCores - numAvailCores;
					} else {
						cores = reqNumCores;
						reqNumCores = 0;
					}
					RsvCoreSet rsvCS = new RsvCoreSet(processor, cores, state);
					cs.add(rsvCS);
					//scs.add(processor);
					
					/*
					 * Each time a resource is selected, the number of available and state of the resources
					 * must be updated. 
					 */
					processor.add(cores, state,0);
				}
			//}
		}while( reqNumCores > 0 && resources.size() != 0);

		return cs;
	}

		
}
