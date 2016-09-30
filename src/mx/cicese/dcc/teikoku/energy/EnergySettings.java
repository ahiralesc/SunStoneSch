package mx.cicese.dcc.teikoku.energy;

/**
 * 
 * The energy settings class contains the amount of Watts consumed by
 * the processing components of the machine, namely: cores, processors,
 * mother board, and cabinets. We assume that the machine components are 
 * homogeneous.
 * 
 *  Energy settings are read from the teikoku_properties file and stored 
 *  in the EnergySettings data structured. 
 *  
 *  
 * @author adan.hirales@cetys.mx
 * @category Energy
 */

public class EnergySettings {
	
	/**
	 * Total number of cores in the machine (size)
	 * 
	 * @category machine
	 */
	public int cores;
	
	
	/**
	 * Number of on cores. At initiation, the machine turns on the specified amount of cores. 
	 * If the number of cores exceed the amount of cores available at the first chassis, then 
	 * the reaming amount are selected from chassis two, and so forth.
	 * 
	 * @category machine
	 *   
	 */
	public int onCores;
	

	/**
	 * The energy consumption fair at the machine location. Cost of Watts per hour.
	 * 
	 * @category machine
	 */
	public float fair;
	
	
	/**
	 * The number of cabinets in the machine
	 * 
	 * @category cabinet
	 * 
	 */
	public int cabinets;
	
	
	/**
	 * The cabinet turn on delay. In milliseconds
	 * 
	 * @category cabinet
	 * 
	 */
	public long cabinetOnDelay;
	
	
	/**
	 * The cabinet turn off delay. In milliseconds
	 * 
	 * @category cabinet
	 *  
	 */
	public long cabinetOffDelay;
	
	
	/**
	 * The cabinet power consumption while in active state. In Watts.
	 * 
	 *  @category cabinet
	 */
	public float cabinetPowerOn;
	
	
	/**
	 * The cabinet power consumption while in standby state. In Watts.
	 * 
	 * @category cabinet
	 */
	public float cabinetPowerStdby;
	
	
	/**
	 * The number of boards per cabinet
	 * 
	 * @category board
	 */
	public int boards;
	
	
	/**
	 * The board turn on delay. In milliseconds
	 * 
	 * @category board
	 */
	public long boardOnDelay;
	
	
	/**
	 * The board turn off delay. In milliseconds
	 * 
	 * @category board
	 */
	public long boardOffDelay;
	
	
	/**
	 * The board power consumption while in active state. In Watts.
	 * 
	 * @category board
	 */
	public float boardPowerOn;
	
	
	/**
	 * The board power consumption while in standby state. In Watts.
	 * 
	 * @category board
	 */
	public float boardPowerStdby;
	
	
	/**
	 * The number of processors per board
	 * 
	 * @category processors
	 */
	public int processors;
	
	
	/**
	 * The processor turn-on delay. In milliseconds
	 * 
	 * @category processor
	 */
	public long processorOnDelay;
	
	
	/**
	 * The processor turn-off delay. In milliseconds
	 * 
	 * @category processor
	 */
	public long processorOffDelay;
	
	
	/**
	 * The processor power consumption while in active state. In Watts
	 * 
	 * @category processor
	 */
	public float processorPowerOn;
	
	
	/**
	 * The processor power consumption while in standby state. In Watts
	 * 
	 * @category processor
	 */
	public float processorPowerStdby;
	
	
	/**
	 * The number of cores per processor
	 * 
	 * @category processor
	 */
	public int coresPerProcessor;
	
	
	/**
	 * 
	 * The core power consumption while in active state. In Watts. 
	 */
	public float corePowerActive;
	
	
	
	/**
	 * 
	 * The core power consumption while in idle state. In Watts
	 */
	public float corePowerIdle;
	
	
	
	/**
	 * The core power consumption while in off state. In Watts. 
	 */
	public float corePowerOff;
	
	
	/**
	 * The timeout delay is used by the EnergyBroker to 
	 * delay turning off cores.
	 * 
	 * When the timeout runs out. The cores that were previously
	 * used by a job transition from idle state to off state. It is
	 * possible, that the set or a subset of cores are reallocated
	 * to a new job. Therefore, the timeout event can only alter
	 * a portion of the resources.
	 *  
	 */
	public long coreTimeOutDelay;
	
	
	/**
	 * Class constructor
	 */
	public EnergySettings(){
		this.fair = 0;
		this.cabinets = 0;
		this.cabinetOnDelay = 0;
		this.cabinetOffDelay = 0;
		this.cabinetPowerOn = 0;
		this.cabinetPowerStdby = 0;
		this.boards = 0;
		this.boardOnDelay = 0;
		this.boardOffDelay = 0;
		this.boardPowerOn = 0;
		this.boardPowerStdby = 0;
		this.processors = 0;
		this.processorOnDelay = 0;
		this.processorOffDelay = 0;
		this.processorPowerOn = 0;
		this.processorPowerStdby = 0;
		this.cores = 0;
		this.onCores = 0;
		this.coresPerProcessor = 0;
		this.corePowerActive = 0;
		this.corePowerIdle = 0;
		this.corePowerOff = 0;
		this.coreTimeOutDelay = 0;
	}

}
