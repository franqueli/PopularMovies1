package com.franqueli.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView synopsisTextView;


    private long movieIdParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        posterImageView = (ImageView)findViewById(R.id.movieDetailImageView);
        titleTextView = (TextView)findViewById(R.id.movieDetailTitleTextView);
        releaseDateTextView = (TextView) findViewById(R.id.movieDetailReleaseDateTextView);
        synopsisTextView = (TextView)findViewById(R.id.movieDetailSynopsisTextView);

        Intent intent = getIntent();
        movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);

        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);

        synopsisTextView.setText("My Movie Synopsis");

    }

    @Override
    protected void onResume() {
        super.onResume();

        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

        Picasso.with(this).load(movieInfo.getPosterURL()).into(posterImageView);
    }
}
