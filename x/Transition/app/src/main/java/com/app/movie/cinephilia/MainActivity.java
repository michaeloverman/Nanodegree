package com.app.movie.cinephilia;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements Callback {

    private static final String msg = "Android: ";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private Utility utility;
    private static View parentLayout;
    public static boolean mTwoPane = false;
    private Context context;
    BroadcastReceiver broadcastReceiver;
    private boolean connectionLostFlag = false;
    private TextView searchText;
    private EditText searchEditText;

    /* Member Variables for Interpolator */
    private Interpolator mInterpolators;
    /* Path for out (growing) animation, from 20% to 100%. */
    private Path mPathOut;
    private static final int INITIAL_DURATION_MS = 500;
    private Animator defaultAppearingAnim, defaultDisappearingAnim;
    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        parentLayout = findViewById(R.id.container);
        PreferenceManager.setDefaultValues(this, R.xml.user_preference, false);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewGroup = (ViewGroup) findViewById(R.id.main_activity);

        searchText = (TextView) findViewById(R.id.search_box);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterpolators = AnimationUtils.loadInterpolator(MainActivity.this
                                ,android.R.interpolator.linear_out_slow_in);
                Intent intent = new Intent(MainActivity.this, MovieSearchActivity.class);
                /*View sharedView = searchText;
                ActivityOptions transitionActivityOptions = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this, sharedView, sharedView.getTransitionName());
                startActivity(intent, transitionActivityOptions.toBundle());*/
                startActivity(intent);
                //(mInterpolators, INITIAL_DURATION_MS, mPathOut);
            }
        });

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MovieFragmentPagerAdapter movieFragmentPagerAdapter = new MovieFragmentPagerAdapter(
                getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(movieFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(movieFragmentPagerAdapter.getTabView(i));
        }
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.lightGrey));

        /*final LayoutTransition transitioner = new LayoutTransition();
        viewGroup.setLayoutTransition(transitioner);
        defaultAppearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_APPEARING);
        defaultDisappearingAnim =
                transitioner.getAnimator(LayoutTransition.CHANGE_DISAPPEARING);*/

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailsFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            setupWindowAnimations();
        }
    }

/*    public void fade(View view){
        ImageView image = (ImageView)findViewById(R.id.imageView);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        image.startAnimation(animation1);
    }*/

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        getMenuInflater().inflate(R.menu.menu_search_movie, menu);
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, MovieSearchActivity.class);
            startActivity(intent);
            //this.overridePendingTransition(R.anim.slide_down_info,R.anim.slide_up_info);
            return true;
        } else if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void defaultItemSelected(MovieModel item){
        if(mTwoPane){
            onItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(MovieModel item){
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(Intent.EXTRA_TEXT, item);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, item);
            ImageView posterImg = (ImageView) findViewById(R.id.image_view_poster);

            //ActivityOptionsCompat options = ActivityOptionsCompat.
              //      makeSceneTransitionAnimation(this, (View)posterImg, "profile");

            //ActivityOptionsCompat options = ActivityOptionsCompat.
              //              makeSceneTransitionAnimation(this);
            //startActivity(intent,options.toBundle());
            startActivity(intent);
            overridePendingTransition(R.transition.slide_exit,R.transition.slide_enter);
        }
    }

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(msg, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        //unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
