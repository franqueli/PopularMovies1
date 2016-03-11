/**
 * Created by Franqueli Mendez on 3/8/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */

package com.franqueli.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.franqueli.android.popularmovies.model.MovieInfo;

public class TrailerListAdapter extends RecyclerView.Adapter <TrailerViewHolder> {

    private MovieInfo movieInfo;

    public TrailerListAdapter(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrailerViewHolder(parent);     // FIXME-fm : implement
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.setVideo(movieInfo.getMovieTrailers().get(position));
    }

    @Override
    public int getItemCount() {
        return movieInfo.getMovieTrailers().size();
    }
}
