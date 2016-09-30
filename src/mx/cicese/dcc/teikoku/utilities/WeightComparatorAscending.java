package mx.cicese.dcc.teikoku.utilities;

import java.util.Comparator;
import java.util.Map;

public class WeightComparatorAscending<V,E> implements Comparator<E> {
	
		Map<V,E> data;
	
		public WeightComparatorAscending(Map<V,E> data) {
			this.data = data;
		}
		
		@Override
		public int compare(E o1, E o2) {
			
			if( ((Number)data.get(o1)).doubleValue() >=
				((Number)data.get(o2)).doubleValue() )
				return 1;
			else
				return -1;
		}
		
}

