package dao;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;


/**
 * data access object for table user
 */
public class UserDAO extends DatabaseConnection {
	private static final UserDAO INSTANCE = new UserDAO();	
	
	private static final Logger log = Logger.getLogger(UserDAO.class.getName());
		
	private UserDAO() {
		super.connect();		
	}
	
	
	
	/**
	 * Checks if the given login data is valid
	 * @param user the username to be checked
	 * @param password the password to be checked
	 * @return true if the login data was valid and false otherwise
	 */
	public boolean login(String user, String password) {
		String pwSecure = get_SHA_256_SecurePassword(password);
		
		String sql = "SELECT * from login where username like ? and password like ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user);
			pstmt.setString(2, pwSecure);
			
			rs = pstmt.executeQuery();
			
			if (rs.next())
	        	return true;
		} catch (SQLException e) {
			log.severe(e.toString());
		} finally {
			closeEverything(rs, pstmt);
		}
		
        return false;
    }
	
	
	/**
	 * Converts the password into a with sha 256 encoded string
	 * @param password the password to be converted
	 * @return a sha 256 representation of the password
	 */
	private String get_SHA_256_SecurePassword(String password) {
		String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            log.severe(e.toString());
        } 
        
        return generatedPassword;
	}
	
	
	
	
	/**
	 * Singleton Pattern   
	 * @return returns the only instance of this DAO  
	 */
	public static UserDAO getInstance() {
        return INSTANCE;
    }
	
}
