package com.irs.main.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the user preferences and has methods that modify and get the different preferences.
 */
public class UserModel implements Parcelable {

    public static final int NO_DIST = -1;

    // the name of the user
    private final String name;

    // whether the user is vegan, vegetarian, kosher, gluten free
    private boolean vegan;
    private boolean vegetarian;
    private boolean kosher;
    private boolean noGlu;

    // maximum distance the user is willing to go to go to a restauraunt
    private int maxDist;

    /**
     * Constructor for the UserModel with all custom fields
     *
     * @param name         the name of the user
     * @param isVegan      whether the user is vegan
     * @param isVegetarian whether the user is vegetarian
     * @param isKosher     whether the user is kosher
     * @param isNoGlu      whether the user is gluten free
     * @param mDist        max distance the user is willing to travel
     */
    public UserModel(String name, boolean isVegan, boolean isVegetarian, boolean isKosher, boolean isNoGlu, int mDist) {
        this.name = name;
        vegan = isVegan;
        vegetarian = isVegetarian;
        kosher = isKosher;
        noGlu = isNoGlu;
        maxDist = mDist;
    }

    /**
     * Constructor for the UserModel given the name. Sets all other preferences to false
     *
     * @param name the name of the user
     */
    public UserModel(String name) { this(name, false, false, false, false, NO_DIST); }

    /**
     * Constructor for the UserModel to pass between activities
     *
     * @param in the parcelable to create the user
     */
    public UserModel(Parcel in) {
        this.name = in.readString();
        boolean[] diet = new boolean[4];
        in.readBooleanArray(diet);

        vegan = diet[0];
        vegetarian = diet[1];
        kosher = diet[2];
        noGlu = diet[3];
        maxDist = in.readInt();
    }

    /**
     * Checks whether the user is vegan
     *
     * @return whether the user is vegan
     */
    public boolean isVegan() {
        return vegan;
    }

    /**
     * Checks whether the user is vegetarian
     *
     * @return whether the user is vegetarian
     */
    public boolean isVegetarian() {
        return vegetarian;
    }

    /**
     * Checks whether the user is kosher
     *
     * @return whether the user is kosher
     */
    public boolean isKosher() {
        return kosher;
    }

    /**
     * Checks whether the user is gluten free
     *
     * @return whether the user is gluten free
     */
    public boolean isNoGlu() { return noGlu; }

    /**
     * Gets the maximum distance that the user is willing to go
     *
     * @return the maximum distance the user is willing
     */
    public int getMaxDist() {
        return maxDist;
    }

    /**
     * Changes the preferences of the user
     *
     * @param none whether all are any preferences that are true
     * @param vegan whether the user is a vegan
     * @param vegetarian whether the user is a vegetarian
     * @param kosher whether the user is kosher
     * @param noGlu whether the user is gluten free
     */
    public void newPrefs(boolean none, boolean vegan, boolean vegetarian, boolean kosher, boolean noGlu, int mDist) {
        if (none) {
            this.vegan = false;
            this.vegetarian = false;
            this.kosher = false;
            this.noGlu = false;
            this.maxDist = NO_DIST;
        } else {
            this.vegan = vegan;
            this.vegetarian = vegetarian ;
            this.kosher = kosher;
            this.noGlu = noGlu;
        }
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };


    @Override
    /**
     * Function for parcelable override. Don't worry about it.
     */
    public int describeContents() {
        return 0;
    }

    @Override
    /**
     * Function for parcelable override. Don't worry about it.
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        boolean[] dietaryPreferences = {vegan, vegetarian, kosher, noGlu};
        dest.writeBooleanArray(dietaryPreferences);
        dest.writeInt(maxDist);
    }
}
