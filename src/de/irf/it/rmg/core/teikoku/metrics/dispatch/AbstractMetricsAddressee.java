/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2009 by the
 *   Robotics Research Institute (Section Information Technology)
 *   at TU Dortmund University, Germany
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
// $Id$

/*
 * "Teikoku Scheduling API" -- A Generic Scheduling API Framework
 *
 * Copyright (c) 2007 by the
 *   Robotics Research Institute (Information Technology Section)
 *   Dortmund University, Germany
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
package de.irf.it.rmg.core.teikoku.metrics.dispatch;

import java.util.HashMap;
import java.util.Map;

import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.persistence.PersistentStore;

/**
 * TODO: not yet commented
 * 
 * @author <a href="mailto:christian.grimme@udo.edu">Christian Grimme</a>, <a
 *         href="mailto:joachim.lepping@udo.edu">Joachim Lepping</a>, and <a
 *         href="mailto:alexander.papaspyrou@udo.edu">Alexander Papaspyrou</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 * 
 */
abstract public class AbstractMetricsAddressee
		implements MetricsAddressee {

	/**
	 * TODO: not yet commented
	 * 
	 */
	private Object[] latestValues;
	
	private Map<Object, Object[]> latestValuesMap=new HashMap<Object, Object[]>();

	/**
	 * TODO: not yet commented
	 * 
	 */
	private PersistentStore persistentStore;

	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee#getLatestValues()
	 */
	public Object[] getLatestValues() {
		return this.latestValues;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee#getLatestValues()
	 */
	public Object[] getLatestValues(Site site) {
		return this.latestValuesMap.get(site);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee#setLatestValues(java.lang.Object[])
	 */
	public void setLatestValues(Object[] latestValues) {
		this.latestValues = latestValues;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee#setLatestValues(java.lang.Object[])
	 */
	public void setLatestValues(Site site,Object[] latestValues) {
		this.latestValues = latestValues;
		latestValuesMap.put(site,latestValues);
		
			
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee#getPersistentStore()
	 */
	public PersistentStore getPersistentStore() {
		return this.persistentStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.dispatch.MetricsAddressee#setPersistentStore(de.irf.it.rmg.core.util.persistence.PersistentStore)
	 */
	public void setPersistentStore(PersistentStore persistentStore) {
		this.persistentStore = persistentStore;
	}
}
