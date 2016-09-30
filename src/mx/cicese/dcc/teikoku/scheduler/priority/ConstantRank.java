package mx.cicese.dcc.teikoku.scheduler.priority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import mx.cicese.dcc.teikoku.scheduler.grid.strategy.composite.CompositeStgyHelper;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.utilities.CriticalPath;
import mx.cicese.dcc.teikoku.workload.job.Edge;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import mx.cicese.dcc.teikoku.workload.job.Vertex;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class ConstantRank<V extends Vertex,E extends Edge> implements Priority<V,E> {
	/**
	 * The Absolute Earliest Start Time (AEST) 
	 */
	private Map<SWFJob,Number> AEST;
	
	
	/**
	 * The Absolute Latest Start Time (ALST) 
	 */
	private Map<SWFJob,Number> ALST;
	
	
	/**
	 * The difference between ALST - AEST 
	 */
	private Map<SWFJob,Number> D;
	
	
	/**
	 * The ranking strategy
	 */
	private DownwardRank<SWFJob,Precedence> downwardLabeler;
    private UpperRank<SWFJob,Precedence> upperLabeler;
    

	
	/**
	 * The priority name
	 */
	private String name = "ConstantRank";
	
	
	/**
	 * The computed upper ranks
	 */
	private Map<V,Number> rank;
	
	
	/**
	 * A temporary data structure used to buffer AEST and ALST information
	 */
	private JobControlBlock jcb;
	
	
	public ConstantRank() {
    	this.AEST =	new HashMap<SWFJob,Number>();
	    this.ALST =	new HashMap<SWFJob,Number>();
	    this.D  = 	new HashMap<SWFJob,Number>();
		this.downwardLabeler = new DownwardRank<SWFJob,Precedence>();
    	this.upperLabeler = new UpperRank<SWFJob,Precedence>();
    	this.rank = new HashMap<V,Number>();
    	this.jcb = new JobControlBlock();
	}
	
	
	@Override
	public void clear() {
		this.AEST.clear();
	    this.ALST.clear();
	    this.D.clear();
		this.downwardLabeler.clear(); 
    	this.upperLabeler.clear();
    	this.rank.clear();
	}


	@SuppressWarnings("unchecked")
	@Override
	public void compute(Hypergraph<V, E> g) {
		clear();
		setRanking((Hypergraph<SWFJob,Precedence>)g);
		buildClusters((Hypergraph<SWFJob,Precedence>)g);
	}


	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public Map<V, Number> getRanking() {
		return this.rank;
	}	
	
	
	
	/* ************************ Supporting routines ****************************** */
	
	/**
	 * Estimates the upper and downward labels for tHE DAG g
	 * 
	 * @param g the DAG (job)
	 * @param jcb the job control block
	 */
    private void setRanking(Hypergraph<SWFJob,Precedence> g){
    	downwardLabeler.clear();
    	downwardLabeler.compute(g);
		Map<SWFJob,Number> down = downwardLabeler.getRanking();
        jcb.setOrdRanking(CompositeStgyHelper.sortDecreasing(down));
        jcb.setRanking("downward",down);
        
        upperLabeler.clear();
		upperLabeler.compute(g);
        Map<SWFJob,Number> up = this.upperLabeler.getRanking();
        jcb.setRanking("upward",up);
    }
    
    
    
	/**
	 * Clusters jobs, the maximum label is identified in each cluster, all jobs in the cluster are set with the same label
	 * @param job
	 * @param jcb
	 */
	private void buildClusters(Hypergraph<SWFJob,Precedence> g) {
		LinkedList<Job> cluster = null;
		Hypergraph<SWFJob,Precedence> w;
				    	
		w = g;
		do{
			Job remove = null;
			this.AEST.clear();
			this.ALST.clear();
			this.D.clear();
		
			long cp = estimateAESTandALST(g,jcb);
			cluster = getNextCluster(g);
			g = pruneTheDAG(g,cluster, w);
		   	
			for(Job m : cluster) {
				if(((SWFJob)m).getJobNumber() == 0)
					remove = m;
			} //End for
			
			if( remove != null)
				cluster.remove(remove);
			
			setRanking(g);
			
			// Label all tasks in cluster with the cost of the 
			// Critical path
			setClusterValues(cluster, cp);
			
		}while(D.size() != 0);
	
	}
	
	
	
	/**
	 * Sets the label for all jobs in cluster. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void setClusterValues(LinkedList<Job> M, long value) {
		for(Job s : M)
			this.rank.put((V)s, value);
	}
	
	
	/**
	 * Estimates the AEST, ALST, and the difference ALST - AEST
	 * 
	 * @param g the graph (workflow)
	 * @param jcb the Job control block {@link JobControlBlock}
	 * 
	 * @return the critical path length
	 */
   
	private long estimateAESTandALST(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb) {
		long cpLength;
		Map<SWFJob,Number> upwardRank = new HashMap<SWFJob,Number>();
		Map<SWFJob,Number> downwardRank = new HashMap<SWFJob,Number>();
			 
		upwardRank.putAll(jcb.getRanking("downward")); 
		downwardRank.putAll(jcb.getRanking("upward")); 
	    AEST.putAll(jcb.getRanking("upward")); 
	    CriticalPath<SWFJob,Precedence> cp = new CriticalPath<SWFJob,Precedence>();
	    cp.compute(g);
	    cpLength = (long) cp.getCriticalPathLength();
	    
	   
	    for(SWFJob j : upwardRank.keySet())
	          ALST.put(j, cpLength - upwardRank.get(j).longValue());

	    for(SWFJob j : AEST.keySet())
	    	D.put(j, ALST.get(j).longValue() - AEST.get(j).longValue());
	    
	    return cpLength;
	}
	
	
	
	
	/**
	 * Gets the next critical path cluster from the DAG (or workflow)
	 * 
	 * @param g the DAG
	 * @return the critical path cluster
	 */
	 private LinkedList<Job> getNextCluster(Hypergraph<SWFJob,Precedence> g) {
		 LinkedList<Job> cluster = new LinkedList<Job>();
		 Set<SWFJob> T = new HashSet<SWFJob>();
		 SWFJob u = null;
		 SWFJob s = null;
		 
		 for(SWFJob v : D.keySet()) 
			 if( D.get(v).longValue() == 0)
				 T.add(v);
		 		 
		 // Select the vertex in T with in degree zero
		 for(SWFJob v : T) 
			 if( g.inDegree(v) == 0 ) {
				 u = v;
				 break;
			 }
		 if( u == null)
			 System.out.println("Error");
		 //{ // AQUI!!!
			 cluster.add(u);
		 
			 while(g.outDegree(u) != 0 ){
				 Set<SWFJob> S = getSuccessors(u,g);
				 if(S.size() != 0) {
					 s = S.toArray(new SWFJob[0])[0];
					 cluster.add(s);
					 u = s;
				 } else
					 break;//End if
			 
		//	 }
		 } //End if
		 return cluster;
	 }
	 
	 
	 /**
		 * Prunes the DAG into a disjoint DAG, creates a dummy job to create a tree
		 * 
		 * @param g the DAG to prune
		 * @param cluster the jobs to erase from the DAG
		 * @param w the original DAG
		 * @return a pruned DAG
		 */
		private Hypergraph<SWFJob,Precedence> pruneTheDAG(	Hypergraph<SWFJob,Precedence> g, 
															LinkedList<Job> cluster, 
															Hypergraph<SWFJob,Precedence> w){
			// Erase Jobs that have been scheduled clustered in the past
			for(Iterator<Job> it=cluster.iterator(); it.hasNext();)
				D.remove((SWFJob)it.next());
			
			DirectedGraph<SWFJob,Precedence> G = new DirectedSparseMultigraph<SWFJob,Precedence>(); 
			for(SWFJob j : D.keySet())
				G.addVertex(j);
			
			for(SWFJob j : D.keySet())
				for(SWFJob s : w.getSuccessors(j))
					if( D.containsKey(s)){
						Precedence p = new Precedence();
						p.setCost(0);
						p.setSuccessor(s);
						p.setPredecessor(j);
						G.addEdge(new Precedence(), j, s, EdgeType.DIRECTED);
					}
					
			
			// Create a dummy job 
			SWFJob v = new SWFJob(null);
			v.setJobNumber(0);
			v.setRunTime(0);
			v.setRequestedTime(0);
			
			for(SWFJob j : D.keySet())
				if(G.inDegree(j) == 0)
					G.addEdge(new Precedence(), v, j, EdgeType.DIRECTED);
			
			return G;
		}	

	    
		
		 /**
		  *  Retrieves the successor of u, as long their ALST - AEST = 0
		  * 
		  * @param u an SWFJob
		  * @param g the graph (workflow)
		  * 
		  * @return u's successors
		  */
		 private Set<SWFJob> getSuccessors(SWFJob u, Hypergraph<SWFJob,Precedence> g) {
			 Set<SWFJob> S = new HashSet<SWFJob>();
			 
			 for(SWFJob s : g.getSuccessors(u)) 
				 if(D.get(s).longValue() == 0)
					 S.add(s);
			 
			 return S;
		 }
	    
}
