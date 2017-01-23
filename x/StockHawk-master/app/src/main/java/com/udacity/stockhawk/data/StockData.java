package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by fabiohh on 12/21/16.
 */

public class StockData {
    private String nSymbol;
    private double nPrice;
    private double nAbsoluteChange;
    private double nPercentageChange;
    private String nHistory;
    private List<Pair<Long, BigDecimal>> mHistoricalQuote;
    private List<String> mXAxisLabelList;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static String LOG = StockData.class.getSimpleName();

    public StockData(String nSymbol, double nPrice, double nAbsoluteChange, double nPercentageChange, String nHistory) {
        this.nSymbol = nSymbol;
        this.nPrice = nPrice;
        this.nAbsoluteChange = nAbsoluteChange;
        this.nPercentageChange = nPercentageChange;
        this.nHistory = nHistory;
        this.mHistoricalQuote = new ArrayList<>();
        this.mXAxisLabelList = new ArrayList<>();

        if (null != nHistory && !nHistory.isEmpty()) {
            String[] historyList = TextUtils.split(nHistory, "\n");
            for(String item : historyList) {
                String[] dateCost = TextUtils.split(item, ",");
                if (dateCost.length < 1) {
                    continue;
                }

                String price = dateCost[1];
                Double stockPrice = Double.valueOf(price.trim());
                Long date = Long.valueOf(dateCost[0]);

                Pair<Long, BigDecimal> pair = new Pair(date, new BigDecimal(stockPrice));

                mXAxisLabelList.add(simpleDateFormat.format(new Date(date)));
                if (stockPrice != null && date > 0) {
                    mHistoricalQuote.add(pair);
                }
            }

            Collections.reverse(mHistoricalQuote);
            Collections.reverse(mXAxisLabelList);

            // change indices to be 0 based.
            mHistoricalQuote = adjustBase(mHistoricalQuote);
        }
    }

    private List<Pair<Long, BigDecimal>> adjustBase(List<Pair<Long, BigDecimal>> pairList) {
        List<Pair<Long, BigDecimal>> newList = new ArrayList<>();
        for (int i=0; i<pairList.size(); i++) {
            Pair<Long, BigDecimal> pair = pairList.get(i);
            Pair<Long, BigDecimal> updatedItem = new Pair(i+0l, pair.second);
            newList.add(updatedItem);
        }

        return newList;
    }

    public List<Pair<Long, BigDecimal>> getHistoricalQuote() {
        return mHistoricalQuote;
    }

    public String[] getXAxisLabelList() {
        return mXAxisLabelList.toArray(new String[mXAxisLabelList.size()]);
    }

    public static StockData fromCursor(Cursor cursor) {
        StockData stockData = new StockData(
                cursor.getString(Contract.Quote.POSITION_SYMBOL),
                cursor.getDouble(Contract.Quote.POSITION_PRICE),
                cursor.getDouble(Contract.Quote.POSITION_ABSOLUTE_CHANGE),
                cursor.getDouble(Contract.Quote.POSITION_PERCENTAGE_CHANGE),
                cursor.getString(Contract.Quote.POSITION_HISTORY)
        );

        return stockData;
    }

    public static LineData getDataForListChart(Context context, List<Pair<Long, BigDecimal>> historicalQuoteList) {

        List<Entry> entries = new ArrayList<>();
        for (Pair<Long, BigDecimal> pair : historicalQuoteList) {
            entries.add(new Entry(pair.first, pair.second.floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, context.getString(R.string.historical_data));

        LineData lineData = new LineData(dataSet);
        return lineData;
    }
}
