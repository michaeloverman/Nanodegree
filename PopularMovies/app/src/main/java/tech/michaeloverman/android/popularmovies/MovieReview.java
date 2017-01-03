package tech.michaeloverman.android.popularmovies;

/**
 * Simple class to hold individual review and author's name
 * Created by Michael on 12/20/2016.
 */

public class MovieReview {
    private String mAuthor;
    private String mReview;
    
    public MovieReview(String author, String review) {
        mAuthor = author;
        mReview = review;
    }
    
    public String getAuthor() {
        return mAuthor;
    }
    
    public String getReview() {
        return mReview;
    }
}
