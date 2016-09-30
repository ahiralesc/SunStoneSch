package mx.cicese.dcc.teikoku.metrics;

import java.util.Hashtable;

import mx.cicese.dcc.teikoku.utilities.CriticalPath;
import mx.cicese.dcc.teikoku.workload.job.CompositeJob;
import mx.cicese.dcc.teikoku.workload.job.Precedence;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import edu.uci.ics.jung.graph.Graph;

public final class MetricHelper {

	/**
	 * A critical path estimation strategy.
	 */
	private CriticalPath<SWFJob,Precedence> cpAlg;
	
	
	/**
	 * A critical path estimation strategy. Used to compute the static 
	 * critical path of the workflow using task runtimes found in the
	 * original log 
	 */
	private CriticalPath<SWFJob,Precedence> cpAlgOriginal;
	
	
	/**
	 * Avoids recomputing the critical path
	 */
	private boolean cpComputed = false;
	
	
	/**
	 * Avoids recomputing the critical path considering the original task run times
	 */
	private boolean cpComputedOriginal = false;
	

	/**
	 * Composite job cmax
	 */
	private double w_cmax;

	
	
	/**
	 * Class constructor
	 */
	public MetricHelper() { 
		cpAlg = new CriticalPath<SWFJob,Precedence>();
		cpAlgOriginal = new CriticalPath<SWFJob,Precedence>();
		w_cmax = -1;
		cpComputed = false;
	} // End MetricHelper
	
	
		
	
	/**
	 * Gets the parallel job waiting time
	 * 
	 * @param job, a parallel job
	 * @return the parallel job waiting time
	 */
	public double p_wait_time(SWFJob job ) {
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double processingTime = DateHelper.convertToSeconds(job.getDuration().distance().length());

		return (endTime - processingTime - releaseTime);
	} // End waitingTime
	
	
	
	/**
	 * Gets the parallel job completion time
	 * 
	 * @param job, a parallel job
	 * @return the parallel job completion time
	 */
	public double p_getCmax(Job job) {
		double endTime = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		return endTime;
	} // getCmax
	
		
	
	/**
	 * Gets the composite job maximum completion time
	 * 
	 * @param job, a composite job
	 * @return the composite job completion time
	 */
	public double w_getCmax(Job job) {
		double maxCompletionTime = -1;
		
		if (w_cmax == -1) {
			for(SWFJob j:((CompositeJob) job).getStructure().getVertices()){
				double completionTime = DateHelper.convertToSeconds(j.getDuration().getCessation().timestamp());
				if (completionTime > maxCompletionTime)
					maxCompletionTime = completionTime;				
			} // End for
			w_cmax = maxCompletionTime;
		} else 
			maxCompletionTime = w_cmax;
		
		return maxCompletionTime;
	}

	
	/**
	 * Gets the composite job aggregate mean waiting time
	 * 
	 * @param job, a composite job
	 * @return the composite job mean waiting time
	 */
	public double w_getWaitingTime(Job job) {
		double cpProcTime = this.w_getCriticalPathCost(job);
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double completionTime = this.w_getCmax(job);
		double waitingTime = completionTime - cpProcTime - releaseTime; 
		return waitingTime;
	}
	
	
	/**
	 * Gets the composite job speedup.  
	 * - It assumes their is no waiting times between jobs with precedence
	 *   constraints
	 * 
	 * @param job, a composite job
	 * @return
	 */
	public double w_getCritalPathSlowDown(Job job) {
		double cmax = this.w_getCmax(job);
		double cpCost = this.w_getCriticalPathCost(job);
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double delay = cmax - releaseTime - cpCost;
		
		double cps = 1 + (delay / cmax);
		return cps; 
	} //End getSpeedUp
	
		
	
	/**
	 * Gets the composite job length ratio
	 * - It assumes their is no waiting times between jobs with precedence
	 *   constraints
	 *   
	 * @param job, a composite job
	 * @return the length ratio of the composite job
	 */
	public double w_getWLR(Job job) {
		double cmax = this.p_getCmax(job);
		double cpCost = this.w_getCriticalPathCost(job);
		double releaseTime = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
		double wlr = cmax /(cpCost + releaseTime);
		
		return wlr;
	} // End getSLR
	
	
	
	// Composite job HELPER methods
	
	/**
	 * Computes a composite job critical path cost. It does not include 
	 * the workflow release time. 
	 * 
	 * The critical path cost is based on job run times only. Communication
	 * cost is not considered. Futhermore, run times might be different from
	 * those in the original log. A job run time might decrease or increase. 
	 * The target machine determines a job run time. 
	 * 
	 * @param job,
	 * @return
	 */
	public double w_getCriticalPathCost(Job job) {
		double cp = 0;
		
		if(!this.cpComputed) { 
			cpAlg.compute(((CompositeJob) job).getStructure());
			this.cpComputed = true;
		} // End if
		//CP cost in seconds
		cp = cpAlg.getCriticalPathLength();
		
		return cp;
	} // End getCriticalPathCost

	
	
	/**
	 * Computes a composite job critical path cost. The critical path is static.
	 * The task execution times correpond to those found in the workload trace.
	 * 
	 * Communication costs are not considered. 
	 *  
	 * @param job,
	 * @return
	 */
	public double w_getCPC_original(Job job) {
		double cp = 0;
				
		Graph<SWFJob, Precedence>  w = ((CompositeJob) job).getStructure();
		
		if(!this.cpComputedOriginal) {
			Hashtable<SWFJob,Number> jobSet = new Hashtable<SWFJob,Number>();
			/* Backup the task run times */
			for(SWFJob j : w.getVertices()) {
				jobSet.put(j, j.getRunTime());
				j.setRunTime(j.getOriginalRunTime());
			}
			this.cpAlgOriginal.compute(w);
			this.cpComputedOriginal = true;
			
			/* Restore runtimes */
			for(SWFJob j : w.getVertices()) {
				j.setRunTime(jobSet.get(j).longValue());
			}
		}		
		//CP cost in seconds
		cp = cpAlgOriginal.getCriticalPathLength();
		
		return cp;
	} // End getCriticalPathCost

	
	/**
	 * Computes the total area of the workflow
	 * The size (or number of resources) the job uses is not considered. Since 
	 * jobs are sequential. If jobs are paralle, then size must be inclueded.
	 * 
	 * @param job,
	 * @return
	 */
	public double w_getTotalWork(Job job) {
		double totalWork = 0;
		
		for(SWFJob j :((CompositeJob) job).getStructure().getVertices()) {
			double work = j.getOriginalRunTime();
			totalWork += work;
		}
		
		return totalWork;
	} // End getCriticalPathCost
	
	
	/**
	 * Gets the maximum size task in Job
	 * 
	 * @param job, a workflow
	 * 
	 * @return the maximum execution time task
	 */
	public double w_getPmax(Job job) {
		double p_max = Double.MIN_VALUE;
		
		for(SWFJob j:((CompositeJob) job).getStructure().getVertices()){
			double rt = j.getOriginalRunTime();
			if( rt > p_max ) 
				p_max = rt;
		}
		return p_max;
	}
	
	
	/**
	 * Clears critical path estimation data structures
	 */
	public void clear() {
		this.cpAlg.clear();
		this.cpComputed = false;
	}// End clear
	
} // End MetricHelper
