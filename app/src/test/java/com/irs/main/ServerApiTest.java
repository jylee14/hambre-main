package com.irs.main;

import com.irs.main.model.FoodDto;
import com.irs.server.DBCreateTagDto;
import com.irs.server.DBFoodDto;
import com.irs.server.DBFoodTagDto;
import com.irs.server.DBLinkTagToFoodDto;
import com.irs.server.DBSetPreferencesDto;
import com.irs.server.DBTagDto;
import com.irs.server.DBUsersFoodDto;
import com.irs.server.PreferencesDto;
import com.irs.server.ServerApi;
import com.irs.yelp.SortType;

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

        DBTagDto[] DBTagDtos = api.getTag();

        for(int i = 0; i < DBTagDtos.length; i++){
            System.out.println(DBTagDtos[i]);
        }
    }
    @Test
    public void getFood_test() {
        ServerApi api = ServerApi.getInstance();

        DBFoodDto[] DBFoodDtos = api.getFood();
        FoodDto[] fromDB = new FoodDto[DBFoodDtos.length];
        for(int i = 0; i < DBFoodDtos.length; i++){
            try {
                DBFoodDto tempDB = DBFoodDtos[i];
                FoodDto temp = new FoodDto(tempDB.name(), tempDB.description(), tempDB.getTag(),tempDB.path());
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

        DBFoodTagDto[] DBFoodTagModels = api.getFoodTags(1);
        for (int i = 0; i < DBFoodTagModels.length; i++) {
            System.out.println("food_id: " + DBFoodTagModels[i].food_id() + " tag_id: " + DBFoodTagModels[i].tag_id());
        }
    }

    @Test
    public void getUsersFood_test() {
        ServerApi api = ServerApi.getInstance();

        String ap = "b0aab7dfbca43fc13dc5fdb8529fe7a0";
        DBUsersFoodDto[] DBUserFoods = api.getUsersFood(ap);

        for(int i = 0 ; i < DBUserFoods.length; i++){
            System.out.println(DBUserFoods[i]);
        }
    }

    @Test
    public void getLinkTagToFood_test() {
        ServerApi api = ServerApi.getInstance();
        int food_id = 1;
        int tag_id =10;
        DBLinkTagToFoodDto DBLinkToTag = api.getLinkTagToFood(food_id,tag_id);
        System.out.println(DBLinkToTag);
    }

    /* TODO: when getUserToFood is testable.
    @Test
    public void getUserToFood_test() {
        ServerApi api = ServerApi.getInstance();
        String ap = "b0aab7dfbca43fc13dc5fdb8529fe7a0";
        int food_id = 1;
        int liked = 1;
        int disliked = 0;

        DBUserToFoodDto[] DBUserToFoodModels = api.getUserToFood(ap,food_id,liked,disliked);
        for (int i = 0; i < DBUserToFoodModels.length; i++) {
            System.out.println(DBUserToFoodModels[i]);
        }
    }
*/
    @Test
    public void getPreferences_test() {
        ServerApi api = ServerApi.getInstance();

        PreferencesDto preferencesDto = api.getPreferences("f2ea485bedb5a9dfc7a6cf372c748d68");
        System.out.println(preferencesDto.user().distance());
    }

    // need a delete tag to test properly
    @Test
    public void createTag_test() {
        ServerApi api = ServerApi.getInstance();

        // change tag name per test so there are no duplicates
        DBCreateTagDto model = api.createTag("peruvian");
    }

    @Test
    public void setPreferences_test() {
        ServerApi api = ServerApi.getInstance();

        DBSetPreferencesDto setPreferencesModel = api.setPreferences("f2ea485bedb5a9dfc7a6cf372c748d68", DietType.Vegetarian, SortType.rating, 12);
        System.out.println(setPreferencesModel.error() + " " + setPreferencesModel.user().distance());
    }

    @Test
    public void uploadFood_test() {
        ServerApi.getInstance().uploadFood(null, "Big_Mac_hamburger.jpg", "big mac", "american", "none",
                "220d763d2aa724c6a59d5649138a0211", 0, 0, 0, 0);
    }
}