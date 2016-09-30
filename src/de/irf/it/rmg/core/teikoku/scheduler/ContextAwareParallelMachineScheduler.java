package de.irf.it.rmg.core.teikoku.scheduler;

/*
 * // $Id$ //
 *
 * tGSF -- teikoku Grid Scheduling Framework
 *
 * Copyright (c) 2006-2016 by the
 *   Computer Science Department 
 *   at CICESE Research Institute, Mexico
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


import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.AbortionNotHandledException;
import de.irf.it.rmg.core.teikoku.exceptions.DequeuingVetoException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.State;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.DecisionVetoException;
import de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener;
import de.irf.it.rmg.core.teikoku.site.Resource;
import de.irf.it.rmg.core.teikoku.site.ResourceBundleBitVectorImpl;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import mx.cicese.dcc.teikoku.energy.*;
import mx.cicese.dcc.teikoku.utilities.AvrgDistance;
import mx.cicese.dcc.teikoku.utilities.ListLabelComparatorDescending;
import mx.cicese.dcc.teikoku.utilities.MapUtil;
import edu.uci.ics.jung.graph.DirectedGraph;



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
public class ContextAwareParallelMachineScheduler extends AbstractScheduler
		implements Scheduler, StrategyListener {

	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
//	final private static Log log = LogFactory.getLog(ParallelMachineScheduler.class);


	/**
	 * TODO: not yet commented Creates a new instance of this class, using the
	 * given parameters.
	 * 
	 */
	public ContextAwareParallelMachineScheduler()
			throws InstantiationException {
		// super(site);
	}
	
	
	/**
	 * Copy constructor
	 * 
	 * @param another
	 */
	public ContextAwareParallelMachineScheduler(ContextAwareParallelMachineScheduler another) {
		super(another);
	}

	/**
	 * Creates a copy 
	 */
	@Override
	public Scheduler clone() {
		return new ContextAwareParallelMachineScheduler(this);
	}
	

	
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.Scheduler
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.Scheduler#activate()
	 */
	public void activate() {
		super.getLocalStrategy().decide(super.getQueue(), super.getSchedule());
	}

	
	
	// -------------------------------------------------------------------------
	// Implementation/Overrides for
	// de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.irf.it.rmg.core.teikoku.scheduler.strategy.StrategyListener#advise(de.irf.it.rmg.core.teikoku.common.Job,
	 *      de.irf.it.rmg.core.teikoku.common.Job)
	 */
	public void advise(Job jobToSchedule, Slot possibleSlot)
			throws DecisionVetoException {
		
		HashSet<Component> CPUs  = new HashSet<Component>();
		Map<Component,Number> phi = new HashMap<Component,Number>();
		LinkedHashMap <Number,LinkedList<Component>> T = new LinkedHashMap <Number,LinkedList<Component>>();
		BitSet pResources=new BitSet();
		
		if (possibleSlot != null) {
			try {
				//try to dequeue Job
				super.dequeue(this, jobToSchedule);
			} catch (DequeuingVetoException e) {
				//Check whether job was aborted on this site
				Period[] periods=jobToSchedule.getLifecycle().findPeriodsFor(State.ABORTED);
				boolean cond=false;
				if (periods!=null){
					Site[] sites=jobToSchedule.getProvenance().findSitesAt(periods[periods.length-1].getAdvent());
					for (Site site: sites){
						if (site==this.getSite()){
							cond=true;
							getQueue().remove(jobToSchedule);
							break;
						}
					}
				}
				if (!cond){
					e.printStackTrace();
				}	
			}
			try{
				/*
				 * adapt job to slot
				 */
				Instant startTime = possibleSlot.getAdvent();
				Period duration = new Period(startTime, possibleSlot.getCessation());
				jobToSchedule.setDuration(duration);

			
				//=================================================================================================================			
				// Build the set of available CPUs
	
				for(Resource r : possibleSlot.getResources()) {
					pResources.set(r.getOrdinal()-1);
					Component cpu = r.getComponent();
					if( !CPUs.contains(cpu) )
						CPUs.add(cpu);
				} 
				
				
				// Gets the number of cores per CPU 
				double cpp = super.getSite().getEnergyManager().getHierarchy().getEnergySettings().coresPerProcessor;
			
			
				// Computes the weight of each CPU based on the number of cores it contains 
				for(Component c : CPUs) {
					double weight = (c.numIdleCores * 3 + c.numOffCores * 2) / ( cpp * 3);
					phi.put(c, weight);
				}
				
				
				// Computers the weight of each CPU based on its distance to others 
				DirectedGraph<Component,Number> g = super.getSite().getEnergyManager().getHierarchy().get();
				Component root = super.getSite().getEnergyManager().getHierarchy().getRoot();
				AvrgDistance<Component,Number> lca = new AvrgDistance<Component,Number>(g, root);
				lca.computeNormalized(g, CPUs);
				
				
				// Combines the weight (linear combination)
				// alpha[0] weight for the distance metric
				// alpha[1] weight for the availability
				double[] alpha = {0.5,0.5};
				for(Component c : phi.keySet()){
					double distance = lca.getAvrgDistance(c);
					double availability = phi.get(c).doubleValue();
					double weight =  availability* alpha[0] + distance * alpha[1];
					weight=weight*10000;
					phi.put(c, (int)weight);
				}
				
			
				// Sorts CPU by weight 
				phi = MapUtil.sortByValue(phi,1); //0----->Ascendent, 1----->Descendent
		         
				
				// Sort by label (bucket sort) 		deseo la lista de nucloes... no la de cpu		
				for(Component c : phi.keySet()) {
					Number weight = (Number) phi.get(c);
					LinkedList<Component> B = T.get(weight);
					if( B == null ){
						B = new LinkedList<Component>();
						B.add(c);
					} else 
						B.add(c);
					T.put(weight, B);
				}
				
				
				// Sort each bucket
				for (LinkedList<Component> B : T.values())
					Collections.sort(B, new ListLabelComparatorDescending<Component>());


				// Select a subset of resources of size equal to that requested by the job 				
				int Count=0, CountS=0,i=0,j=0;
				int size = jobToSchedule.getDescription().getNumberOfRequestedResources();
				ResourceBundle rr;
				Resource[] Icores = new Resource[size];
				Resource[] Ocores = new Resource[size];
				
				//=====Chosen processors=====
				for (LinkedList<Component> B : T.values()){
					for(Component c : B) {
					//Checks the availability of processor resources.
					rr=c.getCoresSet().intersect(possibleSlot.getResources());
						
					for(i=0;i < rr.size();i++){
						if(Count < size)
						{
							//Checks if the processor resources are in idle state. 
							if(rr.get(i).getState().equals(mx.cicese.dcc.teikoku.energy.State.idle))
								{
									//Add the idle resources in a Idle cores list.
									Icores[Count]=rr.get(i);
									Count++;
								}
							
							else if(CountS < size)
								{
								//Checks if the processor resources are in off state and
								//add the resources in a Off cores list.
								if(rr.get(i).getState().equals(mx.cicese.dcc.teikoku.energy.State.off))
									{	
									Ocores[CountS]=rr.get(i);
									CountS++;
									}
								}
						}
						else
							break;
					}//end third for
					if(Count >= size)
						break;
					}//end second for
				if(Count >= size)
						break;
				}//end the first for
				
				//Add the lack resources of the Idle cores list from the off cores list, 
				//taking in count the previous order and the requested resources.  
				for(i=Count;i<size;i++)
				{	
					Icores[i]=Ocores[j];
					j++;
				}
				
				//Agregar los nucleos seleccionados al Simulador.
				ResourceBundleBitVectorImpl rb=new ResourceBundleBitVectorImpl(this.getSite().getSiteInformation(),Icores,size);
				//ResourceBundleBitVectorImpl rb = new ResourceBundleBitVectorImpl(this.getSite().getSiteInformation(), ChosenCores, size);
				jobToSchedule.setResources(rb);
				
				//Estimate the frequency of reallocated idle cores and its fragmentation
				setAllocationProperties(jobToSchedule, rb);
				
				//===================================================================================================================
			
				
				super.getSchedule().addJob(jobToSchedule);
				jobToSchedule.getLifecycle().addEpisode(State.SCHEDULED);
				super.createEvents(jobToSchedule,State.STARTED);
				
			} // try
			catch (InvalidTimestampException e) {
				e.printStackTrace();
			} // catch
			catch (IllegalOccupationException e) {
				e.printStackTrace();
			} // catch
			catch (IllegalScheduleException e) {
				e.printStackTrace();
			} // catch
		} // if
	}


	
	@Override
	public void handleAbortion(Job job) throws AbortionNotHandledException{
		//resubmit the job
		this.putNextJob(job);
	}
	
	
	
	@Override
	public void handleCompletion(Job job){
		getLocalStrategy().handleCompletion(job);
	}
}


