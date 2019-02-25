package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.map.LatLng;

import entities.Category;
import entities.WikiPage;



public class WikiParser_de {
	
	private List<Category> categories;
	private List<WikiPage> pages;
	
	private static final Logger log = Logger.getLogger(WikiParser_de.class.getName());

	public WikiParser_de(){
		categories = new ArrayList<Category>();
		pages = new ArrayList<WikiPage>();
	}
	
	
	/**
	 * Retrieves all sub categories for the given head category
	 * @param mainCategory the head category
	 * @param additionalPageNum an indicator if it's a queue for additional pages
	 */
	public void calculateCategories(String mainCategory, String additionalPageNum) {
		mainCategory = mainCategory.replace(" ", "%20");
		mainCategory = mainCategory.replace("\"", "");
		String query = "";
				
		try {
			if (additionalPageNum.equals("")) 
				query = "https://de.wikipedia.org/w/api.php?format=json&action=query&"
					+ "list=categorymembers&cmtitle=Category:" + mainCategory
					+ "&cmtype=subcat";
			
			else {
				query = "https://de.wikipedia.org/w/api.php?format=json&action=query&"
					+ "list=categorymembers&cmtitle=Category:" + mainCategory
					+ "&cmtype=subcat" + "&cmcontinue=" + URLEncoder.encode(additionalPageNum, "UTF-8");
			}
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		};
		
		String result = "";
		
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httppost = new HttpPost(query);

		    HttpResponse response = httpclient.execute(httppost);
		    
		    HttpEntity entity = response.getEntity();
		    
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
		        StringBuilder sb = new StringBuilder();

		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		        
		        result = sb.toString();
		    }

		} catch (ClientProtocolException e) {
			log.severe(e.toString());
		} catch (IOException e) {
			log.severe(e.toString());
		}
	
		convertJSONtoCategoryMembers(result, mainCategory);
	}

	
	/**
	 * Converts the result string into categories
	 * @param result a result string from a Wikipedia query
	 * @param mainCategory the head category
	 */
	private void convertJSONtoCategoryMembers(String result, String mainCategory) {	
		try {
			JSONObject jObject = new JSONObject(result);
			JSONObject obj1 = jObject.getJSONObject("query");
			JSONArray array1 = obj1.getJSONArray("categorymembers");
	
			for (int i = 0; i < array1.length(); i++) {		
				int pageid = ((JSONObject) array1.get(i)).getInt("pageid");
				String pagetitle = ((JSONObject) array1.get(i)).getString("title");
				pagetitle = pagetitle.substring(10, pagetitle.length());
	
				Category newCat = new Category(pagetitle, pageid + "");
	
				if (!categories.contains(newCat))
					categories.add(newCat);
			}
					
			// if there are additional subcategories
			if (result.contains("cmcontinue")) {
				obj1 = jObject.getJSONObject("query-continue");
				JSONObject obj2 = obj1.getJSONObject("categorymembers");
				String additionalPageNum = obj2.getString("cmcontinue");
				
				calculateCategories(mainCategory, additionalPageNum);
				
			}
		} catch (Exception e) {}

	}

	
	/**
	 * Retrieves all pages for a given category
	 * @param category
	 * @param additionalPageNum an indicator if it's a queue for additional pages
	 */
	public void calculatePagesForCategory(String category, String additionalPageNum) {
		category = category.replace(" ", "%20");
		String query = "";
		
		try {
			if (additionalPageNum.equals("")) 
				query = "https://de.wikipedia.org/w/api.php?format=json&action=query&"
					+ "list=categorymembers&cmtitle=Category:" + category
					+ "&cmtype=page";
			
			else {
				query = "https://de.wikipedia.org/w/api.php?format=json&action=query&"
					+ "list=categorymembers&cmtitle=Category:" + category
					+ "&cmtype=page" + "&cmcontinue=" + URLEncoder.encode(additionalPageNum, "UTF-8");
			}
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		};
				

		String result = "";
		
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httppost = new HttpPost(query);
			
		    HttpResponse response = httpclient.execute(httppost);
		    
		    HttpEntity entity = response.getEntity();
		    
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
		        StringBuilder sb = new StringBuilder();

		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		        
		        result = sb.toString();
		    }

		} catch (Exception e) {
			log.severe(e.toString());
		} 
		
		convertJSONtoPages(result, category);
		
	}
	
	
	/**
	 * Converts the result string into pages
	 * @param result a result string from a Wikipedia query
	 * @param category
	 */
	private void convertJSONtoPages(String result, String category) {
		try {
			JSONObject jObject = new JSONObject(result);
			JSONObject obj1 = jObject.getJSONObject("query");
			JSONArray array1 = obj1.getJSONArray("categorymembers");
	
			for (int i = 0; i < array1.length(); i++) {		
				int pageid = ((JSONObject) array1.get(i)).getInt("pageid");
				String pagetitle = ((JSONObject) array1.get(i)).getString("title");
				
				
				WikiPage newPage = new WikiPage(pagetitle, pageid + "", category);
				// prevent duplicate entries
				if (!pages.contains(newPage))
					pages.add(newPage);
			}
					
			// if there are additional subcategories
			if (result.contains("cmcontinue")) {
				obj1 = jObject.getJSONObject("query-continue");
				JSONObject obj2 = obj1.getJSONObject("categorymembers");
				String additionalPageNum = obj2.getString("cmcontinue");
				
				calculatePagesForCategory(category, additionalPageNum);	
			}
		} catch (Exception e)  {}
	}

	
	/**
	 * Retrieves the Wikipedia content for a given page id
	 * @param id the page id
	 * @return the content of a Wikipedia page
	 */
	public String getExtractById(String id) {
		String query = "https://de.wikipedia.org/w/api.php?format=json&action=query&pageids=" 
			+ id + "&prop=extracts";
		
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(query);

		String result = "";
		
		try {
		    HttpResponse response = httpclient.execute(httppost);
		    
		    HttpEntity entity = response.getEntity();
		    
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
		        StringBuilder sb = new StringBuilder();

		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		        
		        result = sb.toString();
		    }

		} catch (Exception e) {
			log.severe(e.toString());
			return "Sorry. There exists no page to this title!";
		} 
		
		String extract = "";
		
		try {
			JSONObject jObject = new JSONObject(result);
			JSONObject obj1 = jObject.getJSONObject("query");
			JSONObject obj2 = obj1.getJSONObject("pages");
			JSONObject obj3 = obj2.getJSONObject(JSONObject.getNames(obj2)[0]);
			
			// if the json object contains a field named missing - the title was not found
			for (String field : JSONObject.getNames(obj3)) {
				if (field.equals("missing"))
					return "Sorry. There exists no page to this title!";
			}
			
			extract = obj3.getString("extract");
			
			if (extract.equals(""))
				return "Sorry. There exists no page to this title!";
		} catch (Exception e) {}
					
        return extract;			
	}
	
	/**
	 * Retrieves the geocoords for a given page id
	 * @param id the page id
	 * @return a LatLng Object with latitude and longitude or null if the article has no geocoord info
	 */
	public LatLng getCoordsById(String id) {
		String query = "https://de.wikipedia.org/w/api.php?format=json&action=query&pageids=" 
			+ id + "&prop=coordinates";
				
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(query);
		String result = "";
		
		try {
		    HttpResponse response = httpclient.execute(httppost);
		    
		    HttpEntity entity = response.getEntity();
		    
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
		        StringBuilder sb = new StringBuilder();

		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		        
		        result = sb.toString();
		    }

		} catch (Exception e) {
			log.severe(e.toString());
			return null;
		}
		
		JSONObject jObject = new JSONObject(result);
		JSONObject obj1 = jObject.getJSONObject("query");
		JSONObject obj2 = obj1.getJSONObject("pages");
		JSONObject obj3 = obj2.getJSONObject(JSONObject.getNames(obj2)[0]);
		
		// return null if the Wikipedia article has no geocoord info
		if (!result.contains("coordinates"))
			return null;

		JSONArray jArray = obj3.getJSONArray("coordinates");				
		double lat = ((JSONObject) jArray.get(0)).getDouble("lat");
		double lon = ((JSONObject) jArray.get(0)).getDouble("lon");
		
        return new LatLng(lat, lon);			
	}
	
	
	/* working - but probably not needed
	public byte[] getPicture(String text) {
		String url = "";
		text = text.replace(" ", "%20");
		
		String query = "http://en.wikipedia.org/w/api.php?format=json&action=query&titles=" + text + "&prop=pageimages";
		// Create a new HttpClient and Post Header
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(query);

		String result = "";
		
		try {
		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httppost);
		    
		    HttpEntity entity = response.getEntity();
		    
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
		        StringBuilder sb = new StringBuilder();

		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		        
		        result = sb.toString();
		    }

		} catch (ClientProtocolException e) {
			log.severe(e.toString());
		} catch (IOException e) {
			log.severe(e.toString());
		}

		String imageName = "";
		
		JSONObject jObject = new JSONObject(result);
		JSONObject obj1 = jObject.getJSONObject("query");
		JSONObject obj2 = obj1.getJSONObject("pages");
		JSONObject obj3 = obj2.getJSONObject(JSONObject.getNames(obj2)[0]);
		try {
			imageName = obj3.getString("pageimage");
		} catch (JSONException e) {
		}
		
		if (!imageName.equals("")) {			
			query = "https://en.wikipedia.org/w/api.php?action=query&format=json&titles=Image:" + imageName + "&prop=imageinfo&iiprop=url";
			// Create a new HttpClient and Post Header
			httpclient = HttpClientBuilder.create().build();
			httppost = new HttpPost(query);

			result = "";
			
			try {
			    // Execute HTTP Post Request
			    HttpResponse response = httpclient.execute(httppost);
			    
			    HttpEntity entity = response.getEntity();
			    
			    if (entity != null) {
			        InputStream instream = entity.getContent();
			        
			        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
			        StringBuilder sb = new StringBuilder();

			        String line = null;
			        while ((line = reader.readLine()) != null) {
			            sb.append(line + "\n");
			        }
			        
			        result = sb.toString();
			    }

			} catch (ClientProtocolException e) {
				log.severe(e.toString());
			} catch (IOException e) {
				log.severe(e.toString());
			}
						
			jObject = new JSONObject(result);
			obj1 = jObject.getJSONObject("query");
			obj2 = obj1.getJSONObject("pages");
			obj3 = obj2.getJSONObject(JSONObject.getNames(obj2)[0]);
			JSONArray arr1 = obj3.getJSONArray("imageinfo");
			JSONObject obj4 = arr1.getJSONObject(0);
			
			url = obj4.getString("url");
								
		}
		
		
		try {
			return downloadUrl(new URL(url));
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	private byte[] downloadUrl(URL toDownload) {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	    try {
	        byte[] chunk = new byte[4096];
	        int bytesRead;
	        InputStream stream = toDownload.openStream();

	        while ((bytesRead = stream.read(chunk)) > 0) {
	            outputStream.write(chunk, 0, bytesRead);
	        }

	    } catch (IOException e) {
	        log.severe(e.toString());
	        return null;
	    }

	    return outputStream.toByteArray();
	}*/
	
	public List<Category> getCategories() {
		return categories;
	}

	public List<WikiPage> getPages() {
		return pages;
	}
	

	
}
