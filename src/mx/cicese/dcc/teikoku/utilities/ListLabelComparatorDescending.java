package mx.cicese.dcc.teikoku.utilities;

import java.util.Comparator;
import mx.cicese.dcc.teikoku.energy.Component;

public class ListLabelComparatorDescending<V> implements Comparator<V> {
	
	@Override
	public int compare(V o1, V o2) {
		if( ((Component)o1).label >= ((Component)o2).label )
			return 1;  
		else
			return -1;
	}

}
