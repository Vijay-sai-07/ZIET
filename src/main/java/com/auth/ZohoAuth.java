package com.auth;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Logger;

public class ZohoAuth {
    private static HttpClient client = HttpClient.newHttpClient();

    private static Logger log = Logger.getLogger(ZohoAuth.class.getName());

    private final String client_id;
    private final String client_secret;

    private final File fileToSaveTokens;

    private String accessToken;
    private static LocalDateTime time = LocalDateTime.now();

    public ZohoAuth(String client_id, String client_secret, File fileToSaveTokens) throws IOException {
        this.client_id = client_id;
        this.client_secret = client_secret;

        this.fileToSaveTokens = fileToSaveTokens;

    }

    public ZohoAuth(File fileToRetrieveClientDetails, File fileToSaveTokens) throws IOException {
        Properties p = new Properties();
        p.load(new FileReader(fileToRetrieveClientDetails));
        p.load(new FileReader(fileToSaveTokens));

        this.client_id = p.getProperty("client-id");
        this.client_secret = p.getProperty("client-secret");

        this.fileToSaveTokens = fileToSaveTokens;


        retrieveAccessToken();

    }

    public void retrieveAccessToken() throws IOException {
        Properties p = new Properties();
        p.load(new FileReader(fileToSaveTokens));
        accessToken = p.getProperty("access_token");
    }

    public String generateAccessToken(String authCode) throws URISyntaxException, IOException, InterruptedException {
        return generateAccessToken(authCode, false);
    }

    private String generateAccessToken(String authCode, boolean refresh_token) throws IOException, InterruptedException, URISyntaxException {

        String grantType = refresh_token ? "refresh_token" : "authorization_code";
        String code = refresh_token ? "refresh_token" : "code";

        String params = "client_id=" + client_id + "&" +
                "client_secret=" + client_secret + "&" +
                "grant_type=" + grantType + "&" +
                code + "=" + authCode;

        String url = "https://accounts.zoho.com/oauth/v2/token";
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .setHeader("Content-type", "application/x-www-form-urlencoded")
                    .build();
        } catch (URISyntaxException e) {
            log.info("Error In Url: " + url);
//            System.out.println("Please check the url."); // Print
            throw e;
        }

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            log.warning("Interrupted while sending request.");
            throw e;
        }


        if (response.statusCode() == 200) {
            String json = response.body();
            Properties properties = new Properties();

            System.out.println(json);

            String access_token = Utils.extractValue(json, "access_token");
            properties.setProperty("access_token", access_token);

            if (!refresh_token) {
                properties.setProperty("refresh_token", Utils.extractValue(json, "refresh_token"));
            } else {
                properties.setProperty("refresh_token", authCode);
            }

            try {
                try (FileOutputStream out = new FileOutputStream(fileToSaveTokens)) {
                    properties.store(out, "Tokens");
                    log.info("Tokens Saved.");
                }
            } catch (IOException e) {
                log.info("Failed to write on the token file.");
                throw e;
            }


            log.info("Access Token Generated via " + grantType);
            return access_token;
        }

        log.warning("Failed to get Access Token via " + grantType);
        log.warning("Status Code: " + response.statusCode());
        return null;
    }

    public void refreshToken() throws IOException, InterruptedException, URISyntaxException {
        Properties p = new Properties();
        try (FileReader fr = new FileReader(fileToSaveTokens)) {
            p.load(fr);
        }

        String refreshToken = p.getProperty("refresh_token");
        accessToken = generateAccessToken(refreshToken, true);
    }
    
    public String getAuthTokenFirst() {
        try {
//            if (LocalDateTime.now().isAfter(time.plusSeconds(150))) {
//                time = LocalDateTime.now();
                refreshToken();
//            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return accessToken;
    }
    

    public String getAuthToken() {

        return accessToken;
    }

    public void saveClientDetailsToFile(String filename) throws IOException {
        Properties p = new Properties();
        p.setProperty("client-id", client_id);
        p.setProperty("client-secret", client_secret);
        try (FileWriter fw = new FileWriter(filename)) {
            p.store(fw, "Client Details");
        }
    }

}
