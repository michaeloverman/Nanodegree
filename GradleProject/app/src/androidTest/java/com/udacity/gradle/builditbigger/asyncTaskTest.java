package com.udacity.gradle.builditbigger;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael on 1/27/2017.
 *
 * Testing of the AsyncTask modelled on the information provided here:
 * http://www.making-software.com/2012/10/31/testable-android-asynctask/
 *
 * JokeLoader is a wrapper for the AsyncTask, with a basic Listener interface
 * other classes implement in order to receive the data after it is loaded. This
 * test class simply uses the same listener interface.
 */

@RunWith(AndroidJUnit4.class)
public class asyncTaskTest implements JokeLoader.JokeLoaderListener {
    JokeLoader loader;
    CountDownLatch signal;
    String joke;

    @Before
    public void setUp() throws Exception {
        signal = new CountDownLatch(1);
        loader = new JokeLoader(this);
    }

    @After
    public void tearDown() throws Exception {
        signal.countDown();
    }

    @Test
    public void verifyAsyncTaskResponse() throws InterruptedException {
        loader.loadJoke();
        signal.await(30, TimeUnit.SECONDS);

        assert(!TextUtils.isEmpty(joke));
    }

    @Override
    public void jokeLoaded(String joke) {
        signal.countDown();
        this.joke = joke;
    }
}
