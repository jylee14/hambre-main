package com.example.jun.hambre_main;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The class represents the current food shown on the food selection activity.
 */
public class FoodModel implements Parcelable{
    private String name; // name of food
    private String culture; // culture of the food
    private String link; // link to the picture

    protected final static String[] tags = {"spicy", "cold"};

    /**
     * Constructor for the food with name, culture and image link
     * @param name name of the food
     * @param culture name of the culture of the food
     * @param link image link to the picture of the food
     */
    public FoodModel(String name, String culture, String link){
        this.name = name;
        this.culture = culture;
        this.link = link;
    }

    /**
     * Constructor for the food using a parcel
     * @param in the parcel that we use to construct the food
     */
    public FoodModel(Parcel in){
        this.name = in.readString();
        this.culture= in.readString();
        this.link = in.readString();
    }

    public String getLink(){ return link; }

    /**
     * Temporary getter for culture until db is finished
     * @return the culture of the FoodModel
     */
    public String getCulture(){
        return culture;
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FoodModel> CREATOR = new Parcelable.Creator<FoodModel>() {
        public FoodModel createFromParcel(Parcel in) {
            return new FoodModel(in);
        }

        public FoodModel[] newArray(int size) {
            return new FoodModel[size];
        }
    };


    @Override
    /** Function used for parcel override. Don't worry about it */
    public int describeContents() {
        return 0;
    }

    @Override
    /** Creates a parcel out of the current object. Don't worry about it */
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(culture);
        dest.writeString(link);
    }
    public static FoodModel[] toFoodModel(Parcelable[] parcelables) {
        FoodModel[] foods = new FoodModel[parcelables.length];
        System.arraycopy(parcelables, 0, foods, 0, parcelables.length);
        return foods;
    }

    @Override
    public String toString() {
        return "FoodModel{" +
                "name='" + name + '\'' +
                ", culture='" + culture + '\'' +
                ", link='" + link +
                '}';
    }
}
