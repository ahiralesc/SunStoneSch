package mx.cicese.dcc.teikoku.scheduler.grid.strategy.composite;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Instant;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.JRTEstimate;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.scheduler.plan.AllocationEntry;
import mx.cicese.dcc.teikoku.scheduler.plan.JobControlBlock;
import mx.cicese.dcc.teikoku.scheduler.priority.DownwardRank;
import mx.cicese.dcc.teikoku.scheduler.priority.UpperRank;
import mx.cicese.dcc.teikoku.utilities.CriticalPath;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.CompositeJobUtils;
import mx.cicese.dcc.teikoku.workload.job.MemberState;
import mx.cicese.dcc.teikoku.workload.job.Precedence;


public class PCH extends CompositeStgy {
		
	/**
	 * The Absolute Earliest Start Time (AEST) 
	 */
	Map<SWFJob,Number> AEST;
	
	
	/**
	 * The Absolute Latest Start Time (ALST) 
	 */
	Map<SWFJob,Number> ALST;
	
	
	/**
	 * The difference between ALST - AEST 
	 */
	Map<SWFJob,Number> D;
	

	/**
	 * The ranking strategy
	 */
	private DownwardRank<SWFJob,Precedence> downwardLabeler;
    private UpperRank<SWFJob,Precedence> upperLabeler;
    
    
    /**
	 * Used for initialization purposes
	 */
	private boolean initialized;
	
	
	/**
	 * Best cluster execution time estimates
	 */
	Hashtable<String,Hashtable<SWFJob,Number>> estimates;
	Hashtable<String,UUID> assigment;
	
	
	/**
	 * The prefix name for clusters
	 */
	private String clusterName; 
	private boolean flag;
	private int i;
	

	/**
	 * Class constructor
	 */
    public PCH() {
    	this.AEST =	new HashMap<SWFJob,Number>();
	    this.ALST =	new HashMap<SWFJob,Number>();
	    this.D  = 	new HashMap<SWFJob,Number>();
    	this.downwardLabeler = new DownwardRank<SWFJob,Precedence>();
    	this.upperLabeler = new UpperRank<SWFJob,Precedence>();
    	this.estimates = null;
    	this.assigment = null;
    	this.initialized = false;
    	this.clusterName = new String("cluster");
    	this.flag = false;
    	this.i = 1;
	}// End PCH

    
   
