package entities;


/**
 * Represents a Wikipedia category
 */
public class Category {
	private String name;
	private String id;
	
	
	public Category(String name, String id) {
		super();
		this.name = name;
		this.id = id;
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


	@Override
	public String toString() {
		return "Category [name=" + name + ", id=" + id + "]";
	}
	
	@Override
    public boolean equals(Object object) {
        Category cat = (Category) object;
        	
        if (cat != null) {
	        if (cat.getId().equals(id))
	        	return true;
        }

        return false;
    }
	


}
