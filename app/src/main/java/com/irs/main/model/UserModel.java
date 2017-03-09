package com.irs.main.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.irs.main.DietType;
import com.irs.server.PreferencesDto;
import com.irs.server.ServerApi;
import com.irs.yelp.SortType;

/**
 * This class represents the user preferences and has methods that modify and get the different preferences.
 * follows singleton design pattern
 */
public class UserModel {

    private static final int MAX_DIST = 25;

    // the name of the user
    private String name = "Guest";

    // whether the user is vegan, vegetarian, kosher, gluten free
    private DietType dietType = DietType.None;

    // type of sorting (rating by default)
    private SortType sortType = SortType.rating;

    // maximum distance the user is willing to go to go to a restaurant
    private int maxDist = 1;

    private String apiKey = "";

    private boolean isGuest = true;

    // singleton instance
    private static UserModel instance = new UserModel();

    private UserModel() {
    }

    /**
     * Convenience helper method to update all user fields
     *
     * @param name     name of user
     * @param dietType type of diet
     * @param sortType type of sort
     * @param maxDist  distance to limit
     */
    public void updateUser(String name, DietType dietType, SortType sortType, int maxDist, String apiKey) {
        setName(name);
        setDietType(dietType);
        setSortType(sortType);
        setMaxDist(maxDist);
        setApiKey(apiKey);
    }

    /**
     * Sets up a user with a given name and default values
     *
     * @param name name of user
     */
    public void updateWithDefaults(String name) {
        updateUser(name, DietType.GlutenFree.None, SortType.rating, MAX_DIST, "");
    }

    /**
     * Sets the user to all default values
     */
    public void loginGuest() {
        updateWithDefaults("Guest");
    }

    /**
     * login user with api key
     *
     * @param apiKey key to login with
     */
    public void loginAccount(String apiKey) {

        // can't be guest if has key
        isGuest = false;

        // set preferences and update user
        ServerApi server = ServerApi.getInstance();
        PreferencesDto preferences = server.getPreferences(apiKey);
        updateUser(
                preferences.user().email(),
                preferences.user().getDietType(),
                preferences.user().getSortType(),
                preferences.user().distance(),
                preferences.user().api_key());

        System.out.println("UPDATE USER: " + this.sortType.name());

    }

    /**
     * save user data to databasse
     */
    public void saveToDatabase() {

        // can't save for guest
        if (isGuest) {
            return;
        }

        // call lower layer preference class
        System.out.println("SAVE TO DB: " + this.maxDist);
        ServerApi.getInstance().setPreferences(apiKey, dietType, sortType, maxDist);
    }

    /**
     * async call to saveToDatabase
     */
    public void saveToDatabaseAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... what) {
                System.out.println("SAVING TO DB");
                saveToDatabase();

                // have to do this because return type is Void, thanks Java
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                System.out.println("Updated User info in the database");
            }
        }.execute();
    }

    /**
     * upload a photo to db with user data
     *
     * @param pic      picture to upload
     * @param picName  name of picture
     * @param foodName name of food
     * @param culture  culture data
     * @param category category of food
     * @param dietType what diet it is compatible with
     */
    public void uploadPhoto(Bitmap pic, String picName, String foodName, String culture, String category, DietType dietType) {
        System.out.println("UPLOADING TO DB");
        int gluten_free = 0;
        int vegetarian = 0;
        int vegan = 0;
        int kosher = 0;
        switch (dietType) {
            case GlutenFree:
                gluten_free = 1;
                break;
            case Vegan:
                vegan = 1;
                break;
            case Vegetarian:
                vegetarian = 1;
                break;
            case Kosher:
                kosher = 1;
                break;
        }
        ServerApi.getInstance().uploadFood(pic, picName + ".jpg", foodName, culture, category, apiKey, 0, 0, 0, 0);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxDist(int maxDist) {
        this.maxDist = maxDist;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * Gets the maximum distance that the user is willing to go
     *
     * @return the maximum distance the user is willing
     */
    public int getMaxDist() {
        return maxDist;
    }

    public String getName() {
        return name;
    }

    public DietType getDietType() {
        return dietType;
    }

    public String getDietString() {
        String[] types = {"vegetarian", "vegan", "kosher", "gluten_free", "none"};
        for (int i = 0; i < DietType.values().length; i++) {
            if (DietType.values()[i] == dietType) {
                return types[i];
            }
        }
        return null;
    }

    public SortType getSortType() {
        return sortType;
    }

    public static UserModel getInstance() {
        return instance;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
