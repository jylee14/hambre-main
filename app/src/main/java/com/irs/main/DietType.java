package com.irs.main;

/**
 * keep track of diets in the UserModel
 */
public enum DietType {
    Vegetarian, Vegan, Kosher, GlutenFree, None;

    public String toYelpString() {
        switch (this) {
            case Vegetarian:
                return "vegetarian";
            case Vegan:
                return "vegan";
            case Kosher:
                return "kosher";
            case GlutenFree:
                return "gluten_free";
            default:
                return null;
        }
    }
}
