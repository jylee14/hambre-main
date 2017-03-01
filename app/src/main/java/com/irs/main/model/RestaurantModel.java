package com.irs.main.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model the represents the different restaurants.
 */
public class RestaurantModel implements Parcelable {
    private final String name;
    private final String yelpLink;
    private final String imageLink;
    private final int dollarSigns;

    /**
     * Any restaurant object MUST have the following attributes
     * The RestaurantModel object CANNOT change its fields once created.
     *
     * @param name        name of restaurant
     * @param yelpLink    link to restaurant's yelp page
     * @param imageLink   link to picture representing restaurant
     * @param dollarSigns number of dollar signs restaurant has
     */
    public RestaurantModel(String name, String yelpLink, String imageLink, int dollarSigns) {
        this.name = name;
        this.yelpLink = yelpLink;
        this.imageLink = imageLink;
        this.dollarSigns = dollarSigns;
    }

    /**
     * Construct using a parcel
     *
     * @param in Parcel to create restaurant from
     */
    protected RestaurantModel(Parcel in) {
        name = in.readString();
        yelpLink = in.readString();
        imageLink = in.readString();
        dollarSigns = in.readInt();
    }

    public static final Creator<RestaurantModel> CREATOR = new Creator<RestaurantModel>() {
        @Override
        public RestaurantModel createFromParcel(Parcel in) {
            return new RestaurantModel(in);
        }

        @Override
        public RestaurantModel[] newArray(int size) {
            return new RestaurantModel[size];
        }
    };

    /**
     * Gets the name of the restaurance
     *
     * @return The name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the yelp link to the restaurant
     *
     * @return The yelp link to the restaurant
     */
    public String getYelpLink() {
        return yelpLink;
    }

    /**
     * Gets the link of the image of the restaurant
     *
     * @return
     */
    public String getImageLink() {
        return imageLink;
    }

    /**
     * Gets the dollar signs of the restaurant
     *
     * @return The dollar signs of the restaurant
     */
    public int getDollarSigns() {
        return dollarSigns;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(yelpLink);
        dest.writeString(imageLink);
        dest.writeInt(dollarSigns);
    }
}