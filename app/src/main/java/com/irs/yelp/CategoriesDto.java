package com.irs.yelp;

/**
 * Model representing Yelp API Categories JSON object
 */
public class CategoriesDto {
    String alias;
    String title;

    public String alias() {
        return alias;
    }

    public String title() {
        return title;
    }

    public String toString() {
        return this.title + "\t" + this.alias;
    }
}
