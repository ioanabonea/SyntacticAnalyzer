package analizorSintactic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirstFollow {
	
	private Map<String, ArrayList<ArrayList<String>>> gram;
	public Hashtable<String, HashSet<String>> first;
	public Hashtable<String, HashSet<String>> follow;;

		
	public FirstFollow(Hashtable<String, ArrayList<ArrayList<String>>> gram){
		this.gram = gram;
		first = new Hashtable<String, HashSet<String>>();
		follow = new Hashtable<String, HashSet<String>>();
		firstGen();
		followGen();
	}
	
	private void firstGen(){
		
		ArrayList<ArrayList<String>> prods;
		
		Iterator it = (Iterator) gram.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        prods = (ArrayList<ArrayList<String>>) pair.getValue();
	        
	        //init neterminale la multimea vida
	        first.put((String) pair.getKey(),new HashSet<String>());
	        
	        //initializare cu terminale cu el insusi
	        for( int i = 0; i <  prods.size(); i++) {
	        	for(int j = 0; j < prods.get(i).size(); j++) {
	        		String atomProd = prods.get(i).get(j);
	        		if(!gram.containsKey(atomProd) && !first.containsKey(atomProd)) {

	        			HashSet<String> firstSet = new HashSet<String>();
	    	        	firstSet.add(atomProd);
	        			first.put(atomProd,firstSet);
	        		}
	        		
	        	}
	        }
	    }
	    Hashtable<String, HashSet<String>> firstprev;
	    do { //cat timp productiile se modifica si sunt diferite de cele anterioare
	    	firstprev = new Hashtable<String,HashSet<String>>();
	    	for(Map.Entry<String,HashSet<String>> entry : first.entrySet()) {
	    		firstprev.put(entry.getKey(),(HashSet<String>) entry.getValue().clone());
	    	}
	    	
	    	
	    	it = (Iterator) gram.entrySet().iterator();
	   
		    while (it.hasNext()) {  // iau fiecare neterminal in parte 
		        Map.Entry pair = (Map.Entry) it.next();
		        prods = (ArrayList<ArrayList<String>>) pair.getValue();
		        for( ArrayList<String> prod : prods) { // iau fiecare producie in parte
		        	
		        	if(prod.contains("lambda") && prod.size() == 1) {  // daca prod ajuns la final merge intr-un lambda
	        			Set<String> firstSet = first.get(pair.getKey());
	    	        	firstSet.add("lambda");
	        		}
		        	else if(!gram.containsKey(prod.get(0))) {  //daca am gasit un terminal
		        		Set<String> firstSet = first.get(pair.getKey());
	    	        	firstSet.add(prod.get(0));
		        	}
		        	else {
			        	int i = 1;
			        	int j = prod.size()-1;
			        	Set<String> w = (Set<String>) first.get(prod.get(0)).clone();	
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
		    }
	    }while( !firstprev.equals(first));
	    
	}
	
	public Set<String> FirstString( List<String> atomi){
		
		Set<String> w = new HashSet<String>();
		if((atomi.contains("lambda") && atomi.size() == 1) || atomi.size() < 1)
			w.add("lambda");
		else {
			int i = 1;
			int j = atomi.size();
			w.addAll(first.get(atomi.get(0)));
			while (i < j && first.get(atomi.get(i-1)).contains("lambda")) {
				w.addAll(first.get(atomi.get(i)));
				i++;
			}
			if( i < j  || !first.get(atomi.get(i-1)).contains("lambda") ) {
				w.remove("lambda");
			}
		}
		return w;
	}
	
	private void followGen() {
		
		//initializare neterminale
		Iterator it = (Iterator) gram.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(pair.getKey().equals("S")) { //S simbol de start prin conventie
	        	HashSet<String> followSet = new HashSet<String>();
	        	followSet.add("$");
	        	follow.put((String) pair.getKey(),followSet);
	        }
	        else
	        	follow.put((String) pair.getKey(),new HashSet<String>());
	    }
	    
	    Map<String, HashSet<String>> prevFollow;
	    do {
	    	prevFollow = new Hashtable<String,HashSet<String>>();
	    	for( Map.Entry<String, HashSet<String>> entry : follow.entrySet()) {
	    		prevFollow.put(entry.getKey(), (HashSet<String>)entry.getValue().clone());
	    	}
	    	
	    	it = (Iterator) gram.entrySet().iterator();
	 	   
	    	ArrayList<ArrayList<String>> prods;
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry) it.next();
		        prods = (ArrayList<ArrayList<String>>) pair.getValue();
		        for( ArrayList<String> prod : prods) {
		        	
		        	Set<String> netGenSet = follow.get(pair.getKey());
		        	
		        	for( int j = 0; j < prod.size(); j++) {
		        		if(gram.containsKey(prod.get(j))) {
			        		Set<String> M = FirstString(prod.subList(j+1, prod.size())); 
			        		Set<String> crtNetProdSet = follow.get(prod.get(j));
			        		if( j == prod.size()-1 ) {
			        			crtNetProdSet.addAll(netGenSet);	
			        		}
			        		else if(M.contains("lambda")) {
			        			M.remove("lambda");
			        			crtNetProdSet.addAll(netGenSet);
			        			crtNetProdSet.addAll(M);
			        		}
			        		else {
			        			crtNetProdSet.addAll(M);
			        		}
		        		}
		        	}
		        }
		    }
	    	
	    	
	    }while( !prevFollow.equals(follow));
	    
	}

}
