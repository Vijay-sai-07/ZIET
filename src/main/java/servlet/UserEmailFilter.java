package servlet;

import user.manager.Authenticator;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/allFoods", "/mealFoods", "/historyOfMeals", "/nutrientsPerDay", "/userDetails", "/userDetailsUpdate"})
public class UserEmailFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String email = (String) req.getSession().getAttribute("EmailId");
        System.out.println("User Email Set "+email);
        if (email == null) {
            resp.sendRedirect("/accounts/auth");
            return;
        }
        chain.doFilter(request, response);
    }
}
