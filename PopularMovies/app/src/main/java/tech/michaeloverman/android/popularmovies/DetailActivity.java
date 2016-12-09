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

    /* Individual Movie object, holding all the details */
    private Movie mMovie;

    /* Member variables controlling view */
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

        /* Find the specific movie id which originated this activity */
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

        /* Call background task to get the movie's particulars */
        new GetMovieDetailsTask().execute(movieId);
    }

    /**
     * If movie info downloads, display it, and not the error message
     */
    private void showMovieDetails() {
        mErrorMessage.setVisibility(View.INVISIBLE);

        mTitle.setText(mMovie.getTitle());

        Picasso.with(DetailActivity.this)
                .load(NetworkUtils.buildPosterUrl(mMovie.getPosterUrl()))
                .into(mPoster);

        mYear.setText(mMovie.getReleaseYear());

        mDuration.setText(mMovie.getDuration() + getString(R.string.minutes_label));

        mRating.setText(mMovie.getRating() + getString(R.string.rating_out_of));

        mSynopsis.setText(mMovie.getSynopsis());
    }

    /**
     * If movie info does not download, display error message.
     */
    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Async task to run in background, downloading info about the movie.
     */
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
