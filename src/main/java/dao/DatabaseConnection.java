package dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	
	protected Connection conn;
	
	
	/**
	 * connects to the local posgresql database
	 */
	protected void connect() {
		try {
			conn = getLocalConnection();
		}
		catch (Exception e) {
			try {
				conn = getConnection();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Connection to the postgres database on the Heroko Server
	 * @return the database connection
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	private Connection getConnection() throws URISyntaxException, SQLException  {
	    URI dbUri = new URI(System.getenv("HEROKU_POSTGRESQL_BLUE_URL"));

	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

	    Connection c = DriverManager.getConnection(dbUrl, username, password);
	    c.setAutoCommit(false);
	    
	    return c;		
	}
	
	
	/**
	 * Connection to the local database
	 * @return the database connection
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	private Connection getLocalConnection() throws URISyntaxException, SQLException {
		String url = "jdbc:postgresql://localhost/postgres";
	    String user = "manu";
	    String password = "admin";
	
	    Connection c = DriverManager.getConnection(url, user, password);
	    c.setAutoCommit(false);
	    
	    return c;
	}
	
	
	/**
	 * Closes all open resources 
	 * @param rs The ResultSet to Close
	 * @param stmt The Statement or PreparedStatement to Close
	 * @param con The Connection to Close
	 */
	public static void closeEverything(ResultSet rs, Statement stmt) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}

	}

}
