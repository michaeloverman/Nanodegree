package tech.michaeloverman.android.popularmovies;

/**
 * Created by Michael on 12/7/2016.
 *
 * Movie class holds information about the movie.
 *
 * A Builder approach to creating Movie objects is used because not all parameters are
 * required by the main activity that are needed for the detail activity. Also, in the future
 * some movies in the DB have video clips associated with them, and others do not.
 *
 * Builder structure modelled on the basic builder approach to object construction as
 * presented in "Effective Java" by Joshua Bloch.
 */

public class Movie {

    /* Member Variables */
    private final int id; // TheMovieDB id
    private final String title;
    private final String posterUrl;
    private final String synopsis;
    private final String rating;
    private final String releaseDate;
    private final int duration;

    /* GETTERS */
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

    public String getReleaseYear() {
        String[] dates= releaseDate.split("-");
        return dates[0];
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Builder class to create movie object, accommodating different values.
     */
    public static class Builder {
        private int id;
        private String title;
        private String posterUrl;
        private String synopsis;
        private String rating;
        private String releaseDate;
        private int duration;

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
        public Builder duration(int dur) {
            duration = dur;
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
        duration    = builder.duration;
    }
}
