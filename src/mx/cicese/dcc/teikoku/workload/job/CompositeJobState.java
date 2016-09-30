package mx.cicese.dcc.teikoku.workload.job;

/*
 * "Teikoku Workflow Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the:
 *   Departamento de Ciencias Computacionales,
 *   Centro de Investigacon Cientifica y de Educacion Superior de Ensenada (CICESE),
 *   Mexico
 *    
 *   Facultad de Ciencias, 
 *   Ciencias Computacionales,
 *   Universidad Autonoma de Baja California (UABC), 
 *   Mexico
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *   Free Software Foundation, Inc.,
 *   51 Franklin St, Fifth Floor,
 *   Boston, MA 02110, USA
 */
import edu.uci.ics.jung.graph.Graph;
import de.irf.it.rmg.core.teikoku.workload.job.SWFJob;

public enum CompositeJobState {
	/**
	 * TODO: not yet commented
	 * 
	 */
	INACTIVE(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	SUSPENDED(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	ACTIVE(),
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	COMPLETED();
	
	private StateCollection jobStates = new StateCollection();
	
	/**
	 * TODO: not yet commented
	 *
	 * @param source
	 * @return
	 */
	public boolean validateStateChange(CompositeJobState source) {
		boolean result;
		
		switch (this) {
		
			case INACTIVE:
				result = (source == null || SUSPENDED.equals(source)|| ACTIVE.equals(source))? true : false;
				break;
			case SUSPENDED:
				result = INACTIVE.equals(source);
				break;
			case ACTIVE:
				result = INACTIVE.equals(source);
				break;
			case COMPLETED:
				result = ACTIVE.equals(source);
				break;
			default:
				result = false;
				break;
		} // switch
		
		return result;
	}

	public void initialize(Graph<SWFJob,Precedence> structure) {
		jobStates.initialize(structure);
	}
	
	public StateCollection getStateCollection(){
		return this.jobStates;
	} 
}
