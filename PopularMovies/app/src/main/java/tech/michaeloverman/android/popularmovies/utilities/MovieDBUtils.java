package tech.michaeloverman.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import tech.michaeloverman.android.popularmovies.Movie;
import tech.michaeloverman.android.popularmovies.VideoLink;

/**
 * Utility methods for parsing JSON
 * Created by Michael on 12/7/2016.
 *
 * Basic structure of the methods in this class based on similar methods in
 * Sunshine app, specifically S04.03-AddMapAndSharing.
 */

public final class MovieDBUtils {

    private static final String TAG = MovieDBUtils.class.getSimpleName();

    private static final String RESULTS       = "results";
    private static final String ID            = "id";
    private static final String TITLE         = "original_title";
    private static final String SYNOPSIS      = "overview";
    private static final String RATING        = "vote_average";
    private static final String RELEASE       = "release_date";
    private static final String POSTER_PATH   = "poster_path";
    private static final String DURATION      = "runtime";
    private static final String MESSAGE_CODE  = "cod";
    private static final String VIDEOS        = "video";
    private static final String VIDEO_KEY     = "key";
    private static final String VIDEO_TITLE_KEY = "name";

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


        JSONArray moviesArray = moviesJsonObject.getJSONArray(RESULTS);
        movies = new Movie[moviesArray.length()];

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);

            Movie.Builder builder = new Movie.Builder(movie.getInt(ID))
                    .posterUrl(movie.getString(POSTER_PATH));

            movies[i] = builder.build();
        }

        return movies;
    }

    /**
     * Method to parse individual movie data from JSON string. Context is included to allow
     * for location specific date notation in the future.
     *
     * @param context
     * @param movieJsonString
     * @return
     * @throws JSONException
     */
    public static Movie getSingleMovieFromJson(Context context, String movieJsonString)
            throws JSONException {
        if(movieJsonString == null || movieJsonString.equals("") ) Log.d(TAG, "JSON String empty");

        JSONObject movie = new JSONObject(movieJsonString);

        /* Build the Movie object from the JSON data */
        Movie.Builder builder = new Movie.Builder(movie.getInt(ID))
                .posterUrl(movie.getString(POSTER_PATH))
                .title(movie.getString(TITLE))
                .synopsis(movie.getString(SYNOPSIS))
                .rating(movie.getString(RATING))
                .releaseDate(movie.getString(RELEASE))
                .duration(movie.getInt(DURATION));
//                .video(movie.getBoolean(VIDEOS));

        return builder.build();
    }
    
    public static ArrayList<VideoLink> getVideoLinksFromJson(String videoLinkJsonString)
            throws JSONException {
        if(videoLinkJsonString == null || videoLinkJsonString.equals("") ) Log.d(TAG, "Empty JSON String");
        
        ArrayList<VideoLink> links = new ArrayList<>();
        
        JSONObject videoLinks = new JSONObject(videoLinkJsonString);
        
        JSONArray videosArray = videoLinks.getJSONArray("results");
        
        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject video = videosArray.getJSONObject(i);
            String key = video.getString(VIDEO_KEY);
            URL url = NetworkUtils.buildVideoLink(key);
            String title = video.getString(VIDEO_TITLE_KEY);
            links.add(new VideoLink(title, url));
        }
        Log.d(TAG, links.size() + " VideoLinks made!!!!!!!!!!!!!!!!!!!");
        return links;
    }
}
