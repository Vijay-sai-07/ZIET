package com.auth;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;


public class Setup {
//    private static HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the filename you want to write client details to(Just the not the extension):");
        String cliFileName = sc.nextLine()+".config";
        System.out.println("Enter the filename you want to write token details(Just the name again): ");
        String tokenFile = sc.nextLine()+".protected";
        System.out.println("Enter the client-id: ");
        String client_id = sc.nextLine();
        System.out.println("Enter the client-secret: ");
        String client_secret = sc.nextLine();
        System.out.println("Enter Authorization code: ");
        String authCode = sc.nextLine();

        ZohoAuth auth = new ZohoAuth(client_id,client_secret,new File(tokenFile));
        auth.generateAccessToken(authCode);
        auth.saveClientDetailsToFile(cliFileName);

        System.out.println("Setup Successful.");





    }

}