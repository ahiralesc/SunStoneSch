package mx.cicese.dcc.teikoku.energy;

import java.util.Vector;

import org.apache.commons.configuration.Configuration;

import de.irf.it.rmg.core.teikoku.Constants;
import de.irf.it.rmg.core.teikoku.RuntimeEnvironment;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.ConfigurationHelper;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Hierarchy {
	/**
	 * Topology information
	 */
	private EnergySettings energySettings;
	
	
	/**
	 * The energy model is stored as a rooted tree. In this implementation
	 * the rooted tree is mapped to a DAG.
	 */
	private DirectedGraph<Component,Number> hierarchy;
	
	
	/**
	 * The root resource of the energy model
	 */
	private Component root;
	
	
	/**
	 * The leaf nodes of the energy model. It stores the set of processors.
	 */
	private Vector<Component> processors;
	
	
	/**
	 * The containing site
	 */
	Site site;
	
	
	/**
	 * Class constructor
	 */
	public Hierarchy(Site site){
		this.energySettings = new EnergySettings();
		this.site = site;
		
		try{
			loadConfiguration();
		}catch(Exception e) {
			e.printStackTrace();
		}
		hierarchy = new DirectedSparseGraph<Component,Number>();
		processors = new Vector<Component>();
		buildTopology();
	}
	
	
	/**
	 * Gets the DAG data structure that model the energy model
	 * 
	 * @return, a Directed Graph that models the energy model
	 */
	public DirectedGraph<Component,Number> get(){
		return this.hierarchy;
	}
	
	/**
	 * Returns the root of the tree
	 * 
	 * @return
	 */
	public Component getRoot() {
		return this.root;
	}
	
	/**
	 * Gets the root resource of the rooted tree
	 */
	public int getNumberIdleCores() {
		return this.root.get(State.idle);
	}
	
	
	public int getNumberOffCores() {
		return this.root.get(State.off);
	}
	
	/**
	 * Returns the energy settings
	 * 
	 * @return, the machine energy settings
	 */
	public EnergySettings getEnergySettings(){
		return this.energySettings;
	}
	
	
	/**
	 * Loads the energy model settings and stores them in the site information 
	 * data structure.
	 * 
	 */
	private void loadConfiguration() throws InstantiationException {
		
		Configuration c = RuntimeEnvironment.getInstance().getConfiguration()
    			.subset(Site.CONFIGURATION_SECTION);
		
		/* Load the general site properties */
		energySettings.cores =c.getInt(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_SITE_CORES));
		energySettings.fair = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_SITE_FAIR));
		energySettings.onCores = c.getInt(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_SITE_ON_CORES));
				
		
		/* Cabinet specks */
		energySettings.cabinets = c.getInt(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CABINETS));
		energySettings.cabinetOnDelay = c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CABINET_ON_DELAY));
		energySettings.cabinetOffDelay  = c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CABINET_OFF_DELAY));
		energySettings.cabinetPowerOn = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CABINET_POWER_ON));
		energySettings.cabinetPowerStdby = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CABINET_POWER_STDBY));
		
		
		/* Board specks */
		energySettings.boards = c.getInt(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_BOARDS));
		energySettings.boardOnDelay = c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_BOARD_ON_DELAY));
		energySettings.boardOffDelay = c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_BOARD_OFF_DELAY));
		energySettings.boardPowerOn = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_BOARD_POWER_ON));
		energySettings.boardPowerStdby =  c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_BOARD_POWER_STDBY));
		
					
		/* Processor specks */
		energySettings.processors  = c.getInt(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_PROCESSORS));
		energySettings.processorOnDelay = c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_PROCESSOR_ON_DELAY));
		energySettings.processorOffDelay = c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_PROCESSOR_OFF_DELAY));
		energySettings.processorPowerOn = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_PROCESSOR_POWER_ON));
		energySettings.processorPowerStdby = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_PROCESSOR_POWER_STDBY));
		energySettings.coresPerProcessor = c.getInt(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_PROCESSOR_CORES));
		
		/* Core specks */
		energySettings.corePowerActive =c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CORE_POWER_ACTIVE));
		energySettings.corePowerIdle = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CORE_POWER_IDLE));
		energySettings.corePowerOff = c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CORE_POWER_OFF));
		energySettings.coreTimeOutDelay =c.getLong(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_CORE_TIMEOUT));
		
		/*Linear Combination*/
	/*	energySettings.weightForObjectiveOne=c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_LINEARCOMBINATION_WEIGHTFOROBJECTIVEONE));
		energySettings.weightForObjectiveTwo=c.getFloat(ConfigurationHelper.retrieveRelevantKey(c, site.getName(),
                Constants.CONFIGURATION_LINEARCOMBINATION_WEIGHTFOROBJECTIVETWO));*/
		
		
	}// End loading of configuration settings
	
	
	
	/**
	 * Creates a rooted three which models the hierarchy of the parallel machine
	 */
	private void buildTopology(){
		
		/* load the configuration settings */
		int cores = 0;
		
		/* Resource labels. Only processor are currently labeled */
		int label = 0;
		
		/* create the energy model hierarchy */
		/* Site level */
		Component site = new Component(null, energySettings.cores, Type.site);
		site.setFair(energySettings.fair);
		hierarchy.addVertex(site);
		this.root = site;
		site.setSite(this.site);
				
		/* Cabinet level */
		for(int i=0; i<energySettings.cabinets; i++) {
			cores = energySettings.boards * energySettings.processors * energySettings.coresPerProcessor;
			Component cabinet = buildComponent(site, cores, Type.cabinet);
			cabinet.setOffDelay(energySettings.cabinetOffDelay);
			cabinet.setOnDelay(energySettings.cabinetOnDelay);
			cabinet.setPowerOn(energySettings.cabinetPowerOn);
			cabinet.setPowerStdby(energySettings.cabinetPowerStdby);
			cabinet.setSite(this.site);
			
			/* board level */
			for(int j=0; j<energySettings.boards; j++) {
				cores = energySettings.processors * energySettings.coresPerProcessor;
				Component board = buildComponent(cabinet, cores, Type.board);
				board.setOffDelay(energySettings.boardOffDelay);
				board.setOnDelay(energySettings.boardOnDelay);
				board.setPowerOn(energySettings.boardPowerOn);
				board.setPowerStdby(energySettings.boardPowerStdby);
				board.setSite(this.site);
				
				/* Processor level */
				for(int k=0; k<energySettings.processors; k++) {
					Component processor = buildComponent(board, energySettings.coresPerProcessor, Type.processor);
					processor.setOffDelay(energySettings.processorOffDelay);
					processor.setOnDelay(energySettings.processorOnDelay);
					processor.setPowerOn(energySettings.processorPowerOn);
					processor.setPowerStdby(energySettings.processorPowerStdby);
					processor.setLabel(label++);
					processor.setSite(this.site); 
					processors.add(processor);
				}
			}
		}
		
		
		
		
		// Links the list of cores (in the schedule) to the machine organization.
		// It specifically links the leaves (processors) to the set of cores. 
		ResourceBundle coresset = this.site.getSiteInformation().getProvidedResources();
		
		
		
		int j = 1, i = 1, chunk = 1;
		Component p = processors.get(j-1);
		
		for(Resource c : coresset) {
			if(!(i <= (chunk*energySettings.coresPerProcessor))) {
				j++;
				chunk++;
				p=processors.get(j-1);
			}
			i++;
			c.setComponent(p);
		}// End for, linkage of processors to cores
		
		// Conocimiento de los nodos hijo. 
		j = 0; i = 1; int s=0;
			for(Resource c : coresset) 
			{
				//Reiniciar la varible de los nucleos "i" 
				//con el fin de contar de nuevo cada nucleo.
				if(i > energySettings.coresPerProcessor)
				{
					i=1;
				}	
				//Identificar el procesador de cada nodo
				if(c.getComponent().label >= j)
						{
							//Verificar que i sea igual al numero 
							// de nucleos por procesador. 
							if(i>=energySettings.coresPerProcessor)
								{  
									//Crear un subconjunto de "coresset" con "i" y "s".
								    //Agregar los nucleos que pertencen al procesador "j".
									c.getComponent().addCoresSet(coresset.createSubSetWith(i, s));
									j++;
									s=s+i;
								}
						}
				
				i++;
			}
	}
	
	
	public  Vector<Component> getProcessors() {
		return this.processors;
	}
	
	
	/**
	 * Builds a resource and adds it to the energy model hierarchy
	 * 
	 * @param parent		the resource
	 * @param cores		the number of cores the resource contains
	 * @param type		the type of resource
	 */
	private Component buildComponent(Component parent, int cores, Type type) {
		Component leaf = new Component(parent, cores, type);
		hierarchy.addVertex(leaf);
		hierarchy.addEdge(Math.random(), parent, leaf, EdgeType.DIRECTED);
		return leaf;
	}
	
}
