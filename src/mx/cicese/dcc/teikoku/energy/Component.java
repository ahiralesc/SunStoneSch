package mx.cicese.dcc.teikoku.energy;

import java.util.UUID;

import mx.cicese.dcc.teikoku.utilities.Color;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.DateHelper;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;


public class Component implements Comparable<Component>{
	
	
	/**
	 * The color of the resource. Coloring is used to asses in computing the
	 * maximum delay by the energy broker. 
	 */
	private Color color;
		
	
	/**
	 * Energy consumption 
	 */
	private double currentEnergyConsumption;
	
	
	/**
	 * The following three variables are used to compute the number of processor,
	 * boards, and cabinets that are currently ON.
	 */
	private State currentProcessorState;
	
	private State currentBoardState;
	
	private State currentCabinetState;


	/**
	 * The resource energy consumption.
	 * Measured in kWh (kiloWatts per hour)
	 */
	private double energyConsumption;
	
	
	/**
	 * The value per Watt 
	 */
	private float fair;
	
	
	/**
	 * The resource id
	 */
	private UUID id;
	
	
	/**
	 * Preceding time instance (to current time) at which one or more 
	 * cores are released.  
	 */
	private Instant instance;
	
	
	/**
	 * A unique resource label.
	 * 
	 *  Labeling is conducted by height and resource type. That is, labeling
	 *  of processors is independent of labeling of chassis. Labels are  
	 *  integers.
	 * 
	 */
	public int label;
		
	
	/**
	 * Time instance of the occurrence of an event 
	 */
	private Instant lastEventTime;
	
	
	/**
	 * Number of cores that are processing a task at the current
	 * time instance.
	 */
	public int numActiveCores;
		
	
	/**
	 * The number of processors in ON state
	 */
	private int numberOnProcessors;

	
	/**
	 * The number of boards in ON state
	 */
	private int numberOnBoards;
	
	
	/**
	 * The number of cabinets in ON state
	 */
	private int numberOnCabinets;
	
	/**
	 * The set of Cores
	 */
	private ResourceBundle CoresSet;
	

	/**
	 * Number of idle cores at the current time instance. 
	 * Cores remain idle for a short interval of time and can transition to
	 * off or active state.
	 */
	public int numIdleCores;
	
	
	/**
	 * Number of cores that are in off state at the current time instance.
	 *  
	 */
	public int numOffCores;
	

	/**
	 * The resource on delay
	 */
	private long onDelay;
	
	
	/**
	 * The resource off delay
	 */
	private long offDelay;
	
	
	/**
	 * The time the machine is turned on
	 */
	private Instant onTime;
	
	
	/**
	 * The time the machine is turned off
	 */
	private Instant offTime;
	
	
	/**
	 * The resource that contains this resource
	 */
	private Component parent;
	
	
	/**
	 * Energy consumption while in active or idle state, in Watts
	 * 
	 */
	private float powerOn;

	
	/**
	 * Energy consumption while in off state, in Watts
	 */
	
	private float powerStdby;
	
	
	/**
	 * The site (machine) the resource is in
	 */
	private Site site;
	
	
	/**
	 * The resource state
	 */
	private State state;
		
	
	/**
	 * The resource type
	 */
	private Type type;
	

	
	
	
	/* class constructor */
	Component(Component parent, int cores, Type type){
		this.color = Color.WHITE;
		this.currentEnergyConsumption = 0;
		this.currentBoardState = null;
		this.currentCabinetState = null;
		this.currentProcessorState = null;
		this.energyConsumption = 0;
		this.fair = 0;
		this.id = UUID.randomUUID();
		this.instance = null;
		this.label = 0;
		this.lastEventTime = null;
		this.numActiveCores = 0;
		this.numberOnBoards = 0;
		this.numberOnCabinets = 0;
		this.numberOnProcessors = 0;
		this.CoresSet=null;
		this.numIdleCores = 0;
		this.numOffCores = cores;
		this.offDelay = 0;
		this.offTime = null;
		this.onDelay = 0;
		this.onTime = null;
		this.parent = parent;
		this.powerOn = 0;
		this.powerStdby = 0;
		this.site = null;
		this.state = State.standby;
		this.type = type;
	}
	
	
	
	/**
	 * Increases the number of cores by <b>amount</b>. <b>Type</b> variable specifies 
	 * if the number of cores resources in active, idle, or off state 
	 * are incremented by <b>amount</b>.  
	 * 
	 * @param type, the core type to modify
	 * @param amount, the amount to increase
	 */
	public void add(int n, State resState, double chaildEnergy) {
		// counts the number of components currently ON
		if(resState.equals(State.off))
			this.compOnFreqPowerOn();
		
		// Computes the current energy consumption
		this.updateConsumption(chaildEnergy);
		
		/*
		 * Instance of time an event occurred 
		 */
		if( (numActiveCores + numIdleCores) == 0 )
			this.lastEventTime = Clock.instance().now();
		
		state = State.active;
		numActiveCores += n;
		//if(resState.equals(State.standby))
		//	numOffCores -= n;
		if(resState.equals(State.idle))
			numIdleCores -= n;
		if(resState.equals(State.off))
			numOffCores -= n;
		
		if(parent != null)
			parent.add(n, resState, this.currentEnergyConsumption);
		
		this.lastEventTime = Clock.instance().now();
	}
	
