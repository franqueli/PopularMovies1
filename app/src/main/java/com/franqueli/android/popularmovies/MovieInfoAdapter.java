package com.franqueli.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));
        movieInfoList.add(new MovieInfo("Straight Outta Compton", "Dr.Dre, Easy E", "img.jpg", 3, Calendar.getInstance().getTime()));

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
        ImageView posterView;

        if (convertView != null) {
            posterView = (ImageView)convertView;
        } else {
            posterView = new ImageView(this.context);
            posterView.setLayoutParams(new GridView.LayoutParams(200, 400));                   // FIXME: Make these configurable constant that we can change
            posterView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            posterView.setPadding(8, 8, 8, 8);                                                 // FIXME: Make padding configurable
        }

        Picasso.with(this.context).load(R.drawable.straight_outta_compton).into(posterView);

        return posterView;
    }
}
