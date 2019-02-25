package tools;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.map.LatLng;

import entities.Category;
import entities.WikiPage;

public class WikiRetriever_de {

	private List<Category> categories;
	private int counter = 0;

	public WikiRetriever_de() {
		categories = new ArrayList<Category>();
	}

	/**
	 * Retrieves recursively all categories for a specific main category
	 * 
	 * @param mainCategory The main category for which you want to get the
	 *                     subcategories. eg "Churches in Vienna"
	 */
	public void retrieveCategories(String mainCategory) {
		Category mainCat = new Category(mainCategory, "");

		if (!categories.contains(mainCat)) {
			categories.add(mainCat);
		}

		WikiParser_de wp = new WikiParser_de();
		wp.calculateCategories(mainCategory, "");
		List<Category> currentCategories = wp.getCategories();

		for (Category cat : currentCategories) {
			if (counter % 50 == 0) {
				System.out.println("Processed " + counter + " categories.");
			}
			counter++;

			if (cat.getName().equals("Person (Wien)") || cat.getName().equals("Person (\u00d6sterreich)‎")
					|| cat.getName().equals("Politik (\u00d6sterreich)‎")
					|| cat.getName().equals("Preisträger (\u00d6sterreich)‎")) {
				continue;
			}
			// prevent duplicate entries
			if (!categories.contains(cat)) {
				categories.add(cat);
			}

			retrieveCategories(cat.getName());
		}
	}

	/**
	 * Retrieves all pages for the given category
	 * 
	 * @param category The category for which you want to get the pages
	 * @return A list of WikiPages for the given category
	 */
	public List<WikiPage> retrievePagesForCategory(String category) {
		WikiParser_de wp = new WikiParser_de();
		wp.calculatePagesForCategory(category, "");
		return wp.getPages();
	}

	/**
	 * Retrieves all relevant informations to the given WikiPage
	 * 
	 * @param wikiPage The WikiPage for which the geocoords and the text extract
	 *                 shall be aquired
	 * @return The WikiPage with geocoords and text extract or null if no geocoords
	 *         available (we can only use articles with geocoords)
	 */
	public WikiPage getArticleInfo(WikiPage wikiPage) {
		WikiParser_de wp = new WikiParser_de();
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
