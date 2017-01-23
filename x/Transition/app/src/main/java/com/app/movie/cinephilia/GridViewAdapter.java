package com.app.movie.cinephilia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.internal.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GAURAV on 12-12-2015.
 */
public class GridViewAdapter extends ArrayAdapter<MovieModel> {
    private static final String TAG=GridViewAdapter.class.getSimpleName();
    private Context mContext;
    private int layoutResourceId;
    private final Object mLock = new Object();
    private ArrayList<MovieModel> mGridData = new ArrayList<>();

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<MovieModel> mGridData){
        super(mContext, layoutResourceId, mGridData);
        this.mContext = mContext;
        this.layoutResourceId = layoutResourceId;
        this.mGridData = mGridData;
    }

    public ArrayList<MovieModel> getMovies(){
        return this.mGridData;
    }

    @Override
    public MovieModel getItem(int position) {
        return this.mGridData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount(){
        return this.mGridData.size();
    }

    @Override
    public void clear(){
        this.mGridData.clear();
        //notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View counterView, ViewGroup parent){
        View row = counterView;
        final ViewHolder holder;
        if(row==null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.image_view_poster);
            holder.textView = (TextView) row.findViewById(R.id.grid_item_text);
            holder.releaseDateText = (TextView) row.findViewById(R.id.release_date_grid);
            row.setTag(holder);
        } else
            holder = (ViewHolder)row.getTag();

        MovieModel item = getItem(pos);
        final String url = item.getPosterUrl();
        holder.textView.setText(Double.toString(item.getUserRating()));
        holder.releaseDateText.setText(Utility.formatDate(item.getReleaseDate()));
        Picasso.with(mContext)
                .load(url)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.loading)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso
                                .with(mContext)
                                .load(url)
                                .error(R.drawable.imagenotfound)
                                .fit()
                                .centerCrop()
                                .into(holder.imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Error Loading Images", "'");
                                    }
                                });
                    }
                });
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView releaseDateText;
    }

    public void updateValues(ArrayList<MovieModel> elements) {
        for(MovieModel elem: elements){
            mGridData.add(elem);
        }
        notifyDataSetChanged();
    }
}
