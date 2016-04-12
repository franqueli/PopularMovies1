package com.franqueli.android.popularmovies.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Franqueli Mendez on 2/3/16.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class Review extends SugarRecord {

    private String myId;
    private String author;
    private String content;
    private String url;

    private MovieInfo movieInfo;

    // Default constructor for SugarORM
    public Review() {

    }

    public Review(MovieInfo movieInfo, String id, String author, String content, String url) {
        this.movieInfo = movieInfo;
        this.myId = id;

        updateReview(author, content, url);
    }

    @Ignore
    public void updateReview(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }


    public String getMyId() {
        return myId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Review{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
