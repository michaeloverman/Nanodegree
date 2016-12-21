package tech.michaeloverman.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import tech.michaeloverman.android.popularmovies.utilities.NetworkUtils;

/**
 * Adapter to handle movie data, and feed it to the RecyclerView in MainActivity.
 *
 * Created by Michael on 12/7/2016.
 */

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    /* Array of Movies == data */
    private Movie[] mMovies;

    /* Infrastructure to handle clicks */
    private final ThumbnailOnClickHandler mClickHandler;

    public interface ThumbnailOnClickHandler {
        void onClick(Movie movie);
    }

    public ThumbnailAdapter(ThumbnailOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /* Individual view elements */
    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.list_item_thumbnail;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attacheToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, attacheToParentImmediately);

        return new ThumbnailViewHolder(view);
    }

    /* Connect data to view element */
    @Override
    public void onBindViewHolder(ThumbnailViewHolder holder, int position) {
        Movie movie = mMovies[position];

        holder.bind(movie.getPosterUrl());
    }

    @Override
    public int getItemCount() {
        if(mMovies == null) return 0;
        return mMovies.length;
    }

    /**
     * Used to "install" data
     * @param movies
     */
    public void setMovies(Movie[] movies) {
        mMovies = null;
        mMovies = movies;
        notifyDataSetChanged();
    }

    /**
     * Subclass of view elements managed by RecyclerView
     */
    class ThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterView;
        private Context context;

        public ThumbnailViewHolder(View view) {
            super(view);
            mPosterView = (ImageView) view.findViewById(R.id.iv_movie_thumbnail);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        /* Pass necessary info about clicks to clickHandler */
        @Override
        public void onClick(View view) {
            int moviePosition = getAdapterPosition();
            Movie movie = mMovies[moviePosition];
            mClickHandler.onClick(movie);
        }

        /* Get the image for the movie poster, put it in the individual view */
        public void bind(String posterPath) {
            Picasso.with(context)
                    .load(NetworkUtils.buildThumbnailUrl(posterPath))
                    .into(mPosterView);
        }
    }
}
