package com.franqueli.android.popularmovies.model;


import android.graphics.Movie;
import android.util.JsonReader;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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


    public void updateWithJSON(String movieDetailsJSON) throws IOException {
        JsonReader reader = new JsonReader(new CharArrayReader(movieDetailsJSON.toCharArray()));

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "videos" :
                        this.processTrailers(reader);
                        reader.skipValue();               // FIXME : Remove this call here until processTrailers is implemented
                        break;
                    case "reviews" :
                        this.processReviews(reader);
                        reader.skipValue();               // FIXME : Remove this call here until processReviews is implemented
                        break;
                    default:
                        reader.skipValue();

                }
            }
            reader.endObject();

        } finally {
            reader.close();
        }

    }


/*
        private List<MovieInfo> processPopularMovieList(JsonReader reader) throws IOException, ParseException {
            List<MovieInfo> movieInfoList = new ArrayList<>();

            // Clear out all the old popular movies
            MovieInfo.deleteAll(MovieInfo.class);

            reader.beginArray();
            while (reader.hasNext()) {

                String title = null;
                String synopsis = null;
                double rating = 0.0;
                double popularity = 0.0;
                String posterPath = null;
                Date releaseDate = null;
                int movieDBId = 0;

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    // Skip all null values
                    if (reader.peek() == JsonToken.NULL) {
                        reader.skipValue();
                        continue;
                    }

                    if (name.equals("original_title")) {
                        title = reader.nextString();
                    } else if (name.equals("overview")) {
                        synopsis = reader.nextString();
                    } else if (name.equals("vote_average")) {
                        rating = reader.nextDouble();
                    } else if (name.equals("poster_path")) {
                        posterPath = reader.nextString();
                    } else if (name.equals("release_date")) {
                        try {
                            releaseDate = this.movieInfoReleaseDateFormat.parse(reader.nextString());
                        } catch (ParseException pe) {
                            releaseDate = null;
                        }
                    } else if (name.equals("popularity")) {
                        popularity = reader.nextDouble();
                    } else if (name.equals("id")) {
                        movieDBId = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

                // We've retrieved all the properties we need from the json. Now lets save it as an object
                MovieInfo currMovieInfo = new MovieInfo(title, synopsis, posterPath, (float) rating, (float) popularity, releaseDate, movieDBId);
                currMovieInfo.save();
                movieInfoList.add(currMovieInfo);
            }
            reader.endArray();

            SharedPreferences.Editor editor = MainActivity.this.preferences.edit();
            editor.putLong(LAST_SYNCED_PREF, System.currentTimeMillis());
            editor.apply();

            return movieInfoList;
        }
    }


*/


    private List<HashMap> processTrailers(JsonReader reader) {
        System.out.println("***** ProcessTrailers *****");
        return new ArrayList<>();
    }

    private List<HashMap> processReviews(JsonReader reader) {
        System.out.println("***** ProcessReviews *****");
        return new ArrayList<>();
    }
}
