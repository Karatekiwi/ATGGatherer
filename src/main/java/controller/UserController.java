package controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import dao.UserDAO;


@ManagedBean(name="userBean")
@SessionScoped
public class UserController implements Serializable {  
  
	private static final long serialVersionUID = 1L;
	
	public static final String AUTH_KEY = "atg.auth.key";
	
	private String username ="";  
    private String password;  
        
    public UserController() {
    	
    }  

    /**
     * Checks the username and password entered by the user and redirects to the overview page if the login data was valid
     * @return a string representing the overview page or nothing if the login failed
     */
	public String login() { 
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		boolean result = UserDAO.getInstance().login(username, password);
		
		if (result) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, username);
            return "/resources/secured/overview.xhtml?faces-redirect=true";
		}
		else {	
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AUTH_KEY);
	        facesContext.addMessage("userBean", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username or password is incorrect!", ""));
		}
		
		return "";
	}  
	
	
	/**
	 * logout
	 * @return a string representing the index page
	 */
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AUTH_KEY);	
		return "/index.xhtml?faces-redirect=true";
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isAuthorized() {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AUTH_KEY) != null;
	}
}  
