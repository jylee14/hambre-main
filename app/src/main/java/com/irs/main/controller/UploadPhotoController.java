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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UploadPhotoController extends FragmentActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1887;

    private Button chosePhotoButton, saveButton, cancelButton, cameraButton;
    private Bitmap pic;
    private EditText foodName;
    private ImageView selectedPic;
    private String culture, diet, category, name;
    private Spinner cultureSpinner, dietSpinner, categorySpinner;

    //TODO add all cultures in alphabetical order available in yelp API found at:
    //https://www.yelp.com/developers/documentation/v2/all_category_list

    //TODO also change switch statement below for cultureSpinner.OnItemSelected

    private static final String[] culturePaths = {"American (new)", "American (traditional)", "Chinese", "Cuban",
            "Indian", "Italian", "Japanese", "Korean", "Mexican", "Russian", "Thai"};
    private static final String[] dietPaths = {"None", "Vegetarian", "Vegan",
            "Kosher", "Gluten Free"};
    Map<String, DietType> dietMap = new HashMap<String, DietType>() {{
        put("Gluten Free", DietType.GlutenFree);
        put("Kosher", DietType.Kosher);
        put("Vegan", DietType.Vegan);
        put("Vegetarian", DietType.Vegetarian);
        put("None", DietType.None);
    }};
    private static final String[] categoryPaths = {"food", "desert", "fruit", "spicy"};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        foodName = (EditText) findViewById(R.id.name_txt);
        selectedPic = (ImageView) findViewById(R.id.selected_pic);

        setCultureSpinner();
        setDietSpinner();
        setCategorySpinner();
        setSaveButton();
        setChosePhotoButton();
        setCancelButton();
        setCameraButton();
    }

    private void setCameraButton() {
        cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Intent3, CAMERA_REQUEST);
            }
        });
    }

    private void setCancelButton() {
        cancelButton = (Button) findViewById(R.id.cancel_photo_button);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private void setChosePhotoButton() {
        chosePhotoButton = (Button) findViewById(R.id.chose_photo_button);
        chosePhotoButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    private void setSaveButton() {
        saveButton = (Button) findViewById(R.id.save_photo_button);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    name = foodName.getText().toString();
                    System.out.println("Culture: " + culture + "\nDiet: " + diet +
                            "\ncategory: " + category + "\nname: " + name + "\npicName: " + getPicName(name));
                    Toast.makeText(UploadPhotoController.this, "photo submitted!", Toast.LENGTH_SHORT).show();

                    // upload image
                    UserModel.getInstance().uploadPhoto(pic, getPicName(name) +
                            ".jpg", name, culture, category, dietMap.get(diet));


                    startActivity(new Intent(UploadPhotoController.this, FoodFinderController.class));
                    finish();
                } catch (Exception ex) {
                    System.out.println("You will be assimilated. Resistance is futile.");
                    ex.printStackTrace();
                }
            }
        });
    }

    private void setCategorySpinner() {
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        final ArrayAdapter<String> categoryAdapter = new
                ArrayAdapter<>(UploadPhotoController.this, android.R.layout.simple_spinner_item,
                categoryPaths);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categoryPaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDietSpinner() {
        dietSpinner = (Spinner) findViewById(R.id.dietary_spinner);
        final ArrayAdapter<String> dietAdapter = new ArrayAdapter<>(UploadPhotoController.this,
                android.R.layout.simple_spinner_item, dietPaths);
        dietSpinner.setAdapter(dietAdapter);

        dietSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                diet = dietPaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCultureSpinner() {
        cultureSpinner = (Spinner) findViewById(R.id.culture_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadPhotoController.this,
                android.R.layout.simple_spinner_item, culturePaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cultureSpinner.setAdapter(adapter);

        cultureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                culture = culturePaths[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //TODO change these to crop and save as vertical phone pic
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 512);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 2);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private String getPicName(String name) throws UnsupportedEncodingException {
        return URLEncoder.encode(name, "UTF-8");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        // image from camera
        if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            selectedPic.setImageBitmap(photo);
        }
    }
}
