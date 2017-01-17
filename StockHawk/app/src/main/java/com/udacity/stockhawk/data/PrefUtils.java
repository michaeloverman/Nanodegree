package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public final class PrefUtils {

    public static final String PREF_STOCKS_KEY = "stocks";
    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);
            HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());

    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        Timber.d("Stocks in database: " + stocks);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(PREF_STOCKS_KEY);
        editor.apply();

        editor.putStringSet(PREF_STOCKS_KEY, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        Timber.d("PrefUtils addStock()");
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

}
