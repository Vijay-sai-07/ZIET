package user.accounts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data_reader.TableMapper;
import database_handler.DBConnector;
import org.json.JSONObject;

import user.exceptions.InvalidAuthCodeException;
import user.exceptions.UserNotFoundException;
import user.manager.Authenticator;
import user.manager.User;

@WebServlet("/captureToken")
public class OAuthRedirect extends HttpServlet {
    public static TableMapper tableMapper = new TableMapper(DBConnector.publicConnection);
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // TODO Auto-generated method stub
        out.print("<body>");
        if("access_denied".equals(request.getParameter("error"))) {
            out.print("<h1>Access Denied By The User. Don't Trust Us?</h1><br><h2>Don't Worry Everthing is encrypted. FYI. </h2>"); // Except for the Network Admin...
            out.print("<br><h3>IMPORTANT: Also you can visit <a href=\"https://accounts.zoho.com\">Zoho Accounts</a> Anytime to revoke the permission from me. Privacy in your control! (by ZOHO).");
            out.print("</body>");
            return;
        }

        String code = request.getParameter("code");
        User user = null;
        System.out.println(code);
        if(code != null) {
            try {
                JSONObject obj = Authenticator.getTokens(code);
                System.out.println(obj);
                user = User.getUser(obj, request.getSession());
                System.out.println("signed in as: "+user.getEmail());
            } catch (InvalidAuthCodeException e) {
                e.printStackTrace();
            } catch (UserNotFoundException e1){
                response.sendRedirect(Authenticator.bot_redirect);
                return;
            }
        }

        if(user != null) {
            response.sendRedirect(Authenticator.home_redirect);
        }else {
			response.sendRedirect(Authenticator.login_redirect);
        }

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