	/**
	 * 
	 */
	public void addCoresSet(ResourceBundle SetCores){
		this.CoresSet=SetCores;
	}
	
	/**
	 * Computes the number of components in ON state, after turning off resources
	 */
	private void compOnFreqPowerOff() {
		currentProcessorState 	= (type.equals(Type.processor))? state : currentProcessorState;
		currentBoardState 		= (type.equals(Type.board))? state : currentBoardState;
		currentCabinetState		= (type.equals(Type.cabinet))? state : currentCabinetState;
		
		// Processor case
		if( currentProcessorState.equals(State.standby))
			numberOnProcessors--;
		if( parent != null)
			parent.currentProcessorState = currentProcessorState;
		
		// Board case
		if( currentBoardState != null ) {
			if( currentBoardState.equals(State.standby) )
				numberOnBoards--;
			if( parent != null)
				parent.currentBoardState = currentBoardState; 
		}
			
		//Cabinet case
		if( currentCabinetState != null ) {
			if( currentCabinetState.equals(State.standby) )
				numberOnCabinets--;
			if( parent != null)
				parent.currentCabinetState = currentCabinetState;
		}
	}
	
	
	/**
	 * Computer the number of components turned ON.
	 */
	private void compOnFreqPowerOn() {
		currentProcessorState 	= (type.equals(Type.processor))? state : currentProcessorState;
		currentBoardState 		= (type.equals(Type.board))? state : currentBoardState;
		currentCabinetState		= (type.equals(Type.cabinet))? state : currentCabinetState;
		
		// Reevaluate the state. 
		currentProcessorState = (type.equals(Type.processor) && (numActiveCores + numIdleCores >= 1))? 
								State.active : currentProcessorState;
		currentBoardState =		(type.equals(Type.board) && numberOnBoards >= 1)? 
								State.active : currentBoardState;
		currentCabinetState	= 	(type.equals(Type.cabinet) && numberOnCabinets >= 1)? 
								State.active : currentCabinetState;
		
		
		// Processor case
		if( currentProcessorState.equals(State.standby))
			numberOnProcessors++;
		if( parent != null)
			parent.currentProcessorState = currentProcessorState;
		
		// Board case
		if( currentBoardState != null ) {
			if( currentBoardState.equals(State.standby))
				numberOnBoards++;
			if(parent != null)
				parent.currentBoardState = currentBoardState; 
		}
			
		//Cabinet case
		if( currentCabinetState != null ) {
			if( currentCabinetState.equals(State.standby))
				numberOnCabinets++;
			if( parent != null )
				parent.currentCabinetState = currentCabinetState;
		}
		
	}
	
	
	/**
	 * Returns the number of resources active,
	 * idle, or off resources that the resource 
	 * contains. 
	 *  
	 * @param type, the requested type of cores
	 * @return the number of cores 
	 */
	public int get(State type) {
		int amount = 0;
		
		if(type.equals(State.active))
			amount = this.numActiveCores;
		if(type.equals(State.idle))
			amount = this.numIdleCores;
		if(type.equals(State.off))
			amount = this.numOffCores;
		
		return amount;
	}



	/**
	 * Gets the resource color
	 * 
	 * @return a Color object
	 */
	public Color getColor() {
		return this.color;
	}



	/** 
	 * Gets the recourse energy consumption
	 * 
	 * @return the resource energy consumption in Watts
	 */
	public double getEnergyConsumption() {
		return this.energyConsumption;
	}

	/**
	 * Gets the processor leaves 
	 */
	public ResourceBundle getCoresSet(){
		return this.CoresSet;
	}
	
	/**
	 * Gets the fair for the resource
	 * 
	 * @return fair, the fair per Watt
	 */
	public float getFair() {
		return this.fair;
	}
	
	
	/**
	 * Returns the fraction of ON components contained by this component
	 * 
	 * @return a float value
	 */
	public float getRatioOnComponents(Type type) {
		
		EnergySettings settings = site.getEnergyManager().hierarchy.getEnergySettings();
		
		float numCores			= settings.cores;	
		float numProcessors 	= settings.cabinets * settings.boards * settings.processors;
		float numBoards			= settings.cabinets * settings.boards;
		float numCabinets		= settings.cabinets;
		
		float ratio = 0;
		
		
		if(type.equals(Type.site)) {
			float numComponents = this.site.getEnergyManager().getHierarchy().get().getVertices().size()-1;
			ratio = ( numberOnCabinets + numberOnBoards + numberOnProcessors ) / numComponents; 
		}
		
		if(type.equals(Type.cabinet))
			ratio = numberOnCabinets / numCabinets;
		
		if(type.equals(Type.board))
			ratio = numberOnBoards / numBoards;
		
		if(type.equals(Type.processor))
			ratio = numberOnProcessors / numProcessors;
		
		if(type.equals(Type.core))
			ratio = (numActiveCores + numIdleCores) / numCores;
		
		return ratio;
	}



