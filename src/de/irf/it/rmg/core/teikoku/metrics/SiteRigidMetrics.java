package de.irf.it.rmg.core.teikoku.metrics;

import mx.cicese.dcc.teikoku.energy.Component;
import mx.cicese.dcc.teikoku.metrics.MetricHelper;
import mx.cicese.dcc.teikoku.energy.Type;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.kernel.events.WaitTimeTimeoutEvent;
import de.irf.it.rmg.core.teikoku.metrics.AbstractMetric;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;

public class SiteRigidMetrics extends AbstractMetric {
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
	 * Machine size
	 */
	private double m;
	
	/**
	 * The sum of fairness
	 */
	private double sumFairness;

	
	/**
	 * C_opt
	 */
	private double C_opt;
	
	
	/**
	 * Mean normalized response time (mean turnaround time / mean service time) 
	 */
	private double stt; // Sum turnaround time
	private double spt; // Sum processing time
	
	
		
	/**
	 * Used to save information about the last seen job. Used when the Energy component is
	 * switch ON. The WaitTimeTimeoutEvent is trapped, however the job who released the resources
	 * completed in past time. Thus data about the most recently completed job is saved and used
	 * to complete job attributes in results.
	 */
	private double[] lastSeenJobResults = new double[15];
	
	
	/**
	 * The sum of ration of ON components
	 */
	private float sumRatioOnComponents;
	
	private float sumRatioOnCabinets;
	
	private float sumRatioOnBoards;
	
	private float sumRatioOnProcessors;
	
	private float sumRatioOnCores;
	
	private float sumIdleCoreRatio;
	
	private float sumJobFragmentationLenght;
	
	private float numFragmentedJobs;
	
	private float sumIdleCoreReallocationlength;
	
	private int numIdleCoreReallocatedJobs;
	
	
	// The total number of job completions and timeouts
	private float k;
	
	
	public SiteRigidMetrics() {
		super();
		this.initialized = false;
		this.n = 0;
		this.swt = 0;
		this.swct = 0;
		this.sbssd = 0;
		this.sja = 0;	
		this.max_rp = -1;
		this.m = 0;
		this.sumFairness = 0;
		this.sumRatioOnComponents = 0;
		this.sumRatioOnCabinets = 0;
		this.sumRatioOnBoards = 0;
		this.sumRatioOnProcessors = 0;
		this.sumRatioOnCores = 0;
		this.sumIdleCoreRatio = 0;
		this.sumJobFragmentationLenght = 0;
		this.numFragmentedJobs = 0;
		this.sumIdleCoreReallocationlength = 0;
		this.numIdleCoreReallocatedJobs = 0;
		this.k = 0;
		this.stt = 0; 
		this.spt = 0;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[21];
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.teikoku.metrics.AbstractMetric#getHeader()
	 */
	@Override
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "id ";			// the job ID
		values[1] = "cmax ";		// c, job completion time (sec)
		values[2] = "mwt ";			// MWT, Mean waiting time (sec) 
		values[3] = "mbs ";			// MBS, Mean bounded slowdown (sec)
		values[4] = "swct ";		// SWCT, Sum of weighted completion time (sec)
		values[5] = "cf ";			// rho, Competitive factor
		values[6] = "fairness ";	// Raj Jain fairness index
		values[7] = "utilization ";			// U, utilization
		values[8] = "nqj ";			// Number of queued jobs
		values[9] = "ec ";			// Energy Consumption
		values[10] = "aoCom ";		// AOComp, Avrg. Ratio of ON components
		values[11] = "aoCab ";		// AOCab, Avrg. Ratio of ON cabinets
		values[12] = "aob ";		// AOB, Avrg. Ratio of ON boards
		values[13] = "aop "; 		// AOP, Avrg. Ratio of ON processors.
		values[14] = "aoCor	";		// AOCor, Avrg. Ratio of ON cores.
		values[15] = "aicr ";		// AICR, Avrg. Idle core reallocation
		values[16] = "ajf ";		// AJF, Avrg. Job fragmentation
		values[17] = "aicrl ";		// AICRL, Avrg. Idle Core Reallocation length 
		values[18] = "anfj ";		// ANFJ, Avrg. Number of fragmented jobs
		values[19] = "cu ";			// CU, Current utilization
		values[20] = "mntt "; 		// MNTT, Mean Normalized Turnaround time (sec)

		return values;
	} // End getHeader
	
	
	
