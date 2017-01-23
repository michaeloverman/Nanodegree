package com.app.movie.cinephilia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by GAURAV on 23-12-2015.
 */
public class showImageActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_full_image);

        String image = getIntent().getStringExtra("image");
        imageView = (ImageView) findViewById(R.id.full_image);

        Picasso.with(this).load(image).into(imageView);
    }
}
