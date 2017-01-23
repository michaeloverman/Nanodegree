package com.app.movie.cinephilia;

/**
 * Created by GAURAV on 13-12-2015.
 */

import android.os.Bundle;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;

public class DetailsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailsFragment())
                    .commit();
        }
        //setupWindowAnimations();
    }


    /*private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(fade);
    }*/

    @Override
    public void onBackPressed(){
        supportFinishAfterTransition();
    }
}
