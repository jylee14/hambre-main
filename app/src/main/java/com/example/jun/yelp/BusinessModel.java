package com.example.jun.yelp;

import java.math.BigDecimal;

/**
 * Model defining structure of businesses in Yelp JSON response
 */
public class BusinessModel {
    double rating;
    String price;
    String phone;
    String id;
    boolean is_closed;
    CategoriesModel[] categories;
    int review_count;
    String name;
    String url;
    CoordinatesModel coordinates;
    String image_url;
    LocationModel location;

    public double rating() {
        return rating;
    }

    public String price() {
        return price;
    }

    public String phone() {
        return phone;
    }

    public String id() {
        return id;
    }

    public boolean is_closed() {
        return is_closed;
    }

    public CategoriesModel[] categories() {
        return categories;
    }

    public int review_count() {
        return review_count;
    }

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }

    public CoordinatesModel coordinates() {
        return coordinates;
    }

    public String image_url() {
        return image_url;
    }

    public LocationModel location() {
        return location;
    }

    public String toString(){
        return this.name + "\t" + this.phone + "\t" + this.rating + "\t" + this.price + "\t" + this.categories[0] + "\t" + this.location;
    }
}
