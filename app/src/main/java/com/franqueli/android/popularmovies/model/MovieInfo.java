package com.franqueli.android.popularmovies.model;


import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.Date;

/**
 * Created by Franqueli Mendez on 9/4/15.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class MovieInfo extends SugarRecord {
    private String title;
    private String synopsis;
    private float rating;
    private Date releaseDate;
    private String posterPath;
    private float popularity;

    // Default constructor for SugarORM
    public MovieInfo() {

    }


    public MovieInfo(String title, String synopsis, String posterPath, float rating, float popularity, Date releaseDate) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
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

    @Ignore
    public String getPosterURL() {
        return "http://image.tmdb.org/t/p/" + "w185" + posterPath;              // TODO-fm: May want to get the appropriate size based on the device.
    }


    @Override
    public String toString() {
        return "ID: " + getId() + " Popularity: " + popularity + " Title: " + title  + " PosterPath: " + posterPath;
    }
}
