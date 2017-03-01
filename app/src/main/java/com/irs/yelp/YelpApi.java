package com.irs.yelp;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class to expose data from yelp as java objects
 */
public class YelpApi {

    // maintain single instance of this class
    private static YelpApi instance = new YelpApi();

    // info needed to call yelp
    private final String clientId = "iVbg1d0JfvVIpusNJA0SDg";
    private final String clientSecret = "3EkwKuUuSP90d2fdiQ8Fx6TCK7dzHJTx3ZLlg9fEdHoekXEYQr5Oynvxmb9LzwOP";

    // url we can get access token at
    private final String ACCESS_TOKEN_ENDPOINT =
            "https://api.yelp.com/oauth2/token?grant_type=client_credentials&client_id=" + clientId
                    + "&client_secret=" + clientSecret;

    // url we can search businesses at
    private final String SEARCH_ENDPOINT = "https://api.yelp.com/v3/businesses/search";

    private final int CONNECTION_TRIES = 3;
    private final int CONNECTION_SUCCESS = 200;
    // access token for this session
    private AccessTokenModel accessToken;

    /**
     * Constructor. Connects to yelp api to get access token to be used in later queries
     * Should only be called within this class to maintain a single instance (one connection,
     * one access token)
     */
    private YelpApi() {

        int tries = 0;
        boolean connected = false;

        while (tries < CONNECTION_TRIES) {
            tries++;

            // get access token
            try {
                // Connect to the acess token url
                URL obj = new URL(ACCESS_TOKEN_ENDPOINT);
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

                // Send a post request, mozilla user agent header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                // response code for request
                int responseCode = con.getResponseCode();

                if (responseCode != CONNECTION_SUCCESS) {
                    continue;
                }

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
                accessToken = gson.fromJson(response.toString(), AccessTokenModel.class);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("IM A LITTLE TEAPOT SHORT AND STOUT");
            }
        }

        if (!connected) {
            //TODO: Fatal Error, could not connect to internet/YELP

            // App should not continue if we did not connect here
            // but maybe there should be a way to try again?

            System.out.println("RUN YOU PIGEONS ITS ROBERT FROST");
        }
    }

    /**
     * Business Search Method
     * see the url for description of possible parameters and description of response object
     * www.yelp.com/developers/documentation/v3/business_search
     *
     * @param params list of params that should be specified according to Yelp Business Search Docs
     * @return response model containing the POJO corresponding to response or null if invalid call
     */
    public BusinessResponseModel businessSearch(HashMap<String, String> params) {

        BusinessResponseModel result = null;

        try {
            // form the query string
            // e.g. ?key1=value1&key2=value2&key3=value3...

            // initial string
            String queryString = "?";

            // loop through the params object appending each pair into the
            // query string
            Iterator<String> keys = params.keySet().iterator();
            for (int i = 0; i < params.size(); i++) {

                String key = keys.next();
                String value = params.get(key);

                queryString += key + "=" + value;

                // don't add & if we are on the last one
                if (i != params.size() - 1) {
                    queryString += "&";
                }
            }

            // Debug output
            System.err.println("Accessing URL: " + SEARCH_ENDPOINT + queryString);

            // create url of endpoint with the generated query string
            URL obj = new URL(SEARCH_ENDPOINT + queryString);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // Form a GET request with the authorization token
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Authorization", accessToken.token_type() + " " + accessToken.access_token());

            // TODO: Error processing on response code
            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                // the api call was invalid

                // fail silently/return null
                return null;
            }
            /// /System.err.println("Response Code: " + responseCode);

            // Read response stream into response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response json into result object
            Gson gson = new Gson();
            result = gson.fromJson(response.toString(), BusinessResponseModel.class);

            //System.err.println("API response");
            //System.err.println(response.toString());
        } catch (Exception e) {
            System.err.println("BEAUTY AND THE BEAST");
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
        }

        // return result or null on exception
        return result;
    }

    /**
     * get the current instance of the YelpAPI object
     *
     * @return instance of api object
     */
    public static YelpApi getInstance() {
        return instance;
    }
}