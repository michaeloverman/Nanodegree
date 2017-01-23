package com.udacity.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        StockAdapter.StockAdapterOnClickHandler {

    private static final int STOCK_LOADER = 0;
    private static final int VALIDATE_STOCK = 777;
    public static final String INVALID_STOCK_SYMBOL = "invalidstocksymbol";
    public static final String INVALID_STOCK_MESSAGE = "invalidstockmessage";
    public static final String STOCK_SYMBOL_EXTRA = "stocksymbolextra";
    public static final int NO_NETWORK_NO_STOCKS = 1;
    private static final int NO_STOCKS = 2;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView error;
    private StockAdapter adapter;

    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(INVALID_STOCK_MESSAGE);
            Timber.d("LocalBroadcastReceiver message received: " + message);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("onCreate()");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new StockAdapter(this, this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        QuoteSyncJob.initialize(this);
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(MainActivity.this, symbol);
//                getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
                QuoteSyncJob.removeStockFromDB(MainActivity.this, symbol);
            }
        }).attachToRecyclerView(stockRecyclerView);


    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLocalBroadcastReceiver, new IntentFilter(INVALID_STOCK_SYMBOL));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver);
        super.onPause();
        Timber.d("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.d("onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy()");
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(String symbol, View priceView) {
        Timber.d("Symbol clicked: %s", symbol);
        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra(STOCK_SYMBOL_EXTRA, symbol);
        ActivityOptionsCompat activityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        new Pair<View, String>(priceView, getString(R.string.trans_price_view_name)));
        this.startActivity(intent, activityOptions.toBundle());
    }

    @Override
    public void onRefresh() {

        Timber.d("onRefresh()");

        if (!networkUp() && adapter.getItemCount() == 0) {
            Timber.d("No network, nothing in adapter");
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
            updateEmptyView(NO_NETWORK_NO_STOCKS);
        } else if (!networkUp()) {
            Timber.d("No network, stocks 'available'");
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(this).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
        QuoteSyncJob.syncImmediately(this);
    }

    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getFragmentManager(), "StockDialogFragment");
    }

    void addStock(String symbol) {
        Timber.d("MainActivity addStock()");
        if (symbol != null && !symbol.isEmpty()) {

            if (networkUp()) {
                swipeRefreshLayout.setRefreshing(true);
            } else {
                String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            PrefUtils.addStock(this, symbol);
            onRefresh();
//            QuoteSyncJob.syncImmediately(this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        
        if (data.getCount() != 0) {
            error.setVisibility(View.GONE);
        } else {
            error.setVisibility(View.VISIBLE);
            updateEmptyView(NO_STOCKS);
        }
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
    }

    private void updateEmptyView(int status) {
        Timber.d("updateEmptyView()...");
        TextView tv = (TextView) findViewById(R.id.error);
        int message = R.string.empty_stock_list_message;
//        if(adapter.getItemCount() == 0) {
//            Timber.d("adapter.getItemCount() == 0");
//            int problem = StockAdapter.getStatus(this);
        if(status == NO_STOCKS && !networkUp())
            status = NO_NETWORK_NO_STOCKS;

        switch(status) {
            case NO_NETWORK_NO_STOCKS:
                Timber.d("NO_NETWORK_NO_STOCKS");
                message = R.string.error_no_network;
                break;
            case NO_STOCKS:
                Timber.d("NO_STOCKS");
                message = R.string.error_no_stocks;
                break;
            default:
        }
        tv.setText(message);

    }


    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
            item.setTitle("Click to show change in percent");
        } else {
            item.setIcon(R.drawable.ic_dollar);
            item.setTitle("Click to show change in absolute dollars");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
