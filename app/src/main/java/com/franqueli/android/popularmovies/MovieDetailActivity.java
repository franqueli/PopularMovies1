package com.franqueli.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.franqueli.android.popularmovies.model.TheMovieDBAPI;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private TheMovieDBAPI movieDBAPI;


    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView synopsisTextView;
    private TextView runtimeTextView;



    private long movieIdParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        posterImageView = (ImageView) findViewById(R.id.movieDetailImageView);
        titleTextView = (TextView) findViewById(R.id.movieHeaderTitleTextView);
        releaseDateTextView = (TextView) findViewById(R.id.movieDetailReleaseDateTextView);
        ratingTextView = (TextView) findViewById(R.id.movieDetailRatingTextView);
        synopsisTextView = (TextView) findViewById(R.id.movieDetailSynopsisTextView);
        runtimeTextView = (TextView) findViewById(R.id.movieDetailRuntimeTextView);

        Intent intent = getIntent();
        movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);

        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

        movieDBAPI = new TheMovieDBAPI(getString(R.string.moviedb_api_key));

        Log.d(LOG_TAG, "*** review url: " + movieDBAPI.getReviewURL(movieInfo.getMovieDBId() + ""));

        NumberFormat ratingFormat = NumberFormat.getInstance();
        ratingFormat.setMaximumFractionDigits(1);

        titleTextView.setText(movieInfo.getTitle());

        String releaseDateText = "";
        if (movieInfo.getReleaseDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(movieInfo.getReleaseDate());

            releaseDateText = String.format(getString(R.string.release_date_text), calendar.get(Calendar.YEAR));
        }
        releaseDateTextView.setText(releaseDateText);

        runtimeTextView.setText("");                           // FIXME: Need to get runtime information from moviedb api

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


    enum MovieDetailEnum {
        Detail, Videos, Reviews;
    }


    class DownloadMovieDetailsTask extends AsyncTask<MovieDetailEnum, Void, Void> {

        private MovieDetailEnum currentRequestType;


        @Override
        protected Void doInBackground(MovieDetailEnum... params) {
            return null;
        }





    }



}
