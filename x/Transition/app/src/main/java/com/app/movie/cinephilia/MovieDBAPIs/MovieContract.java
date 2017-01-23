package com.app.movie.cinephilia.MovieDBAPIs;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by GAURAV on 19-01-2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.app.movie.cinephilia";
    // Build Base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.app.movie.cinephilia/favouriteMovies/ is a valid path for
    // looking at weather data.
    public static final String PATH_FAVOURITES = "favouriteMovies";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    // FavoriteMoviesEntry Inner class declaring fields for table contents of Movie Database
    public static final class FavoriteMoviesEntry implements BaseColumns{
        public static final String TABLE_NAME = "favouriteMovies";
        // Movie ID as returned by the API
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Vote Count(stored as String) and Vote Average(stored as Float) for the Movie
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        // Original Title return by the API
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        // Release Date, stored as String (as Return by API)
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Overview, stored as String
        public static final String COLUMN_OVERVIEW = "overview";

        // PosterURL and BackDropURL
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;

        public static Uri buildFavouritesURI(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // content://..../MovieId
        public static Uri buildFavouriteMoviesUriWithMovieId(int MovieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(MovieId)).build();
        }

    }
}
