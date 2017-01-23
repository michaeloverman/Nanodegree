package com.app.movie.cinephilia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.app.movie.cinephilia.CastandCrew.CreditsAdapter;
import com.app.movie.cinephilia.CastandCrew.MovieCreditsModel;

import java.util.ArrayList;

/**
 * Created by GAURAV on 20-05-2016.
 */
public class CreditsActivity extends AppCompatActivity {
    private CreditsAdapter mCreditsAdapter;
    private static final String LOG_TAG = CreditsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.credits_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton closeActivityButton = (ImageButton) findViewById(R.id.closeActivity_button);
        closeActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreditsActivity.this.overridePendingTransition(R.anim.slide_down_info, R.anim.no_change);
                finish();
            }
        });

        ArrayList<MovieCreditsModel> creditsModelArrayList = getIntent().getExtras().getParcelableArrayList("CreditsData");
        mCreditsAdapter = new CreditsAdapter(this, R.layout.list_item_credits, creditsModelArrayList);

        ListView listView = (ListView) findViewById(R.id.credits_list);
        listView.setAdapter(mCreditsAdapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_info, R.anim.no_change);
    }
}
