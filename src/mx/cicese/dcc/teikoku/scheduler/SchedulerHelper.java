package mx.cicese.dcc.teikoku.scheduler;

import mx.cicese.dcc.teikoku.schedule.TemporalSchedule;
import de.irf.it.rmg.core.teikoku.common.Slot;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalOccupationException;
import de.irf.it.rmg.core.teikoku.exceptions.IllegalScheduleException;
import de.irf.it.rmg.core.teikoku.exceptions.InvalidTimestampException;
import de.irf.it.rmg.core.teikoku.job.Job;
import de.irf.it.rmg.core.teikoku.job.JobControlBlock;
import de.irf.it.rmg.core.teikoku.scheduler.Strategy;
import de.irf.it.rmg.core.teikoku.site.schedule.Schedule;
import de.irf.it.rmg.core.util.collections.SortedQueue;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.core.util.time.Period;

public final class SchedulerHelper {

	public static  
	void scheduleQueuedJobs(Schedule schedule, SortedQueue<Job> queue, Strategy strategy) {
		
		while(!queue.isEmpty()) {
			/* The strategy determines the next job to place in the schedule.
			 * It also determines the possible placing of such a job in the schedule
			 * The decision must return the SLOT and job that was selected by the 
			 * strategy
			 */
			JobControlBlock jcb = strategy.getNextDecision(queue, schedule);
			Slot slot = jcb.getSlot();
			if( slot != null) {
				Job job = jcb.getJob();
				
				Instant startTime = slot.getAdvent();
				//Period duration = new Period(startTime, TimeHelper.add(startTime, job.getDescription().getEstimatedRuntime()));
				Period duration = new Period(startTime, slot.getCessation());
				job.setDuration(duration);
				job.setResources(slot.getResources().createSubSetWith(job.getDescription().getNumberOfRequestedResources()));
				try {
					schedule.addJob(job);
				} // try
				catch (InvalidTimestampException e) {
					e.printStackTrace();
				} // catch
				catch (IllegalOccupationException e) {
					e.printStackTrace();
				} // catch
				catch (IllegalScheduleException e) {
					e.printStackTrace();
				} // catch */ 
				queue.remove(job);
			} // End if 
		} // End while
	} // End scheduleQueuedJobs 
	
	
	public static  
	void scheduleQueuedJobs(TemporalSchedule schedule, SortedQueue<Job> queue, Strategy strategy) {
		
		while(!queue.isEmpty()) {
			/* The strategy determines the next job to place in the schedule.
			 * It also determines the possible placing of such a job in the schedule
			 * The decision must return the SLOT and job that was selected by the 
			 * strategy
			 */
			JobControlBlock jcb = strategy.getNextDecision(queue, schedule);
			Slot slot = jcb.getSlot();
			if( slot != null) {
				Job job = jcb.getJob();
				
				Instant startTime = slot.getAdvent();
				//Period duration = new Period(startTime, TimeHelper.add(startTime, job.getDescription().getEstimatedRuntime()));
				Period duration = new Period(startTime, slot.getCessation());
				job.setDuration(duration);
				job.setResources(slot.getResources().createSubSetWith(job.getDescription().getNumberOfRequestedResources()));
				try {
					schedule.addJob(job);
				} // try
				catch (InvalidTimestampException e) {
					e.printStackTrace();
				} // catch
				catch (IllegalOccupationException e) {
					e.printStackTrace();
				} // catch
				catch (IllegalScheduleException e) {
					e.printStackTrace();
				} // catch */ 
				queue.remove(job);
			} // End if 
		} // End while
	} // End scheduleQueuedJobs 
}// End ScheduleHelper
