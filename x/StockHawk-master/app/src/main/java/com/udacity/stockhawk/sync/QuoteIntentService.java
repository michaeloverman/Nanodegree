package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

import static com.udacity.stockhawk.ui.StockDetailActivity.QUOTE_URI;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        Bundle arguments = intent.getBundleExtra(QUOTE_URI);
        if (null != arguments) {
            Uri uri = arguments.getParcelable(QUOTE_URI);
            QuoteSyncJob.isValidQuote(getApplicationContext(), Contract.Quote.getStockFromUri(uri));
        } else {
            QuoteSyncJob.getQuotes(getApplicationContext());
        }
    }
}
