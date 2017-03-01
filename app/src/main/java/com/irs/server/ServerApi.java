package com.irs.server;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Singleton server api
 */

public class ServerApi {
    private static ServerApi instance = new ServerApi();

    private final String FOOD_ENDPOINT = "http://159.203.246.214/irs/randomFood.php";
    private final int CONNECTION_TRIES = 3;
    private final String LOGIN_ENDPOINT = "http://159.203.246.214/irs/googleLogin.php";

    // cache to store food models so we only retrieve them once
    private DBFoodModel[] foodModelsCache;

    private ServerApi() {
        // TODO: get user api key here (design not finalized)
    }

    public void authServer(GoogleSignInAccount acct) {
        URL url = null;
        HttpURLConnection client = null;

        try {
            url = new URL(LOGIN_ENDPOINT);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestProperty("User-Agent", "Mozilla/5.0");
            client.setReadTimeout(10000);
            client.setConnectTimeout(15000);
            client.setRequestMethod("POST");
            client.setDoInput(true);
            client.setDoOutput(true);

            client.setRequestProperty("id_token", acct.getIdToken());


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("id_token", acct.getIdToken());
            String query = builder.build().getEncodedQuery();

            OutputStream os = client.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            client.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response);
            System.out.println("log in attempt");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public DBFoodModel[] getFood() {

        // if cache is empty, make request to server
        if (foodModelsCache == null) {

            // variables to verify connection
            int tries = 0;
            boolean connected = false;

            while (tries < CONNECTION_TRIES) {
                tries++;

                // get access token
                try {
                    // Connect to the acess token url
                    URL obj = new URL(FOOD_ENDPOINT);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // Send a post request, mozilla user agent header
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");

                    // response code for request
                    int responseCode = con.getResponseCode();

                    // if we did not connect correctly, we should throw an exception and try again
                    if (responseCode != 200) {
                        continue;
                    }

                    connected = true;

                    // Read response into response buffer
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // parse accessToken object from json
                    Gson gson = new Gson();
                    foodModelsCache = gson.fromJson(response.toString(), DBFoodModel[].class);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("THAT'S NOT ON FIRE");
                }
            }
            // TODO: throw exception because we could not connect to the network
        }
        return foodModelsCache;
    }

    public static ServerApi getInstance() {
        return instance;
    }
}
