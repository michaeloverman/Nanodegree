package tech.michaeloverman.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import tech.michaeloverman.android.popularmovies.data.FavoritesDBHelper;
import tech.michaeloverman.android.popularmovies.databinding.ActivityDetailBinding;
import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

/**
 * DetailActivity shows small movie poster, title, year of release, rating, synopsis,
 * button to show reviews, button to mark/unmark movie as favorite, and links to
 * any available trailers.
 */
public class DetailActivity extends AppCompatActivity
        implements VideoLinkAdapter.VideoLinkAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String MOVIE_TITLE_EXTRA = "movie_title";
    
    /* Individual Movie object, holding all the details */
    private Movie mMovie;

    /* Member variables handling view/GUI */
    ActivityDetailBinding mBinding;
    
    ImageView mPoster;
    Button mFavoriteButton;
    ImageView mFavoriteStar;
    
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    
    private RecyclerView mVideoRecycler;
    private VideoLinkAdapter mVideoLinkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        /* Do not continue, if there is a problem getting the movie id from the intent extras  */
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

        mBinding.titleHeaderLayout.tvTitle.setText(mMovie.getTitle());
        
        Picasso.with(DetailActivity.this)
                .load(NetworkUtils.buildPosterUrl(mMovie.getPosterUrl()))
                .into(mPoster);

        mBinding.detailInfoLayout.tvYear.setText(mMovie.getReleaseYear());

        mBinding.detailInfoLayout.tvDuration.setText(
                String.format(getString(R.string.duration_minutes), mMovie.getDuration()));
        
        mBinding.detailInfoLayout.tvRating.setText(
                String.format(getString(R.string.rating_string), mMovie.getRating()));
        
        mBinding.detailInfoLayout.tvSynopsis.setText(mMovie.getSynopsis());
        
        if(mMovie.isFavorite()) showAsFavorite();

    }
    
    /**
     * Button click handling: favorite marking/unmarking, and read reviews
     * @param view
     */
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
    }
    
    /**
     * If movie is marked as favorite, add to or remove from database, and call method to change views
     * on screen to show new status.
     */
    public void favoriteButtonClicked() {
        /* Access database to store new favorite status */
        final SQLiteDatabase db = new FavoritesDBHelper(getApplicationContext()).getWritableDatabase();
        
        if(!mMovie.isFavorite()) {
            ContentValues values = new ContentValues();
            values.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovie.getId());
            values.put(FavoritesContract.FavoriteEntry.COLUMN_POSTER_URL, mMovie.getPosterUrl());
            
            db.insert(FavoritesContract.FavoriteEntry.TABLE_NAME, null, values);
            mMovie.markFavorite(true);
            showAsFavorite();
        } else {
            String selection = FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID + " LIKE ?";
            String[] selectionArgs = { Integer.toString(mMovie.getId()) };
            db.delete(FavoritesContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
            mMovie.markFavorite(false);
            showAsNonFavorite();
        }
        db.close();
    }
    
    /**
     * Set button text and favorite star
     */
    private void showAsFavorite() {
        mFavoriteButton.setText(R.string.button_text_marked);
        mFavoriteStar.setVisibility(View.VISIBLE);
    }
    
    /**
     * Reset button text and favorite star
     */
    private void showAsNonFavorite() {
        mFavoriteButton.setText(R.string.mark_as_favorite);
        mFavoriteStar.setVisibility(View.INVISIBLE);
    }
    
    /**
     * Open activity showing reviews
     */
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
    
    /**
     * Play selected promo video in implicit intent
     * @param id
     */
    @Override
    public void onClick(int id) {
        Intent videoPlayIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(mMovie.getVideoLink(id).toString()));
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

                Movie movie = MovieDBUtils.getSingleMovieFromJson(
                        DetailActivity.this, movieSearchResultJson);
                movie.setVideoLinks();
                
                if(isFavorite(movieId)) movie.markFavorite(true);
                
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
    
        /**
         * Method to access database to determine whether movie is a marked favorite
         *
         * @param id
         * @return true if in database, false otherwise
         */
        private boolean isFavorite(int id) {
            boolean fave;
            String[] projection = {FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID };
            String selection = FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?";
            String[] selectionArgs = { Integer.toString(id) };
            SQLiteDatabase db = new FavoritesDBHelper(getApplicationContext()).getReadableDatabase();
            
            Cursor cursor = db.query(
                    FavoritesContract.FavoriteEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            
            /* If cursor is not empty, movie is in database */
            if(cursor.getCount() > 0) fave = true;
            else fave = false;
            
            return fave;
        }
    }
}
