package com.franqueli.android.popularmovies.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Franqueli Mendez on 1/29/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
/*
{
        "myId": "5693d51bc3a3687b6b000145",
        "iso_639_1": "en",
        "key": "EIELwayIIT4",
        "name": "The Revenant Official Trailer 1 2015 HD",
        "site": "YouTube",
        "size": 1080,
        "type": "Trailer"
        }
*/
public class Video extends SugarRecord {
    private String myId;
    private String iso;
    private String name;
    private String site;
    private int size;
    private String type;
    private String key;

    private MovieInfo movieInfo;

    // Default constructor for SugarORM
    public Video () {

    }

    public Video(MovieInfo movieInfo, String id, String name, String site, String type, String key, int size, String iso) {
        this.movieInfo = movieInfo;
        this.myId = id;

        this.updateVideo(name, site, type, key, size, iso);
    }

    @Ignore
    public void updateVideo(String name, String site, String type, String key, int size, String iso) {
        this.iso = iso;
        this.name = name;
        this.site = site;
        this.key = key;
        this.size = size;
        this.type = type;
    }

    public String getMyId() {
        return myId;
    }

    public String getIso() {
        return iso;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }


    @Override
    public String toString() {
        String movieInfoString = movieInfo != null ? movieInfo.toString() : "--MovieInfoNull--";
        return "Video{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", site='" + site + '\'' +
                ", key='" + key + '\'' +
                ", MovieInfo: " + movieInfoString + '\'' +
                '}';
    }
}
