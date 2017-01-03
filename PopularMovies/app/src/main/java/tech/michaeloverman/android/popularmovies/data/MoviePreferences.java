package tech.michaeloverman.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import tech.michaeloverman.android.popularmovies.MainActivity;

/**
 * Class to store and retrieve current search basis/criteria
 * Created by Michael on 12/20/2016.
 */

public final class MoviePreferences {
    
    public static final String PREF_SORT_CRITERIUM = "sort";
    
    public static void setSortCriterium(Context context, int sort) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        
        editor.putInt(PREF_SORT_CRITERIUM, sort);
        editor.apply();
    }
    
    public static int getSortCriterium(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_SORT_CRITERIUM, MainActivity.NOWPLAYING);
    }
}
