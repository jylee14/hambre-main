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
import java.util.HashMap;
import java.util.Iterator;

/**
 * Singleton server api
 */

public class ServerApi {
    private static ServerApi instance = new ServerApi();

    private final String SERVER_BASE = "http://159.203.246.214/irs/";
    private final String FOOD_ENDPOINT = SERVER_BASE + "randomFood.php";
    private final String LOGIN_ENDPOINT = SERVER_BASE + "googleLogin.php";
    private final String GET_PREFERENCES_ENDPOINT = SERVER_BASE + "preferences.php";
    private final String SET_PREFERENCES_ENDPOINT = SERVER_BASE + "changePreferences.php";
    private final String CREATE_TAG_ENDPOINT = SERVER_BASE + "createTag.php";
    private final String SET_TAG_FOOD_ENDPOINT = SERVER_BASE + "tagFood.php";
    private final String GET_TAGS_FOOD_ENDPOINT = SERVER_BASE + "getTagsOfFood.php";
    private final String LIKE_FOOD_ENDPOINT = SERVER_BASE + "createFood.php";
    private final String TAG_ENDPOINT = "SERVER_BASE" + "/getTags.php";


    private final int CONNECTION_TRIES = 3;

    // cache to store food models so we only retrieve them once
    private DBFoodModel[] foodModelsCache;
    private DBTagModel[] tagModelsCache;
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

    public PreferencesModel getPreferences(String api_key) {
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key + "");

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(GET_PREFERENCES_ENDPOINT, "POST", params, true);

        // return parsed object
        Gson gson = new Gson();
        PreferencesModel result = gson.fromJson(response.toString(), PreferencesModel.class);
        return result;
    }

    public DBTagModel[] getTag() {

        // if cache is empty, make request to server
        if (tagModelsCache == null) {

            // variables to verify connection
            int tries = 0;
            boolean connected = false;

            while (tries < CONNECTION_TRIES) {
                tries++;

                // get access token
                try {
                    // Connect to the acess token url
                    URL obj = new URL(TAG_ENDPOINT);
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
                    tagModelsCache = gson.fromJson(response.toString(), DBTagModel[].class);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("THAT'S NOT ON FIRE");
                }
            }
            // TODO: throw exception because we could not connect to the network
        }
        return tagModelsCache;
    }

    public DBFoodModel[] getFood() {

        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(FOOD_ENDPOINT, "GET", params, false);

        // return parsed object
        Gson gson = new Gson();
        DBFoodModel[] result = gson.fromJson(response.toString(), DBFoodModel[].class);
        return result;
    }

    public DBFoodTagModel[] getFoodTags(int food_id) {
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("food_id", food_id + "");

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(GET_TAGS_FOOD_ENDPOINT, "POST", params, false);

        // return parsed object
        Gson gson = new Gson();
        DBFoodTagModel[] result = gson.fromJson(response.toString(), DBFoodTagModel[].class);
        return result;
    }

    /**
     * gets a json string, makes multiple connection attempts and works for arbitrary url/method/param combos
     * @param url url to call
     * @param method String value either "GET" or "POST"
     * @param params params to append to request
     * @return
     */
    private String getJSONResponse(
            String urlBase,
            String method,
            HashMap<String, String> params,
            boolean writeToBody) {

        String result = null;

        // variables to verify connection
        int tries = 0;
        boolean connected = false;

        while (tries < CONNECTION_TRIES) {
            tries++;

            // get access token
            try {
/*
                // construct a url with the params provided
                String urlConstructed = url;

                // initialize query string
                String queryString = "?";

                // iterate through key value pairs appending to querystring
                Iterator<String> keys = params.keySet().iterator();
                for (int i = 0; i < params.size(); i++) {
                    String key = keys.next();
                    String value = params.get(key);

                    queryString += key + "=" + value;

                    if (i != params.size() - 1) {
                        queryString += "&";
                    }
                }

                // but only add query string if it has content
                if (!queryString.equals("?")) {
                    urlConstructed += queryString;
                }

                System.out.println("urlConstructed: " + urlConstructed);
                URL obj = new URL(urlConstructed);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // Send a post request, mozilla user agent header
                con.setRequestMethod(method);
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                // response code for request
                int responseCode = con.getResponseCode();

                System.out.println(responseCode + "");
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

                result = response.toString();

                System.out.println(result);
  */
                // Form query string
                String queryString = "";

                Iterator<String> keys = params.keySet().iterator();
                for (int i = 0; i < params.size(); i++) {
                    String key = keys.next();
                    String value = params.get(key);

                    queryString += key + "=" + value;

                    if (i != params.size() - 1) {
                        queryString += "&";
                    }
                }

                URL url = null;
                HttpURLConnection client = null;

                if (writeToBody) {
                    url = new URL(urlBase);
                    client = null;
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestProperty("User-Agent", "Mozilla/5.0");
                    client.setReadTimeout(10000);
                    client.setConnectTimeout(15000);
                    client.setRequestMethod(method);
                    client.setDoInput(true);
                    client.setDoOutput(true);


                    if (!queryString.equals("")) {
                            OutputStream os = client.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(queryString);
                            writer.flush();
                            writer.close();
                            os.close();

                    }
                } else {
                    if (!queryString.equals("")) {
                        urlBase += "?" + queryString;
                    }

                    url = new URL(urlBase);
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestProperty("User-Agent", "Mozilla/5.0");
                    client.setReadTimeout(10000);
                    client.setConnectTimeout(15000);
                    client.setRequestMethod(method);
                    client.setDoInput(true);
                    client.setDoOutput(true);
                }


                client.connect();

                // read response

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();
                System.out.println(response);
                System.out.println("log in attempt");

                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("THAT'S NOT ON FIRE");
            }
        }

        if (tries == CONNECTION_TRIES) {
            System.out.println("fatal error");
            // TODO: fatal exception here
            // NO WIFI
        }

        return result;
    }

    public static ServerApi getInstance() {
        return instance;
    }
}
