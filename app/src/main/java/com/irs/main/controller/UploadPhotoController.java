package com.irs.main.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.irs.main.DietType;
import com.irs.main.R;
import com.irs.main.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class UploadPhotoController extends FragmentActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1887;

    private Bitmap pic;
    private EditText foodName;
    private ImageView selectedPic;
    private String culture, diet, category, name;

    private static final String[] dietPaths = {"None", "Vegetarian", "Vegan",
            "Kosher", "Gluten Free"};

    // Container for dietype.
    private final Map<String, DietType> dietMap = new HashMap<String, DietType>() {{
        put("Gluten Free", DietType.GlutenFree);
        put("Kosher", DietType.Kosher);
        put("Vegan", DietType.Vegan);
        put("Vegetarian", DietType.Vegetarian);
        put("None", DietType.None);
    }};


    private static final String[] categoryPaths = {"food", "dessert", "fruit", "spicy"};
    private static final String [] culturePaths = {"American", "Chinese","Indian", "Italian",
            "Japanese", "Jewish", "Korean", "Mediterranean", "Mexican" , "Middle Eastern",
            "Sushi", "Thai"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        foodName = (EditText) findViewById(R.id.name_txt);
        selectedPic = (ImageView) findViewById(R.id.selected_pic);

        setDietSpinner();
        setCategorySpinner();
        setSaveButton();
        setChoosePhotoButton();
        setCancelButton();
        setCameraButton();
        setCultureSpinner();
    }

    // Method to handle case if user decides to use camera to take a picture to upload.
    private void setCameraButton() {
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Intent3, CAMERA_REQUEST);
            }
        });
    }

    // Cancels upload photo activity and takes user back to "Food Finder" screen.
    private void setCancelButton() {
        Button cancelButton = (Button) findViewById(R.id.cancel_photo_button);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    // Method to submit the image to the database for review.
    private void setSaveButton() {
        Button saveButton = (Button) findViewById(R.id.save_photo_button);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pic == null || foodName.getText().toString().equals("")) {
                    Toast.makeText(UploadPhotoController.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        name = foodName.getText().toString();

                        String picName = getPicName(name);
                        
                        System.out.println("Culture: " + culture + "\nDiet: " + diet +
                                "\ncategory: " + category + "\nname: " + name + "\npicName: " + picName);

                        // upload image
                        UserModel.getInstance().uploadPhotoAsync(pic, picName + ".jpg", name, culture, category, dietMap.get(diet));
                        Toast.makeText(UploadPhotoController.this, "photo submitted!", Toast.LENGTH_SHORT).show();

                        finish();
                    } catch (Exception ex) {
                        System.out.println("You will be assimilated. Resistance is futile.");
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    // Method to handle case when user wants to upload photo from gallery.
    private void setChoosePhotoButton() {
        Button choosePhotoButton = (Button) findViewById(R.id.choose_photo_button);
        choosePhotoButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    // Method to select which category the food image falls under.
    private void setCategorySpinner() {
        Spinner categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        final ArrayAdapter<String> categoryAdapter = new
                ArrayAdapter<>(UploadPhotoController.this, android.R.layout.simple_spinner_item,
                categoryPaths);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Select item.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categoryPaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Method for setting dietary restriction.
    private void setDietSpinner() {
        Spinner dietSpinner = (Spinner) findViewById(R.id.dietary_spinner);
        final ArrayAdapter<String> dietAdapter = new ArrayAdapter<>(UploadPhotoController.this,
                android.R.layout.simple_spinner_item, dietPaths);
        dietSpinner.setAdapter(dietAdapter);

        dietSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Select item.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                diet = dietPaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCultureSpinner(){
        Spinner cultureSpinner = (Spinner)findViewById(R.id.culture_spinner);
        final ArrayAdapter<String> cultureAdapter = new ArrayAdapter<>(UploadPhotoController.this,
                android.R.layout.simple_spinner_item, culturePaths);
        cultureSpinner.setAdapter(cultureAdapter);
        cultureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                culture = culturePaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //Crop image to viewable state.
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //TODO change these to crop and save as vertical phone pic
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 340);
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 4);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, GALLERY_REQUEST);

    }

    private String getPicName(String name){
        return name + "_" + pic.hashCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        // image from camera or gallery
        if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
            pic = (Bitmap) data.getExtras().get("data");
            selectedPic.setImageBitmap(pic);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
