package mx.cicese.dcc.teikoku.metrics.Grid;

import java.util.Map;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.GridEPState;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.metrics.MetricHelper;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

public class GridRigidMetrics extends AbstractMetric {
	/**
	 * Initialization flag
	 */
	private boolean initialized;
	
	/**
	 * Total number of jobs 
	 */
	private double n;
	
	
	/**
	 * Sum of waiting times
	 */
	private double swt;
	
	
	/**
	 * Sum of square waiting times
	 */
	private double sswt;
	
	
	
	/**
	 * Sum of weighted waiting time (a = area)
	 */
	private double swct;
	
	
	/**
	 * Sum of slowdown
	 */
	private double sbssd;
	
	
	/**
	 * Used for estimation of the competitive factor
	 *  - sja, sum of job areas (p * size)
	 *  - max_rp, largest size job
	 */
	private double sja;
	private double max_rp;
	
	/**
	 * Machine size, it accounts for all machines in the Grid
	 */
	private long m;

	
	/**
	 * C_opt
	 */
	private double C_opt;
	
	
	/**
	 * The sum of fairness
	 */
	private double sumFairness;
	
	
	/**
	 * Grid information broker
	 */
	private GridInformationBroker informationBroker;
	
	/*
	 * The minimum release time
	 */
	private double min_rt;
		
	
	public GridRigidMetrics() {
		super();
		this.informationBroker = null;
		this.initialized = false;
		this.n = 0;
		this.swt = 0;
		this.sswt = 0;
		this.swct = 0;
		this.sbssd = 0;
		this.sja = 0;	
		this.max_rp = -1;
		this.m = 0;
		this.sumFairness = 0;
		this.min_rt = Double.MAX_VALUE;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[10];
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "job_id ";							// Job ID
		values[1] = "cmax ";							// c, job completion time, sec
		values[2] = "Mean_waiting_time ";				// MWT, Mean waiting time, sec 
		values[3] = "Mean_bounded_slowdow ";			// MBS, Mean bounded slowdown, sec
		values[4] = "Sum_weighted_completion_time ";	// SWCT, Sum of weighted completion time, sec
		values[5] = "Competitive_factor ";				// rho, Competitive factor
		values[6] = "fairness ";						// Jain fairness index
		values[7] = "utilization ";						// Utilization
		values[8] = "rt_utilization ";					// Real time utilization
		values[9] = "MW ";								// Energy consumption in mega Watts (MW)
		
		return values;
	} // End getHeader
	
	
	
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			//Only calculate the new Value if the metric is the metric at the release site
			if (job.getReleasedSite()==this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			}
		}
	}// End deliverCompletedEvent
	
	
	
	private void handleEvent(Job job, long timestamp) {
		double fairness = 0, sumSlots = 0, kWh = 0;
				
		/* 
		 * If machines sizes are not known query them
		 */
		if(!initialized) {
			informationBroker = (GridInformationBroker)super.getSite().getInformationBroker();
			EndPointData stateData = informationBroker.getPublicInformation(EntityType.STATUS,null,null);
			this.m = ((GridEPState)stateData).size;
		}
		
		// Compute the current utilization and energy consumption
		
		Map<UUID, EndPointData> statData = informationBroker.poll(EntityType.STATUS, null, null);
		for(EndPointData machineState : statData.values()){
			sumSlots += ((ComputeEPState) machineState).usedSlots;
			kWh += ((ComputeEPState) machineState).energyConsumpion;
		}

		
		Object[] values = this.getLatestValuesPrototype();
		MetricHelper mh = new MetricHelper();

		
		this.n++;
		double p = TimeHelper.toSeconds(job.getDuration().distance());
		double c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double size = ((SWFJob) job).getRequestedNumberOfProcessors();
		double wt = mh.p_wait_time((SWFJob)job);
		
		
		
		swt += wt;									// Sum of waiting times
		sswt += (wt * wt);							// Sum of square waiting times
		sbssd += ((c-r) / Math.max(10, p));  		// Sum bounded slowdown
		swct += c * (p * size);						// SWCT, Sum of weighted completion time, sec
		// Estimation of the competitive factor
		if( (r + p) > max_rp )				// Search and select the largest job (Before option 1: r+p; option 2: r + (c-r))
			max_rp = (r + p);				// option 3: r + p + wt. Option 4: r + p + (swt/m) QUITAR TIEMPO DE ESPERA
		sja += (p * size);							// Sum of job areas
		C_opt = Math.max(max_rp, (sja/m));			// The estimated optimal make span
		// Computation of the fairness index
		if( swt == 0 || sswt == 0) {
			fairness = 1;
		} else {
			fairness = (swt * swt) / ( n * sswt);
		}
		
		if( r < min_rt )
			min_rt = r;
		
		this.sumFairness += fairness;
		
		double MW = (kWh/1000000);
		
		double utilization = (sumSlots / m) * 100;
		values[0] = ((SWFJob)job).getJobNumber();
		values[1] = (long) c;			// c, job completion time, sec
		values[2] = (1/n) * swt;		// AWT, Mean waiting time,sec
		values[3] = (1/n) * sbssd;		// MBS, Mean bounded slowdown, sec
		values[4] = swct; 				// SWCT, Sum of weighted completion time, sec
		values[5] = (c / C_opt);		// rho, Competitive factor. (greater than 1) worst case, 1 (best case)
		values[6] = (sumFairness / n);	// Raj Jain fairness index. (1/n) is worst case and 1 (best case)
		values[7] = sja / ( (c-min_rt) * m);		// Over all utilization
		values[8] = utilization;		// Real Time utilization
		values[9] = MW;				// The machine energy consumption in kWh

		super.setLatestValues(values);
		super.manualMakePermanent();
	} // End handleEvent

}