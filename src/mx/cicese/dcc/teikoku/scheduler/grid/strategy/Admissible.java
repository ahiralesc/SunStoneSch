package mx.cicese.dcc.teikoku.scheduler.grid.strategy;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import mx.cicese.dcc.teikoku.information.broker.ComputeEPState;
import mx.cicese.dcc.teikoku.information.broker.EndPointData;
import mx.cicese.dcc.teikoku.information.broker.EntityType;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;

import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public class Admissible {
	
	/**
	 * The list of known machines (or sites).
	 * The list does not include the machine that includes
	 * the Grid scheduler.
	 */
	private LinkedHashMap<UUID,Number> knownMachines;
	
	
	public Admissible(){
		this.knownMachines = new LinkedHashMap<UUID,Number>();
	}
	
	
	/**
	 * Returns the set of known machines
	 * 
	 * @return the set of known machines
	 */
	public LinkedHashMap<UUID,Number> getKnownMachines(){
		return this.knownMachines;
	}
	
	
	/**
	 * Given a parallel rigid job, returns the set of machines
	 * with whose number of resources are equal or greater than 
	 * the amount of resources requested by the job 
	 * 
	 * @param job
	 * @return a list of admissible jobs
	 */
	public LinkedList<UUID> getAdmissibleMachines(SWFJob job) {
		LinkedList<UUID> admissibleSet = new LinkedList<UUID>();
		
		int reqNumProc = job.getRequestedNumberOfProcessors();
		

		for(UUID s : knownMachines.keySet()) {
			if(reqNumProc <= knownMachines.get(s).intValue()) 
				admissibleSet.add(s);
		}
		
		return admissibleSet;
	}
	
	
	/**
	 * Sets the list of known machines
	 * 
	 * @param knownMachines, a list of machine UUID
	 * @param informationBroker, the grid information broker used to poll each machine state data
	 */
	public void setKnownMachines(List<UUID> knownMachines, GridInformationBroker informationBroker) {
		for(UUID machine : knownMachines) {
			EndPointData data = informationBroker.getInformationBroker(machine)
								.getPublicInformation(EntityType.STATUS, null, null);
			this.knownMachines.put(machine, ((ComputeEPState)data).size);
		}
	}

}
