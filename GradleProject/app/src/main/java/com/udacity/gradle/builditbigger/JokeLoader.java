package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;

import com.example.michael.myapplication.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by Michael on 1/27/2017.
 * A wrapper class for the AsyncTask which accesses the Google Cloud Endpoints
 * backend server. Structure modelled on the example provided here:
 * http://www.making-software.com/2012/10/31/testable-android-asynctask/
 *
 * JokeLoaderListener interface provides a simple means to get the data back to
 * other concerned activities/classes.
 */

public class JokeLoader {
    private JokeLoaderListener listener;

    public interface JokeLoaderListener {
        void jokeLoaded(String joke);
    }

    public JokeLoader(JokeLoaderListener listener) {
        this.listener = listener;
    }

    public void loadJoke() {
        new EndpointsAsyncTask().execute();
    }

    class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
        private MyApi myApiService = null;
//        private Context context;

        @Override
        protected String doInBackground(Void... params) {
            if(myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            try {
                return myApiService.getJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // call the listener with the result...
            listener.jokeLoaded(result);
        }
    }
}
