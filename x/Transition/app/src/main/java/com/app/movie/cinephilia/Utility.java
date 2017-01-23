package com.app.movie.cinephilia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by GAURAV on 22-01-2016.
 */
public class Utility{//} extends BroadcastReceiver{
    private Context mContext;
    private ConnectivityManager connectivityManager;
    private static View parentLayout;

    Utility(View view){
        parentLayout = view;
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
         return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }


    private void alert(Context context)
    {   String title = "No Internet Connection";
        String message = Integer.toString(R.string.notOnline);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /* TO get the year */
    public static String formatDate(String date){
        String[] dateArray = date.split("-");
        Log.v("Utility",dateArray[dateArray.length-1]);
        return dateArray[dateArray.length-1];
    }
}