	/**
	 * Gets the resource ID
	 * 
	 * @return the resource ID
	 */
	public UUID getID() {
		return this.id;
	}



	/**
	 * Returns the resource label
	 * 
	 * @return the resource label
	 */
	public int getLabel(){
		return this.label;
	}



	/**
	 * Returns the resource amount of time required to turn it off 
	 * 
	 * @return, the resource off delay in milliseconds
	 */
	public long getOffDelay() {
		return this.offDelay;
	}



	/**
	 * Returns the time instance when the resource was turned off
	 * @return
	 */
	public Instant getOffTime() {
		return this.offTime;
	}



	/**
	 * Returns the amount of time pending to turn on the resource. 
	 * 
	 * @return, the resource on delay in milliseconds
	 */
	public long getOnDelay() {
		long delay = 0, elapsedTime = 0;
		long now = Clock.instance().now().timestamp();
		long startTime = this.onTime.timestamp();
		
		if( now >= startTime && now <= (startTime+onDelay)) {
			elapsedTime = now - startTime;
			delay =  onDelay - elapsedTime;
		} else 
			delay = 0;
		return delay;
		
	}



	/**
	 * Returns the time instance when the resource was turned on
	 * 
	 * @return a time instance
	 */
	public Instant getOnTime() {
		return this.onTime;
	}



	/**
	 * Gets the resource parent ID
	 * 
	 * @return the parent ID
	 */
	public Component getParent(){
		return this.parent;
	}



	/**
	 * Gets the power the resource uses while in 
	 * active or idle state, in Watts
	 * 
	 * @return the power consumption in watts
	 */
	public float getPowerActive() {
		return this.powerOn;
	}



	/**
	 * Gets the power the resource consumes while in 
	 * off state, in Watts
	 * 
	 * @return the power consumption in watts
	 */
	public float getPowerOff() {
		return this.powerStdby;
	}



	/**
	 * Gets the resource current state
	 * 
	 * @return the resource current state
	 */
	public State getState() {
		return this.state;
	}



	/**
	 * 
	 * Returns the preceding time instance (to current time) at which one or more 
	 * cores where released.  
	 *
	 * @return a time stamp
	 */
	public Instant getTimeStamp(){
		return this.instance;
	}



	/**
	 * Returns the resource type
	 * 
	 * @return the resource type
	 */
	public Type getType(){
		return this.type;
	}



	/**
	 * Gets the resource site
	 * 
	 * @return the resource site
	 */
	public Site getSite() {
		return this.site;
	}

/*
	public ResourceBundle getCoresSet(){
		
	}
*/
	/**
	 * Decreases the number of cores by <b>amount</b>. <b>Type</b> variable specifies 
	 * if the number of cores resources in active, idle, or off state 
	 * are decreased by <b>amount</b>.  
	 * 
	 * @param type, the core type to modify
	 * @param amount, the amount to decrease
	 */
	public void remove(int n, State rsState, double chaildEnergy, boolean decrease) {
				
		if(rsState.equals(State.active)) {
			this.updateConsumption(chaildEnergy);
			
			int rac = (numActiveCores - n); // Remaining active cores
			int ric = numIdleCores;			// Remaining idle cores
						
			numActiveCores -= n;
			numIdleCores += n;
			
			state =	( 	(rac > 0 && ric > 0) ||
						(rac == 0 && ric > 0) ||
						(rac > 0 && ric == 0))? State.active : State.standby; 
					
		} else {
			this.updateConsumption(chaildEnergy);
			
			int rac = numActiveCores;		// Remaining active cores
			int ric = (numIdleCores - n);	// Remaining idle cores
						
			if(rsState.equals(State.idle)){
				numIdleCores -= n;
				numOffCores += n;
				
				
				state =	( 	(rac > 0 && ric > 0) ||
							(rac == 0 && ric > 0) ||
							(rac > 0 && ric == 0))? State.active : State.standby; 
				
				if(state.equals(State.standby))
					offTime = Clock.instance().now(); // Record the time instance when the resource is turned off
			}// End state IDLE
		}// End if
		
		// Count the number of components still ON
		if(decrease)
			this.compOnFreqPowerOff();
		
		if( parent != null)
			parent.remove(n, rsState, this.currentEnergyConsumption, decrease);
		
		this.lastEventTime = Clock.instance().now();
	}



