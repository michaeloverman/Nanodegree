package com.udacity.stockhawk.ui;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.StockDetailFragment;
import com.udacity.stockhawk.data.Contract;

/**
 * Created by fabiohh on 12/21/16.
 */

public class StockDetailActivity extends AppCompatActivity {
    public static String QUOTE_URI = "quote_uri";
    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(QUOTE_URI, getIntent().getData());
            mUri = getIntent().getData();
            StockDetailFragment detailFragment = new StockDetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.stock_detail_container, detailFragment)
                    .commit();
        }

        if (null != mUri) {
            setTitle(Contract.Quote.getStockFromUri(mUri));
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        return view;
    }
}
