package controller;


import java.util.List;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dao.PoiDAO;
import entities.Poi;
import tools.NLPHelper;



@ManagedBean(name="poiBean")
@ViewScoped
public class PoiController {
	
	private List<Poi> pois;
	private Poi selectedPoi;
	private Poi selectedPoiGMap;
	private NLPHelper ts = new NLPHelper("en");
	private NLPHelper ts2 = new NLPHelper("de");
	
	
	public PoiController() {		
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		PoiHolder holderBean = (PoiHolder) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "holderBean");
		pois = holderBean.getPois();
	}
	

	public List<Poi> getPois() {
		return pois;
	}

	public void setPois(List<Poi> pois) {
		this.pois = pois;
	}
	
	public int getSize() {
		return pois.size();
	}
	
	public void setSelectedPoi(Poi selectedPoi) {  
        this.selectedPoi = selectedPoi;  
    }
	
	public Poi getSelectedPoi() {	
		return selectedPoi;
	}
	
	
	public Poi getSelectedPoiGMap() {
		return selectedPoiGMap;
	}


	public void setSelectedPoiGMap(Poi selectedPoiGMap) {
		this.selectedPoiGMap = selectedPoiGMap;
	}


	public String getFormattedText() {
		try {
			
			if (selectedPoi.equals("en"))
				return ts.structureTextForView(PoiDAO.getInstance().getSectionsForPoi(selectedPoi));
			else 
				return ts2.structureTextForView(PoiDAO.getInstance().getSectionsForPoi(selectedPoi));
			
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFormattedTextMap() {
		try {
			if (selectedPoiGMap.equals("en"))
				return ts.structureTextForView(PoiDAO.getInstance().getSectionsForPoi(selectedPoiGMap));
			else 
				return ts2.structureTextForView(PoiDAO.getInstance().getSectionsForPoi(selectedPoiGMap));
		} catch (Exception e) {
			return "";
		}
	}

}
