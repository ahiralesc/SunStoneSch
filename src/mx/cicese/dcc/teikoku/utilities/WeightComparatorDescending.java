package mx.cicese.dcc.teikoku.utilities;

import java.util.Comparator;

public class WeightComparatorDescending <V,E> implements Comparator<E> {
	
	@Override
	public int compare(E o1, E o2) {
		
		if( ((Number)o1).doubleValue() >=
			((Number)o2).doubleValue() )
			return -1;  //
		else
			return 1;  //
	}

}
