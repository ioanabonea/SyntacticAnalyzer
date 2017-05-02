package analizorSintactic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class Main {
	
	private static Hashtable<String, ArrayList<ArrayList<String>>> gram = new Hashtable<String, ArrayList<ArrayList<String>>>();
	private static ArrayList<ArrayList<String>> listOfProd;
	
	private static Hashtable<String,Hashtable<String,ArrayList<String>>> tas = new Hashtable<String,Hashtable<String,ArrayList<String>>>();
	private static ArrayList<String> input = new ArrayList<String>();
	private static Stack<String> stiva = new Stack<String>();
	
	public static void main(String[] args) throws IOException {
		
		FileReader file = new FileReader("input.txt");
		BufferedReader buffer = new BufferedReader(file);
		String lineCrt = buffer.readLine();
		
		
		while(  lineCrt !=null ) {
			
				String[] line = lineCrt.split("->");
				String[] prodAux = line[1].split("[|]");
				listOfProd = new ArrayList<ArrayList<String>>();

				ArrayList<String> oneProd;
				
				for(int i = 0; i< prodAux.length; i++){
					oneProd = new ArrayList<String>();
					String[] strings = prodAux[i].trim().split(" ");
					for (String s : strings ) {
						if (!(s == null || s.trim().isEmpty()))
							oneProd.add(s);
					}
					listOfProd.add(oneProd);
				}
				gram.put(line[0].trim(),listOfProd);;
				lineCrt = buffer.readLine();	
				
				if(lineCrt.isEmpty()) {
					lineCrt = buffer.readLine();
					String[] lineAux =lineCrt.split(" ");
					for(int i = 0; i < lineAux.length; i++)
						input.add(lineAux[i]);
					input.add("$");
					break;
				}
		}
		
		//System.out.println(inputWord);
		buffer.close();
		
		Iterator it = gram.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " --> " + pair.getValue());
	    }
	    
	    FirstFollow ff = new FirstFollow(gram);
	    System.out.println();
	    calculTas(ff);
	    checkAccepted();
	
	    
	}
		
	public static void calculTas(FirstFollow ff) {
		
		Iterator it = gram.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Entry) it.next();
			String neterm = (String) pair.getKey();
			for( ArrayList<String> prod : (ArrayList<ArrayList<String>>) pair.getValue()) {
				for( String atom : ff.FirstString(prod)) {
				
						Hashtable<String,ArrayList<String>> linie;
						if(tas.containsKey(neterm)) {
							linie = tas.get(neterm);
						}
						else {
							linie = new Hashtable<String,ArrayList<String>>();
						}
						
						linie.put(atom, prod); 
						tas.put(neterm, linie);
				

				}
				if(ff.FirstString(prod).contains("lambda")) {
					for( String atom : ff.follow.get(neterm)) {
						Hashtable<String,ArrayList<String>> linie;
						if(tas.containsKey(neterm)) {
							linie = tas.get(neterm);
						}
						else {
							linie = new Hashtable<String,ArrayList<String>>();
						}
						linie.put(atom, prod);
						tas.put(neterm, linie);
					}
					if(ff.follow.get(neterm).contains("$")) {
						Hashtable<String,ArrayList<String>> linie;
						if(tas.containsKey(neterm)) {
							linie = tas.get(neterm);
						}
						else {
							linie = new Hashtable<String,ArrayList<String>>();
						}
						linie.put("$", prod);
						tas.put(neterm, linie);
					}
				}
				
			}
			
			
		}
	}

	public static void checkAccepted() {
		
		stiva.add("$");
		stiva.add("S");
		int indInput = 0;
		boolean accepted = true;
		do {
			String X = stiva.lastElement();
			if(!gram.containsKey(X)) {
				if(input.get(indInput).equals(X)) {
					stiva.pop();
					if(!input.get(indInput).equals("$")) {
						indInput++;
					}
				}
				else {
					System.out.println("Cuvantul nu poate fi generat- blocat la token-ul : "+input.get(indInput));
					accepted = false;
					break;
				}
			}
			else {
				ArrayList<String> prod = tas.get(X).get(input.get(indInput));
				System.out.println(X + "->" + prod);
				if(prod != null) {
					stiva.pop();
					if(!(prod.contains("lambda") && prod.size() == 1))
						for(int i = prod.size()-1; i >= 0; i--) {
								stiva.push(prod.get(i));
						}
				}
				else {
					System.out.println("Cuvantul nu poate fi generat ");
					accepted = false;
					break;
				}
			}
			
		}while(!input.get(indInput).equals("$") || stiva.size() > 1);
		
		if(accepted) {
			System.out.println("Cuvantul e acceptat.");
		}
	}
}
