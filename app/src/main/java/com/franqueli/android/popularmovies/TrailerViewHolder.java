package com.franqueli.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.franqueli.android.popularmovies.model.Video;

/**
 * Created by Franqueli Mendez on 3/8/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class TrailerViewHolder extends RecyclerView.ViewHolder {

    private Video mVideo;
    public final View mView;
    public final TextView mTextView;

    public TrailerViewHolder(View view) {
        super(view);

        mView = view;
        mTextView = (TextView) view.findViewById(R.id.trailerTextView);

        // TODO-fm: implement this view holder
    }


    public void setVideo(Video video) {
        mVideo = video;
        // Update the values
        mTextView.setText(mVideo.getName());
    }
}
