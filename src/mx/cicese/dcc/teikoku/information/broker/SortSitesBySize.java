package mx.cicese.dcc.teikoku.information.broker;

import java.util.Comparator;

public class SortSitesBySize implements Comparator<SiteInformationBroker> {
		public int compare(SiteInformationBroker inf1, SiteInformationBroker inf2) {
			
			int res1 = inf1.getSite().getSiteInformation().getNumberOfAvailableResources();
			int res2 = inf2.getSite().getSiteInformation().getNumberOfAvailableResources();
			int result = (res1 < res2 ? -1 : ((res1 == res2) ? 0 : 1));
			
			result = (result != 0 ? result : (inf1.getSite().getName().compareTo(inf2.getSite().getName()))); 
			
	        return result;
		}
} //SortSitesBySize
