package com.franqueli.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView synopsisTextView;


    private long movieIdParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        posterImageView = (ImageView) findViewById(R.id.movieDetailImageView);
        titleTextView = (TextView) findViewById(R.id.movieDetailTitleTextView);
        releaseDateTextView = (TextView) findViewById(R.id.movieDetailReleaseDateTextView);
        ratingTextView = (TextView) findViewById(R.id.movieDetailRatingTextView);
        synopsisTextView = (TextView) findViewById(R.id.movieDetailSynopsisTextView);

        Intent intent = getIntent();
        movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);

        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

        NumberFormat ratingFormat = NumberFormat.getInstance();
        ratingFormat.setMaximumFractionDigits(1);

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);


        titleTextView.setText(movieInfo.getTitle());
        releaseDateTextView.setText(String.format(getString(R.string.release_date_text), dateFormat.format(movieInfo.getReleaseDate())));
        ratingTextView.setText(String.format(getString(R.string.rating_text), ratingFormat.format(movieInfo.getRating())));
        synopsisTextView.setText(movieInfo.getSynopsis());

        String posterURL = movieInfo.getPosterURL();
        if (posterURL == null) {
            posterImageView.setImageDrawable(getResources().getDrawable(R.drawable.movie_place_holder));
        } else {
            Picasso.with(this).load(posterURL).into(posterImageView);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(MOVIE_ID_PARAM, movieIdParam);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        movieIdParam = savedInstanceState.getLong(MOVIE_ID_PARAM, -1);

    }


}
