package tech.michaeloverman.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Michael on 12/20/2016.
 */

public class VideoLinkAdapter extends
        RecyclerView.Adapter<VideoLinkAdapter.VideoLinkAdapterViewHolder> {
    
    private static final String TAG = VideoLinkAdapter.class.getSimpleName();
    private final Context mContext;
    private final VideoLinkAdapterOnClickHandler mClickHandler;
    
    private ArrayList<VideoLink> videos;
    
    public interface VideoLinkAdapterOnClickHandler {
        void onClick(int id);
    }
    public void setLinkData(ArrayList<VideoLink> links) {
        videos = links;
    }
    public VideoLinkAdapter(Context context, VideoLinkAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }
    
    @Override
    public VideoLinkAdapter.VideoLinkAdapterViewHolder
            onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
        view.setFocusable(true);
        return new VideoLinkAdapterViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(VideoLinkAdapter.VideoLinkAdapterViewHolder holder, int position) {
        Log.d(TAG, "Binding ViewHolder position: " + position);
        VideoLink link = videos.get(position);
        holder.titleView.setText(link.getTitle());
    }
    
    @Override
    public int getItemCount() {
        if(videos == null) {
            Log.d(TAG, "null videos on getItemCount()");
            return 0;
        }
        return videos.size();
    }
    
    class VideoLinkAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        final TextView titleView;
        final ImageButton playButton;
        
        VideoLinkAdapterViewHolder(View view) {
            super(view);
            playButton = (ImageButton) view.findViewById(R.id.video_link_play_button);
            titleView = (TextView) view.findViewById(R.id.video_link_title);
            view.setOnClickListener(this);
        }
    
        @Override
        public void onClick(View view) {
            mClickHandler.onClick(getAdapterPosition());
        }
    }
}
