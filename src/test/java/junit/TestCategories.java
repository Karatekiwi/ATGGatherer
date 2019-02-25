package junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import tools.NLPHelper;
import dao.PoiDAO;
import entities.Poi;
import entities.Section;




public class TestCategories {
	
	private HashMap<String, Integer> counters;
	private List<Poi> pois;
	private List<String> categories;
	
	private static final String LANGUAGE = "de";
	
	public TestCategories() {	
		counters = new HashMap<String, Integer>(5000);
		
		if (LANGUAGE.equals("en")) {
			categories = Arrays.asList("Architecture", "History", "Sports", "Geography");
			pois = PoiDAO.getInstance().getAllPois(true);
		}
		else {
			categories = Arrays.asList("Architektur", "Geschichte", "Sport", "Geografie");
			pois = PoiDAO.getInstance().getAllPois(false);
		}
	}
	
	
	/**
	 * checks if the Wikipedia categories match the categories determined by the Categorizer
	 * can be used to improve the training data
	 */
	@Test
	public void categorize() {
		

		NLPHelper ts = new NLPHelper(LANGUAGE);		
			
		int no_match = 0;
		int match = 0;
		List<Section> nmSection = new ArrayList<Section>();
		List<Poi> nmPois = new ArrayList<Poi>();
		
		for (Poi poi : pois) {
			if (poi.getSections() != null) {
				for (Section sec : poi.getSections()) {
					if (categories.contains(sec.getHeader())) {
							if(!sec.getHeader().equals(ts.getCategoryForSection(sec.getContent(), LANGUAGE))) {
								nmSection.add(sec);
								nmPois.add(poi);
								no_match++;
							}
							else
								match++;
					}
				}
			}
		}
		System.out.println("\n=============================");
		System.out.println("Matched categories: " + match);
		System.out.println("Not matched categories: " + no_match);
		System.out.println("=============================");
		
		int i = 0;
		for (Poi sec : nmPois) {
			System.out.print(sec.getName()); 
			System.out.println(" - " + nmSection.get(i).getHeader() + " " + ts.getCategoryForSection(nmSection.get(i).getContent(), LANGUAGE));
			i++;
		}
		
		System.out.println("\n");
	}
	
	
	/**
	 * calculates the most common categories of the Wikipedia article
	 */
	//@Test
	public void calculateCategories() {
		for (Poi poi : pois) {
			for (Section sec : poi.getSections()) {
				insert (sec.getHeader());
			}
		}
		
		Iterator<Entry<String, Integer>> it = sortHashMapByValues(counters).entrySet().iterator();
		
	    while (it.hasNext()) {
	        Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
	        if (pairs.getValue() > 15)
	        	System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        it.remove(); 
	    }
	}
	
	
	
	void insert (String newEl) {	
		if (counters.containsKey(newEl))
			counters.put(newEl, counters.get(newEl)+1);
		else
			counters.put(newEl, 1);

	}
	
	
	public LinkedHashMap<String, Integer> sortHashMapByValues(HashMap<String, Integer> passedMap) {
		List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		Iterator<Integer> valueIt = mapValues.iterator();
	   
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)){
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String)key, (Integer)val);
					break;
				}

			}
		}
		   
		return sortedMap;
	}
}
