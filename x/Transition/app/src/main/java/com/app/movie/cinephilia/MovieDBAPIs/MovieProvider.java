package com.app.movie.cinephilia.MovieDBAPIs;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.movie.cinephilia.MovieDBAPIs.MovieContract.FavoriteMoviesEntry;

/**
 * Created by GAURAV on 24-01-2016.
 */
public class MovieProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final String TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;
    private static SQLiteQueryBuilder sQueryBuilder;

    static final int FAVOURITES = 100;
    static final int FAVOURITE_WITH_ID = 101;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(FavoriteMoviesEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITES,FAVOURITES);
        mUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_FAVOURITES+"/#", FAVOURITE_WITH_ID);
        return mUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = MovieDBHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        Log.v(TAG,"uri: "+uri.toString());
        int match = sUriMatcher.match(uri);
        switch(match){
            case FAVOURITES:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;}
            case FAVOURITE_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        FavoriteMoviesEntry.COLUMN_MOVIE_ID + "= ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;}
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        db.beginTransaction();
        switch (match) {
            case FAVOURITES: {
                long _id = db.insert(MovieContract.FavoriteMoviesEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    returnUri = MovieContract.FavoriteMoviesEntry.buildFavouritesURI(_id);
                    db.setTransactionSuccessful();
                }else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                db.endTransaction();
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int retVal;
        db.beginTransaction();
        switch (match){
            case FAVOURITES: {
                retVal = db.update(MovieContract.FavoriteMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            }default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(retVal!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return retVal;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int retVal;
        if(selection==null)
            selection = "1";
        switch (match){
            case FAVOURITES:
                retVal = db.delete(MovieContract.FavoriteMoviesEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case FAVOURITE_WITH_ID:
                retVal = db.delete(FavoriteMoviesEntry.TABLE_NAME, FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        if(retVal!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return retVal;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVOURITES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavoriteMoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVOURITE_WITH_ID:
                return MovieContract.FavoriteMoviesEntry.CONTENT_ITEM_TYPE;
            case FAVOURITES:
                return MovieContract.FavoriteMoviesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
