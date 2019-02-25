package entities;

import org.primefaces.model.map.LatLng;


/**
 * Represents a Wikipedia page 
 */
public class WikiPage {
	private String name;
	private String id;
	private String extract;
	private LatLng coords;
	private String wikiCategory;
	
	public WikiPage(String name, String id, String wikiCategory) {
		super();
		this.name = name;
		this.id = id;
		this.wikiCategory = wikiCategory;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getExtract() {
		return extract;
	}

	public void setExtract(String extract) {
		this.extract = extract;
	}

	public LatLng getCoords() {
		return coords;
	}

	public void setCoords(LatLng coords) {
		this.coords = coords;
	}
	
	public String getWikiCategory() {
		return wikiCategory;
	}

	public void setWikiCategory(String wikiCategory) {
		this.wikiCategory = wikiCategory;
	}

	@Override
	public String toString() {
		return "WikiPage [name=" + name + ", id=" + id + ", extract=" + extract
				+ ", coords=" + coords + "]";
	}
	
	
	@Override
    public boolean equals(Object object) {
        WikiPage page = (WikiPage) object;
        	
        if (page.getId().equals(id))
        	return true;

        return false;
    }
	
}
