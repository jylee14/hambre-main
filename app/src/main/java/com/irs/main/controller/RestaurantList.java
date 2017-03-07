package com.irs.main.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.irs.main.R;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class RestaurantList extends ArrayAdapter<String> {
    private final Activity context;
    private final String []name;
    private final String []url;
    private final String []imageUrl;
    private final double []rating;
    private final String []price;
    RestaurantList(Activity context, String [] name, String[] url,
                   String[] imageUrl, double[] rating, String[] price){
        super(context, R.layout.list_single, name);
        this.context = context;
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.price = price;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);

        TextView txtName = (TextView)rowView.findViewById(R.id.name);
        txtName.setText(name[position]);
        TextView txtPrice = (TextView)rowView.findViewById(R.id.price);
        txtPrice.setText(price[position]);
        RatingBar ratingBar = (RatingBar)rowView.findViewById(R.id.rating);
        ratingBar.setRating((float)rating[position]);
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.BLACK);

        ImageView imageView = (ImageView)rowView.findViewById(R.id.img);

        //TODO this might block the UI thread.
        try {
            URL url = new URL(imageUrl[position]);
            //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            //imageView.setImageBitmap(bmp);
            Picasso.with(context).load(url.toString()).into(imageView);
        }
        catch (Exception e){
            System.out.println("KAAAAAAAAAAAAAAAAAAAAHHHHNNNN!!!");
        }
        return rowView;
    }

}
