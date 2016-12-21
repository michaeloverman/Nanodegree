package tech.michaeloverman.android.popularmovies;

/**
 * Created by Michael on 12/20/2016.
 */

public class MovieReview {
    private String author;
    private String review;
    
    public MovieReview(String author, String review) {
        this.author = author;
        this.review = review;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getReview() {
        return review;
    }
}
