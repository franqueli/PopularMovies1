package com.franqueli.android.popularmovies.model;

/**
 * Created by Franqueli Mendez on 1/11/16.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class TheMovieDBAPI {
    private String apiKey;
    private static final String movieDBBaseURL = "http://api.themoviedb.org/3%1$s?api_key=%2$s";
    private static final String reviewURLSegment = "/movie/%1$s/reviews";

    public TheMovieDBAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReviewURL(String movieID) {
        String reviewPath = String.format(reviewURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }

}
