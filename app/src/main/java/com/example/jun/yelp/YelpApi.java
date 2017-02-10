package com.example.jun.yelp;

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

    private final String clientId = "iVbg1d0JfvVIpusNJA0SDg";
    private final String clientSecret = "3EkwKuUuSP90d2fdiQ8Fx6TCK7dzHJTx3ZLlg9fEdHoekXEYQr5Oynvxmb9LzwOP";

    private final String accessTokenUrl =
            "https://api.yelp.com/oauth2/token?grant_type=client_credentials" +
            "&client_id=" + clientId +
            "&client_secret=" + clientSecret;

    private final String SEARCH_ENDPOINT  = "https://api.yelp.com/v3/businesses/search";

    // access token for this session
    private AccessTokenModel accessToken;

    /**
     * Constructor. Connects to yelp api to get access token to be used in later queries
     */
    public YelpApi() {
        // get access token
        try {
            // Connect to the acess token url
            URL obj = new URL(accessTokenUrl);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // Send a post request, mozilla user agent header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            // response code for request
            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            accessToken = gson.fromJson(response.toString(), AccessTokenModel.class);

            System.out.println("Got Access Token");
            System.out.println(response.toString());
            System.out.println(accessToken.access_token());

        } catch (Exception e) {
            System.out.println("IM A LITTLE TEAPOT SHORT AND STOUT");
        }
    }

    /**
     * Business Search Method
     * see the url for description of possible parameters and description of response object
     * www.yelp.com/developers/documentation/v3/business_search
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

            // form url
            System.err.println("Accessing URL: " + SEARCH_ENDPOINT + queryString);
            URL obj = new URL(SEARCH_ENDPOINT + queryString);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Authorization", accessToken.token_type() + " " + accessToken.access_token());

            int responseCode = con.getResponseCode();
            System.err.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            result = gson.fromJson(response.toString(), BusinessResponseModel.class);

            System.out.println("API response");
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println("BEAUTY AND THE BEAST");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
        return result;
    }
}
