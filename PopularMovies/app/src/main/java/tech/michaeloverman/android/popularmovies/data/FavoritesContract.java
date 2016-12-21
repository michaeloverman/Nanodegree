package tech.michaeloverman.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Michael on 12/20/2016.
 */

public class FavoritesContract {
    
    public static final String CONTENT_AUTHORITY = "tech.michaeloverman.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";
    
    public static final class FavoriteEntry implements BaseColumns {
        
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();
        
        public static final String TABLE_NAME = "favorites";
        
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_URL = "poster_url";
        
        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
        
    }
}
