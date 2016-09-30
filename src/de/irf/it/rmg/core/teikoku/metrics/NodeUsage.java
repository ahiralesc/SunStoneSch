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
 * Copyright (c) 2006 by the
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
package de.irf.it.rmg.core.teikoku.metrics;

import java.util.ArrayList;
import java.util.List;

import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.util.time.TimeHelper;
import de.irf.it.rmg.sim.kuiga.Clock;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;
import de.irf.it.rmg.sim.kuiga.annotations.MomentOfNotification;
import de.irf.it.rmg.sim.kuiga.annotations.NotificationTime;

/**
 * 
 */
public class NodeUsage extends AbstractMetric {
	
	private List<Long> localNodeUsage;
	
	/**
	 * Creates a new instance of this class, using the given parameters.
	 * 
	 * @see AbstractMetric#AbstractMetric()
	 */
	public NodeUsage() {
		super();
		this.localNodeUsage=new ArrayList<Long>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getHeader()
	 */
	public Object[] getHeader() {
		Object[] values = this.getLatestValuesPrototype();

		values[0] = "%NodeIndex";
		values[1] = "%TotalUtil";
		
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#getLatestValuesPrototype()
	 */
	@Override
	protected Object[] getLatestValuesPrototype() {
		return new Object[2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.metrics.AbstractMetric#acceptJobCompletedEvent(de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent)
	 */
	@AcceptedEventType(value=JobCompletedEvent.class)
	@MomentOfNotification(value=NotificationTime.BEFORE)
	public void deliverCompletedEvent(JobCompletedEvent event){
		//First check tag-existence
		if (event.getTags().containsKey(this.getSite().getUUID().toString())){
			Job job=event.getCompletedJob();
			if (job.getProvenance().getLastCondition()==this.getSite()){
				if (this.localNodeUsage.size()==0){
					for (int i=1;i<=super.getSite().getSiteInformation().getNumberOfAvailableResources();i++){
						this.localNodeUsage.add(0L);
					}
				}
				long duration=TimeHelper.toSeconds(job.getDuration().distance());
				for (Resource r:job.getResources()){
					this.localNodeUsage.set(r.getOrdinal()-1,this.localNodeUsage.get(r.getOrdinal()-1)+duration);
				}
			}
		}
	}
	
	@Override
	public void terminate(){
		Object[] values=new Object[2];
		long makespan=TimeHelper.toSeconds((Clock.instance().progression()));
		for (int i=1;i<=this.localNodeUsage.size();i++){
			values[0]=i;
			values[1]=(this.localNodeUsage.get(i-1)*100)/makespan;
			super.setLatestValues(values);
			super.manualMakePermanent();
		}
		
	}

}
