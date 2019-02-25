package ws;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import dao.LogDAO;
import dao.PoiDAO;
import entities.Log;
import entities.Poi;


@Path("/updater")
public class UpdateComponent {


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPoiList/{offset}")
	public List<Poi> getPoiList(@Context HttpServletRequest request, 
			@PathParam("offset") int offset) {	
		// add 2 hours, because the system time on the heroku server is 2 hours behind
		String host = "remote host.";
		try {
			host = request.getRemoteHost();
		} catch (Exception e) {
			
		}
		
		LogDAO.getInstance().addLog(new Log("WebService Request from " + host, System.currentTimeMillis() + (60*60*1000)));
		List<Poi> allPois = new ArrayList<Poi>();

		allPois.addAll(PoiDAO.getInstance().getAllPoisForWS(offset));
						
		return allPois;
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPoiEnCount")
	public int getPoiEnCount(@Context HttpServletRequest request) {	
		return PoiDAO.getInstance().getPoiCount_en();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPoiCount")
	public int getPoiCount(@Context HttpServletRequest request) {	
		return PoiDAO.getInstance().getPoiCount();
	}
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getUpdateId")
	public String getUpdateId(@Context HttpServletRequest request) {	
		return PoiDAO.getInstance().getUpdateId();
	}
	
}


