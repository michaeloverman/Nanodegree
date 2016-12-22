package tech.michaeloverman.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Michael on 12/20/2016.
 */

public class FavoritesDBHelper extends SQLiteOpenHelper {
    
    public static final String TAG = FavoritesDBHelper.class.getSimpleName();
    
    public static final String DATABASE_NAME = "favorite_movies.db";
    private static final int DATABASE_VERSION = 1;
    
    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, ".... in constructor...");
    }
    
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_TABLE =
                "CREATE TABLE " + FavoritesContract.FavoriteEntry.TABLE_NAME + " (" +
                        FavoritesContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        FavoritesContract.FavoriteEntry.COLUMN_POSTER_URL + " STRING, " +
                        " UNIQUE (" + FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID +
                        ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        
    }
    
}
