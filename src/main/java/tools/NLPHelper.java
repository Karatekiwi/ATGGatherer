package tools;


import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import entities.Section; 


/**
 * NLP Helper
 */
public class NLPHelper {

	private String language;
	private Categorizer cat_en;
	private Categorizer cat_de;

	private int emptyCount = 25;
	
	// sections which do not contain information we need for the ATG - we can discard those sections
	private List<String> unecessaryCategories_en = Arrays.asList("Gallery", "Notes", "References", "Further reading", "External links", 
			"Literature", "See also", "Sources", "Notes and references");
	private List<String> unecessaryCategories_de = Arrays.asList("Gallerie", "Galerie", "Quellen", "Einzelnachweise", "Weblinks", 
			"Literatur", "Siehe auch", "Fußnoten", "Anmerkungen", "Bilder", "Weblink", "Bildergalerie", "Zitat", "Weitere Ansichten", 
			"Weitere Informationen", "Fu\u00dfnoten", "Fu&#223;noten", "Fu&szlig;noten", "Referenzen", "Referenz");

	public NLPHelper(String language) {		
		this.language = language;

		cat_en = new Categorizer("en");
		cat_de = new Categorizer("de");	
	}


	/**
	 * uses a document classifier to get the appropriate category for a text 
	 * @param sectionContent the text
	 * @param language language of the text
	 * @return the determined category for the given input text
	 */
	public String getCategoryForSection(String sectionContent, String language) {
		if (language.equals("en"))
			return cat_en.categorizeSections(sectionContent, language);
		else
			return cat_de.categorizeSections(sectionContent, language);
	}


	/**
	 * splits the whole Wikipedia page into different sections
	 * @param name the name of the POI
	 * @param text the text of the POI
	 * @return a list of sections for the POI
	 */
	public List<Section> getSectionsForPoi(String name, String text) {
		List<Section> sections = new ArrayList<Section>();
		text = removeFootnotes(text);
		Document doc = Jsoup.parse(text);
		String content = text;
		
		try {
			Elements h2Tags = doc.select("h2");

			if (h2Tags.size() == 0) {
				if (language.equals("en"))
					sections.add(new Section("General", content, ""));
				else
					sections.add(new Section("Allgemein", content, ""));

				return cleanupSections(sections);
			}
			
			for (Element e : h2Tags) {
				if (e.html().contains("span"))
					e.replaceWith(doc.createElement("h2").text(e.select("span").text()));
			}
			
			text = doc.html();
			
			content = removeLists(text.substring(0, text.indexOf("<h2>" + cleanHtmlForHeadings(h2Tags.get(0).text()) + "</h2>")));			

			// the first section in Wikipedia is always a section with general information about the poi
			if (language.equals("en"))
				sections.add(new Section("General", content, ""));
			else
				sections.add(new Section("Allgemein", content, ""));

			Element nexttag = null;
			int index2 = -1;

			for (int i = 0; i < h2Tags.size()-1; i++) {
				Element tag = h2Tags.get(i);
				nexttag = h2Tags.get(i+1);
				int index = text.indexOf("<h2>" + cleanHtmlForHeadings(tag.text()) + "</h2>") + cleanHtmlForHeadings(tag.text()).length() + 9;
				index2 = text.indexOf("<h2>" + cleanHtmlForHeadings(nexttag.text()) + "</h2>");

				content = removeLists(text.substring(index, index2));

				Section section = null;

				if (unecessaryCategories_en.contains(tag.text()) || unecessaryCategories_de.contains(tag.text()))
					continue;

				if (language.equals("en"))
					section = new Section(tag.text(), content, getCategoryForSection(content, "en"));
				else
					section = new Section(tag.text(), content, getCategoryForSection(content, "de"));


				sections.add(section);			
			}

			if (h2Tags.size() == 1) {
				nexttag = h2Tags.get(0);
				index2 = text.indexOf("<h2>" + cleanHtmlForHeadings(nexttag.text()) + "</h2>");
			}

			index2 += cleanHtmlForHeadings(nexttag.text()).length() + 9;
			content = removeLists(text.substring(index2 , text.length()));

			if (unecessaryCategories_en.contains(nexttag.text()) || unecessaryCategories_de.contains(nexttag.text()))
				return removeEmptySections(sections);

			if (language.equals("en"))
				sections.add(new Section(nexttag.text(), content, getCategoryForSection(content, "en")));
			else
				sections.add(new Section(nexttag.text(), content, getCategoryForSection(content, "de")));


		} catch (Exception e) {	
			return null;
		}

		return removeEmptySections(sections);

	}


