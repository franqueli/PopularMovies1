package com.franqueli.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.franqueli.android.popularmovies.model.MovieInfo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Franqueli Mendez on 9/10/15.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class DownloadMovieInfoTask extends AsyncTask<String, Void, String> {
    private String LOG_TAG = DownloadMovieInfoTask.class.getSimpleName();
    private SimpleDateFormat movieInfoReleaseDateFormat;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        movieInfoReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            result = downloadMovieInfo(params[0]);
        } catch (IOException e) {
            Log.d(LOG_TAG, "DownloadMovieInfo: " + e.getMessage());
        }

        return result;
    }

    private String downloadMovieInfo(String apiKey) throws IOException {
        String movieInfo;
        InputStream is = null;
        try {
            String urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + apiKey;               // FIXME: Create utility class for moviedb urls
            URL popularMoviesURL = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) popularMoviesURL.openConnection();
            conn.setReadTimeout(10000);                     // 10 sec
            conn.setConnectTimeout(15000 );                 // 15 sec
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
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
        Log.i(LOG_TAG, "*** Result: " + s);                                             // TODO : Parse the returned string and save it to a db. Maybe good use of SugarORM here


        List movieInfoList = null;
        try {
            movieInfoList = readMovieInfoJsonStream(new ByteArrayInputStream(s.getBytes("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "" + movieInfoList);

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


    private List<MovieInfo> readMovieInfoJsonStream (InputStream in) throws IOException, ParseException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        try {
            return processPopularMovieResponse(reader);
        } finally {
            reader.close();
        }
    }

    private List<MovieInfo> processPopularMovieResponse(JsonReader reader) throws IOException, ParseException {
        List<MovieInfo> movieInfoList = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("results")) {
                // TODO: Process movie info list
                movieInfoList = processPopularMovieList(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if (movieInfoList == null) {
            movieInfoList = new ArrayList<>();
        }

        return movieInfoList;
    }

    private List<MovieInfo> processPopularMovieList(JsonReader reader) throws IOException, ParseException {
        List<MovieInfo> movieInfoList = new ArrayList<>();

        // Clear out all the old popular movies
        MovieInfo.deleteAll(MovieInfo.class);

        reader.beginArray();
        while (reader.hasNext()) {

            String title = null;
            String synonsis = null;
            double rating = 0.0;
            double popularity = 0.0;
            String posterPath = null;
            Date releaseDate = null;

            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("original_title")) {
                    title = reader.nextString();
                } else if (name.equals("overview")) {
                    synonsis = reader.nextString();
                } else if (name.equals("vote_average")) {
                    rating = reader.nextDouble();
                } else if (name.equals("poster_path")) {
                    posterPath = reader.nextString();
                } else if (name.equals("release_date")) {
                    releaseDate = this.movieInfoReleaseDateFormat.parse(reader.nextString());
                } else if (name.equals("popularity")) {
                    popularity = reader.nextDouble();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            // We've retrieved all the properties we need from the json. Now lets save it as an object
            MovieInfo currMovieInfo = new MovieInfo(title, synonsis, posterPath, (float) rating, (float)popularity, releaseDate);
            currMovieInfo.save();
            movieInfoList.add(currMovieInfo);
        }
        reader.endArray();

        return movieInfoList;
    }
}
