package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/userDetailsUpdate")
public class UserDetailsUpdate extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int User_id = (Integer) req.getSession().getAttribute("User_id");
        BufferedReader reader = req.getReader();
        StringBuilder jsonInput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonInput.append(line);
        }
        try {
            UserUpdater.updateUserFromJson(User_id, jsonInput.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
