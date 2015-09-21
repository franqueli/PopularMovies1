package com.franqueli.android.popularmovies;

import android.media.Image;

import java.util.Date;

/**
 * Created by Franqueli Mendez on 9/4/15.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class MovieInfo {
    private String title;
    private String synopsis;
    private float rating;
    private Date releaseDate;
    private String posterPath;
    private Image poster;                   // TODO : Just store the reference to the file from Picasso
    private Image posterThumbnail;          // TODO : Just store the reference to the file from Picasso

    public MovieInfo(String title, String synopsis, String posterPath, float rating, Date releaseDate) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
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

    public Image getPoster() {
        // TODO : if image doesn't exist return a placeholder
        return this.poster;
    }

    public Image getPosterThumbnail () {
        return this.posterThumbnail;
    }


    @Override
    public String toString() {
        return "Title " + title + " " + "PosterPath: " + posterPath + "\n";
    }
}
