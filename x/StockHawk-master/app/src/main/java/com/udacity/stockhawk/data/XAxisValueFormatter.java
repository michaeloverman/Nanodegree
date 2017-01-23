package com.udacity.stockhawk.data;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by fabiohh on 12/24/16.
 */

public class XAxisValueFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public XAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        Log.i("XAxisValueFormatter", "Value: " + value);
        return mValues[(int) value];
    }
}
