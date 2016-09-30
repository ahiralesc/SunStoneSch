package mx.cicese.dcc.teikoku.utilities;


import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtil {

	public static <K,V> Map<K,V> sortByValue(Map<K,V> map, int OrderType) {
		
			List<Map.Entry<K,V>> list = new LinkedList<Map.Entry<K,V>>(map.entrySet());
			quicksort(list,0,list.size()-1, OrderType);
			
			Map<K,V> table = new LinkedHashMap<K,V>();
			for(Map.Entry<K, V> entry : list)
				table.put(entry.getKey(), entry.getValue());
		
			return table;
	 	}
	
	
	public static int q = 0;
	
	private static <K,V> void quicksort(List<Map.Entry<K,V>> A, int p, int r, int OrderType) {
			
		// TODO Auto-generated method stub
		
		if(p < r){
	 		q=partition(A, p, r, OrderType);
			quicksort(A, p, q-1, OrderType);
		}
		if (q >= r){
			;
		}
		else{
			q=q+1;
			quicksort(A, q, r, OrderType);
		}	
		
	}

	private static <K,V> int partition(List<Map.Entry<K,V>> A, int p, int r, int OrderType) {
		// TODO Auto-generated method stub
			Map.Entry<K,V> x= A.get(r);
			int i=0;
			i=p-1;
			
			if (OrderType == 0)		
			  {	
			for(int j=p;j<r;j++)
			{
				if(((Number)A.get(j).getValue()).doubleValue() <= ((Number)x.getValue()).doubleValue()) //Change the sign depending on the order of the list
				{                                                                                       //Ascendent use  Array <= pivot
					i=i+1;                                                                              //Descendent use Array >= pivot
					swap(A,i,j); 
				}
			}
			  }
			else if (OrderType == 1)
			{
				for(int j=p;j<r;j++)
				{
					if(((Number)A.get(j).getValue()).doubleValue() >= ((Number)x.getValue()).doubleValue()) //Change the sign depending the order of the list
					{
						i=i+1;
						swap(A,i,j); 
					}
				}
			}
			else{
				System.out.println("Error en el tipo de ordenamiento, debes escoger 0---> Asc. o 1--->Desc.");
			  }
		swap(A,i+1,r);
			
		return (i+1);
	}

	public static <K,V> void swap(List<Map.Entry<K,V>> A, int dex1, int dex2) 

	{
		Map.Entry<K,V> temp = A.get(dex1);
	    A.set(dex1, A.get(dex2));
	    A.set(dex2, temp);
	}


    static <K,V> void printSequence(List<Map.Entry<K,V>> sorted_sequence) 

	    {
	        for (int i = 0; i < sorted_sequence.size(); i++)
	            System.out.print(sorted_sequence.get(i) + " ");
	    }
    
}
