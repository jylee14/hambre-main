package com.irs.yelp;

import java.security.InvalidParameterException;

/**
 * Model representing the Coordinates object in JSON response from Yelp API
 */
public class CoordinatesDto {
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

    public static CoordinatesDto fromString(String input) {
        CoordinatesDto result = new CoordinatesDto();
        if (input.contains(",")) {
            double lat = Double.parseDouble(input.split(",")[0]);
            double lon = Double.parseDouble(input.split(",")[1]);
            result.latitude = lat;
            result.longitude = lon;

            return result;
        } else {
            throw new InvalidParameterException();
        }
    }
}
