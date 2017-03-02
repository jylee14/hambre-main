package com.irs.main;

import com.irs.main.model.FoodModel;
import com.irs.server.DBFoodModel;
import com.irs.server.DBFoodTagModel;
import com.irs.server.DBTagModel;
import com.irs.server.PreferencesModel;
import com.irs.server.ServerApi;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ServerApiTest {

    @Test
    public void getTag_test(){
        ServerApi api = ServerApi.getInstance();

        DBTagModel[] DBTagModels = api.getTag();

        for(int i = 0 ; i < DBTagModels.length; i++){
            System.out.println(DBTagModels[i]);
        }
    }
    @Test
    public void getFood_test() {
        ServerApi api = ServerApi.getInstance();

        DBFoodModel[] DBFoodModels = api.getFood();
        FoodModel[] fromDB = new FoodModel[DBFoodModels.length];
        for(int i = 0; i < DBFoodModels.length; i++){
            try {
                DBFoodModel tempDB = DBFoodModels[i];
                FoodModel temp = new FoodModel(tempDB.name(), tempDB.description(), tempDB.path());
                fromDB[i] = temp;
            }catch(Exception e){
                System.err.println("D'OH");
                e.printStackTrace();
            }
        }

        for(int i = 0 ; i < fromDB.length; i++){
            System.out.println(fromDB[i]);
        }
    }

    @Test
    public void getFoodTag_test() {
        ServerApi api = ServerApi.getInstance();

        DBFoodTagModel[] DBFoodTagModels = api.getFoodTags(1);
        for (int i = 0; i < DBFoodTagModels.length; i++) {
            System.out.println("food_id: " + DBFoodTagModels[i].food_id() + " tag_id: " + DBFoodTagModels[i].tag_id());
        }
    }

    @Test
    public void getPreferences_test() {
        ServerApi api = ServerApi.getInstance();

        PreferencesModel preferencesModel = api.getPreferences("b0aab7dfbca43fc13dc5fdb8529fe7a0");
        System.out.println(preferencesModel.user().api_key());
    }
}