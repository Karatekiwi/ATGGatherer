package controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.map.LatLng;

import dao.PoiDAO;
import entities.Category;
import entities.Poi;
import entities.Section;
import entities.WikiPage;
import tools.NLPHelper;
import tools.WikiRetriever_de;
import tools.WikiRetriever_en;



@ManagedBean
@ViewScoped
public class GathererController implements Serializable {

	private static final long serialVersionUID = 1L;

	private String status;
	private boolean updateComplete = true;
	
	private String country; 
	
	private NLPHelper ts_en = new NLPHelper("en");
	private NLPHelper ts_de = new NLPHelper("de");
	

	// Heroku Server is in a timezone with 2 hours difference
	private int SERVERTIME = 2*60*60*1000;
	final SimpleDateFormat ft = new SimpleDateFormat ("[dd.MM.yyyy HH:mm:ss]");

	@PostConstruct
	public void init() {
		status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Idle";	
	}


	/**
	 * Parse the current content of all Wikipedia sites for a chosen category
	 * saves all information in the database 
	 */
	public void updateData() {
		updateComplete = false;

		final List<Poi> result_en = new ArrayList<Poi>();
		final List<Poi> result_de = new ArrayList<Poi>();

		/** Course of Action
		 * 
		 * 1. Get all subcategories for a given main category
		 * 2. Get all pages in the in step 1. retrieved categories
		 * 3. get article infos to those pages - eliminate those which don't have geocoords
		 * 4. Save all in the sqlite db
		 */

		final WikiRetriever_en wr = new WikiRetriever_en();
		final WikiRetriever_de wr2 = new WikiRetriever_de();
							

		final String mainCategory = country;
		
		status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Retrieving all categories from Wikipedia EN for the main category: " + mainCategory;

		final ExecutorService executor = Executors.newFixedThreadPool(5);
		
		try {
			executor.execute(new Runnable() {
				public void run() {
					wr.retrieveCategories(mainCategory);            	
					status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Success" + " (# categories: " + wr.getCategories().size() + ").\n" + status;
	
					status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Retrieving all pages for the categories.\n"  + status;           	
	
					executor.execute(new Runnable() {
						public void run() {
							final List<WikiPage> pages = new ArrayList<WikiPage>();
							int counter = 0;
							int size = wr.getCategories().size();
							
							List<Category> copy = new ArrayList<Category>(wr.getCategories());
	
							for (Category cat : copy) {
								List<WikiPage> pagesForCategory = wr.retrievePagesForCategory(cat.getName());
								pages.addAll(pagesForCategory);
	
								counter++;
	
								if (size > 10) {
									if (counter%10 == 0)
										status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " categories done.\n"  + status;
	
									else if(counter == size)
										status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " categories done.\n"  + status;
								}
								else 
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " categories done.\n"  + status;
	
							}   
	
							status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Success" + " (# pages: " + pages.size() + ").\n" + status;
							status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Extracting geocoords and text for the pages.\n" + status;	
	
							executor.execute(new Runnable() {
								public void run() {
									int counter = 0;
									int size = pages.size();
									int poisWithGeoCoords_counter = 0;
									int poisWithoutGeoCoords_counter = 0;
									List<String> categoriesWithNoGeoCoords = new ArrayList<String>();
	
									for (WikiPage wikiPage : pages) {
										WikiPage temp = wr.getArticleInfo(wikiPage);
	
										if (temp != null) {
											LatLng coords =  wikiPage.getCoords();                  				
											List<Section> sections = ts_en.getSectionsForPoi(wikiPage.getName(), wikiPage.getExtract());
	
											if (sections != null)
												result_en.add(new Poi(wikiPage.getName(), wikiPage.getId(), coords.getLat(), coords.getLng(), sections, "en", getCountryShort()));
	
											poisWithGeoCoords_counter++;
										}
										else {
											poisWithoutGeoCoords_counter++;
											categoriesWithNoGeoCoords.add(wikiPage.getWikiCategory().replace("%20", " "));
										}
	
										counter ++;
	
										if (counter%25 == 0)
											status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " pages done.\n"  + status;
	
										else if (counter == size)
											status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " pages done.\n"  + status;
	
	
									}
									
									
									PoiDAO.getInstance().deleteTables(getCountryShort(), true);
	
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + poisWithGeoCoords_counter + "/" + size + " of the POIs had geocoords.\n" + status;
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Adding them to the database.\n" + status;
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + PoiDAO.getInstance().addPois(result_en, true) + "\n" + status;
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Done.\n" + status;
	
									System.out.println("geocoords: " + poisWithGeoCoords_counter + "\nno geocoords: " + poisWithoutGeoCoords_counter );
									
									//PoiHolder.pois_en = result_en;
									//printCategories(categoriesWithNoGeoCoords);
	
									/**
									 * Get the whole info again - but now from the Wikipedia DE
									 */
									String category_de = "";
									
									if (mainCategory.contains("Austria"))
										category_de = "Bauwerk in \u00d6sterreich";
									else if (mainCategory.contains("Germany"))
										category_de = "Geb\u00E4ude in Deutschland";
									else if (mainCategory.contains("Switzerland"))
										category_de = "Bauwerk in der Schweiz";
									else if (mainCategory.contains("Vienna"))
										category_de = "Wien";
									else if (mainCategory.contains("Liechtenstein"))
										category_de = "Liechtenstein";
																	
	
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + 
											" Retrieving all categories from Wikipedia DE for the main category: " + category_de + "\n" + status;
	
	
									wr2.retrieveCategories(category_de);            	
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Success" + " (# categories: " + wr2.getCategories().size() + ").\n" + status;
	
									status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Retrieving all pages for the categories.\n"  + status;           	
	
									executor.execute(new Runnable() {
										public void run() {
											final List<WikiPage> pages = new ArrayList<WikiPage>();
											int counter = 0;
											int size = wr2.getCategories().size();
	
											for (Category cat : wr2.getCategories()) {
												pages.addAll(wr2.retrievePagesForCategory(cat.getName()));
	
												counter++;
												if (size > 10) {
													if (counter%10 == 0)
														status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " categories done.\n"  + status;
	
													else if(counter == size)
														status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " categories done.\n"  + status;
												}
												else 
													status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " categories done.\n"  + status;
											}   
	
											status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Success" + " (# pages: " + pages.size() + ").\n" + status;
											status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Extracting geocoords and text for the pages.\n" + status;	
	
											executor.execute(new Runnable() {
												public void run() {
													int counter = 0;
													int size = pages.size();
													int poisWithGeoCoords_counter = 0;
													int poisWithoutGeoCoords_counter = 0;
													List<String> categoriesWithNoGeoCoords = new ArrayList<String>();
	
													for (WikiPage wikiPage : pages) {
														WikiPage temp = wr2.getArticleInfo(wikiPage);
	
														if (temp != null) {
															LatLng coords = wikiPage.getCoords();
															List<Section> sections = ts_de.getSectionsForPoi(wikiPage.getName(), wikiPage.getExtract());
	
															if (sections != null)
																result_de.add(new Poi(wikiPage.getName(), wikiPage.getId(), coords.getLat(), coords.getLng(), sections, "de", getCountryShort()));
	
															poisWithGeoCoords_counter++;
														}
														else {
															categoriesWithNoGeoCoords.add(wikiPage.getWikiCategory().replace("%20", " "));
															poisWithoutGeoCoords_counter++;
														}
														
														counter ++;
	
														if (counter%25 == 0)
															status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " pages done.\n"  + status;
	
														else if(counter == size)
															status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + counter + "/" + size + " pages done.\n"  + status;
	
													}
	
													System.out.println("geocoords: " + poisWithGeoCoords_counter + "\nno geocoords: " + poisWithoutGeoCoords_counter );
													
													PoiDAO.getInstance().deleteTables(getCountryShort(), false);
													
													status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + poisWithGeoCoords_counter + "/" + size + " of the POIs had geocoords.\n" + status;
													status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Adding them to the database.\n" + status;
													status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + PoiDAO.getInstance().addPois(result_de, false) + "\n" + status;
													status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Done.\n" + status;
	
													//PoiHolder.pois_de = result_de;
	
													updateComplete = true;
													//printCategories(categoriesWithNoGeoCoords);
	
													PoiDAO.getInstance().setUpdateId();
													
												}
	
												
											});
										}
									});
								}
	
								/*
								@SuppressWarnings({ "rawtypes", "unchecked" })
								private void printCategories(List<String> categoriesWithNoGeoCoords) {
									//TODO REMOVE
									HashMap<String, Integer> frequencymap = new HashMap<String, Integer>();
	
									for(String a : categoriesWithNoGeoCoords) {
										if(frequencymap.containsKey(a)) {
											frequencymap.put(a, frequencymap.get(a)+1);
										}
										else { 
											frequencymap.put(a, 1); 
										}
									}								
	
									List list = new LinkedList(frequencymap.entrySet());
									Collections.sort(list, new Comparator() {
										public int compare(Object o1, Object o2) {
											return ((Comparable) ((Map.Entry) (o1)).getValue())
													.compareTo(((Map.Entry) (o2)).getValue());
										}
									});
	
									Map result = new LinkedHashMap();
									for (Iterator it = list.iterator(); it.hasNext();) {
										Map.Entry entry = (Map.Entry)it.next();
										result.put(entry.getKey(), entry.getValue());
									}
	
									Iterator it = result.entrySet().iterator();
									while (it.hasNext()) {
										Map.Entry pairs = (Map.Entry)it.next();
										System.out.println(pairs.getKey() + " = " + pairs.getValue());
										it.remove(); // avoids a ConcurrentModificationException
									}
	
	
								} 
								*/
							});
						}
					});
				}
			}); 
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	
	
	

	/**
	 * Resets the database table pois (deletes all stored information) 
	 */
	public void resetTablePois() {
		SimpleDateFormat ft = new SimpleDateFormat ("[dd.MM.yyyy HH:mm:ss]");
		updateComplete = false;
		status = "";
		String delete = PoiDAO.getInstance().deleteTables();
		status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " " + delete + "\n" + status;
		status = ft.format(new Date(System.currentTimeMillis() + SERVERTIME)) + " Reset database complete!\n" + status;
		
		//PoiHolder.pois_de = new ArrayList<Poi>();
		//PoiHolder.pois_en = new ArrayList<Poi>();

		updateComplete = true;

	}



	/**
	 * Stops refreshing the log if the update is complete
	 */
	public void checkIfDone() {
		if(updateComplete) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.execute("PF('poll').stop();");
		}
	}

	
	/**
	 * transfers the category string to a country
	 * @return the country in string representation
	 */
	public String getCountryShort(){
		if (country.contains("Austria"))
			return "Austria";
		else if (country.contains("Germany"))
			return "Germany";
		else if (country.contains("Switzerland"))
			return "Switzerland";
		else
			return "Liechtenstein";
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public boolean isUpdateComplete() {
		return updateComplete;
	}


	public void setUpdateComplete(boolean updateComplete) {
		this.updateComplete = updateComplete;
	}
	
	
	


	public String getCountry() {
		return country;
	}



	public void setCountry(String country) {
		this.country = country;
	}

	
	

}
