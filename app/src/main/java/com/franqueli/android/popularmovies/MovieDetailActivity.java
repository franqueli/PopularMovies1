package com.franqueli.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MovieDetailActivity extends AppCompatActivity {


    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        long movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movieIdParam);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, movieDetailFragment).commit();
        }


        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);
    }


    public void toggleFavorite (View view) {
        MovieDetailFragment detailFragment = (MovieDetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (detailFragment != null) {
            detailFragment.toggleFavorite(view);
        }
    }
}
