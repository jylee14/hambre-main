package com.irs.server;

import android.graphics.Bitmap;
import android.util.Base64;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.irs.main.DietType;
import com.irs.yelp.SortType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
    private final String LOGIN_ENDPOINT_GOOG = SERVER_BASE + "googleLogin.php";
    private final String LOGIN_ENDPOINT_FB = SERVER_BASE + "facebookLogin.php";
    private final String GET_PREFERENCES_ENDPOINT = SERVER_BASE + "preferences.php";
    private final String SET_PREFERENCES_ENDPOINT = SERVER_BASE + "changePreferences.php";
    private final String CREATE_TAG_ENDPOINT = SERVER_BASE + "createTag.php";
    private final String SET_TAG_FOOD_ENDPOINT = SERVER_BASE + "tagFood.php";
    private final String GET_TAGS_FOOD_ENDPOINT = SERVER_BASE + "getTagsOfFood.php";
    private final String TAG_ENDPOINT = SERVER_BASE + "getTags.php";
    private final String USER_TO_FOOD_ENDPOINT = SERVER_BASE + "userToFood.php";
    private final String USERS_FOOD_ENDPOINT = SERVER_BASE + "getUserFoods.php";
    private final String CREATE_FOOD_ENDPOINT = SERVER_BASE + "createFood.php";

    private final int CONNECTION_TRIES = 3;

    // cache to store food models so we only retrieve them once

    private DBTagDto[] tagModelsCache;
    private DBUsersFoodDto[] usersFoodsCache;
    private ServerApi() {
        // TODO: get user api key here (design not finalized)
    }

    public AuthDto authServer(GoogleSignInAccount acct) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id_token", acct.getIdToken());

        String response = getJSONResponse(LOGIN_ENDPOINT_GOOG, "POST", params, true);
        Gson gson = new Gson();
        AuthDto result = gson.fromJson(response, AuthDto.class);
        return result;
    }


    private AuthDto authServer(String token, String loginEndpoint){
        HashMap<String, String> params = new HashMap<>();
        params.put("id_token", token);

        String response = getJSONResponse(loginEndpoint, "POST", params, true);
        Gson gson = new Gson();
        AuthDto result = gson.fromJson(response, AuthDto.class);
        return result;
    }

    public AuthDto authServer(AccessToken token) {
        return authServer(token.getToken(), LOGIN_ENDPOINT_FB);
    }


    public PreferencesDto getPreferences(String api_key) {
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key + "");

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(GET_PREFERENCES_ENDPOINT, "POST", params, true);

        // return parsed object
        Gson gson = new Gson();
        PreferencesDto result = gson.fromJson(response.toString(), PreferencesDto.class);
        return result;
    }

    public DBTagDto[] getTag() {

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
                    tagModelsCache = gson.fromJson(response.toString(), DBTagDto[].class);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("THAT'S NOT ON FIRE");
                }
            }
            // TODO: throw exception because we could not connect to the network
        }
        return tagModelsCache;


    }

    public DBSetPreferencesDto setPreferences(
            String api_key,
            int vegetarian, int vegan, int kosher, int gluten_free,
            int by_rating, int by_distance,
            int distance) {
        HashMap<String, String> params = new HashMap<>();
        params.put("api_key", api_key + "");

        params.put("vegetarian", "" + vegetarian);
        params.put("vegan", "" + vegan);
        params.put("kosher", "" + kosher);
        params.put("gluten_free", "" + gluten_free);

        params.put("by_rating", "" + by_rating);
        params.put("by_distance", "" + by_distance);

        params.put("distance", "" + distance);

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(SET_PREFERENCES_ENDPOINT, "POST", params, true);

        // return parsed object
        Gson gson = new Gson();
        DBSetPreferencesDto result = gson.fromJson(response.toString(), DBSetPreferencesDto.class);

        return result;
    }

    /**
     * Set users preferences (convenience overload)
     *
     * @param api_key  key of user
     * @param dietType user diet
     * @param sortType sort type
     * @param distance radius to search (in miles)
     * @return response with the updated preferences or an error
     */
    public DBSetPreferencesDto setPreferences(String api_key, DietType dietType, SortType sortType, int distance) {
        int vegetarian = 0;
        int vegan = 0;
        int kosher = 0;
        int gluten_free = 0;

        switch (dietType) {
            case Vegetarian:
                vegetarian = 1;
                break;
            case Vegan:
                vegan = 1;
                break;
            case Kosher:
                kosher = 1;
                break;
            case GlutenFree:
                gluten_free = 1;
                break;
        }

        int by_distance = 0;
        int by_rating = 0;

        switch (sortType) {
            case rating:
                by_rating = 1;
                break;
            case distance:
                by_distance = 1;
                break;
            default:
                by_rating = 1;
                break;
        }


        return setPreferences(
                api_key,
                vegetarian, vegan, kosher, gluten_free,
                by_rating, by_distance,
                distance);
    }

    public DBFoodDto[] getFood() {

        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(FOOD_ENDPOINT, "GET", params, false);


        // return parsed object
        Gson gson = new Gson();
        DBFoodDto[] result = gson.fromJson(response.toString(), DBFoodDto[].class);
        return result;
    }

    public DBLinkTagToFoodDto getLinkTagToFood(int tag_id, int food_id){
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("food_id", food_id + "");
        params.put("tag_id", tag_id + "");
        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(SET_TAG_FOOD_ENDPOINT, "GET", params, true);

        // return parsed object
        Gson gson = new Gson();
        DBLinkTagToFoodDto result = gson.fromJson(response.toString(), DBLinkTagToFoodDto.class);
        return result;
    }

    public DBFoodTagDto[] getFoodTags(int food_id) {
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("food_id", food_id + "");

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(GET_TAGS_FOOD_ENDPOINT, "POST", params, false);

        // return parsed object
        Gson gson = new Gson();
        DBFoodTagDto[] result = gson.fromJson(response.toString(), DBFoodTagDto[].class);
        return result;
    }

    /*Not for version 1.0
    * Returns a null pointer exception
    * */
    public DBUsersFoodDto[] getUsersFood(String api_key){
        HashMap<String, String> params = new HashMap<>();
        params.put("api_key", api_key + "");

        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(USERS_FOOD_ENDPOINT, "POST", params, true);

        // return parsed object
        Gson gson = new Gson();
        DBUsersFoodDto[] result = gson.fromJson(response.toString(), DBUsersFoodDto[].class);

        return result;
    }


    /*Not for version 1.0
    * Error: java.lang.IllegalStateException: Expected BEGIN_ARRAY but was BEGIN_OBJECT at line 1 column 2 path
    * */

    /*NOT TESTABLE*/
    public DBUserToFoodDto[] getUserToFood(String api_key, int food_id, int liked, int disliked){
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<>();
        params.put("food_id", food_id + "");
        params.put("api_key", api_key + "");
        params.put("liked", liked + "");
        params.put("disliked", disliked + "");


        // query FOOD_ENDPOINT for a GET request with params
        String response = getJSONResponse(USER_TO_FOOD_ENDPOINT, "POST", params, true);

        // return parsed object
        Gson gson = new Gson();
        DBUserToFoodDto[] result = gson.fromJson(response.toString(), DBUserToFoodDto[].class);
        return result;
    }

    public DBCreateTagDto createTag(String tag_name) {
        // params are empty (no params needed for get food)
        HashMap<String, String> params = new HashMap<>();
        params.put("tag_name", tag_name + "");

        // query CREATE_TAG_ENDPOINT for a GET request with params
        String response = getJSONResponse(CREATE_TAG_ENDPOINT, "POST", params, true);

        // return parsed object
        Gson gson = new Gson();
        DBCreateTagDto result = gson.fromJson(response.toString(), DBCreateTagDto.class);
        return result;
    }

    public byte[] convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        return bytes;

    }

    public String uploadFood(
            Bitmap picture, String pictureName, String name, String culture,
            String category, String api_key,
            int vegetarian, int vegan, int kosher, int gluten_free) {
        byte[] imageString = convertBitmapToString(picture);

        // hope this works

        HashMap<String, String>  params = new HashMap<>();
        String boundary = "BOUNDARY";
    String imageType = "image/jpeg";

        params.put("name", name);
        params.put("culture", culture);
        params.put("category", category);
        params.put("api_key", api_key);
        params.put("vegetarian", "" + vegetarian);
        params.put("vegan", "" + vegan);
        params.put("kosher", "" + kosher);
        params.put("gluten_free","" + gluten_free);
        params.put("submit", "submit");

        String result = null;

        // variables to verify connection
        int tries = 0;
        boolean connected = false;

        while (tries < CONNECTION_TRIES) {
            tries++;


            URL url = null;
            HttpURLConnection client = null;

            // get access token
            try {

                url = new URL(CREATE_FOOD_ENDPOINT);
                client = (HttpURLConnection) url.openConnection();
                client.setRequestProperty("User-Agent", "Mozilla/5.0");
                client.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                client.setReadTimeout(10000);
                client.setConnectTimeout(15000);
                client.setRequestMethod("POST");
                client.setDoInput(true);
                client.setDoOutput(true);

                DataOutputStream dataOS = new DataOutputStream(client.getOutputStream());

                String imageQueryString = "";
                String queryString = "";

                // add picture
                imageQueryString += "--"+ boundary + "\n";
                imageQueryString  += "Content-Disposition: form-data;";
                imageQueryString  += " name=\"picture\";";
                imageQueryString  += " filename=\"" + pictureName + "\"";
                imageQueryString  += "\n";
                imageQueryString  += "Content-Type: " + imageType;
                imageQueryString  += "\n";
                imageQueryString  += "\n";
                dataOS.writeBytes(imageQueryString);
                dataOS.write(imageString);
                dataOS.writeBytes("\n");
                dataOS.writeBytes("\n");

                Iterator<String> keys = params.keySet().iterator();
                for (int i = 0; i < params.size(); i++) {
                    String key = keys.next();
                    String value = params.get(key);

                    queryString += "--" + boundary + "\n";
                    queryString += "Content-Disposition: form-data;";
                    queryString += " name=\"" + key + "\"";
                    queryString += "\n";
                    queryString += "\n";
                    queryString += value;
                    queryString += "\n";
                }
                queryString += "--" + boundary + "--";


                System.out.println("DATA BEING SENT TO SERVER ---------------");
                System.out.println(queryString);
                System.out.println("-----------------------------------------");

                // Write queryString to the body
                if (!queryString.equals("")) {
                    dataOS.writeBytes(queryString);
                    dataOS.flush();
                    dataOS.close();
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

    /**
     * gets a json string, makes multiple connection attempts and works for arbitrary url/method/param combos
     *
     * @param urlBase     url to call
     * @param method      String value either "GET" or "POST"
     * @param params      params to append to request
     * @param writeToBody should write to body of request or head?
     * @return
     */
    private String getJSONResponse(String urlBase, String method, HashMap<String, String> params,boolean writeToBody) {

        String result = null;

        // variables to verify connection
        int tries = 0;
        boolean connected = false;

        while (tries < CONNECTION_TRIES) {
            tries++;

            // get access token
            try {
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