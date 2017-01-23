package com.app.movie.cinephilia.CastandCrew;

/**
 * Created by GAURAV on 20-05-2016.
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
import com.app.movie.cinephilia.reviews.MovieReviewModel;
import com.app.movie.cinephilia.reviews.ReviewAdapter;

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

public class FetchCreditsTask extends AsyncTask<String, Void, ArrayList<MovieCreditsModel>>{

    Activity mContext;
    CreditsAdapter mCreditsAdapter;
    private ProgressDialog progress;

    private final String LOG_TAG = FetchCreditsTask.class.getSimpleName();


    public FetchCreditsTask(Activity context, CreditsAdapter adapter){
        this.mContext = context;
        this.mCreditsAdapter = adapter;
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
    protected ArrayList<MovieCreditsModel> doInBackground(String... params) {

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
        String creditsJsonStr = null;

        try {
            // Construct the URL for the API
            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";

            final String API_KEY_PARAM = "api_key";

            final String APPEND_PATH = "credits";//params[1];

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(APPEND_PATH)
                    .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG,url.toString());

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
            creditsJsonStr = buffer.toString();

            //Log.v(LOG_TAG, "Credits string: " + creditsJsonStr);
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error ", e);
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
                    //Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getCreditsDataFromJson(creditsJsonStr);
        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the trailers Data.
        return null;
    }

    private ArrayList<MovieCreditsModel> getCreditsDataFromJson(String creditsJsonStr)
            throws JSONException {

        // Define json paths
        final String CREDITS_CHARACTER = "character";
        final String CREDITS_NAME = "name";
        final String CREDITS_PROFILE_PATH = "profile_path";

        JSONObject creditsJson = new JSONObject(creditsJsonStr);
        JSONArray creditsArray = creditsJson.getJSONArray("cast");

        ArrayList<MovieCreditsModel> credits = new ArrayList<>(creditsArray.length());
        for(int i = 0; i < creditsArray.length(); i++) {
            String character;
            String name;
            String profile_path;

            // Get the JSON object representing the movie
            JSONObject creditsObject = creditsArray.getJSONObject(i);

            character = creditsObject.getString(CREDITS_CHARACTER);
            name = creditsObject.getString(CREDITS_NAME);
            profile_path = creditsObject.getString(CREDITS_PROFILE_PATH);

            credits.add(new MovieCreditsModel(character, name, profile_path));
        }

        for (MovieCreditsModel movieCreditsModel : credits) {
            //Log.v(LOG_TAG, "CREDITS character: " + movieCreditsModel.mCharacter);
            //Log.v(LOG_TAG, "CREDITS NAME: " + movieCreditsModel.mName);
        }
        return credits;

    }

    @Override
    protected void onPostExecute(ArrayList<MovieCreditsModel> result) {

        //Log.v(LOG_TAG, "TASK POST EXECUTE");
        if(result != null){
            mCreditsAdapter.clear();
            for(MovieCreditsModel elem: result) {
                mCreditsAdapter.add(elem);
            }
        }
        //onReviewDataFetchFinished.reviewDataFetchFinished(true);
        BusProvider.getInstance().post(new AsyncTaskResultEvent(true, "FetchCreditsTask"));
        mCreditsAdapter.notifyDataSetChanged();
        //progress.dismiss();
    }
}
