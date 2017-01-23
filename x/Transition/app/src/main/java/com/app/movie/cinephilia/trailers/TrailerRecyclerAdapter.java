package com.app.movie.cinephilia.trailers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.movie.cinephilia.DetailsFragment;
import com.app.movie.cinephilia.ImageTransformation;
import com.app.movie.cinephilia.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by GAURAV on 12-06-2016.
 */
public class TrailerRecyclerAdapter extends RecyclerView.Adapter<TrailerRecyclerAdapter.TrailerViewHolder> {
    private static final String TAG = TrailerRecyclerAdapter.class.getName();
    private ArrayList<MovieTrailerModel> mTrailerData = new ArrayList<>();
    private Context mContext;
    MovieTrailerModel movieTrailerModel;
    private String trailerUrl;

    private static int viewWidth, viewHeight;

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //public TextView name;
        public ImageView trailerImg;

        public TrailerViewHolder(View view){
            super(view);
            //name = (TextView) view.findViewById(R.id.trailerName);
            trailerImg = (ImageView) view.findViewById(R.id.trailerImg);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickPos = getAdapterPosition();
            trailerUrl="https://www.youtube.com/watch?v="+mTrailerData.get(clickPos).mKey;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            mContext.startActivity(intent);
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        }
    }

    public TrailerRecyclerAdapter(Context context, int layoutResourceId, ArrayList<MovieTrailerModel> trailers){
            mContext = context;
            mTrailerData = trailers;
    }

    @Override
    public TrailerRecyclerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View trailerView = inflater.inflate(R.layout.list_item_trailer, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(trailerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        movieTrailerModel = mTrailerData.get(position);
        //holder.name.setText(movieTrailerModel.mName);

        final String BASE_URL = "http://img.youtube.com/vi/";
        final String url = BASE_URL + movieTrailerModel.mKey + "/0.jpg";
        int width = (int)((double)DetailsFragment.layout_width * 0.6);
        Picasso
                .with(mContext)
                .load(url)
                .transform(new ImageTransformation(width))
                .into(holder.trailerImg);
    }

    @Override
    public int getItemCount() {
        return mTrailerData.size();
    }

    public void addItem(MovieTrailerModel modelItem){
        mTrailerData.add(modelItem);
        notifyItemInserted(mTrailerData.size()-1);
    }

    public void addAllItems(ArrayList<MovieTrailerModel> modelItemList){
        int position = mTrailerData.size()-1;
        mTrailerData.addAll(modelItemList);
        notifyItemRangeInserted(position,modelItemList.size());
    }

    public MovieTrailerModel getItem(int position){
        return mTrailerData.get(position);
    }
}
