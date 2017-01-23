package com.app.movie.cinephilia.reviews;

/**
 * Created by GAURAV on 22-01-2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.app.movie.cinephilia.DataBus.AsyncTaskResultEvent;
import com.app.movie.cinephilia.DataBus.BusProvider;
//import com.app.movie.cinephilia.OnReviewDataFetchFinished;
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

public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<MovieReviewModel>>{

    Activity mContext;
    ReviewAdapter mReviewsAdapter;
    private ProgressDialog progress;

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();


    public FetchReviewTask(Activity context, ReviewAdapter adapter){
        this.mContext = context;
        this.mReviewsAdapter = adapter;
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
    protected ArrayList<MovieReviewModel> doInBackground(String... params) {

        String movieId;
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

            final String APPEND_PATH = "reviews";//params[1];

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
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
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

    private ArrayList<MovieReviewModel> getReviewsDataFromJson(String reviewsJsonStr)
            throws JSONException {

        // Define json paths
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        JSONObject reviewJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray("results");

        ArrayList<MovieReviewModel> reviews = new ArrayList<MovieReviewModel>(reviewArray.length());
        for(int i = 0; i < reviewArray.length(); i++) {
            String author;
            String content;

            // Get the JSON object representing the movie
            JSONObject reviewObject = reviewArray.getJSONObject(i);

            author = reviewObject.getString(REVIEW_AUTHOR);
            content = reviewObject.getString(REVIEW_CONTENT);

            reviews.add(new MovieReviewModel(author, content));
        }

        for (MovieReviewModel movieReviewModel : reviews) {
            Log.v(LOG_TAG, "Review author: " + movieReviewModel.mAuthor);
            Log.v(LOG_TAG, "Review content: " + movieReviewModel.mContent);
        }
        return reviews;

    }

    @Override
    protected void onPostExecute(ArrayList<MovieReviewModel> result) {

        Log.v(LOG_TAG, "TASK POST EXECUTE");
        if(result != null){
            mReviewsAdapter.clear();
            for(MovieReviewModel elem: result) {
                mReviewsAdapter.add(elem);
            }
        }
        //onReviewDataFetchFinished.reviewDataFetchFinished(true);
        BusProvider.getInstance().post(new AsyncTaskResultEvent(true, "FetchReviewTask"));
        mReviewsAdapter.notifyDataSetChanged();
        //progress.dismiss();
    }
}
