package com.franqueli.android.popularmovies;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Franqueli Mendez on 1/11/16.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class TheMovieDBAPI {
    private static final String LOG_TAG = TheMovieDBAPI.class.getSimpleName();

    private String apiKey;
    private static final String movieDBBaseURL = "http://api.themoviedb.org/3%1$s?api_key=%2$s";
    private static final String movieURLSegment = "/movie/%1$s";
    private static final String reviewURLSegment = "/movie/%1$s/reviews";
    private static final String videoURLSegment = "/movie/%1$s/videos";
    private static final String movieDetailURLSegment = "/movie/%1$s/reviews";

    public TheMovieDBAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReviewURL(String movieID) {
        String reviewPath = String.format(reviewURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }

    public String getVideosURL(String movieID) {
        String reviewPath = String.format(videoURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }

    public String getDetailsURL(String movieID) {
        String reviewPath = String.format(movieDetailURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }

    public String getFullMovieDetailsURL(String movieID) {
        String moviePath = String.format(movieURLSegment, movieID);
        String movieDetailsURL = String.format(movieDBBaseURL, moviePath, apiKey);
        movieDetailsURL = movieDetailsURL + "&append_to_response=videos,reviews";

        return movieDetailsURL;
    }


    // TODO : Add the http connection in this class. All you need to do is call this from the doInBackground method

    // TODO : Return an array of objects containing the information for a review
    public String requestReviewJSON(String movieID) {
        return requestPayloadString(getReviewURL(movieID));
    }

    public String requestVideosJSON(String movieID) {
        return requestPayloadString(getVideosURL(movieID));
    }

    public String requestDetails(String movieID) {
        return requestPayloadString(getDetailsURL(movieID));
    }

    public String requestAllDetails(String movieID) {
        return requestPayloadString(getFullMovieDetailsURL(movieID));
    }

    private String requestPayloadString(String urlStr) {
        String result;
        try {
            result =  downloadPayload(urlStr);
        } catch (IOException e) {
            result = "";

            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return result;
    }


    private String downloadPayload(String urlStr) throws IOException {
        String responseString;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);                     // 10 sec
            conn.setConnectTimeout(15000);                 // 15 sec
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            responseString = readIt(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }

        return responseString;
    }

    private String readIt(InputStream urlInputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(urlInputStream));
        StringBuilder contentString = new StringBuilder();

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            contentString.append(inputLine);
        }

        return contentString.toString();
    }


}
