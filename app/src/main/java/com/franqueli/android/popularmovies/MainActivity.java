package com.franqueli.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.franqueli.android.popularmovies.model.MovieInfo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
//        implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String LAST_SYNCED_PREF = "last_synced";
    public static final String SELECTED_SORT_PREF = "sort_selected";
    public static final String GRID_POSITION_PARAM = "GRIDVIEW_POSITION";

    private static final SortOptionsEnum[] SORT_OPTIONS = new SortOptionsEnum[]{SortOptionsEnum.Popularity, SortOptionsEnum.Rating, SortOptionsEnum.Favorites};

    protected GridView movieGridView;
    protected MovieInfoAdapter movieInfoAdapter;

    private SharedPreferences preferences;
    private int selectedSortIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.preferences = getSharedPreferences("Sync", MODE_PRIVATE);
//        this.selectedSortIndex = preferences.getInt(SELECTED_SORT_PREF, 0);
//
//        // Pass the selected Sort Type to the adaptor
//        movieInfoAdapter = new MovieInfoAdapter(this, SORT_OPTIONS[selectedSortIndex]);
//        movieGridView = (GridView) findViewById(R.id.gridview);
//        movieGridView.setStretchMode(GridView.NO_STRETCH);
//        movieGridView.setAdapter(movieInfoAdapter);
//
//        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                List<MovieInfo> allMovies = MovieInfo.listAll(MovieInfo.class);
//
//                MovieInfo selectedMovie = MovieInfo.findById(MovieInfo.class, id);
//
//                if (selectedMovie != null) {
//                    Log.d(LOG_TAG, "Clicked item: " + selectedMovie.getTitle());
//
//                    Intent showMovieDetailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
//                    showMovieDetailIntent.putExtra(MovieDetailActivity.MOVIE_ID_PARAM, id);
//                    startActivity(showMovieDetailIntent);
//                }
//            }
//        });
//
//
//        syncMovieMetadata();
//        restoreScrollPosition(savedInstanceState);
    }

    // FIXME: SavedInstanceState is null why?
//    private void restoreScrollPosition(Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
//            return;
//        }
//
//        int scrollPosition = savedInstanceState.getInt(GRID_POSITION_PARAM, -1);
//        if (scrollPosition >= 0) {
//            movieGridView.setSelection(scrollPosition);
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();

//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt(SELECTED_SORT_PREF, selectedSortIndex);
//        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        int position = movieGridView.getFirstVisiblePosition();
//        outState.putInt(GRID_POSITION_PARAM, position);
    }


//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        restoreScrollPosition(savedInstanceState);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.action_sort);
//
//        Spinner sortSpinner = (Spinner) menuItem.getActionView();
//
//        ArrayAdapter<SortOptionsEnum> sortOptionsAdaptor = new ArrayAdapter<>(this, R.layout.sort_spinner_layout, SORT_OPTIONS);
//        sortOptionsAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        sortSpinner.setAdapter(sortOptionsAdaptor);
//        sortSpinner.setSelection(selectedSortIndex, false);
//        sortSpinner.setOnItemSelectedListener(this);
//
//        return true;
//    }
//
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        this.selectedSortIndex = position;
//        Log.d(LOG_TAG, "Selected Item: " + SORT_OPTIONS[selectedSortIndex]);
//
//        // We're changing sort. Lets clear our the lastsynced time so the data is pulled from the server again.
//        SharedPreferences.Editor editor = MainActivity.this.preferences.edit();
//        editor.remove(LAST_SYNCED_PREF);
//        editor.apply();
//        if (SORT_OPTIONS[selectedSortIndex] != SortOptionsEnum.Favorites) {
//            syncMovieMetadata();
//        } else {
//            movieInfoAdapter = new MovieInfoAdapter(MainActivity.this, SortOptionsEnum.Favorites);
//            movieGridView.setAdapter(movieInfoAdapter);
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        Log.d(LOG_TAG, "Nothing Selected");
//    }
//


//    private void syncMovieMetadata() {
//        long lastSynced = this.preferences.getLong(LAST_SYNCED_PREF, 0);
//
//        Date lastSyncedTime = lastSynced > 0 ? new Date(lastSynced) : null;
//
//        long fifteenMinutesAgoInMillis = System.currentTimeMillis() - (15 * 60 * 1000);
//        if (lastSyncedTime == null || lastSyncedTime.before(new Date(fifteenMinutesAgoInMillis))) {
//            Log.d(LOG_TAG, "**** Syncing: " + lastSynced);
//            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//            if (networkInfo != null && networkInfo.isConnected()) {
//                // fetch data
//                DownloadMovieInfoTask movieInfoTask = new DownloadMovieInfoTask();
//                movieInfoTask.execute(SORT_OPTIONS[this.selectedSortIndex]);
//            } else {
//                // TODO-fm: Instead of displaying a toast message. Display a message in the main view. Along with a retry.
//                Toast.makeText(MainActivity.this, "No network available", LENGTH_SHORT).show();
//            }
//        } else {
//            Log.d(LOG_TAG, "**** Last Synced: " + lastSynced);
//        }
//    }



}
