package tech.michaeloverman.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static tech.michaeloverman.android.popularmovies.MainActivity.NOWPLAYING;
import static tech.michaeloverman.android.popularmovies.MainActivity.POPULAR;
import static tech.michaeloverman.android.popularmovies.MainActivity.TOPRATED;
import static tech.michaeloverman.android.popularmovies.MainActivity.UPCOMING;

/**
 * Static class to handle Networking operations. Methods to create urls
 * and download JSON. Basic structure and approach modelled on Sunshine NetworkUtils.
 *
 * Created by Michael on 12/7/2016.
 */

//  https://api.themoviedb.org/3/movie/550?api_key=

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String THUMBNAIL_SIZE = "w185";
    private static final String POSTER_SIZE = "w342";
    private static final String API_PARAM = "api_key";
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String POPULAR_PATH = "popular";
    private static final String TOPRATED_PATH = "top_rated";
    private static final String UPCOMING_PATH = "upcoming";
    private static final String NOWPLAYING_PATH = "now_playing";
    public static final String VIDEOS_PATH = "videos";
    public static final String REVIEWS_PATH = "reviews";
    private static final String YOUTUBE_BASE = "https://www.youtube.com/watch";
    private static final String YOUTUBE_PARAM = "v";

    /* API Key is stored in separate class. That class file is ignored by git for
    security reasons.
     */
    private static final String API_KEY = MOVIE_DB_KEY.KEY;

    public static String buildThumbnailUrl(String posterPath) {
        return POSTER_BASE_URL + THUMBNAIL_SIZE + posterPath;
    }

    public static String buildPosterUrl(String posterPath) {
        return POSTER_BASE_URL + POSTER_SIZE + posterPath;
    }

    public static URL buildSearchUrl(int criterium) {
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

        return url;
    }
    
    public static URL buildVideoDBUrl(int id, String path) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Integer.toString(id) )
                .appendPath(path)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();
    
        URL url = null;
    
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//    Log.d(TAG, "URL build to get movies: " + url);
        return url;
    
    }
    public static URL buildVideoLink(String key) {
        
        Uri uri = Uri.parse(YOUTUBE_BASE).buildUpon()
                .appendQueryParameter(YOUTUBE_PARAM, key)
                .build();
        URL url = null;
    
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//    Log.d(TAG, "YouTube link built: " + url);
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


}
