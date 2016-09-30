package mx.cicese.dcc.teikoku.metrics.Grid;

import java.util.Map;
import java.util.UUID;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.WorkflowCompletedEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.metrics.MetricHelper;

public class GridWorkflowMetrics extends AbstractMetric {
	
	/**
	 * The number of scheduled composite jobs
	 */
	private double n;
	
	
	/**
	 * Sum of workflow waiting times
	 */
	private double swt;
	
	
	/**
	 * Sum of critical path slow down
	 */
	private double cps;
	
	
	/**
	 * Approximation factor (only of workflows)
	 */
	private double sumAreas;
	
	
	/**
	 * Initialization flag
	 */
	private boolean initialized;
	
	
	/**
	 * Grid information broker
	 */
	private GridInformationBroker gInfoBroker;
	
	
	/**
	 * Machine size, it accounts for all machines in the Grid
	 */
	private double m;
	
	
	/**
	 * The maximum machine clock rate
	 */
	private double maxClockRate;

	/**
	 * Sum of machine clock rates
	 */
	private double sumMachineClockRates;
	
	
	/**
	 * Class constructor
	 */
	public GridWorkflowMetrics() {
		super();
		this.n = 0;
		this.swt = 0;
		this.cps = 0;
		this.sumAreas = 0;
		this.initialized = false;
		this.m = 0;
		this.maxClockRate = Float.MIN_VALUE;
		this.sumMachineClockRates = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[5];
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "cmax ";
		values[1] = "mean_waiting_time ";		
		values[2] = "mean_crital_path_slowdown ";
		values[3] = "Approximation_factor ";
		values[4] = "utilization";
		
		return values;
	} // End getHeader
	
	
	@AcceptedEventType(value=WorkflowCompletedEvent.class)
	public void deliverCompletedEvent(WorkflowCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			//if (job.getReleasedSite()==this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			//}
		}
	}// End deliverCompletedEvent
	
	
	
	private void handleEvent(Job job, long timestamp) {
		Object[] values = this.getLatestValuesPrototype();
		
		/* 
		 * If machines sizes are not known query them
		 */
		if(!initialized) {
					
			this.gInfoBroker = (GridInformationBroker) super.getSite().getInformationBroker();
			Map<UUID,EndPointData> stateData = gInfoBroker.poll(EntityType.STATUS, null, null);
			
			/*
			 * Consult the machine size and speed
			 */
			for(UUID machine : stateData.keySet()) {
				double mi = (double) ((ComputeEPState)stateData.get(machine)).size;
				this.m += mi;
				
				/* 
				 * Set the maximum machine (site) clock rate
				 */
				double machineClockRate = ((ComputeEPState)stateData.get(machine)).clockRate;
				this.sumMachineClockRates += machineClockRate; 
				if( machineClockRate > this.maxClockRate )
					this.maxClockRate = machineClockRate;
				
			} //End for
		}
		
		
		
		MetricHelper mh = new MetricHelper();
		
		this.n++;
		swt += mh.w_getWaitingTime(job);
		double sp = mh.w_getCritalPathSlowDown(job); // No esta sumando el tiempo de liberacion.
		cps += sp;
		
		
		/* COMPUTE THE APPROXIMATION FACTOR */
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		
		/* The largest task */
		double p_max = (mh.w_getPmax(job)/this.maxClockRate) + r;
		
		/* The critical path cost */
		double cp = (mh.w_getCPC_original(job) / this.maxClockRate) + r;
		
		/* The total work of this workflow */
		double tja = mh.w_getTotalWork(job);
		this.sumAreas += tja;
		double sar = (this.sumAreas / (this.m * this.sumMachineClockRates));
		
		/* Make c_opt the maximum over the largest task, critical path cost or total work */
		double max = Math.max(p_max, cp);
		double apf = Math.max(max, sar);
				
		/* The workflow completion time */
		double cmax = mh.w_getCmax(job); 
		
		/* Grid utilization */
		double utilization = this.sumAreas / (this.m * cmax);
		
		values[0] = cmax;
		values[1] = (1/n) * swt; 
		values[2] = (1/n) * cps;
		values[3] = (cmax/apf);
		values[4] = utilization;
				
		mh.clear();
		
		// System.out.println("Workflow id : " + job.getName() + " completed");
		super.setLatestValues(values);
		super.manualMakePermanent();
	} // End handleEvent

}
