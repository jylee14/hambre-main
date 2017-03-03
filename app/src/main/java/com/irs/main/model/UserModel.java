package com.irs.main.model;

import com.irs.main.DietType;
import com.irs.main.SortType;

/**
 * This class represents the user preferences and has methods that modify and get the different preferences.
 * follows singleton design pattern
 */
public class UserModel {

    private static final int MAX_DIST = 25;

    // the name of the user
    private final String name;

    // whether the user is vegan, vegetarian, kosher, gluten free
    private DietType dietType;

    // type of sorting
    private SortType sortType;

    // maximum distance the user is willing to go to go to a restauraunt
    private int maxDist;


    private UserModel instance = new UserModel();

    /**
     * Constructor for the UserModel with all custom fields
     *
     * @param name         the name of the user
     * @param pref         the user's dietary preference, -1 for no pref
     * @param mDist        max distance the user is willing to travel
     */
    public UserModel(String name, int pref, int mDist) {

        this.name = name;
        prefIndex = (pref > 4 || pref < -1 ? -1 : pref);
        maxDist = mDist;
    }

    /**
     * Constructor for the UserModel given the name. Sets all other preferences to false
     *
     * @param name the name of the user
     */
    public UserModel(String name) {
        this(name, -1, MAX_DIST);
    }

    public UserModel() {
        this("Guest");
    }

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
     */
    public void newPrefs(int pref, int mDist) {
        prefIndex = (pref > 4 || pref < -1 ? -1 : pref);
    }

    public static UserModel getInstance() {
        return instance;
    }

//
//    /**
//     * Constructor for the UserModel to pass between activities
//     *
//     * @param in the parcelable to create the user
//     */
//    public UserModel(Parcel in) {
//        this.name = in.readString();
//        prefIndex = in.readInt();
//        maxDist = in.readInt();
//    }
//
//    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
//    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
//        public UserModel createFromParcel(Parcel in) {
//            return new UserModel(in);
//        }
//
//        public UserModel[] newArray(int size) {
//            return new UserModel[size];
//        }
//    };
//
//
//    @Override
//    /**
//     * Function for parcelable override. Don't worry about it.
//     */
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    /**
//     * Function for parcelable override. Don't worry about it.
//     */
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.name);
//        dest.writeInt(prefIndex);
//        dest.writeInt(maxDist);
//    }
}
