package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import entities.Log;


/**
 * data access object for table log
 */
public class LogDAO extends DatabaseConnection {
	private static final LogDAO INSTANCE = new LogDAO();
	
	private static final Logger logger = Logger.getLogger(LogDAO.class.getName());
	
	private LogDAO() {
		super.connect();	
	}

	
	/**
	 * Retrieves all entried in the logs table
	 * @return a list of all log entries
	 */
	public List<Log> getAllLogs() {
		List<Log> logs = new ArrayList<Log>();
		
		String selectQuery = "SELECT  * FROM log";
		ResultSet rs = null;
		
        try {	    	
	        rs = conn.createStatement().executeQuery(selectQuery);
	 
			while (rs.next()) {
				Log log = new Log(rs.getInt("id"), rs.getString("log_text"), rs.getLong("date"));
				logs.add(log);
 
			}
		} catch (SQLException e) {
			logger.severe(e.toString());
		} finally {
			closeEverything(rs, null);
		}
        
        Collections.sort(logs);

		return logs;
	}
	
	/**
	 * Adds a single log entry to the database
	 * @param log the log entry to add to the database
	 * @return a string representing a success or error message
	 */
	public String addLog(Log log) { 
		PreparedStatement ps = null;
		String insertString = "INSERT into log (date, log_text) VALUES (?,?)";
				
		try {
	        ps = conn.prepareStatement(insertString);      
        	ps.setLong(1, log.getDate());
	        ps.setString(2, log.getText());
	        ps.executeUpdate();
	        conn.commit();

	        return " Success!";
	        
	    } catch (SQLException e ) {
	    	logger.severe(e.toString());
	        
	        if (conn != null) {
	            try {
	                conn.rollback(); 
	            } catch(SQLException excep) {
	               excep.printStackTrace();
	            }
	        }
	        
	        return " Error while adding log to database (" + e.getLocalizedMessage() + "). Transaction is being rolled back!";
	    } finally {
			closeEverything(null, ps);
		}

    }
	    

	
	/**
	 * Singleton Pattern   
	 * @return returns the only instance of this DAO  
	 */
	public static LogDAO getInstance() {
	    return INSTANCE;
	}


}