	/**
	 * Sets the power the resource consumes while in
	 * active or idle state, in Watts
	 * 
	 * @param watts, the consumed energy
	 */
	public void setPowerOn(float power) {
		this.powerOn = power;
	}
	
	
	
	/**
	 * Sets the power the resource consumes while in
	 * standby state, in Watts
	 * 
	 * @param watts, the consumed energy
	 */
	public void setPowerStdby(float power) {
		this.powerStdby = power;
	}
	
	
	/**
	 * Sets the fair for the resource
	 * 
	 * @param fair, the fair per Watt
	 */
	public void setFair(float fair) {
		this.fair = fair;
	}
	
	
	
	/**
	 * Sets a time stamp
	 * 
	 * @param time, a instance of time
	 */
	public void setTimeStamp(Instant time) {
		this.instance = time;
	}
	
	
	/**
	 * Sets the resource type
	 * 
	 * @param type, the resource type
	 */
	public void setType(Type type){
		this.type = type;
	}
	
	
	/**
	 * Sets the resource on delay
	 * 
	 * @param onDelay, the on delay in milliseconds
	 */
	public void setOnDelay(long onDelay) {
		this.onDelay = onDelay;
	}
	
	
	/**
	 * Sets the resource off delay
	 * 
	 * @param offDelay, the off delay in milliseconds
	 */
	public void setOffDelay(long offDelay){
		this.offDelay = offDelay;
	}
	
	
	/**
	 * Sets the resource parent id
	 * 
	 * @param parent, the UUID of the parent
	 */
	public void setParent(Component parent) {
		this.parent = parent;
	}
	
	
	/**
	 * Sets the resource color
	 * 
	 * @param color, the new coloring for the resource
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	
	
	/**
	 * Sets the resource energy consumption
	 * 
	 * @param energyConsumption
	 */
	public void setEnergyConsumption(double energyConsumption) {
		this.energyConsumption = energyConsumption;
	}
	
	
	
	/**
	 * Updates the energy consumption of this resource
	 * 
	 * @param cores, the amount of cores used before 
	 */
	private void updateConsumption(double chaildEnergy) {
		
		float corePowerActive = 0, corePowerIdle = 0;
		
		if(site != null) {
			corePowerActive = site.getEnergyManager().getHierarchy().getEnergySettings().corePowerActive;
			corePowerIdle = site.getEnergyManager().getHierarchy().getEnergySettings().corePowerIdle;
		}
		
		this.currentEnergyConsumption=chaildEnergy;
		
		if(lastEventTime == null)
			return;
		
		Instant now = Clock.instance().now();
		
		/* 
		 * Energy consumption is measured in kWh  
		 */
		
		double interval = now.timestamp() - this.lastEventTime.timestamp();
		interval = DateHelper.convertToSeconds((long)interval);
		interval = interval / 3600;
		
		
		if(type.equals(Type.processor)) {
			// Estimation for core power consumption 
			currentEnergyConsumption += (interval * corePowerActive) * numActiveCores;
			currentEnergyConsumption += (interval * corePowerIdle) * numIdleCores;
			// Estimation for processor power consumption
			currentEnergyConsumption += ( 	(numActiveCores >= 1) & (numIdleCores == 0) |
											(numActiveCores == 0) & (numIdleCores >= 1) |
											(numActiveCores >= 1) & (numIdleCores >= 1)	)? 
					(interval * powerOn) :
					(interval * powerStdby);
		} // End processor 
		
		if(type.equals(Type.cabinet) || type.equals(Type.board)) {
			currentEnergyConsumption += ( 	(numActiveCores >= 1) & (numIdleCores == 0) |
											(numActiveCores == 0) & (numIdleCores >= 1) |
											(numActiveCores >= 1) & (numIdleCores >= 1)	)? 
				(interval * powerOn) :
				(interval * powerStdby);
		}
			
			energyConsumption+=currentEnergyConsumption;
	}
	
	
	
	public void updatePowerConsumption(double chaildEnergy) {
		this.updateConsumption(chaildEnergy);
		if( parent != null)
			parent.updatePowerConsumption(currentEnergyConsumption);
		this.lastEventTime = Clock.instance().now();
	}
	
	/**
	 * Sets the resource label. Labels must be unique
	 * 
	 * @param label
	 */
	public void setLabel(int label){
		this.label = label;
	}
	
	
	/**
	 * The site the resource belongs to
	 *
	 * @param site
	 */
	public void setSite(Site site) {
		this.site = site;
	}



	@Override
	public int compareTo(Component o) {
		int result = 0; 
		
		if(this.label < o.label)
			result = -1;
		if(this.label > o.label)
			result = 1;

		return result;
	}
}
