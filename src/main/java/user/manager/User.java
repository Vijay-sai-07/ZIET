package user.manager;

import org.json.JSONObject;
import user.accounts.OAuthRedirect;
import user.exceptions.InvalidLoginAttemptException;
import user.exceptions.UserNotFoundException;
import user.utils.Base64Decrypter;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


// ADD Object ZohoAuth // IMPORTANT

public class User {
    private final String email;
    private User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public static User getUser(JSONObject object, HttpSession session) throws InvalidLoginAttemptException, UserNotFoundException {
        if (!object.getString("scope").contains("email")) {
            throw new InvalidLoginAttemptException("Invalid Login Attempt (scope 'email' required)");
        }
        JSONObject obj = new JSONObject(Base64Decrypter.decodeBase64Url(object.getString("id_token").split("\\.")[1]));
        String email = obj.getString("email");
        try {
            ArrayList<Map<String, ?>> userDetails = OAuthRedirect.tableMapper.prepareAndReadMap("SELECT User_id FROM user WHERE EmailId = ?", email);
            System.out.println(userDetails);
            if (userDetails.isEmpty()) {
                System.out.println(email+" redirected to bot");
                throw new UserNotFoundException("User Not Signed Up Yet");
            }
            int User_id = (int) userDetails.get(0).get("User_id");
            session.setAttribute("User_id",User_id);
            session.setAttribute("EmailId",email);
            OAuthRedirect.tableMapper.prepareAndExecute("INSERT INTO user_login (User_id) VALUES (?)",User_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new User(email);
    }
}
