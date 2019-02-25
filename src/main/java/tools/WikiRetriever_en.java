package tools;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.map.LatLng;

import entities.Category;
import entities.WikiPage;

public class WikiRetriever_en {
	
	private List<Category> categories;
	
	private int counter = 0;
	
	
	public WikiRetriever_en() {
		categories = new ArrayList<Category>();
	}
	
	
	/**
	 * Retrieves recursively all categories for a specific main category
	 * @param mainCategory The main category for which you want to get the subcategories. eg "Churches in Vienna"
	 */
	public void retrieveCategories(String mainCategory) {
		Category mainCat = new Category(mainCategory, "");
		
		if (!categories.contains(mainCat))
			categories.add(mainCat);
		
		WikiParser_en wp = new WikiParser_en();
		wp.calculateCategories(mainCategory, "");
		List<Category> currentCategories = wp.getCategories();

		for (Category cat : currentCategories) {
			if (counter%50== 0)
				System.out.println("Processed " + counter + " categories.");
			counter++;
			
			if ( cat.getName().equals("People from Vienna") ||  cat.getName().equals("Sportspeople from Vienna") || 
					cat.getName().equals("Austrian people‎") ||
					cat.getName().equals("Austria stubs‎") ||
					cat.getName().equals("Austrian society‎") ||
					cat.getName().equals("Austria templates‎") ||
					cat.getName().equals("Politics of Austria‎")) {
				continue;
			}
			// prevent duplicate entries
			if (!categories.contains(cat))
				categories.add(cat);
			
			retrieveCategories(cat.getName());
		}	
	}
	
	/**
	 * Retrieves all pages for the given category
	 * @param category The category for which you want to get the pages
	 * @return A list of WikiPages for the given category
	 */
	public List<WikiPage> retrievePagesForCategory(String category) {
		WikiParser_en wp = new WikiParser_en();
		wp.calculatePagesForCategory(category, "");	 
		return wp.getPages();
	}
	
	
	/**
	 * Retrieves all relevant informations to the given WikiPage
	 * @param wikiPage The WikiPage for which the geocoords and the text extract shall be aquired
	 * @return The WikiPage with geocoords and text extract or null if no geocoords available (we can only use articles with geocoords)
	 */
	public WikiPage getArticleInfo(WikiPage wikiPage) {
		WikiParser_en wp = new WikiParser_en();	
		String id = wikiPage.getId();
		
		LatLng coords = wp.getCoordsById(id);
		
		if (coords == null) {
			return null;
		}
			
		wikiPage.setCoords(coords);
		wikiPage.setExtract(wp.getExtractById(id));
		
		return wikiPage;
	}
	
	

	public List<Category> getCategories() {
		return categories;
	}
	
}