	@AcceptedEventType(value=JobCompletedEvent.class)
	public void deliverCompletedEvent(JobCompletedEvent event){
		Job job=event.getCompletedJob();
		
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			if (job.getProvenance().getLastCondition() == this.getSite()){
				this.handleEvent(job,DateHelper.convertToSeconds(TimeHelper.toLongValue(event.getTimestamp())));
			}
		}
	}// End deliverCompletedEvent
	
	
	
	
	@AcceptedEventType(value=WaitTimeTimeoutEvent.class)
	public void deliverCompletedEvent(WaitTimeTimeoutEvent event){
		
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			if (event.getSite() == this.getSite()){
				/* ENERGY Management start */
				if(this.getSite().getComponents().get("EnergyManager"))
					this.handleEvent();	
				/* ENERGY Management end */
			}
		}
	}// End deliverCompletedEvent
	
	
	
	
	private void handleEvent() {
		Object[] values = this.getLatestValuesPrototype();
		
		values[0] = lastSeenJobResults[0];	// job id
		values[1] = lastSeenJobResults[1];	// c, job completion time, sec
		values[2] = lastSeenJobResults[2];	// AWT, Mean waiting time,sec
		values[3] = lastSeenJobResults[3];	// MBS, Mean bounded slowdown, sec
		values[4] = lastSeenJobResults[4]; 	// SWCT, Sum of weighted completion time, sec
		values[5] = lastSeenJobResults[5];	// rho, Competitive factor
		values[6] = lastSeenJobResults[6];	// Raj Jain fairness index
		values[7] = lastSeenJobResults[7];	// Utilization
		values[8] = lastSeenJobResults[8];	// The size of the local queue 
		values[15] = lastSeenJobResults[9];	// Avrg. Ratio of idle cores reallocated
		values[16] = lastSeenJobResults[10];// Avrg. Ratio of job fragmentation
		values[17]=  lastSeenJobResults[11];// Avrg. Idle Core Reallocation length
		values[18] = lastSeenJobResults[12];// Avrg. Number of fragmented jobs
		values[19] = lastSeenJobResults[13];// The actual utilization
		values[20] = lastSeenJobResults[14];// Mean normilized turn around time
		
		/* ENERGY Management start */
		if(super.getSite().getComponents().get("EnergyManager")) {
			//Total energy consumption
			Component machine = super.getSite().getEnergyManager().getHierarchy().getRoot();
			double energyConsump = machine.getEnergyConsumption();
			
			//The fraction of components currently on
			float ratioOnComponents = 	machine.getRatioOnComponents(Type.site);
			float ratioOnCabinets	=	machine.getRatioOnComponents(Type.cabinet);
			float ratioOnBoards		=	machine.getRatioOnComponents(Type.board);
			float ratioOnProcessors =	machine.getRatioOnComponents(Type.processor);
			float ratioOnCores		=	machine.getRatioOnComponents(Type.core);
	
			this.k++;
			
			this.sumRatioOnComponents += ratioOnComponents;
			this.sumRatioOnCabinets += ratioOnCabinets;
			this.sumRatioOnBoards += ratioOnBoards;
			this.sumRatioOnProcessors += ratioOnProcessors; 
			this.sumRatioOnCores += ratioOnCores;
	
			values[9] = energyConsump;					// Energy consumption
			values[10] = (sumRatioOnComponents / k);	// Avrg. Ratio of ON components
			values[11] = (sumRatioOnCabinets / k ); 	// Avrg. Ratio of ON cabinets
			values[12] = (sumRatioOnBoards / k);		// Avrg. Ratio of ON boards
			values[13] = (sumRatioOnProcessors / k);	// Avrg. Ratio of ON processors
			values[14] = (sumRatioOnCores / k);			// Avrg. Ratio of ON cores
		} else {
			values[9] 	= -1;	// Energy consumption
			values[10] 	= -1;	// Avrg. Ratio of ON components
			values[11] 	= -1;	// Avrg. Ratio of ON cabinets
			values[12] 	= -1;	// Avrg. Ratio of ON boards
			values[13] 	= -1;	// Avrg. Ratio of ON processors
			values[14] 	= -1;	// Avrg. Ratio of ON cores
		}
		/* ENERGY Management end */
		
		super.setLatestValues(values);
		super.manualMakePermanent();	
		
		} // End handleEvent
	
	
	

	private void handleEvent(Job job, long timestamp) {
		double fairness = 0, avrReallocationLength = 0;
		
		/* 
		 * If the machine size is not known, query it
		 */
		if(!initialized) 
			m = super.getSite().getSiteInformation().getNumberOfAvailableResources(); 
		
		Object[] values = this.getLatestValuesPrototype();
		MetricHelper mh = new MetricHelper();
		double freeCores = super.getSite().getScheduler().getSchedule().getActualFreeness();
		
		this.n++;									// Number of jobs 
		double p = TimeHelper.toSeconds(job.getDuration().distance());
		double c = DateHelper.convertToSeconds(job.getDuration().getCessation().timestamp());
		double r = DateHelper.convertToSeconds(job.getReleaseTime().timestamp());
		double size = ((SWFJob) job).getRequestedNumberOfProcessors();
		double wt = mh.p_wait_time((SWFJob)job);
	
		stt += (wt + p) / p;						// Sum turnaround times
		swt += wt;									// Sum of waiting times
		sswt += (wt * wt);							// Sum of square waiting times
		sbssd += ((c-r) / Math.max(10, p));  		// Sum bounded slowdown
		swct += c * (p * size);						// SWCT, Sum of weighted completion time, sec
		// Estimation of the competitive factor
		if( (r+p) > max_rp )						// Search and select the largest job
			max_rp = (r + p);
		sja += (p * size);							// Sum of job areas
		C_opt = Math.max(max_rp, (sja/m));
		
		// Computation of the fairness index
		if( swt == 0 || sswt == 0) {
			fairness = 1;
		} else {
			fairness = (swt * swt) / ( n * sswt);
		}
		this.sumFairness += fairness; 
		
		// Number of jobs in the local queue
		int queueSize = super.getSite().getScheduler().getQueue().size();

		// The job ID
		long jid = ((SWFJob)job).getJobNumber();
		
		/* ENERGY Management start */
		if(super.getSite().getComponents().get("EnergyManager")) {
			//Total energy consumption 
			Component machine = super.getSite().getEnergyManager().getHierarchy().getRoot();
			double energyConsump = machine.getEnergyConsumption();
					
			//The fraction of components currently on
			float ratioOnComponents = 	machine.getRatioOnComponents(Type.site);
			float ratioOnCabinets	=	machine.getRatioOnComponents(Type.cabinet);
			float ratioOnBoards		=	machine.getRatioOnComponents(Type.board);
			float ratioOnProcessors =	machine.getRatioOnComponents(Type.processor);
			float ratioOnCores		=	machine.getRatioOnComponents(Type.core);
			
			// The idle core reallocation and machine reallocation rates
			float jobIdleCoreRatio = job.getRatioIdleCores();
			float jobFragmentationLenght = job.getFragmentation();
			float idleCoreReallocLength = job.getIdleCoreReallocLength(); 
			
			this.k++;
					
			this.sumRatioOnComponents += ratioOnComponents;
			this.sumRatioOnCabinets += ratioOnCabinets;
			this.sumRatioOnBoards += ratioOnBoards;
			this.sumRatioOnProcessors += ratioOnProcessors; 
			this.sumRatioOnCores += ratioOnCores;
			
			this.sumIdleCoreRatio += jobIdleCoreRatio;
			this.sumJobFragmentationLenght += jobFragmentationLenght;
			if(jobFragmentationLenght > 1)
				this.numFragmentedJobs++;
			
			if( idleCoreReallocLength > -1) {
				this.sumIdleCoreReallocationlength += idleCoreReallocLength;
				numIdleCoreReallocatedJobs++;
			}
			
			//avrReallocationLength = (numIdleCoreReallocatedJobs > 1)? (sumIdleCoreReallocationlength/numIdleCoreReallocatedJobs):0;
			avrReallocationLength = idleCoreReallocLength;
			
			values[9] = energyConsump;					// Energy consumption
			values[10] = (sumRatioOnComponents / k);	// Avrg. Ratio of ON components
			values[11] = (sumRatioOnCabinets / k ); 	// Avrg. Ratio of ON cabinets
			values[12] = (sumRatioOnBoards / k);		// Avrg. Ratio of ON boards
			values[13] = (sumRatioOnProcessors / k);	// Avrg. Ratio of ON processors
			values[14] = (sumRatioOnCores / k);			// Avrg. Ratio of ON cores
			values[15] = (sumIdleCoreRatio / n);		// Avrg. Ratio of idle cores reallocated
			values[16] = (sumJobFragmentationLenght/n); // Avrg. Ratio of job fragmentation
			values[17] = avrReallocationLength;			// Avrg. Idle Core Reallocation length
			values[18] = (numFragmentedJobs / n);		// Avrg. Number of fragmented jobs			
		} else {
			values[9] 	= -1;	// Energy consumption
			values[10] 	= -1;	// Avrg. Ratio of ON components
			values[11] 	= -1;	// Avrg. Ratio of ON cabinets
			values[12] 	= -1;	// Avrg. Ratio of ON boards
			values[13] 	= -1;	// Avrg. Ratio of ON processors
			values[14] 	= -1;	// Avrg. Ratio of ON cores
			values[15]  = -1; 	// Avrg. Ratio of idle cores reallocated
			values[16]  = -1; 	// Avrg. Ratio of job fragmentation
			values[17]	= -1;	// Avrg. Idle Core Reallocation length
			values[18]	= -1;	// Avrg. Number of fragmented jobs 
		}
		/* ENERGY Management end */
		
		double mawt = (1/n) * swt;
		double mbs = (1/n) * sbssd;
		double rho = (c / C_opt);
		double _fairness = (sumFairness / n);
		double _utilization = sja / (c * m);
		double cutilization = 1 - (freeCores / m);
		double _mntt = stt / n;
		
		values[0] = jid;				// job id
		values[1] = (long) c;			// c, job completion time, sec
		values[2] = mawt;				// AWT, Mean waiting time, sec
		values[3] = mbs;				// MBS, Mean bounded slowdown, sec
		values[4] = swct; 				// SWCT, Sum of weighted completion time, sec
		values[5] = rho;				// rho, Competitive factor
		values[6] = _fairness;			// Raj Jain fairness index
		values[7] = _utilization;		// Utilization
		values[8] = queueSize;			// The size of the local queue 
		values[19] = cutilization; 		// Current utilization
		values[20] = _mntt;				// Mean normalized turnaround time

		//avrReallocationLength = (numIdleCoreReallocatedJobs > 1)? (sumIdleCoreReallocationlength/numIdleCoreReallocatedJobs):0;

		//Save results in the a temporary array 
		lastSeenJobResults[0] = jid;
		lastSeenJobResults[1] = (long) c;
		lastSeenJobResults[2] = mawt;
		lastSeenJobResults[3] = mbs;
		lastSeenJobResults[4] = swct;
		lastSeenJobResults[5] = rho;
		lastSeenJobResults[6] = _fairness;
		lastSeenJobResults[7] = _utilization;
		lastSeenJobResults[8] = queueSize;			
		lastSeenJobResults[9] = (sumIdleCoreRatio / n);
		lastSeenJobResults[10]= (sumJobFragmentationLenght/n);
		lastSeenJobResults[11]= avrReallocationLength;
		lastSeenJobResults[12]= (numFragmentedJobs / n);
		lastSeenJobResults[13] = cutilization;
		lastSeenJobResults[14] = _mntt; 
								
		
		super.setLatestValues(values);
		super.manualMakePermanent();	
		
		} // End handleEvent

}