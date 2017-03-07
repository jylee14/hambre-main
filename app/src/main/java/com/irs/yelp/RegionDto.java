package com.irs.yelp;

/**
 * RegionDTO object returned as part of a yelp JSON response
 */
public class RegionDTO {
    private CoordinatesDTO center;
    public CoordinatesDTO center() {
        return center;
    }
}
