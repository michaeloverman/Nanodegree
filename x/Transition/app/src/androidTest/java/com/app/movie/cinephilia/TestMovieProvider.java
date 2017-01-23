package com.app.movie.cinephilia;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.app.movie.cinephilia.MovieDBAPIs.MovieContract;
import com.app.movie.cinephilia.MovieDBAPIs.MovieContract.FavoriteMoviesEntry;

/**
 * Created by GAURAV on 26-01-2016.
 */
public class TestMovieProvider extends AndroidTestCase {
    private static final String TAG = TestMovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITES,100);
        mUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_FAVOURITES+"/*", 101);
        return mUriMatcher;
    }

    public String checkType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case 101:
                return MovieContract.FavoriteMoviesEntry.CONTENT_ITEM_TYPE;
            case 100:
                return MovieContract.FavoriteMoviesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(FavoriteMoviesEntry.CONTENT_URI);
        Log.v("type: ",checkType(FavoriteMoviesEntry.CONTENT_URI));
        Log.v(TAG, "uri: " + FavoriteMoviesEntry.CONTENT_URI.toString());
        assertEquals("Error: the FavouriteMovieEntry CONTENT_URI should return FavoriteMoviesEntry.CONTENT_TYPE",
                FavoriteMoviesEntry.CONTENT_TYPE, type);
    }
}
