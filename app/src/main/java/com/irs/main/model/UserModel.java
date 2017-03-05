package com.irs.main.model;

import android.os.AsyncTask;

import com.irs.main.DietType;
import com.irs.server.PreferencesModel;
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

    private UserModel() {}

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

    public void loginAccount(String apiKey) {
        isGuest = false;
        ServerApi server = ServerApi.getInstance();
        PreferencesModel preferences = server.getPreferences(apiKey);
        updateUser(
                preferences.user().email(),
                preferences.user().getDietType(),
                preferences.user().getSortType(),
                preferences.user().distance(),
                preferences.user().api_key());

        System.out.println("UPDATE USER: " + this.sortType.name());

    }

    public void saveToDatabase() {
        if (isGuest) {
            return;
        }
        System.out.println("SAVE TO DB: " + this.maxDist);
        ServerApi.getInstance().setPreferences(apiKey, dietType, sortType, maxDist);
    }

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
}
