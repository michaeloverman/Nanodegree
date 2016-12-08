package tech.michaeloverman.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import tech.michaeloverman.android.popularmovies.SearchCriteria;

/**
 * Created by Michael on 12/7/2016.
 */

//  https://api.themoviedb.org/3/movie/550?api_key=

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w342";
    private static final String API_PARAM = "api_key";
    private static final String API_KEY = MOVIE_DB_KEY.KEY;
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String POPULAR_PATH = "popular";
    private static final String TOPRATED_PATH = "top_rated";
    private static final String UPCOMING_PATH = "upcoming";
    private static final String NOWPLAYING_PATH = "now_playing";

    public static String buildPosterUrl(String posterPath) {
//        Uri uri = Uri.parse(POSTER_BASE_URL).buildUpon()
//                .appendPath(POSTER_SIZE)
//                .appendPath(posterPath)
//                .build();
//
//        URL url = null;
//        try {
//            url = new URL(uri.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        String url = POSTER_BASE_URL + POSTER_SIZE + posterPath;

        Log.v(TAG, "Poster uri: " + url);

        return url;
    }

    public static URL buildSingleMovieUrl(int id) {

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Integer.toString(id) )
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Search url: " + url);

        return url;
    }

    public static String getJsonFromUrl(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = connection.getInputStream();

            Scanner scan = new Scanner(in);
            scan.useDelimiter("\\A");

            boolean hasInput = scan.hasNext();
            if(hasInput) {
                return scan.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d(TAG, "Problem connecting to MovieDB");
        } finally {
            connection.disconnect();
        }
        return null;
    }

    public static URL buildSearchUrl(SearchCriteria criterium) {
        String search;
        switch(criterium) {
            case POPULAR:
                search = POPULAR_PATH;
                break;
            case TOPRATED:
                search = TOPRATED_PATH;
                break;
            case UPCOMING:
                search = UPCOMING_PATH;
                break;
            case NOWPLAYING:
                search = NOWPLAYING_PATH;
                break;
            default:
                search = "";
        }
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(search)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Search url: " + url);

        return url;
    }
}
