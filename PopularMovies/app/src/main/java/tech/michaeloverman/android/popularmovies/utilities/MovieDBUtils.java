package tech.michaeloverman.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import tech.michaeloverman.android.popularmovies.Movie;

/**
 * Created by Michael on 12/7/2016.
 *
 * Basic structure of the methods in this class based on similar methods in
 * Sunshine app, specifically S04.03-AddMapAndSharing.
 */

public final class MovieDBUtils {

    private static final String TAG = MovieDBUtils.class.getSimpleName();

    /* TODO: Figure out a way to set this value in a setting or menu thing. */
    private static int NUM_MOVIES_TO_FETCH = 100;

    /**
     * This method parses JSON from theMovieDB. I am including context in the constructor now,
     * so that if it is eventually decided to make the Release Date conform to local presentation
     * standards, the context will be there.
     *
     * @param context
     * @param moviesJsonString
     * @return
     * @throws JSONException
     */
    public static Movie[] getMoviesFromJson(Context context, String moviesJsonString)
            throws JSONException {

        if(moviesJsonString == null || moviesJsonString.equals("") ) Log.d(TAG, "JSON String empty");

        final String RESULTS       = "results";
        final String ID            = "id";
        final String TITLE         = "original_title";
        final String SYNOPSIS      = "overview";
        final String RATING        = "vote_average";
        final String RELEASE       = "release_date";
        final String POSTER_PATH   = "poster_path";
        final String MESSAGE_CODE  = "cod";
//        final String TOTAL_RESULTS = "total_results";

        Movie[] movies = null;

        JSONObject moviesJsonObject = new JSONObject(moviesJsonString);

        /* Check for error */
        if (moviesJsonObject.has(MESSAGE_CODE)) {
            int code = moviesJsonObject.getInt(MESSAGE_CODE);

            switch(code) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Invalid Search URL */
                    return null;
                default:
                    /* Don't know what the problem may be.... */
                    return null;
            }
        }

//        int totalResults = moviesJsonObject.getInt(TOTAL_RESULTS);
//
//        int moviesToFetch = totalResults < NUM_MOVIES_TO_FETCH ? totalResults : NUM_MOVIES_TO_FETCH;
//
        JSONArray moviesArray = moviesJsonObject.getJSONArray(RESULTS);
        movies = new Movie[moviesArray.length()];

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);

            Movie.Builder builder = new Movie.Builder(movie.getInt(ID))
                    .posterUrl(movie.getString(POSTER_PATH))
                    .title(movie.getString(TITLE))
                    .synopsis(movie.getString(SYNOPSIS))
                    .rating(movie.getString(RATING))
                    .releaseDate(movie.getString(RELEASE));

            movies[i] = builder.build();
        }

        return movies;
    }
}
