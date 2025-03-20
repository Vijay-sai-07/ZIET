package BotHandles;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import org.json.JSONObject;

@WebServlet("/fetchData")
public class FetchDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // For demonstration, let's create a dummy JSON response
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "success");
        jsonResponse.put("message", "Here is your requested data!");

        // You can fetch real data from a database here
        // For example, data from a database can be added to this JSONObject.

        // Send JSON response to the client
        response.setCharacterEncoding("UTF-8");
        out.write(jsonResponse.toString());
        out.flush();
        System.out.println("jsadf");
    }
}

