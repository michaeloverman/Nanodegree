package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jokedisplaylibrary.JokeDisplayActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class MainActivity extends AppCompatActivity implements JokeLoader.JokeLoaderListener {

    private InterstitialAd mInterAd;
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new EndpointsAsyncTask().execute();

        mInterAd = new InterstitialAd(this);
        mInterAd.setAdUnitId(getString(R.string.test_ad_unit_id));

        mInterAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                displayJoke();
            }
        });

        requestNewInterstitial();

        mSpinner = (ProgressBar) findViewById(R.id.spinner);

    }

    private void requestNewInterstitial() {
        AdRequest req = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterAd.loadAd(req);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {


        mSpinner.setVisibility(View.VISIBLE);
        if(mInterAd.isLoaded()) {
            mInterAd.show();
        } else {
            displayJoke();
        }
    }

    private void displayJoke() {
        JokeLoader loader = new JokeLoader(this);
        loader.loadJoke();
    }

    @Override
    public void jokeLoaded(String joke) {
        Intent intent = new Intent(this, JokeDisplayActivity.class);
        intent.putExtra(JokeDisplayActivity.JOKE_STRING_EXTRA, joke);
        mSpinner.setVisibility(View.INVISIBLE);
        startActivity(intent);
    }
}
