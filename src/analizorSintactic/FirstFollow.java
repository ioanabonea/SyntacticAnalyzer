package analizorSintactic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FirstFollow {
	
	private Map<String, ArrayList<ArrayList<String>>> gram;
	private Map<String, Set<String>> first;
	private Map<String, Set<String>> follow;;

		
	public FirstFollow(Hashtable<String, ArrayList<ArrayList<String>>> gram){
		this.gram = gram;
		first = new Hashtable<String, Set<String>>();
		follow = new Hashtable<String, Set<String>>();
		firstGen();
	}
	
	private void firstGen(){
		
		ArrayList<ArrayList<String>> prods;
		
		Iterator it = (Iterator) gram.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        prods = (ArrayList<ArrayList<String>>) pair.getValue();
	        
	        //init neterminale
	        first.put((String) pair.getKey(),new HashSet<String>());
	        
	        //initializare cu terminale
	        for( int i = 0; i <  prods.size(); i++) {
	        	for(int j = 0; j < prods.get(i).size(); j++) {
	        		String atomProd = prods.get(i).get(j);
	        		if(!gram.containsKey(atomProd) && !first.containsKey(atomProd)) {

	        			Set<String> firstSet = new HashSet<String>();
	    	        	firstSet.add(atomProd);
	        			first.put(atomProd,firstSet);
	        		}
	        		
	        	}
	        }
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    Map<String, Set<String>> firstprev;
	    do {
	    	firstprev = new Hashtable<>(first);
	    	
	    	it = (Iterator) gram.entrySet().iterator();
	   
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry) it.next();
		        prods = (ArrayList<ArrayList<String>>) pair.getValue();
		        for( ArrayList<String> prod : prods) {
		        	
		        	if(prod.contains("lambda") && prod.size() == 1) {
	        			Set<String> firstSet = first.get(pair.getKey());
	    	        	firstSet.add("lambda");
	        		}
		        	else if(!gram.containsKey(prod.get(0))) {
		        		Set<String> firstSet = first.get(pair.getKey());
	    	        	firstSet.add(prod.get(0));
		        	}
		        	else {
			        	int i = 1;
			        	int j = prod.size()-1;
			        	Set<String> w = first.get(prod.get(0));	
			        	while(i <= j && first.get(prod.get(i-1)).contains("lambda") ) {
			        		w.addAll(first.get(prod.get(i)));
			        		i++;
			        	}
	        			if( i <= j  || !first.get(prod.get(i-1)).contains("lambda") ) {
	        				w.remove("lambda");
	        			}
	        			first.get(pair.getKey()).addAll(w);
		        	}
		        }
		        
		        it.remove(); // avoids a ConcurrentModificationException
		    }
	    }while( !firstprev.equals(first));
	    
	}
	
	private Set<String> FirstString( ArrayList<String> atomi){
		
		Set<String> w = new HashSet<String>();
		if((atomi.contains("lambda") && atomi.size() == 1) || atomi.size() < 1)
			w.add("lambda");
		else {
			int i = 1;
			int j = atomi.size();
			while (i < j && first.get(atomi.get(i-1)).contains("lambda")) {
				w.addAll(first.get(atomi.get(i)));
				i++;
			}
			if( i <= j  || !first.get(atomi.get(i-1)).contains("lambda") ) {
				w.remove("lambda");
			}
		}
		return w;
	}

}
