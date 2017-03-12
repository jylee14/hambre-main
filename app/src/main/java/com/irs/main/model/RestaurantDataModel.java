package com.irs.main.model;

import com.irs.yelp.BusinessDto;
import com.irs.yelp.BusinessResponseDto;
import com.irs.yelp.SortType;
import com.irs.yelp.YelpApi;

import java.util.HashMap;
import java.util.Objects;

/**
 * DataController class which accesses the Yelp API to get Restaurant Data in the form of the
 * Restaurant POJO
 */
public class RestaurantDataModel {
    private YelpApi api = YelpApi.getInstance();

    /**
     * Method to retrieve a list of restaurants based on a search query
     *
     * @param count    number of restaurants to retrieve (max 50)
     * @param sortType how to sort the response
     * @param openNow  retrieve only open restaurants
     * @return list of restaurants
     */
    public static BusinessDto[] getRestaurants(
            String category,
            String query,
            SortType sortType,
            int radius,
            int count,
            boolean openNow) {

        //BusinessDto[] restaurants = new BusinessDto[count];
        String urlName = noSpace(query);

        String categoriesParam = ((category == null || Objects.equals(category, "")) ? "food" : category);
        categoriesParam += ",restaurants";
        if (UserModel.getInstance().getDietType().toYelpString() != null) {
            categoriesParam += "," + UserModel.getInstance().getDietType().toYelpString();
        }

        // Set the POST params based on method parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("latitude", "" + UserModel.getInstance().getLatitude());
        params.put("longitude", "" + UserModel.getInstance().getLongitude());
        params.put("categories", categoriesParam);
        params.put("term", urlName);
        params.put("sort_by", "" + sortType);
        params.put("radius", "" + radius);
        params.put("limit", "" + count);
        params.put("open_now", openNow ? "true" : "false");

        System.err.println("Lat: " + UserModel.getInstance().getLatitude() + "\tLong: " + UserModel.getInstance().getLongitude());
        System.err.println("RUNNING RESTAURANT SEARCH");
        // get the response
        BusinessResponseDto response = YelpApi.getInstance().businessSearch(params);

        // working here
        //System.out.println(response.businesses()[0].distance());

        return response.businesses();
    }

    private static String noSpace(String link) {
        int spaces = 0;
        for (int i = 0; i < link.length(); i++)
            if (link.charAt(i) == ' ')
                spaces++;

        char[] url = new char[(2 * spaces) + link.length()];
        int urlPos = url.length - 1;
        for (int i = link.length() - 1; i >= 0; i--) {
            if (link.charAt(i) != ' ') {
                url[urlPos] = link.charAt(i);
                urlPos--;
            } else {
                url[urlPos] = '0';
                url[urlPos - 1] = '2';
                url[urlPos - 2] = '%';
                urlPos -= 3;
            }
        }

        return new String(url);
    }

}
