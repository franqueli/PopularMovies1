package com.franqueli.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.franqueli.android.popularmovies.model.Video;

/**
 * Created by Franqueli Mendez on 3/8/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class TrailerViewHolder extends RecyclerView.ViewHolder {

    private Video video;

    public TrailerViewHolder(View view) {
        super(view);

        // TODO-fm: implement this view holder
    }


    public void setVideo(Video video) {
        this.video = video;
        // Update the values
    }
}
