package de.irf.it.rmg.core.teikoku.kernel.events;

import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.util.time.Instant;
import de.irf.it.rmg.sim.kuiga.Event;

public class VoltageFrequencyRegulationEvent  extends Event {
		
		public VoltageFrequencyRegulationEvent(final Instant timestamp, final Site location) {
			super(timestamp, location.getUUID().toString(), location);
		}
	
		
		// -------------------------------------------------------------------------
		// Implementation/Overrides for de.irf.it.rmg.sim.kuiga.Event
		// -------------------------------------------------------------------------

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.irf.it.rmg.core.teikoku.kernel.events.Event#getType()
		 */
		@Override
		final public int getOrdinal() {
			return EventType.VOLTAGE_FREQUENCY_REGULATION.ordinal();
		}
}
