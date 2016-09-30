package mx.cicese.dcc.teikoku.energy;


import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import mx.cicese.dcc.teikoku.processor.DVFS;

import de.irf.it.rmg.core.teikoku.kernel.events.WaitTimeTimeoutEvent;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleSortedSetImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.Kernel;
import de.irf.it.rmg.sim.kuiga.Event;



public class EnergyBroker {
	
	
	/**
	 * The energy hierarchy model
	 */
	Hierarchy hierarchy;
	
	
	/**
	 * 
	 * The containing site UUID
	 */
	Site site;
	

	/**
	 * The Machine State at a given instance of time (machineState).
	 * It stores cores that are being used or have been recently released by a job. That 
	 * is active or idle cores. 
	 */
	private ResourceBundle machineState; 
	
	
	private DVFS processorDVFS;
	
	
	/**
	 * The table of events (TOE)
	 */
	private Hashtable<Resource, WaitTimeTimeoutEvent> TOE;
	
		
	/**
	 * Class constructor
	 */
	public EnergyBroker(Site site) {
		this.site = site;
		this.hierarchy = new Hierarchy(site);
		this.machineState = new ResourceBundleSortedSetImpl();
		this.TOE = new Hashtable<Resource,WaitTimeTimeoutEvent>();
		this.processorDVFS = new DVFS(site.getSiteInformation().getProvidedResources(),hierarchy.getProcessors(), site, 4); // Modificar aqui
	}
	
	
	
	/**
	 * Powers ON the cores allocated to the given job.
	 * If necessary, powers ON the components containing the cores.
	 * 
	 * @param job, a rigid job
	 */
	public void handlePowerUpEvent(SWFJob job) { 
		// Estimates total energy consumption
		computeConsumption();
		
		
		// Gets the set of cores that were selected by the scheduler
		ResourceBundle coreSet = job.getResources();
		
		
		// Aggregates the  A by processor and state
		Hashtable<Component, int[]> processors = this.aggregateCores(coreSet);
		
		// UP : AFTER TIMER UPDATE
		//this.debugCounters(processors.keySet(), "Up event after");
		
		// Updates the core counters: INCREMENTS ACTIVE CORE COUNT
		for(Component c : processors.keySet()) {
			int index = 0;
			for(Number n : processors.get(c)) {
				if( n.intValue() > 0 ) {
					if(index == State.idle.ordinal())
						c.add(n.intValue(), State.idle, 0);
					if(index == State.off.ordinal()) 
						c.add(n.intValue(), State.off, 0);
				}
				index++;
			}
		}
		
		// UP : BEFORE TIMER UPDATE
		//this.debugCounters(processors.keySet(), "Up event before");
		
		// Finds the events that contain cores selected by the scheduler
		Hashtable<Event,ResourceBundle> events = getEvents(coreSet);
		
		// Updates kernel events and TOE
		if(events.size()!=0)
			updateEvents(events,coreSet);
		
		// Updates the machine state
		for(Resource r : coreSet) {
			if( r.getState().equals(State.off) || r.getState().equals(State.idle)) {
				r.setState(State.active);
				machineState.add(r);
			}
		}

	}
	
	
	
	/**
	 * Powers OFF the cores allocated to the given job.
	 * If necessary, powers DOWN the components containing the cores.
	 * 
	 * @param job, a rigid job
	 */
	public void handlePowerDownEvent(SWFJob job) {
		boolean countComponents = false;
		
		long timeOutDelay = hierarchy.getEnergySettings().coreTimeOutDelay;
		ResourceBundle idleCoreSet = new ResourceBundleSortedSetImpl(); // The idle core set
		
		if(timeOutDelay == 0)
			countComponents = true;
		
		// Computes all components energy consumption
		computeConsumption();
		
		
		// Gets the selected set of cores
		ResourceBundle coreSet = job.getResources();
		
		
		// Aggregates cores by processor and state
		Hashtable<Component, int[]> processors = this.aggregateCores(coreSet);
		
		// DOWN : AFTER TIMER UPDATE
		// this.debugCounters(processors.keySet(), "Down event after");
		
		// Updates the core counters: INCREMENTS IDLE OR OFF CORE COUNT
		for(Component c : processors.keySet()) {
			int index = 0;
			for(Number n : processors.get(c)) {
				if( n.intValue() > 0 & index == State.active.ordinal())
					c.remove(n.intValue(), State.active, 0, countComponents);
				index++;
			}
			
		} 
		// DOWN : BEFORE TIMER UPDATE
		// this.debugCounters(processors.keySet(),"Down event before");
	
		// Changes the state of the cores to idle
		for(Resource r : coreSet) {
			r.setState(State.idle);
				if(timeOutDelay > 0 ) 
					idleCoreSet.add(r);
			else r.setState(State.off);
				
			// Records the time instance when the core transitioned to IDLE state
			r.setInstance();
		}
		
		
		// The time instance were the event will timeout
		if( timeOutDelay > 0 ) {
			
			// Create timeOut event and store it in the simulation kernel
			Instant timeOut = TimeFactory.newMoment( Clock.instance().now().timestamp() +  timeOutDelay);
			WaitTimeTimeoutEvent wtt = new WaitTimeTimeoutEvent(timeOut, idleCoreSet, site);
			wtt.setCompletionTime(Clock.instance().now());
			Kernel.getInstance().dispatch(wtt);

			
			// Register cores and their corresponding event in TEO
			for(Resource r : coreSet) 
				TOE.put(r, wtt);
		} 
	}
	
	
	
	
	/**
	 * 
	 * @param event
	 */
	public void handleTimeOutEvent(WaitTimeTimeoutEvent event) {
		boolean countComponents = false;
		long timeOutDelay = hierarchy.getEnergySettings().coreTimeOutDelay;
		
		
		if(timeOutDelay > 0)
			countComponents = true;
		

		// Estimates total energy consumption
		computeConsumption();
		
		
		// Gets the set of selected cores
		ResourceBundle coreSet = event.getCores();
		
		
		// Valid states include active and idle. OFF is not possible.
		Hashtable<Component, int[]> processors = this.aggregateCores(coreSet);
		
		// TIMEOUT : BEFORE TIMER UPDATE
		// this.debugCounters(processors.keySet(),"Timeout event after");
				
		// Updates the core counters: INCREMENTS OFF CORE COUNT
		for(Component c : processors.keySet()) {
			int index = 0;
			for(Number n : processors.get(c)) {
				if(index == State.idle.ordinal() & (n.intValue() > 0))
					c.remove(n.intValue(), State.idle, 0, countComponents);
				index++;
			}
		}
		
		// TIMEOUT : BEFORE TIMER UPDATE
		// this.debugCounters(processors.keySet(),"Timeout event before");
		
		// Registers cores and its corresponding events in TEO
		for(Resource r : coreSet) 
			TOE.remove(r);

		
		// Changes the state of the cores to idle
		for(Resource r : coreSet) {
			if(r.getState().equals(State.idle)) {
				r.setState(State.off);
				machineState.remove(r);
			}
		}
		
	}
	
	
	
