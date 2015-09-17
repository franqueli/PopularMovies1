package com.franqueli.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Franqueli Mendez on 9/10/15.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class DownloadMovieInfoTask extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            result = downloadMovieInfo(params[0]);
        } catch (IOException e) {
            Log.d("DownloadMovieInfo", "DownloadMovieInfo: " + e.getMessage());
        }

        return result;
    }

    private String downloadMovieInfo(String apiKey) throws IOException {
        String movieInfo;
        InputStream is = null;
        try {
            String urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + apiKey;               // FIXME: The api key needs to be part of the configuration and not shipped with the code
            URL popularMoviesURL = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) popularMoviesURL.openConnection();
            conn.setReadTimeout(10000);                     // 10 sec
            conn.setConnectTimeout(15000 );                 // 15 sec
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("DownloadMovieTask", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            movieInfo = readIt(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }

        return movieInfo;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.i("DownloadMovieInfo", "*** Result: " + s);                                             // TODO : Parse the returned string and save it to a db. Maybe good use of SugarORM here
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
