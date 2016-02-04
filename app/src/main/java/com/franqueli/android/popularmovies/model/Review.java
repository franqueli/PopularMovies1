package com.franqueli.android.popularmovies.model;

/**
 * Created by Franqueli Mendez on 2/3/16.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class Review {

    private String id;
    private String author;
    private String content;
    private String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }


    public String getId() {
        return id;
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
