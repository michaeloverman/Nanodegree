package com.app.movie.cinephilia.MovieDBAPIs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.app.movie.cinephilia.MovieDBAPIs.MovieContract.FavoriteMoviesEntry;

/**
 * Created by GAURAV on 24-01-2016.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "FavourtieMovies.db";

    /** **/
    private static MovieDBHelper sInstance;

    /* Usage for Singleton Pattern
     * In any activity just pass the context and use the singleton method
      * MovieDBHelper helper = MovieDBHelper.getInstance(this);*/
    public static synchronized MovieDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MovieDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /* Constructor is declared private as in that case it cannot be called from
     * outside and that help us keeping a Singleton Instance of Database
      * so as to memory leaks and unnecessary allocations.
       * One has to call getInstance to obtain a reference to the MovieDBHelper*/
    private MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_FAVMOVIE_TABLE = "Create TABLE " + FavoriteMoviesEntry.TABLE_NAME+"("
                        + FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                        + FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
                        + FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                        + FavoriteMoviesEntry.COLUMN_OVERVIEW + " TEXT, "
                        + FavoriteMoviesEntry.COLUMN_VOTE_AVG + " REAL, "
                        + FavoriteMoviesEntry.COLUMN_VOTE_COUNT + " TEXT, "
                        + FavoriteMoviesEntry.COLUMN_POSTER_URL + " TEXT, "
                        + FavoriteMoviesEntry.COLUMN_BACKDROP_URL + " TEXT, "
                        + " UNIQUE (" + FavoriteMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVMOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
