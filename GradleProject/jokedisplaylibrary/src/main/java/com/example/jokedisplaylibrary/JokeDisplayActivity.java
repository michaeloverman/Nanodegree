package com.example.jokedisplaylibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Michael on 1/26/2017.
 */

public class JokeDisplayActivity extends AppCompatActivity {
    public static final String JOKE_STRING_EXTRA = "jokestringextra";

    TextView jokeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_display);

        jokeView = (TextView) findViewById(R.id.joke_view);

        Intent intent = getIntent();

        if(intent.hasExtra(JOKE_STRING_EXTRA)) {
            String joke = intent.getStringExtra(JOKE_STRING_EXTRA);
            Log.d("JokeDisplayActivity", "Joke received: " + joke);
            jokeView.setText(joke);
        }
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_image, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
