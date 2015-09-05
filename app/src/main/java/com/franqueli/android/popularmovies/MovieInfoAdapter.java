package com.franqueli.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
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
        movieInfoList = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return movieInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
