package mx.cicese.dcc.teikoku.energy;

/**
 * When a job completes its execution cores that where allocated to 
 * it are freed.
 * 
 * DeAllocatedProcessor is used to record the time instance at which
 * the cores are freed. It also records the processor identifier 
 * that contains the cores. 
 *  
 * DeAllocatedProcessor objects are inserted the Idle Resource Buffer
 * (IRB) in the machine energy manager.
 * 
 * @see EnergyBroker
 */


/**
 * Upon job arrival the machine scheduler selects a set of cores for the
 * job based on its resource requirements. Let S be such set.
 * 
 * For sake of illustration assume |S| > 1 and that cores in S are distributed 
 * among two processor evenly. Let S1 and S2 be two subset of cores in P1 
 * and P2 correspondingly, such that S1 U S2 = S.
 * 
 * The AllotedProcessor data structure is used to store the number of cores
 * allocated to the job. It also indicates the state of the cores at the 
 * time of the allocation. 
 * 
 * For the previous example, assume that the allocated cores are IDLE at the
 * time of allocation. Two AllocatedProcessor objects are created: 
 * - AllocatedProcessor<Processor1.ID,|S1|,IDLE>
 * - AloocatedProcessor<Processor2.ID,|S2|,IDLE>
 * 
 * Both objects are inserted later in two the ARB in the EnergyManager 
 *  
 */


public class RsvCoreSet {
	/**
	 * Identifier of the allocated processor
	 */
	public Component processor;

	
	/**
	 * The number of allocated cores in the processor
	 */
	public int numCores;
	
	
	/**
	 * The state of the cores at the time of the allocation
	 */
	public State state;
	
	
	/**
	 * Class constructor
	 */
	public RsvCoreSet(Component processor, int numCores, State state) {
		this.processor = processor;
		this.numCores = numCores;
		this.state = state;
	}
}
