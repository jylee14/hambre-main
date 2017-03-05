package com.irs.yelp;

/**
 * Model representing the Coordinates object in JSON response from Yelp API
 */
public class CoordinatesDTO {
    private double latitude;
    private double longitude;

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public String toString() {
        return latitude + "," + longitude;
    }
}
