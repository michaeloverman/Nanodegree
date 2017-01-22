package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Michael on 1/20/2017.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetIntentService extends IntentService {
    public final String TAG = WidgetIntentService.class.getSimpleName();

    private static final String[] QUOTE_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_COMPANY_NAME
    };
    static final int INDEX_QUOTE_ID = 0;
    static final int INDEX_QUOTE_SYMBOL = 1;
    static final int INDEX_QUOTE_PRICE = 2;
    static final int INDEX_QUOTE_ABSOLUTE_CHANGE = 3;
    static final int INDEX_QUOTE_PERCENTAGE_CHANGE = 4;
    static final int INDEX_QUOTE_COMPANY_NAME = 5;

    DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());

    public WidgetIntentService() {
        super("WidgetIntentService");
        dollarFormatWithPlus.setPositivePrefix("+$");
        Timber.d("WIDGET IntentService created");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("onHandleIntent()");
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = widgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        Set<String> prefStocks = PrefUtils.getStocks(this);
        Set<String> stocks = new HashSet<>();
        stocks.addAll(prefStocks);
        String[] stocksArray = (String[]) stocks.toArray();

        String displayMode = PrefUtils.getDisplayMode(this);

        Uri quoteLocationUri = Contract.Quote.URI;
        Cursor data = getContentResolver().query(quoteLocationUri,
                QUOTE_COLUMNS,
                Contract.Quote.COLUMN_SYMBOL,
                stocksArray,
                null);
        data.moveToFirst();

        for(int widgetId : widgetIds) {
            String symbol = data.getString(INDEX_QUOTE_SYMBOL);
            float price = data.getFloat(INDEX_QUOTE_PRICE);
            float absChange = data.getFloat(INDEX_QUOTE_ABSOLUTE_CHANGE);
            float percentChange = data.getFloat(INDEX_QUOTE_PERCENTAGE_CHANGE);
            String fullName = data.getString(INDEX_QUOTE_COMPANY_NAME);

            int width = getWidgetWidth(widgetManager, widgetId);
            int breakPoint = getResources().getDimensionPixelSize(R.dimen.widget_break_point);
            int layoutId = R.layout.widget_layout;

//            if(width >= breakPoint) {
//                layoutId = R.layout.widget_layout;
//            } else {
//                layoutId = R.layout.widget_narrow_layout;
//            }

            RemoteViews views = new RemoteViews(
                    getPackageName(),
                    layoutId);
            views.setTextViewText(R.id.symbol, symbol);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                views.setContentDescription(R.id.symbol, fullName);
            }

            if(displayMode.equals(getResources().getString(R.string.pref_display_mode_absolute_key))) {
                views.setTextViewText(R.id.change, dollarFormatWithPlus.format(absChange));
            } else {
                views.setTextViewText(R.id.change, percentageFormat.format(percentChange / 100));
            }

            if(layoutId == R.layout.widget_layout) {
                views.setTextViewText(R.id.price, dollarFormat.format(price));
            }

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            widgetManager.updateAppWidget(widgetId, views);
        }
    }

    private int getWidgetWidth(AppWidgetManager manager, int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
        }
        Bundle options = manager.getAppWidgetOptions(id);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidth, displayMetrics);
        }
        return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
    }
}
