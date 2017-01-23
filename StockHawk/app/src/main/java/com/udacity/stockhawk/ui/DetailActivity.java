package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import yahoofinance.Stock;

/**
 * Created by Michael on 1/17/2017.
 */

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int DETAIL_LOADER = 1;
    Stock mStock;
    String mStockSymbol;
    String mStockHistoryString;
//    TextView mCompanyName;
    LineChart mGraph;
    TextView mCurrentValueView;
    TextView mDailyHiLoView;
    TextView mAnnualHiLoView;
    float mCurrentValue;
    String[] xAxisLabels;

    DecimalFormat dollarFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("onCreate()");

        setContentView(R.layout.activity_detail);

        mGraph = (LineChart) findViewById(R.id.detail_line_graph);
        mCurrentValueView = (TextView) findViewById(R.id.detail_current_value);
        mDailyHiLoView = (TextView) findViewById(R.id.detail_daily_hilo_value);
        mAnnualHiLoView = (TextView) findViewById(R.id.detail_annual_hilo_value);

        Intent intent = this.getIntent();
        mStockSymbol = intent.getStringExtra(MainActivity.STOCK_SYMBOL_EXTRA);
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        this.supportStartPostponedEnterTransition();
    }

    private void getAllTheDetails(Cursor data) {
        data.moveToFirst();
        String name = data.getString(Contract.Quote.POSITION_COMPANY_NAME);
        setTitle(name);
        getSupportActionBar().setHomeActionContentDescription(R.string.up_button_content_description);
        mCurrentValue = data.getFloat(Contract.Quote.POSITION_PRICE);
        mCurrentValueView.setText(dollarFormat.format(mCurrentValue));
        mDailyHiLoView.setText(data.getString(Contract.Quote.POSITION_DAILY_HILO));
        mAnnualHiLoView.setText(data.getString(Contract.Quote.POSITION_ANNUAL_HILO));

        mStockHistoryString = data.getString(Contract.Quote.POSITION_HISTORY);

        List<Entry> entries = parseDataString(mStockHistoryString);
        LineDataSet dataSet = new LineDataSet(entries, "Stock Price");
        dataSet.setColor(Color.RED);
        LineData lineData = new LineData(dataSet);

        mGraph.getXAxis().setValueFormatter(new DateAxisFormatter(xAxisLabels));
        DollarAxisFormatter dollarFormatter = new DollarAxisFormatter();
        mGraph.getAxisLeft().setDrawLabels(false);
        mGraph.getAxisRight().setValueFormatter(dollarFormatter);
        mGraph.getLegend().setEnabled(false);

        mGraph.setData(lineData);

        mGraph.invalidate();
    }

    private List<Entry> parseDataString(String history) {
        ArrayList<Entry> entries = new ArrayList<>();
        String[] lines = history.split("\\n");
        xAxisLabels = new String[lines.length];
        int dataPosition = 0;
        for(int i = lines.length - 1; i >= 0; i--) {
            String[] data = lines[i].split(",");
//            float date = Float.parseFloat(data[0]);
            float close = Float.parseFloat(data[1]);
            entries.add(new Entry(dataPosition, close));
            xAxisLabels[dataPosition] = data[0];
//            Timber.d("Entry: " + date + ", " + close);
            dataPosition++;
        }
//        Timber.d(entries.size() + " entries made");
        return entries;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("onCreateLoader()");
//        return new CursorLoader(this,
//                Contract.Quote.URI,
//                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
//                Contract.Quote.COLUMN_SYMBOL,
//                new String[] { mStockSymbol },
//                Contract.Quote.COLUMN_SYMBOL);
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(mStockSymbol),
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("onLoadFinished()");
        if(data.getCount() != 0) {
            getAllTheDetails(data);
        } else {
            displayDatabaseErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void displayDatabaseErrorMessage() {
        // TODO do something to explain the problem
    }

    public class DateAxisFormatter implements IAxisValueFormatter {
        private String[] mDates;
        private SimpleDateFormat mFormat = new SimpleDateFormat("MMM yy");

        public DateAxisFormatter(String[] values) {
            mDates = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            long millis = Long.parseLong(mDates[(int) value]);
            return mFormat.format(new Date(millis));
        }
    }
    public class DollarAxisFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return "$" + value;
        }
    }
}
