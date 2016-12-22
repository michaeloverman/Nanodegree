package tech.michaeloverman.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import tech.michaeloverman.android.popularmovies.data.FavoritesContract;
import tech.michaeloverman.android.popularmovies.databinding.ActivityDetailBinding;
import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity
        implements VideoLinkAdapter.VideoLinkAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String MOVIE_TITLE_EXTRA = "movie_title";
    
    /* Individual Movie object, holding all the details */
    private Movie mMovie;

    /* Member variables controlling view */
//    @BindView(R.id.tv_title) TextView mTitle;
//    @BindView(R.id.iv_movie_poster) ImageView mPoster;
    ImageView mPoster;
//    @BindView(R.id.tv_year) TextView mYear;
//    @BindView(R.id.tv_duration) TextView mDuration;
//    @BindView(R.id.tv_rating) TextView mRating;
//    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    ActivityDetailBinding mBinding;
    Button mFavoriteButton;
    ImageView mFavoriteStar;
    
    
//    @BindView(R.id.tv_detail_error_message) TextView mErrorMessage;
//    @BindView(R.id.pb_detail_download_indicator) ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    
    private RecyclerView mVideoRecycler;
    private VideoLinkAdapter mVideoLinkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
//        ButterKnife.bind(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mErrorMessage = (TextView) findViewById(R.id.tv_detail_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_detail_download_indicator);
        mPoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mFavoriteButton = (Button) findViewById(R.id.favorite_button);
        mFavoriteStar = (ImageView) findViewById(R.id.favorite_star);
        mVideoRecycler = (RecyclerView) findViewById(R.id.rv_video_links);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mVideoRecycler.setLayoutManager(layoutManager);
        mVideoRecycler.setHasFixedSize(true);
        mVideoLinkAdapter = new VideoLinkAdapter(this, this);

        /* Find the specific movie id which originated this activity */
        Intent intent = this.getIntent();
        int movieId = -1;
        if(intent != null) {
            if(intent.hasExtra(Intent.EXTRA_UID)) {
                movieId = intent.getIntExtra(Intent.EXTRA_UID, -1);
            }
        }

        if (movieId == -1) this.onStop();
        /* Call background task to get the movie's particulars */
        new GetMovieDetailsTask().execute(movieId);
        
        mVideoRecycler.setAdapter(mVideoLinkAdapter);
    }

    /**
     * If movie info downloads, display it, and not the error message
     */
    private void showMovieDetails() {
        mErrorMessage.setVisibility(View.INVISIBLE);

//        mTitle.setText(mMovie.getTitle());
        mBinding.titleHeaderLayout.tvTitle.setText(mMovie.getTitle());
        
        Picasso.with(DetailActivity.this)
                .load(NetworkUtils.buildPosterUrl(mMovie.getPosterUrl()))
                .into(mPoster);

//        mYear.setText(mMovie.getReleaseYear());
        mBinding.detailInfoLayout.tvYear.setText(mMovie.getReleaseYear());

//        mDuration.setText(mMovie.getDuration() + getString(R.string.minutes_label));
        mBinding.detailInfoLayout.tvDuration.setText(mMovie.getDuration() + " minutes");
        
//        mRating.setText(mMovie.getRating() + getString(R.string.rating_out_of));
        mBinding.detailInfoLayout.tvRating.setText(mMovie.getRating() + " / 10.0");
        
//        mSynopsis.setText(mMovie.getSynopsis());
        mBinding.detailInfoLayout.tvSynopsis.setText(mMovie.getSynopsis());
        
        // check DB for whether marked as Favorite on not
        // set button text
        // set star
        

    }

    public void buttonClicked(View view) {
        
        switch(view.getId()) {
            case R.id.favorite_button:
                favoriteButtonClicked();
                break;
            case R.id.reviews_button:
                openReviewsActivity();
                break;
            default:
                
                
        }
//        Log.d(TAG, button + " button clicked");
    }
    
    public void favoriteButtonClicked() {
        if(!mMovie.isFavorite()) {
            ContentValues values = new ContentValues();
            values.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovie.getId());
            values.put(FavoritesContract.FavoriteEntry.COLUMN_POSTER_URL, mMovie.getPosterUrl());
    
            this.getContentResolver().insert(FavoritesContract.FavoriteEntry.CONTENT_URI, values);
            
            mMovie.markFavorite(true);
            showAsFavorite();
        } else {
            
            mMovie.markFavorite(false);
            unfavorite();
        }
    }
    
    private void showAsFavorite() {
        mFavoriteButton.setText(R.string.button_text_marked);
        mFavoriteStar.setVisibility(View.VISIBLE);
    }
    private void unfavorite() {
        mFavoriteButton.setText(R.string.mark_as_favorite);
        mFavoriteStar.setVisibility(View.INVISIBLE);
    }
    private void openReviewsActivity() {
        Intent reviewsIntent = new Intent(this, ReviewActivity.class);
        reviewsIntent.putExtra(Intent.EXTRA_UID, mMovie.getId());
        reviewsIntent.putExtra(MOVIE_TITLE_EXTRA, mMovie.getTitle());
        startActivity(reviewsIntent);
    }
    /**
     * If movie info does not download, display error message.
     */
    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onClick(int id) {
        Intent videoPlayIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(mMovie.getVideoLink(id).toString()));
//        Uri videoLink = mMovie.getVideoLink(id);
//        videoPlayIntent.setData(videoLink);
        startActivity(videoPlayIntent);
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
                movie.setVideoLinks();
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
                mVideoLinkAdapter.setLinkData(mMovie.getVideoLinks());
                
                showMovieDetails();
            } else {
                showErrorMessage();
            }
        }
    }
}