    /**
     * Schedules or rescheduled independent jobs of a composite job
     * 
     * @param	Job, a composite job
     * @param 	JobControlBlock, the state of the composite job
     */
    public AllocationEntry schedule(Job job, JobControlBlock jcb) {
    	   	    	
    	this.i = 1;
    	
    	if(jcb.getBufferedJobs().size() == 0 && jcb.getState() != true) {
    		Hypergraph<SWFJob,Precedence> g = ((CompositeJob)job).getStructure();
    		LinkedList<Job> cluster = null;
    		Hypergraph<SWFJob,Precedence> w;
    		flag = true;
    		this.assigment = new Hashtable<String,UUID>();
    		    	
    		w = g;
    		do{
    			Job remove = null;
    			// Gets the next cluster of tasks to schedule 
    			this.AEST.clear();
    			this.ALST.clear();
    			this.D.clear();
    		
    			estimateAESTandALST(g,jcb);
    			cluster = getNextCluster(g);
    			g = pruneTheDAG(g,cluster, w);
    		   	
    			/* Get estimate for clustered jobs */
    			for(Job m : cluster) {
    				m.getLifecycle().addEpisode(State.RELEASED);
    				if(((SWFJob)m).getJobNumber() == 0)
    					remove = m;
    			} //End for
    			
    			if( remove != null)
    				cluster.remove(remove);
    			setRanking(g,jcb);
    			 
    			// Allocates the cluster to a site
    			this.allocateCluster(cluster, job, g, w, jcb);
    		}while(D.size() != 0);
    	
    		// Create allocation entries	
    		List<SWFJob> ignoreMe = this.createAllocationEntries(job, i, jcb);

    		// Buffer jobs that are not ready o be schedule
    		this.bufferJobs(ignoreMe, jcb);
    	
    		// Must also store information that tells us were jobs are to be allocated in jcb
    		jcb.setEstimates(this.estimates);
    		jcb.setAssigments(this.assigment);
   		
    		//Clean all data structures
    		this.AEST.clear();
    		this.ALST.clear();
    		this.D.clear();
    		this.estimates.clear();
    		this.assigment.clear();
    		
    		//Indicates that various allocations have been all ready created.
    		jcb.setInitialize();
    		
    	} else 
    		this.reSchedule(jcb, job);
    	
    	return null;
  }//end schedule
  
   
  
  
    /**
    * Estimates the last node of cluster 
    * @param cluster
    * @param g the graph (workflow)
    * @return the last node of cluster
    */    
   private SWFJob getLastNodeOfCluster(LinkedList<Job> cluster,Hypergraph<SWFJob,Precedence> g){
	   //SWFJob lastNode = cluster.toArray(new SWFJob[0])[cluster.size()];
	   SWFJob lastNode=null;
	  // do{
	   for(Iterator<Job> it = cluster.iterator(); it.hasNext();) {
			SWFJob m = (SWFJob)it.next();
		//if(g.getPredecessors(m).size()!= 0)
	        //for(SWFJob s:g.getSuccessors(m)){
	        	if(cluster.contains(m)){
	        		lastNode=m;
	        	}//end if
	       // }//end for
	   }//end iterator
	  // }while(g.getSuccessors(lastNode).size()!=0);
	   return lastNode;
   }// end getLastNodeOfCluster   
   
   
   /**
    * Estimates the Successor of last node of cluster
    * @param lastNode
    * @param g the graph (workflow)
    * @return the Successor of last node of cluster
    */
   	private SWFJob getSucessorNode(SWFJob lastNode,Hypergraph<SWFJob,Precedence> g){
   		Hashtable<SWFJob,Number> t = (Hashtable<SWFJob,Number>) this.estimates.get("cluster1");
   		for(SWFJob suc:g.getSuccessors(lastNode))
   			if(t.containsKey(suc))
			   return suc;
	   return null;
   }//end getSucessorLastNode    

   	
	/**
	 * Estimates the AEST, ALST, and the difference ALST - AEST
	 * 
	 * @param g the graph (workflow)
	 * @param jcb the Job control block {@link JobControlBlock}
	 */
   
