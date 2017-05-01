package analizorSintactic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Main {
	
	private static Map<String, ArrayList<ArrayList<String>>> gram = new Hashtable<String, ArrayList<ArrayList<String>>>();
	private static ArrayList<ArrayList<String>> listOfProd;
	private static ArrayList<String> oneProd;

	
	public static void main(String[] args) throws IOException {
		
		FileReader file = new FileReader("input.txt");
		BufferedReader buffer = new BufferedReader(file);
		String lineCrt = buffer.readLine();
		String inputWord = "";
		
		while(  lineCrt !=null ) {
			
				String[] line = lineCrt.split("->");
				String[] prodAux = line[1].split("[|]");
				listOfProd = new ArrayList<ArrayList<String>>();
				
				for(int i = 0; i< prodAux.length; i++){
					oneProd = new ArrayList<String>();
					String[] strings = prodAux[i].split(" ");
					for (String s : strings ) {
						if (!(s == null || s.trim().isEmpty()))
							oneProd.add(s);
					}
					listOfProd.add(oneProd);
				}
				gram.put(line[0],listOfProd);;
				lineCrt = buffer.readLine();		
		}
		
		//System.out.println(inputWord);
		buffer.close();
		
		
		Iterator it = gram.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " --> " + pair.getValue());	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	
	}

}
