package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by Michael on 1/19/2017.
 */

public class WidgetProvider extends AppWidgetProvider {

    public static final String LAYOUT_ID_EXTRA = "layoutIdExtra";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("WIDGET PROVIDER onUpdate()");
//        context.startService(new Intent(context, WidgetIntentService.class));
        for (int i = 0; i < appWidgetIds.length; i++) {
//            int layoutId = getWidgetLayoutByWidth(context, appWidgetManager, appWidgetIds[i]);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views, appWidgetIds[i]);
            } else {
                setRemoteAdapterV11(context, views, appWidgetIds[i]);
            }
            Intent clickIntentTemplate =
//                    context.getResources().getBoolean(R.bool.use_detail_activity)
//                            ? new Intent(context, DetailActivity.class)
//                            : new Intent(context, MainActivity.class);
                    new Intent(context, DetailActivity.class);
            // change this back, once tablet layout worked out...

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Timber.d("WIDGET PROVIDER onReceive(): " + intent.getAction());
        if(QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction()) ||
                AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(intent.getAction())) {
            Timber.d("WIDGET updating");
//            context.startService(new Intent(context, WidgetRemoteViewsService.class));
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, getClass()));
            manager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Timber.d("WIDGET OPTIONS CHANGED");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

//    private int getWidgetLayoutByWidth(Context context, AppWidgetManager manager, int id) {
//        int width = getWidgetWidth(context, manager, id);
//        int targetWidth = context.getResources().getDimensionPixelSize(R.dimen.widget_break_point);
//        Timber.d("WIDGET WIDTH: " + width + ", TARGETWIDTH: " + targetWidth);
//        if(width >= targetWidth) {
//            Timber.d("WIDGET sending normal layout");
//            return R.layout.widget_layout;
//        } else {
//            Timber.d("WIDGET sending narrow layout");
//            return R.layout.widget_narrow_layout;
//        }
//    }
//    private int getWidgetWidth(Context context, AppWidgetManager manager, int id) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            return context.getResources().getDimensionPixelSize(R.dimen.widget_default_width);
//        }
//        Bundle options = manager.getAppWidgetOptions(id);
//        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
//            int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
//            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidth, displayMetrics);
//        }
//        return context.getResources().getDimensionPixelSize(R.dimen.widget_default_width);
//
//    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, final RemoteViews views, int widgetId) {
        Timber.d("setting remote adapter");
        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        views.setRemoteAdapter(R.id.widget_list, intent);
    }
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, RemoteViews views, int widgetId) {
        Timber.d("setting remote adapter v11");
        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        views.setRemoteAdapter(0, R.id.widget_list, intent);
    }
}
