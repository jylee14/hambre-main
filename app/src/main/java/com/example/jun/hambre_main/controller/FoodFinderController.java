package com.example.jun.hambre_main.controller;

import com.example.jun.hambre_main.model.FoodModel;
import com.example.jun.server.*;

public class FoodFinderController {
    public FoodModel[] getFoodFromServer(){
        //connecting db to main
        ServerApi api = ServerApi.getInstance();
        DBFoodModel[] DBFoodModels = api.getFood();
        FoodModel[] fromDB = new FoodModel[DBFoodModels.length];
        for(int i = 0; i < DBFoodModels.length; i++){
            try {
                DBFoodModel tempDB = DBFoodModels[i];
                FoodModel temp = new FoodModel(tempDB.name(), tempDB.name(), "" + tempDB.path());
                fromDB[i] = temp;
            }catch(Exception e){
                System.err.println("D'OH");
                e.printStackTrace();
            }
        }
        return fromDB;
    }
}
