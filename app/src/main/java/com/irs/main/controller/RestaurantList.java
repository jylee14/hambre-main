package com.irs.main.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irs.main.R;
import com.irs.yelp.BusinessModel;

import java.net.URL;

/**
 * Created by jeff on 3/2/17.
 */

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
        TextView txtRating = (TextView)rowView.findViewById(R.id.rating);
        txtRating.setText("" +rating[position]);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.img);

        //TODO this might block the UI thread.
        try {
            URL url = new URL(imageUrl[position]);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        }
        catch (Exception e){
            System.out.println("KAAAAAAAAAAAAAAAAAAAAHHHHNNNN!!!");
        }


        return rowView;

    }

}
