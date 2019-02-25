package controller;

import java.util.List;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import entities.Poi;




@ManagedBean(name="mapBean")
@ViewScoped
public class MapController {
	
	private MapModel advancedModel;
	private Marker marker;

	
	public MapController() {
		advancedModel = new DefaultMapModel();
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		PoiHolder holderBean = (PoiHolder) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "holderBean");
		
		List<Poi> pois = holderBean.getPoisMap();
				
		// TODO - different colors for different poi types + add type to map
		if (pois != null) {
			for (Poi poi : pois) {
				advancedModel.addOverlay(new Marker(new LatLng(poi.getLatitude(), poi.getLongitude()), "ID: " + poi.getId() + " - " + poi.getName(), 
						poi, "http://maps.google.com/mapfiles/ms/micons/yellow-dot.png"));
			}
		}
		
	}

	public MapModel getAdvancedModel() {
		return advancedModel;
	}

	public void setAdvancedModel(MapModel advancedModel) {
		this.advancedModel = advancedModel;
	}

	public void onMarkerSelect(OverlaySelectEvent event) {  
        marker = (Marker) event.getOverlay();  
    }

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}


	public void setPois(List<Poi> pois) {
		for (Poi poi : pois) {
			advancedModel.addOverlay(new Marker(new LatLng(poi.getLatitude(), poi.getLongitude()), "ID: " + poi.getId() + " - " + poi.getName(), 
					"(" + poi.getLatitude() + ", " + poi.getLongitude() + ")", "http://maps.google.com/mapfiles/ms/micons/yellow-dot.png"));
		}
	}

}
