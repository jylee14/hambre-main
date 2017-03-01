package com.irs.main.model;

/**
 * This class represents the user preferences and has methods that modify and get the different preferences.
 */
public class UserModel{

    private static final int MAX_DIST = 25;
    private static boolean initialized = false;
    // the name of the user
    private final String name;

    // whether the user is vegan, vegetarian, kosher, gluten free
    private int prefIndex = -1;

    // maximum distance the user is willing to go to go to a restauraunt
    private int maxDist;

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


    /**
     * code called to initiate database connection, needs to be called before
     * other method calls are made
     */
    public void connect() {
        initialized = true;
        // logon here
    }

    /**
     * Add UserModel item to database
     *
     * @param user UserModel to add into the database
     */
    public void create(UserModel user) {
        // add food to database
    }

    /**
     * Read item from database
     *
     * @param id item to read
     * @return
     */
    public UserModel read(int id) {
        return new UserModel("", -1, MAX_DIST);
    }

    /**
     * Update item in the database
     *
     * @param id      id of entry to update
     * @param newData UserModel containing the desired changes
     * @return same UserModel if successful, null otherwise
     */
    public UserModel update(int id, UserModel newData) {
        return null;
    }

    /**
     * Delete item in the database
     *
     * @param id id of item to delete
     * @return true if successfully deleted, false if failed/user doesnt exist
     */
    public boolean destroy(int id) {
        return true;
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
