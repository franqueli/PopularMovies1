package com.franqueli.android.popularmovies.model;


import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private boolean current;
    private int movieDBId;
    private String title;
    private String synopsis;
    private float rating;
    private Date releaseDate;
    private String posterPath;
    private float popularity;
    private int runtime;

    @Ignore
    private List<Video> movieTrailers;

    // Default constructor for SugarORM
    public MovieInfo() {

    }


    public MovieInfo(String title, String synopsis, String posterPath, float rating, float popularity, Date releaseDate, int movieDBId) {
        this.movieDBId = movieDBId;
        this.updateMovieInfo(title, synopsis, posterPath, rating, popularity, releaseDate);
    }


    public void updateMovieInfo(String title, String synopsis, String posterPath, float rating, float popularity, Date releaseDate) {
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

    public int getMovieDBId() {
        return movieDBId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }


    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int getRuntime() {
        return runtime;
    }

    @Ignore
    public static MovieInfo findMovieInfo(int movieDBId) {
        MovieInfo movieInfo = null;
        List<MovieInfo> movies = MovieInfo.find(MovieInfo.class,"movie_db_id = ?", movieDBId + "");
        if (movies != null && movies.size() > 0) {
            movieInfo = movies.get(0);
        }

        return movieInfo;
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
    public static List<MovieInfo> favoriteMovies() {
        return MovieInfo.find(MovieInfo.class,"favorite = ?", 1 + "");
    }

    @Ignore
    public static List<MovieInfo> currentMovies() {
        return MovieInfo.find(MovieInfo.class,"current = ?", 1 + "");
    }

    @Ignore
    public List<Video> getMovieTrailers() {
        return Video.find(Video.class, "movie_info = ?", getId() + "");
    }

    @Ignore
    public List<Review> getMovieReviews() {
        return Review.find(Review.class, "movie_info = ?", getId() + "");
    }



    @Override
    public String toString() {
        return "ID: " + getId() + " Popularity: " + popularity + " Title: " + title  + " PosterPath: " + posterPath;
    }


    public void updateWithJSON(String movieDetailsJSON) throws IOException {
        JsonReader reader = new JsonReader(new CharArrayReader(movieDetailsJSON.toCharArray()));

        int runtime = 0;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "videos" :
                        reader.beginObject();

                        while (reader.hasNext()) {
                            if (reader.nextName().equals("results")) {
                                this.movieTrailers = this.processTrailers(reader);
                                System.out.println("**** Videos " + movieTrailers);
                            } else {
                                reader.skipValue();
                            }
                        }

                        reader.endObject();
                        break;
                    case "reviews" :
                        reader.beginObject();
                        while (reader.hasNext()) {
                            if (reader.nextName().equals("results")) {
                                System.out.println("***** Reviews " + this.processReviews(reader));
                            } else {
                                reader.skipValue();
                            }
                        }
                        break;
                    case "runtime" :
                        runtime = reader.nextInt();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();

        } finally {
            reader.close();
        }

        this.runtime = runtime;

        this.save();
    }

/*
    "videos": {
        "results": [
        {
            "id": "5693d51bc3a3687b6b000145",
                "iso_639_1": "en",
                "key": "EIELwayIIT4",
                "name": "The Revenant Official Trailer 1 2015 HD",
                "site": "YouTube",
                "size": 1080,
                "type": "Trailer"
        }
        ]
    },
    */
    // FIXME: Video data is getting duplicated
    private List<Video> processTrailers(JsonReader reader) throws IOException {

        ArrayList<Video> videoInfo = new ArrayList<>();
        Log.d("MovieInfo","***** ProcessTrailers *****");

        reader.beginArray();

        while (reader.hasNext()) {
            Video currentVideo = readVideo(reader);
            if (currentVideo != null) {
                currentVideo.save();
                videoInfo.add(currentVideo);
            }
        }

        reader.endArray();

        Log.d("MovieInfo: Videos", Arrays.toString(videoInfo.toArray()));
        return videoInfo;
    }

/*
    {
        "id": "5693d51bc3a3687b6b000145",
            "iso_639_1": "en",
            "key": "EIELwayIIT4",
            "name": "The Revenant Official Trailer 1 2015 HD",
            "site": "YouTube",
            "size": 1080,
            "type": "Trailer"
    }
*/
    private Video readVideo(JsonReader reader) throws IOException {
        String name = "";
        String type = "";
        String id = "";
        String iso = "";
        String site = "";
        String key = "";
        int size = -1;

        reader.beginObject();

        while (reader.hasNext()) {
            String token = reader.nextName();

            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue();
                continue;
            }

            switch (token) {
                case "id":
                    id = reader.nextString();
                    break;
                case "iso_639_1":
                    iso = reader.nextString();
                    break;
                case "key":
                    key = reader.nextString();
                    break;
                case "name":
                    name = reader.nextString();
                    break;
                case "site":
                    site = reader.nextString();
                    break;
                case "type":
                    type = reader.nextString();
                    break;
                case "size":
                    size = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        reader.endObject();

        Video video;
        List<Video>videoList = Video.find(Video.class, "my_id = ?", id);
        if (videoList.size() > 0) {
            video = videoList.get(0);
        } else {
            video = new Video(this, id, name, site, type, key, size, iso);
        }

        return video;
    }

//    "reviews": {
//        "page": 1,
//                "results": [
//        {
//            "id": "568bbeaec3a3680e01007bb7",
//                "author": "rahuliam",
//                "content": "The Revenant, a ravishingly violent Western survival yarn from Alejandro González Iñárritu, has a healthy few, scattered like acorns across its two-and-a-half-hour canvas..... no matter how extended, the film’s tense story is under the director’s complete control...DiCaprio’s performance is an astonishing testament to his commitment to a role. cinematographer Emmanuel Lubezki done a great job..as a supporting actor tom hardy is brilliant..must watch...",
//                "url": "http://j.mp/1O8khtT"
//        }
//        ],
//        "total_pages": 1,
//                "total_results": 1
//    }

    private List<Review> processReviews(JsonReader reader) throws IOException {
        List<Review> reviews = new ArrayList<>();
        System.out.println("***** ProcessReviews *****");

        reader.beginArray();

        while (reader.hasNext()) {
            reviews.add(readReview(reader));
        }

        reader.endArray();

        return reviews;
    }


//    {
//        "id": "568bbeaec3a3680e01007bb7",
//            "author": "rahuliam",
//            "content": "The Revenant, a ravishingly violent Western survival yarn from Alejandro González Iñárritu, has a healthy few, scattered like acorns across its two-and-a-half-hour canvas..... no matter how extended, the film’s tense story is under the director’s complete control...DiCaprio’s performance is an astonishing testament to his commitment to a role. cinematographer Emmanuel Lubezki done a great job..as a supporting actor tom hardy is brilliant..must watch...",
//            "url": "http://j.mp/1O8khtT"
//    }

    private Review readReview(JsonReader reader) throws IOException {
        String id = "";
        String author = "";
        String content = "";
        String url = "";

        reader.beginObject();

        while (reader.hasNext()) {
            String token = reader.nextName();

            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue();
                continue;
            }

            switch (token) {
                case "id":
                    id = reader.nextString();
                    break;
                case "author":
                    author = reader.nextString();
                    break;
                case "content":
                    content = reader.nextString();
                    break;
                case "url":
                    url = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        reader.endObject();


        Review review;
        List<Review>reviewList = Review.find(Review.class, "my_id = ?", id);
        if (reviewList.size() > 0) {
            review = reviewList.get(0);
        } else {
            review = new Review(this, id, author, content, url);
        }

        return review;
    }



}
