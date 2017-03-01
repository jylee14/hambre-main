package com.irs.main.controller;

import com.irs.main.model.FoodModel;
import com.irs.server.*;

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
