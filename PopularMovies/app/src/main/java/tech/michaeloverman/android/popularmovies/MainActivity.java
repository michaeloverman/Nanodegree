package tech.michaeloverman.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.michaeloverman.android.popularmovies.data.FavoritesContract;
import tech.michaeloverman.android.popularmovies.data.MoviePreferences;
import tech.michaeloverman.android.popularmovies.utilities.MovieDBUtils;
import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements ThumbnailAdapter.ThumbnailOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* Key for storing sort method in onSaveInstanceState bundle */
    private static final String CURRENT_SEARCH = "search_index";

    /* Static variables for tracking sort method */
    public static final int TOPRATED = 1;
    public static final int POPULAR = 2;
    public static final int NOWPLAYING = 3;
    public static final int UPCOMING = 4;
    public static final int FAVORITES = 5;
    
    public static final String[] MAIN_FAVORITES_PROJECTION = {
            FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID,
            FavoritesContract.FavoriteEntry.COLUMN_POSTER_URL,
    };
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_POSTER_URL = 1;
    
    
    /* Member variables for controlling MainActivity view */
    @BindView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    @BindView(R.id.pb_download_indicator)
    ProgressBar mLoadingIndicator;

    private ThumbnailAdapter mThumbnailAdapter;

    private int mCurrentSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mCurrentSearch = savedInstanceState.getInt(CURRENT_SEARCH, 2);
            changeHeader();
        } else {
            mCurrentSearch = MoviePreferences.getSortCriterium(this);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, determineSpanCount());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mThumbnailAdapter = new ThumbnailAdapter(this);
        mRecyclerView.setAdapter(mThumbnailAdapter);

        loadMovies();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SEARCH, mCurrentSearch);
    }

    /**
     * Sets number of columns in GridLayout: 2 columns for portrait, 3 for landscape.
     * Eventually should accommodate wider variety of devices by being more sophisticated
     *  and determining width and height in pixels, and calculating number of columns from that.
     *
     * @return
     */
    private int determineSpanCount() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 2;
        } else {
            return 3;
        }
    }

    /* Inflate single menu item */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sort_by)
            showPopup(findViewById(R.id.sort_by));

        return true;
    }

    /* PopupMenu for sorting options */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup, popup.getMenu());
        popup.show();
    }

    /* Handle clicks on PopupMenu options */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.popular:
                mCurrentSearch = POPULAR;
                break;
            case R.id.rated:
                mCurrentSearch = TOPRATED;
                break;
            case R.id.current:
                mCurrentSearch = NOWPLAYING;
                break;
            case R.id.upcoming:
                mCurrentSearch = UPCOMING;
                break;
            case R.id.favorites:
                mCurrentSearch = FAVORITES;
                break;
            default:
                mCurrentSearch = POPULAR;
        }
    
        MoviePreferences.setSortCriterium(this, mCurrentSearch);

        /* Reload movies with new search criteria */
        loadMovies();

        /* Change view title to reflect current search criteria */
        changeHeader();

        return true;
    }

    /**
     * Changes title of app window to show current search/sort criteria
     */
    private void changeHeader() {
        String label = null;
        switch(mCurrentSearch) {
            case POPULAR:
                label = getString(R.string.pref_most_popular);
                break;
            case TOPRATED:
                label = getString(R.string.pref_highest_rated);
                break;
            case NOWPLAYING:
                label = getString(R.string.pref_now_showing);
                break;
            case UPCOMING:
                label = getString(R.string.pref_upcoming);
                break;
            default:
                label = getString(R.string.app_name);
        }
        this.setTitle(label);
    }

    /* Calls AsyncTask to download movies */
    private void loadMovies() {
        new GetMoviesTask().execute(mCurrentSearch);
    }

    /* If movies download, show movies, not error message */
    private void showMoviePosters() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /* If movies do not download, show error message */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Handle selection of movie from GridLayout, create intent, and start child activity
     * with detail information
     * @param movie
     */
    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destination = DetailActivity.class;
        Intent intent = new Intent(context, destination);
        intent.putExtra(Intent.EXTRA_UID, movie.getId());
        startActivity(intent);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        
        return new CursorLoader(this,
                FavoritesContract.FavoriteEntry.CONTENT_URI,
                MAIN_FAVORITES_PROJECTION,
                null,
                null,
                null);
        
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() != 0) {
            Movie[] films = new Movie[data.getCount()];
            data.moveToFirst();
            for(int i=0; i < data.getCount(); i++) {
                try {
                    films[i] = MovieDBUtils.getSingleMovieFromJson(this, NetworkUtils.getJsonFromUrl(
                            NetworkUtils.buildSingleMovieUrl(data.getInt(MainActivity.INDEX_MOVIE_ID))));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        mThumbnailAdapter.setMovies(films);
        }
        
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        
    }
    
    /**
     * AsyncTask to download movies in background.
     */
    private class GetMoviesTask extends AsyncTask<Integer, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(Integer... criteria) {

            if(criteria.length == 0) {
                return null;
            }

            int crit = criteria[0];
            if(crit == FAVORITES) {
                return getFavoriteMovies();
            } else {
                URL movieSearchUrl = NetworkUtils.buildSearchUrl(crit);
    
                try {
                    String movieSearchResultJson = NetworkUtils.getJsonFromUrl(movieSearchUrl);
        
                    Movie[] movies = MovieDBUtils.getMoviesFromJson(MainActivity.this, movieSearchResultJson);
        
                    return movies;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null) {
                showMoviePosters();
                mThumbnailAdapter.setMovies(movies);
            } else {
                showErrorMessage();
            }
        }
        
        private Movie[] getFavoriteMovies() {
            Movie[] movies;
            Uri queryUri = FavoritesContract.FavoriteEntry.CONTENT_URI;
            String [] projection = {FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID};
            
            Cursor cursor = getApplicationContext().getContentResolver().query(
                    queryUri,
                    projection,
                    null,
                    null,
                    null);
            
            int count = cursor.getCount();
            movies = new Movie[count];
            for (int i = 0; i < count; i++) {
                cursor.move(i);
                int id = cursor.getInt(INDEX_MOVIE_ID);
                String url = cursor.getString(INDEX_POSTER_URL);
                movies[i] = new Movie.Builder(id)
                        .posterUrl(url)
                        .build();
            }
            
            
            return movies;
        }
    }
}
