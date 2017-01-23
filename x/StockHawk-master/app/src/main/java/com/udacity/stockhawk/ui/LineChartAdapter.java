package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.LineData;

/**
 * Created by fabiohh on 12/21/16.
 */

public class LineChartAdapter extends CursorAdapter {

    public LineChartAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public void setData(Cursor cursor) {

    }

    public LineData getData() {

//        LineDataSet dataSet = new LineDataSet(historicalEntryList.get(), "Label");
//
//        dataSet.setColor();
//        dataSet.setValueTextColor();

        return new LineData();
    }
}
