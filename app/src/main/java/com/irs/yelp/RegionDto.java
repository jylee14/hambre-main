package com.irs.yelp;

/**
 * RegionDto object returned as part of a yelp JSON response
 */
public class RegionDto {
    private CoordinatesDto center;
    public CoordinatesDto center() {
        return center;
    }
}
