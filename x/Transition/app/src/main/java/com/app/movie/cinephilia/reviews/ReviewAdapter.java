package com.app.movie.cinephilia.reviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.movie.cinephilia.R;

import java.util.ArrayList;

/**
 * Created by GAURAV on 22-01-2016.
 */
public class ReviewAdapter extends ArrayAdapter<MovieReviewModel> {
    private ArrayList<MovieReviewModel> mReviewData = new ArrayList<>();
    private boolean isExpanded = false;
    ImageButton expandReview;

    public static class ViewHolder{
        public TextView author;
        public TextView content_collapsed;
        public TextView content_expanded;
    }

    public ReviewAdapter(Context context, int layoutResourceId, ArrayList<MovieReviewModel> reviews){
        super(context, layoutResourceId, reviews);
        this.mReviewData = reviews;
    }

    @Override
    public int getCount(){
        return this.mReviewData.size();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_review, parent, false);
            viewHolder.author = (TextView) convertView.findViewById(R.id.text_view_author);
            viewHolder.content_collapsed = (TextView) convertView.findViewById(R.id.text_view_content_collapsed);
            viewHolder.content_expanded = (TextView) convertView.findViewById(R.id.text_view_content_expanded);

            expandReview = (ImageButton) convertView.findViewById(R.id.expand_review);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        MovieReviewModel movieReviewModel = mReviewData.get(pos);
        viewHolder.author.setText(movieReviewModel.mAuthor);
        viewHolder.content_collapsed.setText(movieReviewModel.mContent);
        viewHolder.content_expanded.setText(movieReviewModel.mContent);
        expandReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !isExpanded){
                    isExpanded = true;
                    viewHolder.author.setTextColor(
                            getContext()
                                    .getResources()
                                    .getColor(R.color.colorPrimary));
                    viewHolder.content_collapsed.setVisibility(View.GONE);
                    viewHolder.content_expanded.setVisibility(View.VISIBLE);
                } else if( isExpanded){
                    isExpanded = false;
                    viewHolder.author.setTextColor(
                            getContext()
                                    .getResources()
                                    .getColor(R.color.black));
                    viewHolder.content_expanded.setVisibility(View.GONE);
                    viewHolder.content_collapsed.setVisibility(View.VISIBLE);
                }
            }
        });


        return convertView;
    }
}
