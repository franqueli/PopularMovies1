package com.franqueli.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.franqueli.android.popularmovies.model.Video;

/**
 * Created by Franqueli Mendez on 3/8/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Video mVideo;
    public final View mView;
    public final TextView mTextView;

    public TrailerViewHolder(View view) {
        super(view);

        mView = view;
        mTextView = (TextView) view.findViewById(R.id.trailerTextView);

        // TODO-fm: implement this view holder

        mView.setOnClickListener(this);
    }


    public void setVideo(Video video) {
        mVideo = video;
        // Update the values
        mTextView.setText(mVideo.getName());
    }

    @Override
    public void onClick(View v) {
        String videoSite = mVideo.getSite();
        if ("YouTube".equalsIgnoreCase(videoSite)) {
            mView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mVideo.getKey())));
        }
    }
}
