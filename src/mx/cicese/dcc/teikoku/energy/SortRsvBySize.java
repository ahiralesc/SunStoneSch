package mx.cicese.dcc.teikoku.energy;

import java.util.Comparator;
import de.irf.it.rmg.core.teikoku.kernel.events.WaitTimeTimeoutEvent;

import de.irf.it.rmg.sim.kuiga.Event;



public class SortRsvBySize implements Comparator<Event> {
	public int compare(Event ev1, Event ev2) {
//		int c1 = ((WaitTimeTimeoutEvent)ev1).getReservation().numCores;
//		int c2 = ((WaitTimeTimeoutEvent)ev1).getReservation().numCores;
		
//		int result = (c1 < c2 ? -1 : ((c1 == c2) ? 0 : 1));
		return 1;
	}
}
