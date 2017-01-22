package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import timber.log.Timber;

/**
 * Created by Michael on 1/20/2017.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {
    public final String TAG = WidgetRemoteViewsService.class.getSimpleName();


    public WidgetRemoteViewsService() {
        super();
        Timber.d("Instance of WidgetRemoteViewsService created.");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        Timber.d("in onGetViewFactory() - return new Factory...");
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
