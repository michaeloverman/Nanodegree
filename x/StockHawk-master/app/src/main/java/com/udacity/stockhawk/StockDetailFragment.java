package com.udacity.stockhawk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.udacity.stockhawk.data.StockData;
import com.udacity.stockhawk.data.XAxisValueFormatter;

import static com.udacity.stockhawk.data.Contract.Quote.QUOTE_COLUMNS;
import static com.udacity.stockhawk.ui.StockDetailActivity.QUOTE_URI;

/**
 * Created by fabiohh on 12/22/16.
 */

public class StockDetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    Context mContext;
    Uri mUri;
    LineChart mLineChart;

    private static final int DETAIL_LOADER = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.stock_fragment_detail, container, false);
        mLineChart = (LineChart) fragmentView.findViewById(R.id.chart);

        Bundle arguments = getArguments();
        if (null != arguments) {
            mUri = arguments.getParcelable(QUOTE_URI);
        }
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mUri,
                QUOTE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            StockData stockData = StockData.fromCursor(data);

            if (null != stockData) {
                mLineChart.setData(StockData.getDataForListChart(mContext, stockData.getHistoricalQuote()));
                XAxis xAxis = mLineChart.getXAxis();

                xAxis.setValueFormatter(new XAxisValueFormatter(stockData.getXAxisLabelList()));
                mLineChart.setBackgroundColor(getResources().getColor(R.color.white));
                mLineChart.invalidate();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
