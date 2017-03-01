package com.irs.yelp;

/**
 * Model representing location object in JSON response from Yelp API
 */
public class LocationModel{
    private String city;
    private String country;
    private String address2;
    private String address3;
    private String state;
    private String address1;
    private String zip_code;

    public String getCity(){
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getState() {
        return state;
    }

    public String getAddress1() {
        return address1;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getAddress(){
        String mainAddress = address1 + " ";
        mainAddress += (address2 == null ? "" : address2) + " ";
        return mainAddress + city + " " + state + " " + zip_code;
    }

    public String toString(){
        return getAddress();
    }
}
