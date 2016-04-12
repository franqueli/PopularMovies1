package com.franqueli.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.franqueli.android.popularmovies.model.MovieInfo;

/**
 * Created by Franqueli Mendez on 4/11/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class MovieReviewAdapter extends RecyclerView.Adapter <MovieReviewViewHolder> {

    private MovieInfo movieInfo;

    public MovieReviewAdapter(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
    }


    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);

        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        holder.setReview(movieInfo.getMovieReviews().get(position));
    }

    @Override
    public int getItemCount() {
        int count = movieInfo.getMovieReviews().size();
        return count;
    }

}
