package com.app.movie.cinephilia;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.movie.cinephilia.CastandCrew.MovieCreditsModel;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by GAURAV on 26-06-2016.
 */
public class SearchListAdapter extends ArrayAdapter<MovieModel> {
    private ArrayList<MovieModel> searchList = new ArrayList<>();
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<MovieModel> mSearchData;

    private class ViewHolder{
        TextView searchResult;
    }

    public SearchListAdapter(Context mContext, int layoutResourceId, ArrayList<MovieModel> mSearchData){
        super(mContext, layoutResourceId, mSearchData);
        this.mContext = mContext;
        this.layoutResourceId = layoutResourceId;
        this.mSearchData = mSearchData;
    }

    @Override
    public MovieModel getItem(int position) {
        return this.mSearchData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount(){
        return this.mSearchData.size();
    }

    @Override
    public void clear(){
        this.mSearchData.clear();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutResourceId, parent, false);
            viewHolder.searchResult = (TextView) convertView.findViewById(R.id.search_result_text);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        Log.v("SearchListAdapter","pos: "+pos);
        MovieModel movieCredtsModel = mSearchData.get(pos);
        viewHolder.searchResult.setText(movieCredtsModel.getTitle());
        return convertView;
    }

    public void updateList(ArrayList<MovieModel> elements) {
        for(MovieModel elem: elements){
            mSearchData.add(elem);
            Log.v("SearchListAdapter",Integer.toString(mSearchData.size()));
        }
        notifyDataSetChanged();
    }
}
