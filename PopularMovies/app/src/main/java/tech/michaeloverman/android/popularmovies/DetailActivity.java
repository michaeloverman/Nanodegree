package tech.michaeloverman.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;

    private TextView mTitle;
    private ImageView mPoster;
    private TextView mYear;
    private TextView mDuration;
    private TextView mRating;
    private TextView mSynopsis;

    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = this.getIntent();
        int movieId = -1;
        if(intent != null) {
            if(intent.hasExtra(Intent.EXTRA_UID)) {
                movieId = intent.getIntExtra(Intent.EXTRA_UID, -1);
            }
        }

        mTitle = (TextView) findViewById(R.id.tv_title);

        mPoster = (ImageView) findViewById(R.id.iv_movie_poster);

        mYear = (TextView) findViewById(R.id.tv_year);

        mDuration = (TextView) findViewById(R.id.tv_duration);

        mRating = (TextView) findViewById(R.id.tv_rating);

        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);

        mErrorMessage = (TextView) findViewById(R.id.tv_detail_error_message);
        mLoadingIndicator = (ProgressBar) findViewById((R.id.pb_detail_download_indicator));

        new GetMovieDetailsTask().execute(movieId);
    }

    private void showMovieDetails() {
        mErrorMessage.setVisibility(View.INVISIBLE);

        mTitle.setText(mMovie.getTitle());

        Picasso.with(DetailActivity.this)
                .load(NetworkUtils.buildPosterUrl(mMovie.getPosterUrl()))
                .into(mPoster);

        mYear.setText(mMovie.getReleaseYear());

        mDuration.setText(mMovie.getDuration() + " minutes");

        mRating.setText(mMovie.getRating() + " / 10.0");

        mSynopsis.setText(mMovie.getSynopsis());
    }
    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private class GetMovieDetailsTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(Integer... id) {

            /* If no movie id, don't search... */
            if(id.length == 0) {
                return null;
            }

            int movieId = id[0];
            URL movieUrl = NetworkUtils.buildSingleMovieUrl(movieId);

            try {
                String movieSearchResultJson = NetworkUtils.getJsonFromUrl(movieUrl);

                Movie movie = MovieDBUtils.getSingleMovieFromJson(DetailActivity.this, movieSearchResultJson);

                return movie;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movie != null) {
                mMovie = movie;
                showMovieDetails();
            } else {
                showErrorMessage();
            }
        }
    }
}
