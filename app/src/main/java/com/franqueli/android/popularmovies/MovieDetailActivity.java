package com.franqueli.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    enum MovieDetailEnum {
        Detail, Videos, Reviews;
    }


    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private TheMovieDBAPI movieDBAPI;


    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView synopsisTextView;
    private TextView runtimeTextView;
    private Button favoriteButtonView;

    private RecyclerView recyclerView;

    private long movieIdParam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);

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
            MovieDetailFragment firstFragment = MovieDetailFragment.newInstance(movieIdParam);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }


        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);
//        getSupportFragmentManager().findFragmentById(R.id.movie_detail_fragment)
    }


//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_movie_detail);
//
//        posterImageView = (ImageView) findViewById(R.id.movieDetailImageView);
//        titleTextView = (TextView) findViewById(R.id.movieHeaderTitleTextView);
//        releaseDateTextView = (TextView) findViewById(R.id.movieDetailReleaseDateTextView);
//        ratingTextView = (TextView) findViewById(R.id.movieDetailRatingTextView);
//        synopsisTextView = (TextView) findViewById(R.id.movieDetailSynopsisTextView);
//        runtimeTextView = (TextView) findViewById(R.id.movieDetailRuntimeTextView);
//        favoriteButtonView = (Button) findViewById(R.id.movieDetailFavoriteButton);
//        recyclerView = (RecyclerView) findViewById(R.id.movieDetailTrailerRecyclerView);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//
//        synopsisTextView.setMovementMethod(new ScrollingMovementMethod());
//
//        Intent intent = getIntent();
//        movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);
//
//        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        movieDBAPI = new TheMovieDBAPI(getString(R.string.moviedb_api_key));
//
//        updateView();
//
//        DownloadMovieDetailsTask movieInfoTask = new DownloadMovieDetailsTask();
//        movieInfoTask.execute(MovieDetailEnum.Detail);
//
//    }
//
//    private void updateView() {
//        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);
//
//        NumberFormat ratingFormat = NumberFormat.getInstance();
//        ratingFormat.setMaximumFractionDigits(1);
//
//        titleTextView.setText(movieInfo.getTitle());
//
//        String releaseDateText = "";
//        if (movieInfo.getReleaseDate() != null) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(movieInfo.getReleaseDate());
//
//            releaseDateText = String.format(getString(R.string.release_date_text), calendar.get(Calendar.YEAR));
//        }
//        releaseDateTextView.setText(releaseDateText);
//
//        int movieRuntime = movieInfo.getRuntime();
//        if (movieRuntime > 0) {
//            runtimeTextView.setVisibility(View.VISIBLE);
//            runtimeTextView.setText(String.format(getString(R.string.runtime_text), movieRuntime));
//        } else {
//            runtimeTextView.setVisibility(View.GONE);
//        }
//
//        ratingTextView.setText(String.format(getString(R.string.rating_text), ratingFormat.format(movieInfo.getRating())));
//        synopsisTextView.setText(movieInfo.getSynopsis());
//
//        favoriteButtonView.setText(movieInfo.isFavorite() ? "Unfavorite" : "Mark as favorite");
//
//        String posterURL = movieInfo.getPosterURL();
//        if (posterURL == null) {
//            posterImageView.setImageDrawable(getResources().getDrawable(R.drawable.movie_place_holder));
//        } else {
//            Picasso.with(this).load(posterURL).into(posterImageView);
//        }
//
//
//        // TODO-fm: should we be resetting this each time
//        recyclerView.setAdapter(new TrailerListAdapter(movieInfo));
//    }
//
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putLong(MOVIE_ID_PARAM, movieIdParam);
//    }
//
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        movieIdParam = savedInstanceState.getLong(MOVIE_ID_PARAM, -1);
//    }
//
//
//    public void toggleFavorite (View view) {
//        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);
//        movieInfo.setFavorite(!movieInfo.isFavorite());
//
//        movieInfo.save();
//
//        favoriteButtonView.setText(movieInfo.isFavorite() ? "Unfavorite" : "Mark as favorite");
//    }
//
//
//    class DownloadMovieDetailsTask extends AsyncTask<MovieDetailEnum, Void, Void> {
//
//        private MovieDetailEnum currentRequestType;
//
//        @Override
//        protected Void doInBackground(MovieDetailEnum... params) {
//
//            MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);
//
//            try {
//                movieInfo.updateWithJSON(movieDBAPI.requestAllDetails(movieInfo.getMovieDBId() + ""));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            updateView();
//        }
//    }
}
