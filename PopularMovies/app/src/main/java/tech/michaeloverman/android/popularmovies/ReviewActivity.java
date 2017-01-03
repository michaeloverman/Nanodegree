package tech.michaeloverman.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

/**
 * Activity to display reviews of specific movies.
 * Created by Michael on 12/20/2016.
 */

public class ReviewActivity extends AppCompatActivity {
    private static final String TAG = ReviewActivity.class.getSimpleName();
    
    /* Member variables */
    private String mTitle;
    private int mId;
    
    private ArrayList<MovieReview> mReviews;
    
    /* Layout variables */
    private RecyclerView mRecyclerView;
    private TextView mTitleView;
    private MovieReviewAdapter mReviewAdapter;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting Review Activity onCreate()");
        setContentView(R.layout.activity_reviews);
        
        mTitleView = (TextView) findViewById(R.id.tv_reviews_title);
        mErrorMessage = (TextView) findViewById(R.id.review_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.review_download_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.review_recycler);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mReviewAdapter = new MovieReviewAdapter(this);
        
        /* Get the movie details from the initiating intent */
        Intent intent = this.getIntent();
        mId = -1;
        mTitle = "";
        if(intent != null) {
            if(intent.hasExtra(Intent.EXTRA_UID)) {
                mId = intent.getIntExtra(Intent.EXTRA_UID, -1);
            }
            if(intent.hasExtra(DetailActivity.MOVIE_TITLE_EXTRA)) {
                mTitle = intent.getStringExtra(DetailActivity.MOVIE_TITLE_EXTRA);
                mTitleView.setText(mTitle);
            }
        }
        
        /* With the movie details (mId!), fetch the data and set on the recycler view */
        new GetReviewsTask().execute(mId);
        mRecyclerView.setAdapter(mReviewAdapter);
    }
    
    /**
     * If review info does not download, display error message.
     */
    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
    }
    
    /**
     * AsyncTask to fetch reviews from online database.
     */
    private class GetReviewsTask extends AsyncTask<Integer, Void, MovieReview[]> {
    
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    
        @Override
        protected MovieReview[] doInBackground(Integer... id) {
            
            if(id.length == 0) {
                return null;
            }
            
            int movieId = id[0];
            URL url = NetworkUtils.buildVideoDBUrl(movieId, NetworkUtils.REVIEWS_PATH);
            
            try {
                String reviewJson = NetworkUtils.getJsonFromUrl(url);
                
                MovieReview[] reviews = MovieDBUtils.getReviewsFromJson(reviewJson);
                
                return reviews;
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    
        @Override
        protected void onPostExecute(MovieReview[] movieReviews) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieReviews != null) {
                mReviewAdapter.setReviews(movieReviews);
            } else {
                showErrorMessage();
            }
            /* Nougat was setting up the recycler view so fast, the data was not getting there in time.
             * This call solves that problem.
             */
            mRecyclerView.requestLayout();
        }
    }
}
