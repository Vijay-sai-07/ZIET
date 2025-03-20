package user.manager;

import org.json.JSONObject;
import user.exceptions.InvalidAuthCodeException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Authenticator {
    private static final String client_id = "1000.ES74VKLVUKS8PP89I3OTLNCXLJURGR";
    private static final String client_secret = "4b60540c8bf334f536f4b5f61bb2ae4775b7e7be66";

    private static final String baseUrl = "https://accounts.zoho.com/";

    private static final String auth = "oauth/v2/auth";
    private static final String token = "oauth/v2/token";

    private static final String host = "10.52.0.126";
    private static final String productBaseUrl = "http://" + host + ":8080/FinalBoss/";
    private static final String auth_redirect = productBaseUrl+"captureToken";
    public static final String home_redirect = productBaseUrl+"HomePage.html";
    public static final String bot_redirect = productBaseUrl+"bot.html";
    public static final String login_redirect = home_redirect;

    private static final HttpClient client = HttpClient.newHttpClient();

    public static URI getAuthURL() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("response_type", "code");
        parameters.put("client_id", client_id);
        parameters.put("scope", "email");
        parameters.put("redirect_uri", auth_redirect);
        parameters.put("access_type", "offline");
        parameters.put("prompt", "login");

        // Build the body content (application/x-www-form-urlencoded format)
        StringJoiner params = new StringJoiner("&");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            // URL encode the parameter values to ensure they are correctly encoded for a URL
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
            params.add(entry.getKey() + "=" + encodedValue);
        }

        System.out.println(URI.create(baseUrl + auth + params));
        return URI.create(baseUrl + auth + "?" + params);
    }

    public static JSONObject getTokens(String code) throws InvalidAuthCodeException {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("client_id", client_id);
        parameters.put("client_secret", client_secret);
//        parameters.put("scope", Arrays.stream(OAuthScopes).map(Scope::getOAuth).collect(Collectors.joining(",")))
        parameters.put("grant_type", "authorization_code");
        parameters.put("redirect_uri", auth_redirect);
        parameters.put("code", code);

        // Build the body content (application/x-www-form-urlencoded format)
        StringJoiner params = new StringJoiner("&");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            // URL encode the parameter values to ensure they are correctly encoded for a URL
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
            params.add(entry.getKey() + "=" + encodedValue);
        }
        String url = baseUrl + token;
        String param = params.toString();


        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(param))
                    .setHeader("Content-type", "application/x-www-form-urlencoded")
                    .build();
        } catch (URISyntaxException ignored) {
            return null;
        }

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject res = new JSONObject(response.body());
            if (res.has("error")) {
                throw new InvalidAuthCodeException();
            }
            return res;
        } catch (InterruptedException | IOException ignored) {

        }
        return null;
    }
}