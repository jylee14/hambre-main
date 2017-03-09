package com.irs.main.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The class represents the current food shown on the food selection activity.
 */
public class FoodDto implements Parcelable {
    private String name; // name of food
    private String culture; // culture of the food
    private String tag;
    private String link; // link to the picture

    /**
     * Constructor for the food with name, culture and image link
     *
     * @param name    name of the food
     * @param culture name of the culture of the food
     * @param link    image link to the picture of the food
     */
    public FoodDto(String name, String culture, String tag, String link) {
        this.name = name;
        this.culture = culture;
        this.tag = tag;
        this.link = link;
    }

    /**
     * Constructor for the food using a parcel
     *
     * @param in the parcel that we use to construct the food
     */
    public FoodDto(Parcel in) {
        this.name = in.readString();
        this.culture = in.readString();
        this.tag = in.readString();
        this.link = in.readString();
    }

    public String getLink() {
        return link;
    }

    /**
     * Temporary getter for culture until db is finished
     *
     * @return the culture of the FoodDto
     */
    public String getCulture() {
        return culture;
    }

    public String getTag() {
        return this.tag;
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FoodDto> CREATOR = new Parcelable.Creator<FoodDto>() {
        public FoodDto createFromParcel(Parcel in) {
            return new FoodDto(in);
        }

        public FoodDto[] newArray(int size) {
            return new FoodDto[size];
        }
    };


    @Override
    /** Function used for parcel override. Don't worry about it */
    public int describeContents() {
        return 0;
    }

    @Override
    /** Creates a parcel out of the current object. Don't worry about it */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(culture);
        dest.writeString(tag);
        dest.writeString(link);
    }

    public static FoodDto[] toFoodModel(Parcelable[] parcelables) {
        FoodDto[] foods = new FoodDto[parcelables.length];
        System.arraycopy(parcelables, 0, foods, 0, parcelables.length);
        return foods;
    }

    @Override
    public String toString() {
        return "FoodDto{" + "name='" + name + '\'' + ", culture='" + culture + '\'' + ", link='" + link + '}';
    }
}