	/**
	 * removes empty sections from the content
	 * @param sections a list of sections where some of them might be empty
	 * @return a list of sections with no empty ones
	 */
	private List<Section> removeEmptySections(List<Section> sections) {
		List<Section> finalSections = new ArrayList<Section>();

		for (Section sec : sections) {						
			if(!checkForEmptySections(sec.getContent()))
				finalSections.add(sec);
		}

		return cleanupSections(finalSections);
	}



	/**
	 * cleans up the sections
	 * @param sections the sections
	 * @return a cleaner version of the sections
	 */
	private List<Section> cleanupSections(List<Section> sections) {
		for (Section sec : sections) {	
			Document doc = Jsoup.parse(sec.getContent());

			Elements links = doc.select("p");

			for (Element link : links) {
				if (link.text().contains("Vorlage:Infobox") && link.text().contains("fehlt")) {
					link.remove();
				}
			}

			sec.setContent(doc.toString().replace("/h2&gt; \n", ""));
		}

		return sections;
	}


	/**
	 * gets a clean representation of the header (special character removal)
	 * @param text the text to clean up
	 * @return a clean version of the text
	 */
	private String cleanHtmlForHeadings(String text) {
		String result = escapeHtml(text);

		return result.replace("&ndash;", "–")
				.replace("&ldquo;", "“")
				.replace("&bdquo;", "„")
				.replace("&#324;", "ń")
				.replace("&#536;", "Ș");
	}


	/**
	 * checks if a section if empty
	 * @param content the content of the section
	 * @return true if it is empty, false if not
	 */
	private boolean checkForEmptySections(String content) {		
		if (content.trim().equals(""))
			return true;

		if (Jsoup.parse(content).text().length() < emptyCount)
			return true;

		return false;		
	}


	/** 
	 * removes lists from the text
	 * @param content the content
	 * @return the content with removed lists
	 * @throws Exception
	 */
	private String removeLists(String content) throws Exception {
		Document doc = Jsoup.parse(content);	

		// remove lists
		for (Element el : doc.select("ul")) {
			el.remove();				
		}

		for (Element el : doc.select("ol")) {
			el.remove();				
		}

		/* remove list headers eg: There is a Wiki entry where it says: Other famous people buried in Döbling are: */ 
		for (Element el : doc.select("p")) {
			if (el.text().trim().endsWith(":"))
				el.remove();
		}

		// recheck for empty h3 + h4 headers
		for (Element el : doc.select("h3")) {
			if (el.nextElementSibling() != null) {
				if(el.nextElementSibling().toString().length() < emptyCount
						|| el.nextElementSibling().toString().startsWith("<h3>"))
					el.remove();
			}
			else
				el.remove();

		}


		// recheck for empty h3 + h4 headers
		for (Element el : doc.select("h4")) {
			if (el.nextElementSibling() != null) {
				if(el.nextElementSibling().toString().length() < emptyCount
						|| el.nextElementSibling().toString().startsWith("<h4>"))
					el.remove();
			}
			else
				el.remove();

		}
		
		for (Element el : doc.select("p")) {
			if (el.toString().contains("siehe auch"))
				el.remove();
		}

		return doc.toString();
	}


	/**
	 * removes footnotes from the text
	 * @param text the text
	 * @return a clean text without footnotes
	 */
	public String removeFootnotes(String text) {	
		text = text.replaceAll("\\[\\d*\\]", "");

		Document doc = Jsoup.parse(text);

		Elements ulTags = doc.select("sub");

		for (Element el : ulTags) {
			el.remove();	
		}	

		return doc.toString();
	}


	/**
	 * structures the text for the view in the backend web application
	 * @param sections the sections 
	 * @return a structured representation of the section
	 */
	public String structureTextForView(List<Section> sections) {
		String result = "";

		for (Section sec : sections) {
			//TODO remove the part within the ()
			if (sec.getCategory().equals(""))
				result += "<h3>" + sec.getHeader() + "</h3>";
			else
				result += "<h3>" + sec.getHeader() + " (" + sec.getCategory() + ")" + "</h3>";

			result += sec.getContent()
					.replace("<h3>", "<h4>").replace("</h3>", "</h4>")
					.replace("<h4>", "<h5>").replace("</h4>", "</h5>");

		}

		return result;
	}

}
