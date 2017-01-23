package com.app.movie.cinephilia;

/**
 * Created by GAURAV on 27-06-2016.
 */
public interface Callback {
    void onItemSelected(MovieModel item);
    void defaultItemSelected(MovieModel item);
}
