package com.app.movie.cinephilia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.stetho.urlconnection.SimpleRequestEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by GAURAV on 20-12-2015.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieModel>> {
    private final String LOG_TAG2 = FetchMovieTask.class.getSimpleName();
    String inp_url, JSONResponse;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    private String sort_by = null;
    private ProgressDialog progress;
    private Activity mActivity;
    private OnMovieDataFetchFinished fetchFinishedCallback;
    private String fetchType = null;
    private String searchQuery = null;

    // Page Number for Endless Scrolling
    private String pageNumber = "1";

    private static final int PAGE_LIMIT = 1;
    public static final String TOTAL_PAGES_KEY = "total_pages";
    public static final String TOTAL_RESULTS_KEY = "total_results";
    public static final String RESULTS_KEY = "results";
    public static final String ORIGINAL_TITLE_KEY = "original_title";
    public static final String VOTE_AVERAGE_KEY = "vote_average";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String OVERVIEW_KEY = "overview";
    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String VOTE_COUNT = "vote_count";
    public static final String BACKDROP_PATH_KEY = "backdrop_path";
    public static final String PAGE_NUMBER_KEY = "page";
    public static final String RESULTS_kEY = "results";
    public static final String ID = "id";
    //final AVLoadingIndicatorDialog dialog=new AVLoadingIndicatorDialog(this);

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP_ENCODING = "gzip";

    public FetchMovieTask(Activity activity, OnMovieDataFetchFinished callback, String fetchType){
        this.mActivity = activity;
        this.fetchFinishedCallback = callback;
        this.fetchType = fetchType;
    }

    private static void requestDecompression(HttpURLConnection conn) {
        conn.setRequestProperty(HEADER_ACCEPT_ENCODING, GZIP_ENCODING);
    }

    public ArrayList<MovieModel> getMovies(String sort_by, String pageNum) throws IOException{
        String responseJSONStr;
        ArrayList<MovieModel> movies = new ArrayList<>();

        //for(int i=1; i<=PAGE_LIMIT; i++) {
            Uri.Builder builder = new Uri.Builder();

            if( this.fetchType.compareTo("fetchMovies") == 0) {
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        //.appendPath("discover")
                        .appendPath("movie")
                        .appendPath(sort_by)
                        //.appendQueryParameter("sort_by", sort_by)
                        //.appendQueryParameter("page", pageNum)
                        .appendQueryParameter("api_key", mActivity.getString(R.string.api_key));
            } else if( this.fetchType.compareTo("searchQuery") == 0) {
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("search")
                        .appendPath("movie")
                        .appendQueryParameter("query",sort_by)
                        .appendQueryParameter("api_key", mActivity.getString(R.string.api_key));
            } else if( this.fetchType.compareTo("fetchSearchedMovie") == 0) {
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sort_by)
                        .appendQueryParameter("api_key", mActivity.getString(R.string.api_key));
            }

            inp_url = builder.build().toString();

            URL url = new URL(inp_url);
            Log.v(LOG_TAG2,"url: "+url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode;
            try {
                responseCode = urlConnection.getResponseCode();
            }catch (IOException e){
                responseCode = urlConnection.getResponseCode();
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                responseJSONStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                responseJSONStr = null;
            }
            responseJSONStr = buffer.toString();
            try {
                Log.v(LOG_TAG2,Integer.toString(responseJSONStr.length()));
                movies.addAll(parseResult(responseJSONStr));
            }catch (JSONException e) {
                Log.e(LOG_TAG2, e.getMessage(), e);
                e.printStackTrace();
            }
        //}
        return movies;
    }

    @Override
    protected void onPreExecute(){
        /*progress = new ProgressDialog(mActivity);
        progress.setMessage("Loading Data");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);*/
        //progress.show();
    }

    protected ArrayList<MovieModel> doInBackground(String... params) {
        Log.v(LOG_TAG2,"in background");
        try {
            if (params.length == 2) {
                if (params[0].equals("Most Popular"))
                    sort_by = "popular";
                else if (params[0].equals("Highest Rated"))
                    sort_by = "top_rated";

                pageNumber = params[1];
            } else if (params.length == 1) {
                pageNumber = "0";
                sort_by = params[0];
            }
            return getMovies(sort_by, pageNumber);
        } catch (IOException e) {
            JSONResponse = null;
            Log.d(LOG_TAG2, e.getLocalizedMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG2, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieModel> result){
        if(result != null){
            fetchFinishedCallback.MovieDataFetchFinished(result);
            /*for(MovieModel elem: result) {
                mGridAdapter.add(elem);
            }*/
        }
        //progress.dismiss();
    }

    private ArrayList<MovieModel> parseResult(String result) throws JSONException {
        try {
            //Log.v(LOG_TAG2,"parsing result:"+result);
            JSONObject response = new JSONObject(result);

            if (this.fetchType.compareTo("fetchMovies") == 0 || this.fetchType.compareTo("searchQuery") == 0) {
                JSONArray posts = response.getJSONArray("results");
                ArrayList<MovieModel> items = new ArrayList<>();
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    MovieModel movie = new MovieModel(post.getString(ORIGINAL_TITLE_KEY),
                            post.getDouble(VOTE_AVERAGE_KEY),
                            post.getString(RELEASE_DATE_KEY),
                            post.getString(OVERVIEW_KEY),
                            post.getString(VOTE_COUNT),
                            post.getString(BACKDROP_PATH_KEY),
                            post.getInt(ID),
                            post.getString(POSTER_PATH_KEY));
                    Log.v(LOG_TAG2, post.getString(ORIGINAL_TITLE_KEY));
                    items.add(movie);
                }
                return items;
            } else if (this.fetchType.compareTo("fetchSearchedMovie") == 0){
                ArrayList<MovieModel> items = new ArrayList<>();
                MovieModel movieModel = new MovieModel(response.getString(ORIGINAL_TITLE_KEY),
                        response.getDouble(VOTE_AVERAGE_KEY),
                        response.getString(RELEASE_DATE_KEY),
                        response.getString(OVERVIEW_KEY),
                        response.getString(VOTE_COUNT),
                        response.getString(BACKDROP_PATH_KEY),
                        response.getInt(ID),
                        response.getString(POSTER_PATH_KEY));
                items.add(movieModel);
                return items;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
