package com.franqueli.android.popularmovies;

import android.app.Application;

import com.orm.SugarContext;

/**
 * Created by Franqueli Mendez on 11/24/15.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class PopularMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
