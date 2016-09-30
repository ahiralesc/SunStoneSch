package mx.cicese.dcc.teikoku.scheduler.grid.strategy.composite;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.priority.DownwardRank;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * TODO: Re implenent so that a workflow is allocated to the same machine always
 * 
 * @author ahiralesc
 *
 */
public class Rand extends CompositeStgy{

	/**
	 * The ranking strategy
	 */ 
	private DownwardRank<SWFJob,Precedence> rankLabeler;
	
	/**
	 * Class constructor 
	 */
	public Rand() {
		 rankLabeler = new DownwardRank<SWFJob,Precedence>();
	}// End HEFT
	
	
	/**
	 * 	Estimates the ranking of the composite job
	 * 
	 *  @param g	the composite job structure 
	 */
	public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb){
		rankLabeler.clear();
		rankLabeler.compute(g);
		jcb.setRanking("downward",rankLabeler.getRanking());
		jcb.setOrdRanking(CompositeStgyHelper.sortDecreasing(rankLabeler.getRanking()));
	}// getRanking

	
	/**
	 *	Schedules a composite job and updates the jcb 
	 * 
	 * 	@param job		the composite job to schedule
	 * 	@param jcb		a jcb
	 */
	public AllocationEntry schedule(Job job, JobControlBlock jcb) {
				
		/* Create the list of jobs to schedule */
		Hypergraph<SWFJob,Precedence> g = ((CompositeJob)job).getStructure();
		
		List<SWFJob> indJobs = CompositeJobUtils.getIndependentJobs(g);
		List<SWFJob> ordIndJobs = CompositeStgyHelper.getSortedIndpJobs(jcb.getOrdRanking(),indJobs);	
		
		/* Select the first job from the list for scheduling */
		for(Iterator<SWFJob> it = ordIndJobs.iterator(); it.hasNext();) {
			SWFJob m = it.next();
			
			// Set the state of the job
			m.getLifecycle().addEpisode(State.RELEASED);
			
			/* Update m's release time to max AFT of predecessor nodes (if any) */
			if( g.inDegree(m) != 0 ) {
				long r = DateHelper.convertToSeconds(CompositeJobUtils.getMaximumStartTime(g, m, jcb));
				m.setSubmitTime(r);
			} //End if
			
			// Gets the same site for all jobs
			UUID s = super.gridInfBroker.getKnownMachines().get(0);   
			
			/* Update the scheduling jcb */
			AllocationEntry entry = new AllocationEntry(m,s,jcb.getRanking("downward",m));
			jcb.addEntry(entry, entry.getDestination());
		}// End for	
		
		return null;
	}//End schedule
	
	

		
	/**
	 * Gets the maximum Absolute Finishing Time (AFT) of 
	 * all predecessors of a given job
	 * 
	 * @param g			a graph containing precedence constraints
	 * @param m			the successor job
	 * @param jcb		the jcb holding the AFT times 
	 * @return			the maximum AFT 
	 */
	@SuppressWarnings(value = "unchecked")
	private long predecessorsMaxAFT(Hypergraph<SWFJob,Precedence> g, 
				SWFJob m, JobControlBlock jcb) {
		long max = 0;
		Set <Value> AFT = new HashSet<Value>();
		
		if(g.getPredecessors(m).size()!= 0) {
			for(SWFJob p : g.getPredecessors(m))
				AFT.add(new Value(p,jcb.getAFT(p)));
			max = ((Value)Collections.max(AFT, new HEFT.ValueComparator())).getValue().longValue();
		}// End if
		
		return max;
	}// End predecessorsMaxAFT

	
	/**
	 * Borrar
	 */
	public void initialize() {}
	
	
	/* 
	 * Auxiliary classes used for sorting or getting the minimum or maximum value
	 * in a given set.
	 */
	static class ValueComparator implements Comparator {
		public int compare(Object obj1, Object obj2) {
			if(!(obj1 instanceof Value) || !(obj2 instanceof Value))
				throw new ClassCastException();
			long value = ((Value)obj1).getValue().longValue() - ((Value)obj2).getValue().longValue();
			return (new Double(Math.floor(value))).intValue();
		}
	}
	
	private static class Value{
		private Object v;
		private Number n;
		
		public Value(Object v, Number n){
			this.v = v;
			this.n = n;
		}
		
		public Number getValue() {
			return this.n;
		}
		
		public Object getKey() {
			return this.v;
		}
	} //End Value
}
