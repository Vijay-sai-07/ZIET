package servlet;

import user.manager.Authenticator;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*") // Apply filter to all requests
public class UserIPFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String path = req.getServletPath();
        String ipAddress = req.getRemoteAddr();
        String sessionIP = (String) session.getAttribute("ipAddress");
        if (sessionIP == null) {
            session.setAttribute("ipAddress", ipAddress);
            chain.doFilter(request, response); // Allow request to continue
        } else if (ipAddress.equals(sessionIP)) {
            chain.doFilter(request, response); // IP matches, proceed
        } else {
            session.invalidate(); // Invalidate session if IP changed
            resp.sendRedirect(Authenticator.home_redirect); // Redirect user to login page
        }
        System.out.println("Original "+ipAddress);
        System.out.println("Session val "+sessionIP);
    }
}
