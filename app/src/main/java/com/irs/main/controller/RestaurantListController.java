package com.irs.main.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.irs.main.R;
import com.irs.yelp.CoordinatesDto;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.DecimalFormat;

public class RestaurantListController extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] name;
    private final String[] url;
    private final String[] imageUrl;
    private final double[] rating;
    private final String[] price;
    private final CoordinatesDto[] coordinates;
    private final double[] distances;
    private final double METERS_TO_MILES = 1600;

    RestaurantListController(Activity context, String[] name, String[] url,
                             String[] imageUrl, double[] rating, String[] price, CoordinatesDto[] coordinates,double[] distances) {

        super(context, R.layout.list_single, name);
        this.context = context;
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.price = price;
        this.coordinates = coordinates;
        this.distances = distances;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);

        TextView txtName = (TextView) rowView.findViewById(R.id.name);
        txtName.setText(name[position]);
        TextView txtPrice = (TextView) rowView.findViewById(R.id.price);
        txtPrice.setText(price[position]);
        RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.rating);
        ratingBar.setRating((float) rating[position]);
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.BLACK);

        DecimalFormat df = new DecimalFormat("#.##");

        TextView distanceTxt = (TextView)rowView.findViewById(R.id.distance_txt);
        distanceTxt.setText("  " +df.format(distances[position]/METERS_TO_MILES) + " mi.");

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        Button mapButton = (Button) rowView.findViewById(R.id.map_button);
        mapButton.setFocusable(false);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps(coordinates[position], name[position]);
            }
        });

        //TODO this might block the UI thread.
        try {
            URL url = new URL(imageUrl[position]);
            //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            //imageView.setImageBitmap(bmp);
            Picasso.with(context).load(url.toString()).into(imageView);
        } catch (Exception e) {
            System.out.println("KAAAAAAAAAAAAAAAAAAAAHHHHNNNN!!!");
        }
        return rowView;
    }

    private void openMaps(CoordinatesDto coordinates, String label) {
        String uriBegin = "geo:" + coordinates;
        String query = coordinates.toString() + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

}
