package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a POI (point of interest)
 */
public class Poi implements Comparable<Poi>, Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String wikiId;
	private String name;
	private double latitude;
	private double longitude;
	private List<Section> sections = new ArrayList<Section>();
	private String language;
	private String country;

	
	public Poi () {
		
	}

	public Poi(String name, String wikiId, double latitude, double longitude, List<Section> sections, String language, String country) {
		super();
		
		this.wikiId = wikiId;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.sections = sections;
		this.language = language;
		this.country = country;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getWikiId() {
		return wikiId;
	}

	public void setWikiId(String wikiId) {
		this.wikiId = wikiId;
	}

	public List<Section> getSections() {
		return sections;
	}
	
	public void addSection(Section section) {
		this.sections.add(section);
	}
	

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}


	@Override
	public int compareTo(Poi o) {
		return ((Integer)(this.id)).compareTo(o.getId());
	}

	
}
