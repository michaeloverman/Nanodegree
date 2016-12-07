package tech.michaeloverman.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;


public class MainActivity extends AppCompatActivity
        implements ThumbnailAdapter.ThumbnailOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SPAN_COUNT = 3;
//    private static final String EXTRA_MOVIE = "extra_movie";

    private RecyclerView mRecyclerView;
    private ThumbnailAdapter mThumbnailAdapter;

    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);

        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_download_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mThumbnailAdapter = new ThumbnailAdapter(this);

        mRecyclerView.setAdapter(mThumbnailAdapter);

        loadMovies();
    }

    private void loadMovies() {
        /* TODO: Make this get the search criteria from a menu item */
        SearchCriteria criteria = SearchCriteria.POPULAR;
        new GetMoviesTask().execute(criteria);
    }

    private void showMoviePosters() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destination = DetailActivity.class;
        Intent intent = new Intent(context, destination);
        intent.putExtra(Intent.EXTRA_UID, movie.getId());
        startActivity(intent);
    }

    private class GetMoviesTask extends AsyncTask<SearchCriteria, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(SearchCriteria... criteria) {

            /* TODO: Make something default?? */
            /* If no search criteria, don't search... */
            if(criteria.length == 0) {
                return null;
            }

            SearchCriteria crit = criteria[0];
            URL movieSearchUrl = NetworkUtils.buildSearchUrl(crit);

            try {
                String movieSearchResultJson = NetworkUtils.getJsonFromUrl(movieSearchUrl);

                Movie[] movies = MovieDBUtils.getMoviesFromJson(MainActivity.this, movieSearchResultJson);

                return movies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null) {
                showMoviePosters();
                mThumbnailAdapter.setMovies(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
