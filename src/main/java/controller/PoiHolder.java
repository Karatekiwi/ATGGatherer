package controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import dao.PoiDAO;
import entities.Poi;



@ManagedBean(name="holderBean")
@SessionScoped
public class PoiHolder {
	
	private boolean english = false;
	private Integer index;
	
	
    public PoiHolder() {    	

    }
    
    public List<Poi> getPois() {
    	if (english)
    		return PoiDAO.getInstance().getAllPois(true);
    	else
    		return PoiDAO.getInstance().getAllPois(false);
	}
    
    
    public List<Poi> getPoisMap() {
    	if (english)
    		return PoiDAO.getInstance().getAllPoisForMap(true);
    	else
    		return PoiDAO.getInstance().getAllPoisForMap(false);
	}
        
    public boolean isEnglish() {
		return english;
	}
	

	public String setEnglish() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String view = ctx.getViewRoot().getViewId();
		index = getIndexOfView(view);
		this.english = true;
		
		return view;
	}

	public String setGerman() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String view = ctx.getViewRoot().getViewId();
		index = getIndexOfView(view);
		
		this.english = false;
		return view;
	}

	public Integer getIndex() {
		return index;
	}
	
	public Integer getIndexOfView(String view) {
		if (view.contains("overview"))
			return 0;
		else if (view.contains("mapview")) 
			return 1;
		else if (view.contains("pois")) 
			return 2;
		else if (view.contains("action")) 
			return 3;
		else 
			return 4;
	}

	public String setIndex(Integer index) {
		this.index = index;
		
		 switch (index) {
			 case 0:
				 return "overview.xhtml?faces-redirect=true";
	         case 1: 
	        	 return "mapview.xhtml?faces-redirect=true";
	         case 2:  
	        	 return "pois.xhtml?faces-redirect=true";
	         case 3:  
	        	 return "action.xhtml?faces-redirect=true";
	         case 4:
	        	 return "log.xhtml?faces-redirect=true";
	         default:
	        	 return "";
		 }

	}

}
