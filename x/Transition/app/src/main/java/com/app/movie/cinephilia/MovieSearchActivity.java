package com.app.movie.cinephilia;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by GAURAV on 26-06-2016.
 */
public class MovieSearchActivity extends AppCompatActivity implements OnMovieDataFetchFinished {
    private static final String TAG = MovieSearchActivity.class.getName();
    private static String searchType = null;
    private FetchMovieTask fetchMovieTask;
    private SearchListAdapter mSearchAdapter;
    private boolean searchTaskComplete = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final EditText editText = (EditText) findViewById(R.id.search_edit);
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        mSearchAdapter = new SearchListAdapter(this, R.layout.list_item_search, new ArrayList<MovieModel>());
        final LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);

        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //if (!event.isShiftPressed()) {
                    // the user is done typing.
                    String[] args = {editText.getText().toString()};
                    searchType = "searchMovies";
                    emptyLayout.setVisibility(View.GONE);
                    fetchMovieTask = new FetchMovieTask(MovieSearchActivity.this, MovieSearchActivity.this, "searchQuery");
                    fetchMovieTask.execute(args);
                    return true; // consume.
                    //}
                }
                return false;
            }
        };

        editText.setOnEditorActionListener(editorActionListener);
        final ListView listView = (ListView) findViewById(R.id.searchList);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(mSearchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel item = mSearchAdapter.getItem(position);

                if (searchTaskComplete) {
                    searchType = "fetchSelectedItem";
                    String[] args = {Integer.toString(item.getId())};
                    FetchMovieTask fetchMovie = new FetchMovieTask(MovieSearchActivity.this,
                            MovieSearchActivity.this, "fetchSearchedMovie");
                    fetchMovie.execute(args);
                }
            }
        });
    }


    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void MovieDataFetchFinished(ArrayList<MovieModel> movies) {
        searchTaskComplete = true;
        Log.v(TAG,"search complete");
        if (searchType.compareTo("fetchSelectedItem") == 0){
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, movies.get(0));

            Bundle bundle = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this)
                    .toBundle();
            startActivity(intent,bundle);
        }else if (searchType.compareTo("searchMovies") == 0) {
            mSearchAdapter.clear();
            mSearchAdapter.updateList(movies);
        }
    }
}
