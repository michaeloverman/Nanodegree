package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {
    }

    static void getQuotes(Context context) {

        Timber.d("getQuotes() -- Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {

            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                noNetworkMessage(context);
                return;
            }

            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockCopy.toArray(new String[stockCopy.size()]);

            Timber.d(stockCopy.toString());

            if (stockArray.length == 0) {
                return;
            }


            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                String symbol = iterator.next();


                Stock stock = quotes.get(symbol);
                String companyName = stock.getName();
                if(companyName == null || companyName.equals("")) {
                    Timber.d("Invalid Stock Symbol: " + symbol);
                    quotes.remove(symbol);
                    invalidSymbol(context, symbol);
                    continue;
                }


                StockQuote quote = stock.getQuote();
//                Timber.d(stock.toString() + ": " + quote.toString());
                float price = 0f;
                float change = 0f;
                float percentChange = 0f;
                if (quote.getPrice() != null) {
                    price = quote.getPrice().floatValue();
                    change = quote.getChange().floatValue();
                    percentChange = quote.getChangeInPercent().floatValue();
                } else {
                    invalidSymbol(context, symbol);
                    continue;
                }
                String dailyHiLo;
                if(quote.getDayHigh() != null) {
                    float dailyHigh = quote.getDayHigh().floatValue();
                    float dailyLow = quote.getDayLow().floatValue();
                    dailyHiLo = context.getString(R.string.hi_lo_slash_string,
                            DecimalFormat.getCurrencyInstance(Locale.US).format(dailyHigh),
                            DecimalFormat.getCurrencyInstance(Locale.US).format(dailyLow));
                } else {
                    dailyHiLo = context.getString(R.string.no_daily_trading_empty_string);
                }
                float annualHigh = quote.getYearHigh().floatValue();
                float annualLow = quote.getYearLow().floatValue();
                String annualHiLo = context.getString(R.string.hi_lo_slash_string,
                        DecimalFormat.getCurrencyInstance(Locale.US).format(annualHigh),
                        DecimalFormat.getCurrencyInstance(Locale.US).format(annualLow));


                // WARNING! Don't request historical data for a stock that doesn't exist!
                // The request will hang forever X_x
                List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                StringBuilder historyBuilder = new StringBuilder();

                for (HistoricalQuote it : history) {
                    historyBuilder.append(it.getDate().getTimeInMillis());
                    historyBuilder.append(", ");
                    historyBuilder.append(it.getClose());
                    historyBuilder.append("\n");
                }

                ContentValues quoteCV = new ContentValues();
                quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                quoteCV.put(Contract.Quote.COLUMN_COMPANY_NAME, companyName);
                quoteCV.put(Contract.Quote.COLUMN_DAILY_HILO, dailyHiLo);
                quoteCV.put(Contract.Quote.COLUMN_ANNUAL_HILO, annualHiLo);

                quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

                quoteCVs.add(quoteCV);

            }

            context.getContentResolver()
                    .bulkInsert(Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

            // Notify of data update
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    private static void invalidSymbol(Context context, String symbol) {
        PrefUtils.removeStock(context, symbol);
        removeStockFromDB(context, symbol);
        invalidSymbolMessage(context, symbol);
    }

    private static void invalidSymbolMessage(Context context, String symbol) {
        String message = context.getString(R.string.toast_invalid_stock_symbol, symbol);
        Timber.d(message);
        Timber.d("Sending broadcast back to Main");
        Intent intent = new Intent(MainActivity.INVALID_STOCK_SYMBOL);
        intent.putExtra(MainActivity.INVALID_STOCK_MESSAGE, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void noNetworkMessage(Context context) {
        String message = context.getString(R.string.no_network_error_message);
        Timber.d("noNetworkMessage: " + message);
        Intent intent = new Intent(MainActivity.INVALID_STOCK_SYMBOL);
        intent.putExtra(MainActivity.INVALID_STOCK_MESSAGE, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void removeStockFromDB(Context context, String symbol) {
        int i = context.getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);

        // Notify of data update
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
        Timber.d(i + " rows deleted from DB");

    }
    
    private static void schedulePeriodic(Context context) {

        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {
        Timber.d("in syncImmediately()");
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Timber.d("network available...");
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {
            Timber.d("network not available... sending message");
            noNetworkMessage(context);

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }
    

}
