package com.franqueli.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Franqueli Mendez on 9/4/15.
 *
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class MovieInfoAdapter extends BaseAdapter {
    private Context context;

    private List <MovieInfo>movieInfoList;

    public MovieInfoAdapter(Context context) {
        this.context = context;
        movieInfoList = MovieInfo.listAll(MovieInfo.class);
    }

    // TODO-fm: Need to sort the list

    @Override
    public int getCount() {
        return movieInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return movieInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movieInfoList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView posterView;

        if (convertView != null) {
            posterView = (ImageView)convertView;
        } else {
            posterView = new ImageView(this.context);
            posterView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            posterView.setAdjustViewBounds(true);
        }

        Picasso.with(posterView.getContext()).load(movieInfoList.get(position).getPosterURL()).into(posterView);

        return posterView;
    }
}
