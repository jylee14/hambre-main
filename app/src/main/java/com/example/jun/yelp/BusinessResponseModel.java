package com.example.jun.yelp;

import java.io.Serializable;

/**
 * Model representing Yelp response to a business search request
 */
public class BusinessResponseModel implements Serializable{
    private String total;
    private BusinessModel[] businesses;
    private RegionModel region;

    public String total() {
        return total;
    }

    public BusinessModel[] businesses() {
        return businesses;
    }

    public RegionModel region() {
        return region;
    }
}

