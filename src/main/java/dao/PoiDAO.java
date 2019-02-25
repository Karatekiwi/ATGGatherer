package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import entities.Poi;
import entities.Section;



/**
 * data access object for table poi
 */
public class PoiDAO extends DatabaseConnection {
	
	private static final  PoiDAO INSTANCE = new  PoiDAO();	
	private final int LIMIT = 500;
	private static final Logger log = Logger.getLogger(PoiDAO.class.getName());
	
	private PoiDAO() {
		super.connect();	
	}
	

	
	/**
	 * Retrieves all entries in the poi table without the sections
	 * @return  a list of all poi entries
	 */
    public List<Poi> getAllPois(boolean english) {
        ArrayList<Poi> poiList = new ArrayList<Poi>();
        String selectQuery = "";
        
        if (english)
        	selectQuery= "select * from poi WHERE language LIKE 'en'";
        else
        	selectQuery= "select * from poi WHERE language LIKE 'de'";
        
        
        ResultSet rs = null;
        		
        try {	    	
	        rs = conn.createStatement().executeQuery(selectQuery);
	        
			while (rs.next()) {
				Poi poi = new Poi();
				poi.setId(Integer.parseInt(rs.getString(1)));
				poi.setWikiId(rs.getString(2));
				poi.setName(rs.getString(3));
				poi.setLatitude(rs.getDouble(4));
				poi.setLongitude(rs.getDouble(5));
				poi.setLanguage(rs.getString(6));
				poi.setCountry(rs.getString(7));
				
				poiList.add(poi); 
			}

		} catch (SQLException e) {
			log.severe(e.toString());
			e.printStackTrace();
		} finally {
			closeEverything(rs, null);
		}

        Collections.sort(poiList);
        
        return poiList;
    }
    
    
    /**
	 * Retrieves all entries in the poi table with only the rudimentary information (to improve speed)
	 * @return  a list of all poi entries
	 */
    public List<Poi> getAllPoisForMap(boolean english) {
        ArrayList<Poi> poiList = new ArrayList<Poi>();
        String selectQuery = "";
        
        if (english)
        	selectQuery= "select * from poi WHERE language LIKE 'en'";
        else
        	selectQuery= "select * from poi WHERE language LIKE 'de'";
        
        
        ResultSet rs = null;
        		
        try {	    	
	        rs = conn.createStatement().executeQuery(selectQuery);
	        
			while (rs.next()) {
				Poi poi = new Poi();
				poi.setId(rs.getInt(1));
				poi.setName(rs.getString(3));
				poi.setLatitude(rs.getDouble(4));
				poi.setLongitude(rs.getDouble(5));
				
				poiList.add(poi); 
			}

		} catch (SQLException e) {
			log.severe(e.toString());
			e.printStackTrace();
		} finally {
			closeEverything(rs, null);
		}
        
        return poiList;
    }
    
    
    

	/**
	 * Retrieves all entries in the pois table including the sections
	 * (for the WebService - to send all data to the client application)
	 * @return  a list of all poi entries
	 */
    public List<Poi> getAllPoisForWS(int offset) {
        HashMap<Integer, Poi> poiList = new HashMap<Integer, Poi>();
        String selectQuery = "select * from poi LIMIT " + LIMIT + " OFFSET " + offset;
        
        
        ResultSet rs = null;
        ResultSet rs2 = null;
        		
        try {	    	
	        rs = conn.createStatement().executeQuery(selectQuery);
	        
	        
			while (rs.next()) {
				Poi poi = new Poi();
				poi.setId(rs.getInt(1));
				poi.setWikiId(rs.getString(2));
				poi.setName(rs.getString(3));
				poi.setLatitude(rs.getDouble(4));
				poi.setLongitude(rs.getDouble(5));
				poi.setLanguage(rs.getString(6));
				poi.setCountry(rs.getString(7));

				String selectQuerySections = "select * from section where pois_id=" + poi.getId();
				rs2 = conn.createStatement().executeQuery(selectQuerySections);
				
				while (rs2.next()) {
					Section sec = new Section(rs2.getInt(1), rs2.getInt(2), rs2.getString(3), rs2.getString(4), rs2.getString(5));	
					poi.addSection(sec);
				}
				
				rs2.close();
				
				poiList.put(poi.getId(), poi); 
			}
			
		} catch (SQLException e) {
			log.severe(e.toString());
			e.printStackTrace();
		} finally {
			closeEverything(rs, null);
		}
        
        List<Poi> pois = new ArrayList<Poi>();
        
        Iterator<Map.Entry<Integer, Poi>> it = poiList.entrySet().iterator();
        
        while (it.hasNext()) {
        	Map.Entry<Integer, Poi> pairs = it.next();
        	pois.add(pairs.getValue());
        	it.remove();
        }
                
        poiList = null;

        Collections.sort(pois);
        
        return pois;
    }
    
    
    
    
    
