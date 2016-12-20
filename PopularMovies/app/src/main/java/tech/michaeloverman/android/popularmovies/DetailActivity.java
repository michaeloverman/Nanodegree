package tech.michaeloverman.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.michaeloverman.android.popularmovies.databinding.ActivityDetailBinding;
import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    /* Individual Movie object, holding all the details */
    private Movie mMovie;

    /* Member variables controlling view */
//    @BindView(R.id.tv_title) TextView mTitle;
    @BindView(R.id.iv_movie_poster) ImageView mPoster;
//    @BindView(R.id.tv_year) TextView mYear;
//    @BindView(R.id.tv_duration) TextView mDuration;
//    @BindView(R.id.tv_rating) TextView mRating;
//    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    ActivityDetailBinding mBinding;
    
    
    @BindView(R.id.tv_detail_error_message) TextView mErrorMessage;
    @BindView(R.id.pb_detail_download_indicator) ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        /* Find the specific movie id which originated this activity */
        Intent intent = this.getIntent();
        int movieId = -1;
        if(intent != null) {
            if(intent.hasExtra(Intent.EXTRA_UID)) {
                movieId = intent.getIntExtra(Intent.EXTRA_UID, -1);
            }
        }

        /* Call background task to get the movie's particulars */
        new GetMovieDetailsTask().execute(movieId);
    }

    /**
     * If movie info downloads, display it, and not the error message
     */
    private void showMovieDetails() {
        mErrorMessage.setVisibility(View.INVISIBLE);

//        mTitle.setText(mMovie.getTitle());
        mBinding.tvTitle.setText(mMovie.getTitle());
        
        Picasso.with(DetailActivity.this)
                .load(NetworkUtils.buildPosterUrl(mMovie.getPosterUrl()))
                .into(mPoster);

//        mYear.setText(mMovie.getReleaseYear());
        mBinding.tvYear.setText(mMovie.getReleaseYear());

//        mDuration.setText(mMovie.getDuration() + getString(R.string.minutes_label));
        mBinding.tvDuration.setText(mMovie.getDuration() + " minutes");
        
//        mRating.setText(mMovie.getRating() + getString(R.string.rating_out_of));
        mBinding.tvRating.setText(mMovie.getRating() + " / 10.0");
        
//        mSynopsis.setText(mMovie.getSynopsis());
        mBinding.tvSynopsis.setText(mMovie.getSynopsis());
        
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
