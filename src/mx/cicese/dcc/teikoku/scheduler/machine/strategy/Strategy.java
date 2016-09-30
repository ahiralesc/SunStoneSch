package mx.cicese.dcc.teikoku.scheduler.machine.strategy;

import java.util.List;

import mx.cicese.dcc.teikoku.energy.Component;
import mx.cicese.dcc.teikoku.energy.RsvCoreSet;
import mx.cicese.dcc.teikoku.energy.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import edu.uci.ics.jung.graph.DirectedGraph;


public interface Strategy {
	
	public List<RsvCoreSet> select(
			SWFJob job, DirectedGraph<Component,Number> hierarchy, 
			List<Component> resources, State state);
	
}
