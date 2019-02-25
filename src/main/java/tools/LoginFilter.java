package tools;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.UserController;
 

@WebFilter("/secured/*")  
public class LoginFilter implements Filter {
 
    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	if (((HttpServletRequest) request).getSession().getAttribute(UserController.AUTH_KEY) == null) {
    		//System.out.println("User is not authenticated! Redirecting to login page.");
	        ((HttpServletResponse) response).sendRedirect("/index.xhtml");
	    } 
    	
    	else
	      chain.doFilter(request, response);

    }
 
    public void init(FilterConfig config) throws ServletException {}
 
    public void destroy() {}  
     
}
