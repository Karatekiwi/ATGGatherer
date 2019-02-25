package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import dao.LogDAO;
import entities.Log;



@ManagedBean(name="logBean")
@ViewScoped
public class LogController {

	private LineChartModel dateModel;
	private List<Log> logs;
	private String logText;
	

	
	public LogController() {
		logs = LogDAO.getInstance().getAllLogs();
		logText = "10.10.2014 - now - New design for the WebApp."
				+ "\n25.08.2014 - 09.09.2014 - text classification, database refactoring."
				+ "\n18.08.2014 - 24.08.2014 - Backend POI identification, Text Cleanup."
				+ "\n04.08.2014 - 10.08.2014 - Android Application - GUI, Look and Feel, Location determination."
				+ "\n12.07.2014 - Cleanup and refactoring."
				+ "\n09.07.2014 - Added new feature: category specific retrieval of WikiPages."
				+ "\n09.07.2014 - Cleanup and refactoring."
				+ "\n15.06.2014 - Android Application - including webservice call."
				+ "\n12.06.2014 - Working on Android Application."
				+ "\n05.06.2014 - Calling webservice from Android Application."
				+ "\n04.06.2014 - GUI ctd."
				+ "\n03.06.2014 - Android Application GUI first steps."
				+ "\n01.06.2014 - Android Application init."
				+ "\n31.05.2014 - Using Wikipedia itself for gathering the POIs."
				+ "\n30.05.2014 - Looking for alternatives to using GoogleMaps for the POI retrieval."
				+ "\n30.05.2014 - Working on gathering the POIs."
				+ "\n29.05.2014 - Working on gathering the POIs."
				+ "\n29.05.2014 - Added dummy pois - for testing purposes."
				+ "\n29.05.2014 - Working on adding the database to the web app."
				+ "\n28.05.2014 - added basic login security measures"
				+ "\n28.05.2014 - init";
	}
	
		
	@PostConstruct
	public void init() {
        createDateModel();
    }
	
	
	/**
	 * Creates the data for the log chart for the last 7 days
	 */
	private void createDateModel() {           
        dateModel = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Android Application");
 
        series1.set(getDateString(-6), getLogsForDate(-6));
        series1.set(getDateString(-5), getLogsForDate(-5));
        series1.set(getDateString(-4), getLogsForDate(-4));
        series1.set(getDateString(-3), getLogsForDate(-3));
        series1.set(getDateString(-2), getLogsForDate(-2));
        series1.set(getDateString(-1), getLogsForDate(-1));
        series1.set(getDateString(0), getLogsForDate(0));
 
        dateModel.addSeries(series1);
         
        dateModel.setTitle("WebService Requests in the last week");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("# of Requests");
        dateModel.getAxis(AxisType.Y).setMin(0);
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMax(getDateString(0));
        axis.setTickCount(7);
        axis.setMin(getDateString(-6));
        axis.setTickFormat("%d.%m.%Y");
         
        dateModel.getAxes().put(AxisType.X, axis);
    }
	
	/**
	 * Formats a date into a date/time string.
	 * @param day an integer which stands for the day difference to today. 
	 * 	      eg. 0 returns the current day, -1 returns yesterday, +1 returns tomorrow
	 * @return the day in string format yyyy-MM-dd
	 */
	private String getDateString(int day) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);
        Date todate1 = cal.getTime();
        return df.format(todate1);
	}
	
	
	/**
	 * Returns the number of log entries
	 * @param day an integer which stands for the day difference to today. 
	 * 	      eg. 0 returns the current day, -1 returns yesterday, +1 returns tomorrow
	 * @return the number of log entries for the requested day
	 */
	private int getLogsForDate(int day){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		int count = 0;
		for (Log log : logs) {
			if (getDateString(day).equals(df.format(new Date(log.getDate()))))
				count++;
				
		}
		return count;
	}
	
	public LineChartModel getDateModel() {
		return dateModel;
	}

	public void setDateModel(LineChartModel dateModel) {
		this.dateModel = dateModel;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public String getLogText() {
		return logText;
	}
	
	
}
