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

    private SortOptionsEnum sortBy;
    private List <MovieInfo>movieInfoList;

    public MovieInfoAdapter(Context context, SortOptionsEnum sortBy) {
        this.context = context;
        this.sortBy = sortBy;

        refreshMovieList(sortBy);
    }

    private void refreshMovieList(SortOptionsEnum sortBy) {
        String orderBy = null;
        switch (sortBy) {
            case Favorites:
                movieInfoList = MovieInfo.favoriteMovies();
                break;
            default:
                movieInfoList = MovieInfo.currentMovies();
                break;
        }

        // TODO : Don't order in query. The order of the movies should be determined by the query
//        movieInfoList = MovieInfo.find(MovieInfo.class, "", null, "", orderBy, "");
//        movieInfoList = MovieInfo.listAll(MovieInfo.class);
//        movieInfoList = MovieInfo.currentMovies();
//        System.out.println("*** Movies: " + movieInfoList);
    }

    public void setSortBy(SortOptionsEnum sortBy) {
        this.sortBy = sortBy;

        refreshMovieList(this.sortBy);
        this.notifyDataSetChanged();
    }

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

        String posterURL = movieInfoList.get(position).getPosterURL();
        if (posterURL == null) {
            posterView.setImageDrawable(context.getResources().getDrawable(R.drawable.movie_place_holder));
        } else {
            Picasso.with(posterView.getContext()).load(movieInfoList.get(position).getPosterURL()).into(posterView);
        }

        return posterView;
    }
}
