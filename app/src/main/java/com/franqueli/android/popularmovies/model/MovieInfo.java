package com.franqueli.android.popularmovies.model;


import android.graphics.Movie;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.Date;
import java.util.List;

/**
 * Created by Franqueli Mendez on 9/4/15.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class MovieInfo extends SugarRecord {
    private boolean favorite;
    private int movieDBId;
    private String title;
    private String synopsis;
    private float rating;
    private Date releaseDate;
    private String posterPath;
    private float popularity;

    // Default constructor for SugarORM
    public MovieInfo() {

    }


    public MovieInfo(String title, String synopsis, String posterPath, float rating, float popularity, Date releaseDate, int movieDBId) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.movieDBId = movieDBId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public float getRating() {
        return rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public int getMovieDBId() {
        return movieDBId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Ignore
    public String getPosterURL() {
        String posterURL = null;
        if (posterPath != null) {
            posterURL = "http://image.tmdb.org/t/p/" + "w185" + posterPath;              // TODO-fm: May want to get the appropriate size based on the device.
        }

        return posterURL;
    }

    @Ignore
    public List<MovieInfo> favoriteMovies() {
        return MovieInfo.find(MovieInfo.class,"favorite = ?", 1+"");
    }

    @Override
    public String toString() {
        return "ID: " + getId() + " Popularity: " + popularity + " Title: " + title  + " PosterPath: " + posterPath;
    }
}
