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
 * Created by Michael on 12/7/2016.
 */

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    private Movie[] mMovies; // MAYBE THIS ISN'T STRING...?

    private final ThumbnailOnClickHandler mClickHandler;

    public interface ThumbnailOnClickHandler {
        void onClick(Movie movie);
    }

    public ThumbnailAdapter(ThumbnailOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.list_item_thumbnail;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attacheToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, attacheToParentImmediately);

        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ThumbnailViewHolder holder, int position) {
        Movie movie = mMovies[position];

        holder.bind(movie.getPosterUrl());

//        holder.mMovieText.setText(movie);
    }

    @Override
    public int getItemCount() {
        if(mMovies == null) return 0;
        return mMovies.length;
    }

    public void setMovies(Movie[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    class ThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        public final TextView mMovieText;
        public final ImageView mPosterView;
        private Context context;
        private String posterPath = "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";

        public ThumbnailViewHolder(View view) {
            super(view);
//            mMovieText = (TextView) view.findViewById(R.id.iv_movie_thumbnail);
            mPosterView = (ImageView) view.findViewById(R.id.iv_movie_thumbnail);
            context = view.getContext();
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int moviePosition = getAdapterPosition();
            Movie movie = mMovies[moviePosition];
            mClickHandler.onClick(movie);
        }

        public void bind(String posterPath) {
            Picasso.with(context)
                    .load(NetworkUtils.buildPosterUrl(posterPath))
                    .into(mPosterView);

        }
    }
}
