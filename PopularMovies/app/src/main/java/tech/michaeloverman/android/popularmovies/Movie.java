package tech.michaeloverman.android.popularmovies;

import android.os.Parcelable;

/**
 * Created by Michael on 12/7/2016.
 *
 * I chose to use a Builder approach to creating Movie objects because I see potential
 * for much expansion in the future, and not all parameters will be required, necessarily.
 * In particular, I have noticed that some movies in the DB have video clips associated
 * with them, and others do not.
 *
 * Builder structure modelled on the basic builder approach to object construction as
 * presented in "Effective Java" by Joshua Bloch.
 */

public class Movie {
    private final int id;
    private final String title;
    private final String posterUrl;
    private final String synopsis;
    private final String rating;
    private final String releaseDate;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public static class Builder {
        private int id;
        private String title;
        private String posterUrl;
        private String synopsis;
        private String rating;
        private String releaseDate;

        public Builder(int id) {
            this.id = id;
        }

        public Builder title(String str) {
            title = str;
            return this;
        }

        public Builder posterUrl(String str) {
            posterUrl = str;
            return this;
        }
        public Builder synopsis(String str) {
            synopsis = str;
            return this;
        }
        public Builder rating(String str) {
            rating = str;
            return this;
        }
        public Builder releaseDate(String str) {
            releaseDate = str;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

    private Movie(Builder builder) {
        id          = builder.id;
        title       = builder.title;
        posterUrl   = builder.posterUrl;
        synopsis    = builder.synopsis;
        rating      = builder.rating;
        releaseDate = builder.releaseDate;
    }
}