    public List<Section> getSectionsForPoi(Poi poi) {
    	List<Section> sections = new ArrayList<Section>();
    	String selectQuery = "select * from section where pois_id=" + poi.getId();
    	
    	ResultSet rs = null;
    	
    	try {
    		rs = conn.createStatement().executeQuery(selectQuery);

			while (rs.next()) {
				Section sec = new Section(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5));	
				sections.add(sec);
			}
			
    	} catch (Exception e) {
    		log.severe(e.toString());
    	} finally {
			closeEverything(rs, null);
		}
    	
    	return sections;
    }
    
   
    
    /**
     * Adds multiple poi entries to the database
     * @param pois the poi entries to add to the database
     * @param english true if the language is english
     * @return a string representing a success or error message
     */
    public String addPois(List<Poi> pois, boolean english) {
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;

		String insertPois = "INSERT into poi (id, wiki_id, name, latitude, longitude, language, country) VALUES (?,?,?,?,?,?,?)";
		String insertSections = "INSERT into section (id, pois_id, header, content, category) VALUES (?, ?, ?, ?, ?)";
		String selectIdPois = "SELECT max(id) from poi";
		String selectIdSections = "SELECT max(id) from section";
		
		try {			
	        ps1 = conn.prepareStatement(insertPois);
	        ps2 = conn.prepareStatement(insertSections);
	        
	        Statement s1 = conn.createStatement();
			s1.execute(selectIdPois);    
			ResultSet rs1 = s1.getResultSet(); 

			
			Statement s2 = conn.createStatement();
			s2.execute(selectIdSections);    
			ResultSet rs2 = s2.getResultSet(); 

	        
	        int poi_id = -1;
	        int section_id = -1;
	        
	        if (rs1.next()) {
	        	poi_id = rs1.getInt(1)+1;
	  		}
	        
	        if (rs2.next()) {
	        	section_id = rs2.getInt(1)+1;
	  		}
 	        
	        for (Poi poi : pois) {	 
	        	if (poi.getName().contains("'"))
	        		poi.setName(poi.getName().replace("'", "''"));
	        		        	
	        	String selectQuery = "SELECT * from poi WHERE wiki_id  LIKE '" + poi.getWikiId() + "'";
	        	ResultSet rs = conn.createStatement().executeQuery(selectQuery);
	        	if (rs.next())
	        		continue;	        	
	        	
	        	ps1.setInt(1, poi_id);
	        	ps1.setString(2, poi.getWikiId());
		        ps1.setString(3, poi.getName());
		        ps1.setDouble(4, poi.getLatitude());
		        ps1.setDouble(5, poi.getLongitude());
		        ps1.setString(6, poi.getLanguage());
		        ps1.setString(7, poi.getCountry());
		        
		        ps1.executeUpdate();

		        
		        if (poi.getSections() != null) {
			        for (Section sec : poi.getSections()) {
			        	ps2.setInt(1, section_id);
			        	ps2.setInt(2, poi_id);
			        	ps2.setString(3, sec.getHeader());
			        	ps2.setString(4, sec.getContent());
			        	ps2.setString(5, sec.getCategory());
			        	
			        	ps2.executeUpdate();
			        	
			        	section_id = section_id+1;
			        }
		        }

		        poi_id = poi_id+1;
		        
		        
		        conn.commit();

	        }
	        
	        return " Success!";
	        
	    } catch (SQLException e ) {
	        log.severe(e.toString());
	        
	        if (conn != null) {
	            try {
	                conn.rollback(); 
	            } catch(SQLException excep) {
	               excep.printStackTrace();
	            }
	        }
	        
	        return " Error while adding POIs to database (" + e.getLocalizedMessage() + "). Transaction is being rolled back!";
	    } finally {
			closeEverything(null, ps1);
		}

    }
    
    
  	/**
  	 * Returns the number of pois in the database
  	 * @return number of pois in the poi table
  	 */
    public int getPoiCount_en() {
        String countQuery = "SELECT COUNT(id) as num FROM poi WHERE language LIKE 'en'";
        ResultSet rs = null;
        int count = 0;
        
        Statement stmt = null;
        
        try {
			stmt = conn.createStatement();
	        rs = stmt.executeQuery(countQuery);
	        
	        while (rs.next()) {
	        	count = rs.getInt("num");
	        }
	        
	    } catch (SQLException e ) {
	        log.severe(e.toString());
	    } finally {
			closeEverything(null, stmt);
		}
        
        return count;
    }
    
    
    /**
  	 * Returns the number of pois in the database
  	 * @return number of pois in the poi table
  	 */
    public int getPoiCount() {
        String countQuery = "SELECT COUNT(id) as num FROM poi";
        ResultSet rs = null;
        int count = 0;
        
        Statement stmt = null;
        
        try {
			stmt = conn.createStatement();
	        rs = stmt.executeQuery(countQuery);
	        
	        while (rs.next()) {
	        	count = rs.getInt("num");
	        }
	        
	    } catch (SQLException e ) {
	        log.severe(e.toString());
	    } finally {
			closeEverything(null, stmt);
		}
        
        return count;
    }
    
    /*
    public void addPictureToPoi(Poi poi, byte[] pic) {
    	PreparedStatement ps = null;

		String insertString = "UPDATE " + TABLE_POIS + " SET " + KEY_PIC + "=? "
				+ "WHERE " +  KEY_NAME + " LIKE \"" +  poi.getName() +"\"";
				
		try {
			c = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
			c.setAutoCommit(false);
	        ps = c.prepareStatement(insertString);

	        ps.setBytes(1, pic);
	     	        
	        ps.executeUpdate();
            c.commit();

	    } catch (SQLException e ) {
	        log.severe(e.toString());
	        
	        if (c != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                c.rollback();
	            } catch(SQLException excep) {
	               excep.printStackTrace();
	            }
	        }
	    } 
    }*/
    
    
  
    /**
     * deletes the table entries for the given country and language
     * @param country
     * @param english
     * @return a string representation of the error msg
     */
    public String deleteTables(String country, boolean english) {	
    	String deleteQuery = "";
    	
    	if (english)
    		deleteQuery = "DELETE FROM poi where country='" + country + "' and language='en'";
    	else
    		deleteQuery = "DELETE FROM poi where country='" + country + "' and language='de'";
        
    	Statement stmt;
    	    	
		try {
			stmt = conn.createStatement();
			stmt.execute(deleteQuery);
			conn.commit();
		} catch (SQLException e) {
			log.severe(e.toString());
			return "Deleting tables - error (" + e.getLocalizedMessage() + ")";
		}
				
		return "Deleting tables - success.";
	}
    
    
    /**
     * deletes the section and the poi tables
     * @return a string representation of the error msg
     */
    public String deleteTables() {	
    	String deleteQuery1 = "DELETE from section";
    	String deleteQuery2 = "DELETE from poi";
        
    	Statement stmt;
    	
		try {
			stmt = conn.createStatement();
			stmt.execute(deleteQuery1);
			stmt.execute(deleteQuery2);
			
			conn.commit();
		} catch (SQLException e) {
			log.severe(e.toString());
			return "Deleting tables - error (" + e.getLocalizedMessage() + ")";
		}
				
		return "Deleting tables - success.";
	}


    
    /**
     * Creates the tables
     * @return a string representing a success or error message
     */
	public String createTables() {
		String createQuery = "CREATE TABLE IF NOT EXISTS poi (id integer primary key, wiki_id text, name text, latitude decimal, longitude decimal, language text, country text)";		
		String createQuery_sections = "CREATE TABLE IF NOT EXISTS section (id integer primary key, pois_id integer references pois(id), wiki_id text, header text, content text, category text)";
		String createQuery_settings = "CREATE TABLE IF NOT EXISTS settings (id integer primary key, update_id text)";
		
		String index = "CREATE INDEX lang on poi (language)";
        String index2 = "CREATE INDEX pois_id on section (pois_id)";
        String index3 = "CREATE INDEX cnt on poi (country)";
				
    	Statement stmt = null;
    	
		try {
			stmt = conn.createStatement();
			stmt.execute(createQuery);
			stmt.execute(createQuery_sections);
			stmt.execute(createQuery_settings);
			
			stmt.execute(index);
			stmt.execute(index2);
			stmt.execute(index3);
			
			conn.commit();
		} catch (SQLException e) {
			log.severe(e.toString());
			return "Creating tables - error (" + e.getLocalizedMessage() + ")";
		} finally {
			closeEverything(null, stmt);
		}

		return "Creating tables - success.";
	}
	
		
	/**
	 * Retrieves the current update id
	 * @return the update id
	 */
	public String getUpdateId() {
		String query = "SELECT * FROM settings where id='1'";
        ResultSet rs = null;
        String id = "";
        
        Statement stmt = null;
        
        try {
			stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);
	        
	        while (rs.next()) {
	        	id = rs.getString("update_id");
	        }
	        
	    } catch (SQLException e ) {
	        log.severe(e.toString());
	    } finally {
			closeEverything(null, stmt);
		}

        return id;
	}

	
	/**
	 * Sets the current update ID
	 * @return a string representing a success or error message
	 */
	public String setUpdateId() {		
		String query = "UPDATE settings set update_id='" + UUID.randomUUID().toString() + "' where id=1";
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.execute(query);
			conn.commit();
		} catch (SQLException e) {
			log.severe(e.toString());
			return "Updating uuid - error (" + e.getLocalizedMessage() + ")";
		} finally {
			closeEverything(null, stmt);
		}
				
		return "Updating uuid - success.";
	}
	
	
    
  
	/**
	 * Singleton Pattern   
	 * @return returns the only instance of this DAO  
	 */
	public static PoiDAO getInstance() {
        return INSTANCE;
    }

}
