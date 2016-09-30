package de.irf.it.rmg.core.teikoku.scheduler.strategy;

import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.site.ResourceBundle;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;
import de.irf.it.rmg.core.util.time.TimeFactory;
import de.irf.it.rmg.sim.kuiga.Clock;

public class FCFS2Strategy extends AbstractStrategy {
	/**
	 * The default log facility for this class, using the <a
	 * href="http://commons.apache.org/logging">Apache "commons.logging" API</a>.
	 * 
	 * @see org.apache.commons.logging.Log
	 */	
	final private static Log log = LogFactory.getLog(RandomStrategy.class);
	
	
	
	public FCFS2Strategy() {
		super();
	}
	
	
	@Override
	public void decide(Queue<Job> queue, Schedule schedule) {
		
		if(!queue.isEmpty()) {
			
			// Select the job to schedule
			Job jobToSchedule = queue.peek();
			
			/*
			 * try to schedule the given job
			 */
			Slot possibleSlot = null;
			
			/*
			 * determine start (now) and end (eternity) time and use as search
			 * area for schedule
			 */
			Instant startTime = Clock.instance().now();
			Instant endTime = TimeFactory.newEternity();
			Period searchArea = new Period(startTime, endTime);
			int size = jobToSchedule.getDescription().getNumberOfRequestedResources();
			
			
			// Finds the time interval where Sj or more resources are available
			try {
				possibleSlot = schedule.findNextFreeSlot(jobToSchedule
							.getDescription().getEstimatedRuntime(),
							 size, searchArea);
			} catch (InvalidTimestampException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalOccupationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
						
			if (!possibleSlot.getAdvent().equals(startTime)) {
					possibleSlot = null;
			} else { 
			
				
				ResourceBundle selectedResources = possibleSlot.getResources().createSubSetWith(size);
						
				/*
				 * no: the job cannot be started immediately, thus return null
				 */
				//System.out.println("Time : " + Clock.instance().now().timestamp() + " queue size " + queue.size() + "jobs " + queuedJobs(queue));
				
			
					
				//System.out.println("Allocated job " + jobToSchedule.getName() + " requieres "  + size + " allocated " + selectedResources);
				possibleSlot.setResources(selectedResources);
			}
			
			/*
			 * notify (advise) the listener
			 */
			try {
				super.getStrategyListener().advise(jobToSchedule, possibleSlot);
			}
			catch (DecisionVetoException e) {
				String msg = "decision vetoed: properties in slot \""
						+ possibleSlot + "\" not accepted for job \""
						+ jobToSchedule + "\"; skipping current.";
				log.info(msg, e);
			}
		}
	}
	
	
	private String queuedJobs(Queue<Job> queue){
		String list = new String();
		for(Job j :queue){
			list += j.getName() + ",";
		}
		
		return list;
	}
}
