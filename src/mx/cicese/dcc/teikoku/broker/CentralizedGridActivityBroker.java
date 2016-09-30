/**
 * 
 */
package mx.cicese.dcc.teikoku.broker;

import java.util.Map;
import de.irf.it.rmg.core.teikoku.site.Site;
import de.irf.it.rmg.core.teikoku.site.SiteContainer;
import de.irf.it.rmg.core.teikoku.job.Job;


/**
 * @author ahirales
 *
 */
public final class CentralizedGridActivityBroker extends AbstractGridActivityBroker{


	/**
	 * The CentralizedGridActivityBroker knows all ActivityBrokers in the Grid
	 */
	@Override
	public void initializeKnownActivityBroker(){
		Map<String,Site> sites = SiteContainer.getInstance().getAllAvailableSites();
		for (Site s:sites.values()){
			if (!(s == this.getSite())){
				super.addKnownActivityBroker(s.getActivityBroker());
			}
		}
	}
	
	public void delegate(Job job) {}

}
