package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

//    public static final String STOCK_SYMBOL = "stocksymbol";

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if(intent.hasExtra(STOCK_SYMBOL)) {
//            QuoteSyncJob.isValidStock(getApplicationContext(), intent.getStringExtra(STOCK_SYMBOL));
//        } else {
            QuoteSyncJob.getQuotes(getApplicationContext());
//        }
        Timber.d("Intent handled");
    }


}
