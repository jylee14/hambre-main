package com.example.jun.yelp;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class to wrap Yelp API functionality for easy access
 * credit: brianodisho.org/blog/get-data-using-the-yelp-api-and-java/
 */
public class YelpUtilities {

    public static YelpUtilities instance = new YelpUtilities();
    private YelpApi yelpApi;

    /**
     * Instantiate a YelpAPI object by using YelpAPIFactory with your API keys.
     */
    private YelpUtilities() {
        yelpApi = new YelpApi();
    }

    /**
     * returns a copy of the current yelpApi object
     * @return YelpApi
     */
    public YelpApi getYelpApi() {
        return yelpApi;
    }

    /**
     * Get a new instance of the YelpAPI
     */
    public void reset() {
        instance = new YelpUtilities();
    }

    /**
     * Returns the current instance of the YelpUtilities class
     * @return current instance of YelpUtilities fetched
     */
    public static YelpUtilities getInstance() {
        return instance;
    }


}
