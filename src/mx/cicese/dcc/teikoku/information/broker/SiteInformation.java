/*
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2013 by the
 *   CICESE Research Center and  
 *   CETYS University, Mexico
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

package mx.cicese.dcc.teikoku.information.broker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.irf.it.rmg.core.teikoku.metrics.Metrics;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.util.time.Instant;


/**
 * The computing endpoint is a specialization of the Endpoint class for a service that provides 
 * processing capabilities. In teikoku, the computing endpoint is referred to as ComputeSite.
 * <p>
 * 
 * The SiteInformation interfaces provides methods to access ComputeSite state and runtime data.
 * <p>
 *   
 * This implementation is based on GLUE Specification V. 2.0. The GLUE specification is an information 
 * model for Grid entities described using the natural language and UML Class Diagrams.
 * <p>
 *  
 *  @author <a href="mailto:ahiralesc@gmail.com">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * 	@version $Version$, $Date$
 * 
 *  @category Information system
 * 
 */

public interface SiteInformation {

	/**
	 * TODO: not yet commented
	 *  DELETE?
	 */
	void addConferedResources(int amount);

	/**
	 * TODO: not yet commented
	 * 
	 */
	void addLentResources(List<Resource> rl);


	/**
	 * TODO: not yet commented
	 *  DELETE?
	 */
	public Map<Integer, Instant> getMapLentResToTime();

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	int getNumberOfAvailableResources();

	/**
	 * TODO: not yet commented
	 *  DELETE?
	 */
	ResourceBundle getLentResources();

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	Set<Metrics> getProvidedMetrics();

	/**
	 * TODO: not yet commented
	 *
	 * @return
	 */
	ResourceBundle getProvidedResources();
	
	/**
	 * TODO: not yet commented
	 * 
	 */
	public boolean getServingState();
	
	/**
	 * TODO: not yet commented
	 *  DELETE?
	 */
	Instant getTimeOfLoan(Resource r);

	/**
	 * TODO: not yet commented
	 *  DELETE?
	 */
	void removeConferedResources(int amount); 
}
