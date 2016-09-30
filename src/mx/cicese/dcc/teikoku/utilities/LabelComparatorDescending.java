package mx.cicese.dcc.teikoku.utilities;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

import mx.cicese.dcc.teikoku.energy.Component;

public class LabelComparatorDescending <V,E> implements Comparator<V> {
	
	Map<V,E> data;

	@SuppressWarnings("unchecked")
	public LabelComparatorDescending (Map<V,E> data) {
		this.data = (Map<V, E>) ((Hashtable<V, E>) data).clone();
	}
	
	@Override
	public int compare(V o1, V o2) {
		
		if( ((Component)o1).label >=
			((Component)o2).label )
			return 1;  //
		else
			return -1;  //
	}

}