	private void computeConsumption() {
		// Aggregate I by processor
		Hashtable<Component, int[]> processors = this.aggregateCores(machineState);
						
		// Estimate power consumption
		for( Component c : processors.keySet())
			c.updatePowerConsumption(0);
	}
	
	
	
	/**
	 * Aggregate cores by processor and state 
	 * 
	 * @param coreset, the set of cores.
	 * @return	disjoint sets of cores aggregated by processor and state.
	 */
	private Hashtable<Component, int[]> aggregateCores(ResourceBundle coreSet) {
		Hashtable<Component, int[]> processors = new Hashtable<Component,int[]>();
		
		// Cores are aggregated by state and processor
		for(Resource r : coreSet) {
			Component processor = r.getComponent();
			if( !processors.containsKey(processor) ) {
				int index = r.getState().ordinal();
				int[] count = new int[State.values().length];
				count[index] = 1;
				processors.put(processor, count);
			} else {
				int index = r.getState().ordinal();
				int[] count = processors.get(processor);
				count[index]++;
			}
		}// End for
		
		return processors;
	}
	
	

	
	private void updateEvents(Hashtable<Event,ResourceBundle> events, ResourceBundle coreSet){
	
		for(ResourceBundle rs : events.values()){
			ResourceBundle e = rs.intersect(coreSet);
			WaitTimeTimeoutEvent event = this.TOE.get((Resource)rs.get(0));
			ResourceBundle e_k = event.getCores();
			ResourceBundle diff = e_k.difference(e);
			event.setCores(diff);
			for(Resource r : rs)
				TOE.remove(r);
			if(event.getCores().size() == 0)
				Kernel.getInstance().getEventQueue().remove(event);
			
		}
	}
	
	
	
	private Hashtable<Event,ResourceBundle> getEvents(ResourceBundle coreSet) {
		ResourceBundle le = null;
		Hashtable<Event,ResourceBundle> events = new Hashtable<Event,ResourceBundle>();
		
		if(TOE.size() != 0){
			for(Resource r: coreSet){
				WaitTimeTimeoutEvent e = (WaitTimeTimeoutEvent) TOE.get(r);
				if(e != null){
					le = (!events.keySet().contains(e) )? 	new ResourceBundleSortedSetImpl() :
															events.get(e);
					le.add(r);
					events.put(e, le);
				}
			}
		}
		
		return events;
	}
	
	
	@SuppressWarnings("unused")
	private void debugCounters(Set<Component> components, String msg) {
		Component[] componentArray = components.toArray(new Component[0]);
		Arrays.sort(componentArray);
		long now = Clock.instance().now().timestamp();
		
		System.out.println( "-------- Time instance : " + now/1000 + " : "+ msg + " --------");
		System.out.println("Label 	:	Active	:	Idle	:	Off");
		
		
		for(Component c : componentArray) 
			System.out.print(c.label + "	:	" + c.numActiveCores + " 	:	" + c.numIdleCores + " 	:	" + c.numOffCores +"\n");
		
		System.out.println();
	}
	
	
	/**
	 * Return the energy broker hierarchy and configuration settings
	 * 
	 * @return, an object of type Hierarchy
	 */
	public Hierarchy getHierarchy() {
		return this.hierarchy;
	}
	
	
	public void scaleVoltageAndFrequency() {
		// Select all resources that 
		processorDVFS.onWindowCompletion();
	}
	
}
