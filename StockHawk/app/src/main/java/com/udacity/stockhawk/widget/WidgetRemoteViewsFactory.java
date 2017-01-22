package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Michael on 1/20/2017.
 */

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor data = null;
    private String displayMode = "";
//    private int layoutId;
    AppWidgetManager widgetManager;
    int appWidgetId;
    private Context mContext;

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


    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        percentageFormat.setPositivePrefix("+");
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
//        if(intent.hasExtra(WidgetProvider.LAYOUT_ID_EXTRA)) {
//            if(intent.getIntExtra(WidgetProvider.LAYOUT_ID_EXTRA, R.layout.widget_list_item)
//                    == R.layout.widget_layout) {
//                layoutId = R.layout.widget_list_item;
//                Timber.d("NORMAL WIDGET WIDTH LAYOUT BEING USED");
//            } else if (intent.getIntExtra(WidgetProvider.LAYOUT_ID_EXTRA, R.layout.widget_layout)
//                    == R.layout.widget_narrow_layout) {
//                layoutId = R.layout.widget_narrow_list_item;
//                Timber.d("NARROW WIDGET WIDTH LAYOUT");
//            } else {
//                Timber.d("WIDGET TROUBLE::: LAYOUT NOT CONVEYED");
//                layoutId = R.layout.widget_list_item;
//            }
//        }
        if(intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

    }

    @Override
    public void onCreate() {
        Timber.d("REMOTEvIEWSfACTORY onCreate()");
        widgetManager = AppWidgetManager.getInstance(mContext);

    }

    @Override
    public void onDataSetChanged() {
        Timber.d("WIDGET onDataSetChanged()");
        if(data != null) {
            data.close();
        }

        // temporarily clear identity
        final long identityToken = Binder.clearCallingIdentity();

        Uri quoteLocationUri = Contract.Quote.URI;
        data = mContext.getContentResolver().query(quoteLocationUri,
                QUOTE_COLUMNS,
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL + " ASC");

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    private int getWidgetWidth() {
        Bundle options = widgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return mContext.getResources().getDimensionPixelSize(R.dimen.widget_default_width);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }

//        int layoutId;
        int widgetWidth = getWidgetWidth();
//        RemoteViews views = new RemoteViews(mContext.getPackageName(), layoutId);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        if(widgetWidth >= mContext.getResources().getDimensionPixelSize(R.dimen.widget_break_point)) {
            views.setInt(R.id.price, "setVisibility", View.VISIBLE);
        } else {
//            layoutId = R.layout.widget_narrow_list_item;
            views.setInt(R.id.price, "setVisibility", View.GONE);
        }

        displayMode = PrefUtils.getDisplayMode(mContext);

        String symbol = data.getString(INDEX_QUOTE_SYMBOL);
        float price = data.getFloat(INDEX_QUOTE_PRICE);
        float absChange = data.getFloat(INDEX_QUOTE_ABSOLUTE_CHANGE);
        float percentChange = data.getFloat(INDEX_QUOTE_PERCENTAGE_CHANGE);
        String fullName = data.getString(INDEX_QUOTE_COMPANY_NAME);
        Timber.d("WIDGET Stock: " + symbol + ": " + price + ", " + absChange);

        views.setTextViewText(R.id.symbol, symbol);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            views.setContentDescription(R.id.symbol, fullName);
        }

        if(displayMode.equals(mContext.getResources().getString(R.string.pref_display_mode_absolute_key))) {
            Timber.d("WIDGET: setting change to $");
            views.setTextViewText(R.id.change, dollarFormatWithPlus.format(absChange));
        } else {
            Timber.d("WIDGET: setting change to %");
            views.setTextViewText(R.id.change, percentageFormat.format(percentChange / 100));
        }

        if (absChange >= 0) {
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

//        if(layoutId == R.layout.widget_list_item) {
            views.setTextViewText(R.id.price, dollarFormat.format(price));
//        }

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(MainActivity.STOCK_SYMBOL_EXTRA, symbol);
        views.setOnClickFillInIntent(R.layout.widget_list_item, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if(data.moveToPosition(position))
            return data.getLong(INDEX_QUOTE_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
