package com.app.movie.cinephilia.trailers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.app.movie.cinephilia.DataBus.AsyncTaskResultEvent;
import com.app.movie.cinephilia.DataBus.BusProvider;
import com.app.movie.cinephilia.R;

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
 * Created by GAURAV on 31-01-2016.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<MovieTrailerModel>> {

    Activity mContext;
    //TrailerAdapter mTrailersAdapter;
    TrailerRecyclerAdapter mTrailersAdapter;
    //OnReviewDataFetchFinished onReviewDataFetchFinished;
    private ProgressDialog progress;

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();


    public FetchTrailerTask(Activity context, TrailerRecyclerAdapter adapter){
        this.mContext = context;
        this.mTrailersAdapter = adapter;
        //this.onReviewDataFetchFinished = onReviewDataFetchFinished;
    }

    @Override
    protected void onPreExecute(){
        /*progress = new ProgressDialog(mContext);
        progress.setMessage("Loading Data");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();*/
    }

    @Override
    protected ArrayList<MovieTrailerModel> doInBackground(String... params) {

        String movieId;

        // If there's no sortby param
        if (params.length == 0) {
            return null;
        }

        movieId = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailersJsonStr = null;

        try {
            // Construct the URL for the API
            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";

            final String API_KEY_PARAM = "api_key";

            final String APPEND_PATH = "videos";//params[1];

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(APPEND_PATH)
                    .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            trailersJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Reviews string: " + trailersJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movies data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getReviewsDataFromJson(trailersJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the trailers Data.
        return null;
    }

    private ArrayList<MovieTrailerModel> getReviewsDataFromJson(String reviewsJsonStr)
            throws JSONException {

        // Define json paths
        final String TRAILER_NAME = "name";
        final String TRAILER_KEY = "key";
        final String TRAILER_SITE = "site";
        String name;
        String key;
        String site;

        JSONObject reviewJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray("results");

        ArrayList<MovieTrailerModel> reviews = new ArrayList<MovieTrailerModel>(reviewArray.length());
        for(int i = 0; i < reviewArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject reviewObject = reviewArray.getJSONObject(i);

            name = reviewObject.getString(TRAILER_NAME);
            key = reviewObject.getString(TRAILER_KEY);
            site = reviewObject.getString(TRAILER_SITE);

            reviews.add(new MovieTrailerModel(key,name,site));
        }

        for (MovieTrailerModel MovieTrailerModel : reviews) {
            Log.v(LOG_TAG, "Trailer name: " + MovieTrailerModel.mName);
            Log.v(LOG_TAG, "Trailer key: " + MovieTrailerModel.mKey);
            Log.v(LOG_TAG, "Trailer site: " + MovieTrailerModel.mSite);
        }
        return reviews;

    }

    @Override
    protected void onPostExecute(ArrayList<MovieTrailerModel> result) {

        Log.v(LOG_TAG, "TASK POST EXECUTE");
        if(result != null){
            //mTrailersAdapter.clear();
            //for(MovieTrailerModel elem: result) {
            //    mTrailersAdapter.add(elem);
            //}
            for(MovieTrailerModel elem: result)
                mTrailersAdapter.addItem(elem);
        }
        BusProvider.getInstance().post(new AsyncTaskResultEvent(true, "FetchTrailerTask"));
        mTrailersAdapter.notifyDataSetChanged();
        //progress.dismiss();
    }
}
