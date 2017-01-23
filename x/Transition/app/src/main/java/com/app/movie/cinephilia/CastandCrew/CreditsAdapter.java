package com.app.movie.cinephilia.CastandCrew;

import android.content.Context;
import android.graphics.Movie;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.movie.cinephilia.CircleTransform;
import com.app.movie.cinephilia.CropSquareTransformation;
import com.app.movie.cinephilia.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by GAURAV on 20-05-2016.
 */
public class CreditsAdapter extends ArrayAdapter<MovieCreditsModel> {
    private ArrayList<MovieCreditsModel> mCreditsData = new ArrayList<>();
    private Context mContext;

    public static class ViewHolder{
        public TextView character;
        public TextView name;
        public ImageView profile_picture;
    }

    public CreditsAdapter(Context context, int layoutResourceId, ArrayList<MovieCreditsModel> credits){
        super(context, layoutResourceId, credits);
        this.mContext = context;
        this.mCreditsData = credits;
    }

    public ArrayList<MovieCreditsModel> getCreditsData(){
        return mCreditsData;
    }

    @Override
    public int getCount(){
        return this.mCreditsData.size();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_credits, parent, false);
            viewHolder.character = (TextView) convertView.findViewById(R.id.character);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        MovieCreditsModel movieCredtsModel = mCreditsData.get(pos);
        viewHolder.character.setText(movieCredtsModel.mCharacter);
        viewHolder.name.setText(movieCredtsModel.mName);

        final String url = movieCredtsModel.getPosterUrl();
        Picasso.with(mContext)
                .load(url)
                .transform(new CircleTransform())
                .into(viewHolder.profile_picture);


        return convertView;
    }
}
