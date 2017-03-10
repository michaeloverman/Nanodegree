/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sunshinewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Digital watch face. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class SunshineWatch extends CanvasWatchFaceService {

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    private String mHighTemp = "-";
    private String mLowTemp = "-";
    private int mConditionId = 905;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<Engine> mWeakReference;

        public EngineHandler(SunshineWatch.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SunshineWatch.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private static final String TAG = "Watchface Engine";
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;

        // variables needed for using layout to 'paint' the watchface
        private int specW, specH;
        private View mLayout;
        private TextView date, hour, minute, hitemp, lotemp;
        private ImageView divider, weatherIcon;
        private final Point displaySize = new Point();

        boolean mIsRound;

        boolean mAmbient;
        Calendar mCalendar;
        Date mDate;

        SimpleDateFormat mHourOfDayFormat;
        SimpleDateFormat mDayOfWeekFormat;

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatch.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        float mYOffset;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mIsRound = getResources().getConfiguration().isScreenRound();

            // This bit found at: https://sterlingudell.wordpress.com
            LayoutInflater inflater = (LayoutInflater) getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            mLayout = inflater.inflate(mIsRound ? R.layout.watch_layout_round :
                    R.layout.watch_layout_square, null);
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);

            specW = View.MeasureSpec.makeMeasureSpec(displaySize.x,
                    View.MeasureSpec.EXACTLY);
            specH = View.MeasureSpec.makeMeasureSpec(displaySize.y,
                    View.MeasureSpec.EXACTLY);

            date = (TextView) mLayout.findViewById(R.id.date_text);
            hour = (TextView) mLayout.findViewById(R.id.hours_text);
            minute = (TextView) mLayout.findViewById(R.id.minute_text);
            divider = (ImageView) mLayout.findViewById(R.id.divider_line);
            hitemp = (TextView) mLayout.findViewById(R.id.high_temp_text);
            lotemp = (TextView) mLayout.findViewById(R.id.lo_temp_text);
            weatherIcon = (ImageView) mLayout.findViewById(R.id.weather_icon);
            // ... up to here...

            Resources resources = SunshineWatch.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            mCalendar = Calendar.getInstance();
            mDate = new Date();
            initFormats();

            // check if most recent data is present and use it until new data arrives
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SunshineWatch.this);
            mHighTemp = prefs.getString("hiTemp", "-");
            mLowTemp = prefs.getString("loTemp", "-");
            mConditionId = prefs.getInt("weatherId", 0);

            mGoogleApiClient.connect();

        }



        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            mGoogleApiClient.disconnect();
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                initFormats();
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Initialize date/time formats for strings
         */
        private void initFormats() {
            mHourOfDayFormat = new SimpleDateFormat("h:", Locale.getDefault());
            mHourOfDayFormat.setCalendar(mCalendar);
            mDayOfWeekFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
            mDayOfWeekFormat.setCalendar(mCalendar);
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatch.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SunshineWatch.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            mIsRound = insets.isRound();

            Log.d(TAG, "onApplyWindowInsets() :" + mIsRound);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;

                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            mDate.setTime(now);

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
                date.setVisibility(View.GONE);
                weatherIcon.setVisibility(View.GONE);
                lotemp.setVisibility(View.GONE);
                hitemp.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            } else {
                canvas.drawColor(getResources().getColor(R.color.watch_background));
                date.setVisibility(View.VISIBLE);
                weatherIcon.setVisibility(View.VISIBLE);
                lotemp.setVisibility(View.VISIBLE);
                hitemp.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
            }

            hour.setText(mHourOfDayFormat.format(mDate));
            minute.setText(String.format("%02d", mCalendar.get(Calendar.MINUTE)));
            date.setText(mDayOfWeekFormat.format(mDate));

            weatherIcon.setImageResource(getWeatherIcon(mConditionId));
            hitemp.setText(mHighTemp);
            lotemp.setText(mLowTemp);

            mLayout.measure(specW, specH);
            mLayout.layout(0,0,mLayout.getMeasuredWidth(),
                    mLayout.getMeasuredHeight());

            mLayout.draw(canvas);

        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "googleApiConnected...");
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended()");

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed()");
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            Log.d(TAG, "wear data changed");
            for (DataEvent event : dataEventBuffer) {
                if (event.getType() == DataEvent.TYPE_CHANGED &&
                        event.getDataItem().getUri().getPath().equals("/weather_data")) {
                    DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    mHighTemp = dataMap.getString("hiTemp");
                    mLowTemp = dataMap.getString("loTemp");
                    mConditionId = dataMap.getInt("weatherId");

                    SharedPreferences.Editor prefs = PreferenceManager
                            .getDefaultSharedPreferences(SunshineWatch.this).edit();
                    prefs.putString("hiTemp", mHighTemp);
                    prefs.putString("loTemp", mLowTemp);
                    prefs.putInt("weatherId", mConditionId);
                    prefs.commit();

                    Log.d(TAG, "hi: " + mHighTemp + ", lo: " + mLowTemp);
                    invalidate();

                }
            }
        }

        private int getWeatherIcon(int weatherId) {
        /*
         * Based on weather code data for Open Weather Map.
         */
            if (weatherId >= 200 && weatherId <= 232) {
                return R.drawable.art_storm;
            } else if (weatherId >= 300 && weatherId <= 321) {
                return R.drawable.art_light_rain;
            } else if (weatherId >= 500 && weatherId <= 504) {
                return R.drawable.art_rain;
            } else if (weatherId == 511) {
                return R.drawable.art_snow;
            } else if (weatherId >= 520 && weatherId <= 531) {
                return R.drawable.art_rain;
            } else if (weatherId >= 600 && weatherId <= 622) {
                return R.drawable.art_snow;
            } else if (weatherId >= 701 && weatherId <= 761) {
                return R.drawable.art_fog;
            } else if (weatherId == 761 || weatherId == 771 || weatherId == 781) {
                return R.drawable.art_storm;
            } else if (weatherId == 800) {
                return R.drawable.art_clear;
            } else if (weatherId == 801) {
                return R.drawable.art_light_clouds;
            } else if (weatherId >= 802 && weatherId <= 804) {
                return R.drawable.art_clouds;
            } else if (weatherId >= 900 && weatherId <= 906) {
                return R.drawable.art_storm;
            } else if (weatherId >= 958 && weatherId <= 962) {
                return R.drawable.art_storm;
            } else if (weatherId >= 951 && weatherId <= 957) {
                return R.drawable.art_clear;
            }

            Log.e("SunshineWatchFace", "Unknown Weather: " + weatherId);
            return R.drawable.art_storm;
        }
    }
}
