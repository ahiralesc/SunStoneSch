package mx.cicese.dcc.teikoku.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import mx.cicese.dcc.teikoku.energy.Component;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.VoltageFrequencyRegulationEvent;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Distance;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Event;
import de.irf.it.rmg.sim.kuiga.Kernel;

public class DVFS {
	
	HashMap<Resource, CoreControlInformation> coreControlInformation;
	
	HashMap<Component, ProcessorControlInformation> processorControlInformation;	
	
	HashSet<Component> dirtyProcessors;
	
	private long windowSize;
	
	private long nextWindowDeadLine;
	
	private Site site;
	
	
	public class ProcessorControlInformation{
		public double frequency;
		public double excess;
	
		public ProcessorControlInformation() {
			frequency = 0;
			excess = 0;
		}
	}
	
	
	public class CoreControlInformation {
		public double procTime;
		public double frequency;
		public double excess;
		
		public CoreControlInformation(){
			procTime = 0;
			frequency = 0;
			excess = 0;
		}
	}
	
	
	
	public DVFS(ResourceBundle coreSet, Vector<Component> processors, Site site, long windowSize){
		coreControlInformation = new HashMap<Resource,CoreControlInformation>();
		processorControlInformation = new HashMap<Component, ProcessorControlInformation>();
		dirtyProcessors = new HashSet<Component>();
		
		this.site = site;
		
		// Initiates the core control information
		for(Resource core : coreSet) {
			CoreControlInformation cStatus= new CoreControlInformation();
			cStatus.procTime = 0;
			cStatus.frequency = 1;
			coreControlInformation.put(core, cStatus);
		}
		

		for(Component processor: processors){
			ProcessorControlInformation pStatus = new ProcessorControlInformation();
			pStatus.excess = 0;
			pStatus.frequency = 1;
			processorControlInformation.put(processor, pStatus);
		}

		// Loads DVFS window size data
		this.windowSize = windowSize;
		nextWindowDeadLine = windowSize;
			
		// Create and register the first event
		Instant deadline = TimeHelper.add(Clock.instance().now(), new Distance(windowSize));
		Event dvfsEvent = new VoltageFrequencyRegulationEvent(deadline,site);
		Kernel.getInstance().dispatch(dvfsEvent);
	}

	
	/**
	 * Moves the window by window size time instances.
	 * The new deadline is defined as current deadline + window size
	 * 
	 */
	public void moveWindowDeadline() {
		this.nextWindowDeadLine += this.windowSize;
	}
	
	
	
	/**
	 * Updates the utilization of the cores allocated to the job  
	 * 
	 * @param job, the completed job
	 */
	public void updateCoreUtilization(SWFJob job) {
		
		long rw = nextWindowDeadLine - windowSize; // Window start time
		
		ResourceBundle coreSet = job.getResources();
		
		long rj = job.getReleaseTime().timestamp();
		long cj = job.getCompletionTime().timestamp();
		
		for(Resource core : coreSet ){
			CoreControlInformation coreInformation = coreControlInformation.get(core);
			long duration = ( rj <= rw)? cj - rw :  cj - rj;
			coreInformation.procTime += duration;
			Component processor = core.getComponent();
			
			if(!dirtyProcessors.contains(processor))
				dirtyProcessors.add(processor);
		}
	}
	
	
	/** 
	 * Updates the core processing times of jobs whose completion time excess the window size
	 */
	private void updateCoreUtilization() {
		long windowEnd  = Clock.instance().now().timestamp();
		long windowStart = windowEnd - windowSize;
		
		Set<Job> jobs = this.site.getScheduler().getSchedule().getScheduledJobs();
		
		for(Job job : jobs){
			long jobStart = ((SWFJob)job).getReleaseTime().timestamp();
			
			ResourceBundle rb = ((SWFJob)job).getResources();
			for(Resource resource : rb) {
				long processingTime = (windowStart < jobStart)? (windowEnd - jobStart) : windowSize;
				this.coreControlInformation.get(resource).procTime += processingTime;
			}
		}
	}
	
	
	// No existe la diferencia entre tiempos de ocio generados por cedes voluntarios o 
	private void scaleFrequency() {
		double minFrequency = 0.3;
		Vector<Component> processors = this.site.getEnergyManager().getHierarchy().getProcessors();
		
		for(Component processor : processors) {
			boolean frequencyChange = false;
			double newFrequency = 0;
			double excess = processorControlInformation.get(processor).excess;
			double processingTime = getMeanProcTime(processor); // + excess
			double idle = windowSize - processingTime;
			double frequency = processorControlInformation.get(processor).frequency;
			
			// Va a varial?
			double utilization = processingTime / windowSize;
			double nextExcess = processingTime  - (frequency * windowSize);
			
			
			if(excess < 0)
				excess = 0;
				
			if( excess > idle )
				newFrequency = 1.0;
			else if( utilization > 0.7 ) {
				newFrequency = frequency + 0.2;
			} else if( utilization < 0.5) {
				newFrequency = frequency - (0.6 - utilization);
			}else {
				newFrequency = frequency;
			}
			
			if( newFrequency > 1.0) {
				newFrequency = 1.0;
			} else if( newFrequency < minFrequency) {
				newFrequency = minFrequency;
			}
			
			if( frequency != newFrequency ) {
				processorControlInformation.get(processor).frequency = newFrequency;
				frequencyChange = true;
			}
			
			processorControlInformation.get(processor).excess = nextExcess;
			
			// Update the processor completion times in case of a frequency change
			if(frequencyChange) {
				System.out.println("Must update completion times");
			}
		}
		
	}
	
	
	
	private double getMeanProcTime(Component processor){
		ResourceBundle cores = processor.getCoresSet();
		double numCores = cores.size();
		double sumProcessingTime = 0;
		
		for(Resource core: cores)
			sumProcessingTime = coreControlInformation.get(core).procTime;
		
		return (sumProcessingTime/numCores);
	}
	
		
	public void onWindowCompletion( ) {
		updateCoreUtilization();
		scaleFrequency();
	}
	

	public long getWindowSize() {
		return this.windowSize;
	}
}
