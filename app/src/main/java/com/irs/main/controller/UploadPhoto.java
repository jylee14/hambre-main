package com.irs.main.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.irs.main.R;

/**
 * Created by jeff on 3/3/17.
 */

public class UploadPhoto extends AppCompatActivity {

    private Button chosePhotoButton, saveButton, cancelButton;
    private Bitmap pic;
    private ImageView selectedPic;
    private EditText catagoryInput, cultureInput;
    private String catagory, culture;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        selectedPic = (ImageView)findViewById(R.id.selected_pic);
        catagoryInput = (EditText)findViewById(R.id.catagory_editText );
        cultureInput = (EditText)findViewById(R.id.culture_editText);

        chosePhotoButton = (Button)findViewById(R.id.chose_photo_button);
        chosePhotoButton.setOnClickListener(new Button.OnClickListener(){
           @Override
            public void onClick(View v){
                pickImage();
           }
        });
        saveButton = (Button)findViewById(R.id.save_photo_buttton);
        saveButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                catagory = catagoryInput.getText().toString();
                culture = cultureInput.getText().toString();
                System.out.println("Catagory: " + catagory +
                "\nCulture: " + culture);
                Toast.makeText(UploadPhoto.this, "photo uploaded!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cancelButton = (Button)findViewById(R.id.cancel_photo_button);
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }

        });


    }

    public void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //TODO change these to crop and save as vertical phone pic
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data){
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                pic = extras.getParcelable("data");
                selectedPic.setImageBitmap(pic);
            }
        }
    }
}