	private void estimateAESTandALST(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb) {
		double cpLength;
		Map<SWFJob,Number> upwardRank = new HashMap<SWFJob,Number>();
		Map<SWFJob,Number> downwardRank = new HashMap<SWFJob,Number>();
			 
		upwardRank.putAll(jcb.getRanking("downward")); 
		downwardRank.putAll(jcb.getRanking("upward")); 
	    AEST.putAll(jcb.getRanking("upward")); 
	    CriticalPath<SWFJob,Precedence> cp = new CriticalPath<SWFJob,Precedence>();
	    cp.compute(g);
	    cpLength =cp.getCriticalPathLength();
	      	    
	    //ALST - AEST para todos los nodos !!!!!!!!!!!!!!!!!!!!ESTA MAL!!!!!!!!!!!!!!!!!!!!!!!!!1
	    for(SWFJob j : upwardRank.keySet())
	          ALST.put(j, cpLength - upwardRank.get(j).longValue());

	    for(SWFJob j : AEST.keySet())
	    	D.put(j, ALST.get(j).longValue() - AEST.get(j).longValue());
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
	
	

	
	
	private void allocateCluster(	LinkedList<Job> cluster,
									Job job,
									Hypergraph<SWFJob,Precedence> g,
									Hypergraph<SWFJob,Precedence> w,
									JobControlBlock jcb
								) {
		Map<UUID,EndPointData> avail = null;
		UUID minClusterSite = null;
		UUID minSite = null;
			
				
		/* This code decides where to put each cluster */
		if(flag){
			
			
			/* Determines were to place the first cluster and stores its estimates */
			avail = super.gridInfBroker.poll(EntityType.ESTIMATE, cluster, jcb); 
			estimates = new Hashtable<String,Hashtable<SWFJob,Number>>();
			SWFJob lastNodeOfCluster = getLastNodeOfCluster(cluster,g);
			long minAEFT = Long.MAX_VALUE;
			   for(UUID s : avail.keySet()){
					long aeftAtSite = ((JRTEstimate)avail.get(s)).earliestFinishingTime.get(lastNodeOfCluster).timestamp();
					if(aeftAtSite < minAEFT) {
						minSite = s;
						minAEFT = aeftAtSite; 
					}//End if    					
				}//End for	
	
			  Hashtable<SWFJob,Number> t = new Hashtable<SWFJob,Number>();
			 for(Job j : cluster)
				 t.put((SWFJob)j, ((JRTEstimate)avail.get(minSite)).earliestFinishingTime.get((SWFJob)j).timestamp());
			 estimates.put(clusterName + i, t);
			 assigment.put(clusterName + i, minSite);
			flag = false;
		}//End if
		else {
			
			/* Determines were to place the second, third, etc. clusters and stores subsequent estimates */
			i++;
			avail = super.gridInfBroker.poll(EntityType.ESTIMATE, cluster, jcb);
			SWFJob lastNodeOfCluster = getLastNodeOfCluster(cluster,g);
			SWFJob lastNodeSucesor = getSucessorNode(lastNodeOfCluster,w);
			if(lastNodeSucesor != null) {    			
				//obtener el AFT del sucesor del ultimo nodo
				long aftLastNodeSucc =((Hashtable<SWFJob,Number>) this.estimates.get("cluster1")).get(lastNodeSucesor).longValue(); 
				//Get cluster last node absolute finishing time from all estimates    		    
				long diffClusterAFT = Long.MAX_VALUE;
				for(UUID s : avail.keySet()){
					//obtener el EFT  del ultimo nodo del cluster.
					long aftLastNode = ((JRTEstimate)avail.get(s)).earliestFinishingTime.get(lastNodeOfCluster).timestamp();
					long diff = aftLastNodeSucc - aftLastNode;  
					if( diff < diffClusterAFT ) {
						minClusterSite = s;
						diffClusterAFT = diff; 	
					}// End if
				} // End for
			} else {
				
				// There is a cluster that does not have a successor in the critical path
				long minAEFT = Long.MAX_VALUE;
				
				for(UUID s : avail.keySet()){
					long aeftAtSite = ((JRTEstimate)avail.get(s)).earliestFinishingTime.get(lastNodeOfCluster).timestamp();
					if(aeftAtSite < minAEFT) {
						minClusterSite = s;
					}//End if    					
				}//End for	
			}
			
			 	//Must update AFT of all nodes that have been previously scheduled
			 	int numClusters = this.assigment.keySet().size();
			 	for(int c=1; c<=numClusters; c++) {
			 		Hashtable<SWFJob,Number> ci = estimates.get(clusterName+c);
			 		for(SWFJob j : ci.keySet()){
			 			Instant eft = ((JRTEstimate)avail.get(minClusterSite)).earliestFinishingTime.get(j);
			 			if(eft != null) {
			 				ci.put(j,eft.timestamp());
			 			} //End if
			 		}//End for
			 		estimates.put("cluster"+c, ci);
			 	}//End for
			 	
			
			 Hashtable<SWFJob,Number> t = new Hashtable<SWFJob,Number>();
			 for(Job j : cluster)
				 t.put((SWFJob)j, ((JRTEstimate)avail.get(minClusterSite)).earliestFinishingTime.get((SWFJob)j).timestamp());
			 estimates.put(clusterName + i, t);
			 assigment.put(clusterName + i, minClusterSite);
	    		}// End where to allocate each cluster
	}
	
	
	

	private List<SWFJob> createAllocationEntries(Job job, int i, JobControlBlock jcb) {
    	List<SWFJob> ignoreMe = new LinkedList<SWFJob>(); 
    	// Only send jobs that are independent first
    	List<SWFJob> indpJobs = CompositeJobUtils.getIndependentJobs(((CompositeJob)job).getStructure());
    	UUID siteUUID = null;
    	int numClusters = this.assigment.keySet().size();
    	for(SWFJob j : indpJobs) {
    		ignoreMe.add(j);
    		for(int c=1; c<=numClusters; c++) {
    			Set<SWFJob> ci = estimates.get(clusterName+c).keySet();
    			if(ci.contains(j)) {
    				siteUUID = assigment.get(clusterName+i);
    				break;
    			}
    		}
    		AllocationEntry entry = new AllocationEntry(j,siteUUID,-1);
			jcb.addEntry(entry, siteUUID);
    	}
    	return ignoreMe;
	}
	
	
	
	private void bufferJobs(List<SWFJob> ignoreMe, JobControlBlock jcb) {
		int numClusters = this.assigment.keySet().size();    	
    	
		// Now buffer the jobs that a not independent
   		for(int c=1; c<=numClusters; c++) {
   			Hashtable<SWFJob,Number> ci = estimates.get(clusterName+c);
   			for(SWFJob j: ci.keySet()) {
   				if(!ignoreMe.contains(j)) 
   					jcb.bufferJob(j,DateHelper.convertToSeconds(ci.get(j).longValue())-j.getRunTime());
   			}
    	}
	}
	
	
	private void reSchedule(JobControlBlock jcb, Job job) {
		// Select next set of independent jobs and send them to their preallocated site
		if( jcb.getBufferedJobs().size() != 0 ) {
			
			List<SWFJob> indpJobs = CompositeJobUtils.getIndependentJobs(((CompositeJob)job).getStructure());
			HashMap<SWFJob,Number> b = (HashMap<SWFJob, Number>) jcb.getBufferedJobs();
			
			// Select the minimum release of all jobs
			long min = Long.MAX_VALUE;
			for(SWFJob j : indpJobs) {
				long t = b.get(j).longValue();
				if(t<min)
					min = t;
			}
			
			//Now select the set of jobs with min release time, and send them to the kernel
			UUID siteUUID = null;
			List<SWFJob> removeMe = new LinkedList<SWFJob>();
			for(SWFJob j : indpJobs) {
				if(b.get(j).longValue() == min) {
	
					int numClusters = jcb.getAssigment().keySet().size(); 
			    	for(int c=1; c<=numClusters; c++) {
			    			Set<SWFJob> ci = jcb.getEstimates().get("cluster"+c).keySet();
			    			if(ci.contains(j)) {
			    				siteUUID = jcb.getAssigment().get("cluster"+c);
			    				break;
			    			}
			    	}
					AllocationEntry entry = new AllocationEntry(j,siteUUID,-1);
	    			jcb.addEntry(entry, siteUUID);
	    			removeMe.add(j);
				}
			}// End for
			
			//Reset state of NOT EXPLORED to jobs that are not scheduled
			for(SWFJob j : indpJobs) {
				if(!removeMe.contains(j))
					j.setMemberState(MemberState.NOT_EXPLORED);
			}
			
			
			//Now delete the jobs that have been scheduled so they are no longer considered
			for(SWFJob j: removeMe) {
				jcb.removeBufferedJob(j);
			}
		}
	}
	


	
	/**
	 * Estimates the upper and downward labels for tHE DAG g
	 * 
	 * @param g the DAG (job)
	 * @param jcb the job control block
	 */
    public void setRanking(Hypergraph<SWFJob,Precedence> g, JobControlBlock jcb ){
    	
    	downwardLabeler.clear();
    	downwardLabeler.compute(g);
		Map<SWFJob,Number> down = downwardLabeler.getRanking();
        jcb.setOrdRanking(CompositeStgyHelper.sortDecreasing(down));
        jcb.setRanking("downward",down);
        
        this.upperLabeler.clear();
		this.upperLabeler.compute(g);
        Map<SWFJob,Number> up = this.upperLabeler.getRanking();
        jcb.setRanking("upward",up);
    }
    
    
       
	
    @Override
	public void initialize() {
		this.initialized = true; 
	}

    /**
     * Internal data structure used in ... 
     * 
     */
    public class Entry {
		public Job job;
		
		public long minAEFT;
		
		public Site site;
	}
    
}



