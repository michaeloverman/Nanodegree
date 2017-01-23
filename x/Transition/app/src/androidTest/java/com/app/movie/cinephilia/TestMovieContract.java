package com.app.movie.cinephilia;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.app.movie.cinephilia.MovieDBAPIs.MovieContract;

/**
 * Created by GAURAV on 26-01-2016.
 */
public class TestMovieContract extends AndroidTestCase {
    public String MOVIE_ID = "266447";

    public void testBuildFavouritesWithId(){
        Uri locationUri = MovieContract.FavoriteMoviesEntry.buildFavouriteMoviesUriWithMovieId(MOVIE_ID);
        Log.v("TestMovieContract: ", locationUri.toString());
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                MOVIE_ID, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.app.movie.cinephilia/favouriteMovies/"+MOVIE_ID);
    }
}
