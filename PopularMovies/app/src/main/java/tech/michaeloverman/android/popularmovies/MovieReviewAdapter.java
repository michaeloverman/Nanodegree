package tech.michaeloverman.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Adapter class to handle movie reviews for display in the ReviewActivity recycler view
 * Created by Michael on 12/20/2016.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder> {
    
    private static final String TAG = MovieReviewAdapter.class.getSimpleName();
    
    private MovieReview[] mReviews;
    private final Context mContext;
    
    public MovieReviewAdapter(Context context) {
        mContext = context;
    }
    
    /**
     * Install the reviews in the MovieReview[] array
     * @param reviews
     */
    public void setReviews(MovieReview[] reviews) {
        if(reviews.length > 0) mReviews = reviews;
        else {
            /* If there are no reviews, created 'fake' review to explain */
            MovieReview emptyReview = new MovieReview(null, mContext.getString(R.string.no_reviews_message));
            mReviews = new MovieReview[1];
            mReviews[0] = emptyReview;
        }
        
    }
    
    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_review, parent, false);
        return new MovieReviewAdapterViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(MovieReviewAdapterViewHolder holder, int position) {
        Log.d(TAG, "Movie Review onBindViewHolder " + position);
        if(position >= mReviews.length) position = 0;
        MovieReview review = mReviews[position];
        holder.authorView.setText(review.getAuthor());
        holder.reviewView.setText(review.getReview());
    }
    
    @Override
    public int getItemCount() {
        if(mReviews == null) return 0;
        else return mReviews.length;
    }
    
    class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView authorView;
        final TextView reviewView;
        
        public MovieReviewAdapterViewHolder(View view) {
            super(view);
            authorView = (TextView) view.findViewById(R.id.review_author_view);
            reviewView = (TextView) view.findViewById(R.id.review_review_view);
        }
    }
}
