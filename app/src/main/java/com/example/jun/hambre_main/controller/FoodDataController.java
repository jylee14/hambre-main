package com.example.jun.hambre_main.controller;

import com.example.jun.hambre_main.model.FoodModel;

/**
 * DataController for food data
 */
public class FoodDataController {
    public void connect() {
        // logon here
    }

    /**
     * Add food item to database
     * @param food FoodModel to add into the database
     */
    public void create(FoodModel food) {
        // add food to database
    }

    /**
     * Specialized method to retrieve a list of food items
     * @param count number of items to retrieve
     * @return FoodModel[] which contains a list of FoodModel items retrieved from the database
     */
    public FoodModel[] getFoodItems(int count) {
        // code to get food of proper id, can call readFood
        return new FoodModel[0];
    }

    /**
     * Read item from database
     * @param id item to read
     * @return
     */
    public FoodModel read(int id) {
        return new FoodModel("", "", "");
    }

    /**
     * Update item in the database
     * @param id id of entry to update
     * @param newData FoodModel containing the desired changes
     * @return same FoodModel if successful, null otherwise
     */
    public FoodModel update(int id, FoodModel newData) {
        return new FoodModel("", "", "");
    }

    /**
     * Delete item in the database
     * @param id id of item to delete
     * @return FoodModel representing data which was destroyed
     */
    public FoodModel destroy(int id) {
        return new FoodModel("", "", "");
    }
}