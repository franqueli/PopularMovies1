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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String LAST_SYNCED_PREF = "last_synced";
    public static final String SELECTED_SORT_PREF = "sort_selected";
    public static final String GRID_POSITION_PARAM = "GRIDVIEW_POSITION";

    private static final SortOptionsEnum[] SORT_OPTIONS = new SortOptionsEnum[]{SortOptionsEnum.Popularity, SortOptionsEnum.Rating};

    protected GridView movieGridView;
    protected MovieInfoAdapter movieInfoAdapter;

    private SharedPreferences preferences;
    private int selectedSortIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.preferences = getSharedPreferences("Sync", MODE_PRIVATE);
        this.selectedSortIndex = preferences.getInt(SELECTED_SORT_PREF, 0);

        // Pass the selected Sort Type to the adaptor
        movieInfoAdapter = new MovieInfoAdapter(this, SORT_OPTIONS[selectedSortIndex]);
        movieGridView = (GridView) findViewById(R.id.gridview);
        movieGridView.setStretchMode(GridView.NO_STRETCH);
        movieGridView.setAdapter(movieInfoAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<MovieInfo> allMovies = MovieInfo.listAll(MovieInfo.class);

                MovieInfo selectedMovie = MovieInfo.findById(MovieInfo.class, id);

                if (selectedMovie != null) {
                    Log.d(LOG_TAG, "Clicked item: " + selectedMovie.getTitle());

                    Intent showMovieDetailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
                    showMovieDetailIntent.putExtra(MovieDetailActivity.MOVIE_ID_PARAM, id);
                    startActivity(showMovieDetailIntent);
                }
            }
        });


        syncMovieMetadata();
        restoreScrollPosition(savedInstanceState);
    }

    // FIXME: SavedInstanceState is null why?
    private void restoreScrollPosition(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        int scrollPosition = savedInstanceState.getInt(GRID_POSITION_PARAM, -1);
        if (scrollPosition >= 0) {
            movieGridView.setSelection(scrollPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SELECTED_SORT_PREF, selectedSortIndex);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int position = movieGridView.getFirstVisiblePosition();
        outState.putInt(GRID_POSITION_PARAM, position);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreScrollPosition(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_sort);

        Spinner sortSpinner = (Spinner) menuItem.getActionView();

        ArrayAdapter<SortOptionsEnum> sortOptionsAdaptor = new ArrayAdapter<>(this, R.layout.sort_spinner_layout, SORT_OPTIONS);
        sortOptionsAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortSpinner.setAdapter(sortOptionsAdaptor);
        sortSpinner.setSelection(selectedSortIndex, false);
        sortSpinner.setOnItemSelectedListener(this);

        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selectedSortIndex = position;
        Log.d(LOG_TAG, "Selected Item: " + SORT_OPTIONS[selectedSortIndex]);

        // We're changing sort. Lets clear our the lastsynced time so the data is pulled from the server again.
        SharedPreferences.Editor editor = MainActivity.this.preferences.edit();
        editor.remove(LAST_SYNCED_PREF);
        editor.apply();

        syncMovieMetadata();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(LOG_TAG, "Nothing Selected");
    }



    private void syncMovieMetadata() {
        long lastSynced = this.preferences.getLong(LAST_SYNCED_PREF, 0);

        Date lastSyncedTime = lastSynced > 0 ? new Date(lastSynced) : null;

        long fifteenMinutesAgoInMillis = System.currentTimeMillis() - (15 * 60 * 1000);
        if (lastSyncedTime == null || lastSyncedTime.before(new Date(fifteenMinutesAgoInMillis))) {
            Log.d(LOG_TAG, "**** Syncing: " + lastSynced);
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data
                DownloadMovieInfoTask movieInfoTask = new DownloadMovieInfoTask();
                movieInfoTask.execute(SORT_OPTIONS[this.selectedSortIndex]);
            } else {
                // TODO-fm: Instead of displaying a toast message. Display a message in the main view. Along with a retry.
                Toast.makeText(MainActivity.this, "No network available", LENGTH_SHORT).show();
            }
        } else {
            Log.d(LOG_TAG, "**** Last Synced: " + lastSynced);
        }
    }



    /**
     * Created by Franqueli Mendez on 9/10/15.
     * <p/>
     * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
     */
    class DownloadMovieInfoTask extends AsyncTask<SortOptionsEnum, Void, String> {
        private String LOG_TAG = DownloadMovieInfoTask.class.getSimpleName();
        private SimpleDateFormat movieInfoReleaseDateFormat;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            movieInfoReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }

        @Override
        protected String doInBackground(SortOptionsEnum... params) {
            String result = null;
            try {
                result = downloadMovieInfo(params[0]);
            } catch (IOException e) {
                Log.d(LOG_TAG, "DownloadMovieInfo: " + e.getMessage());
            }

            return result;
        }

        private String downloadMovieInfo(SortOptionsEnum sortOption) throws IOException {
            String sortParam ;
            switch (sortOption) {
                case Rating:
                    sortParam = "vote_average.desc";
                    break;
                case Popularity:
                default:
                    sortParam = "popularity.desc";
                    break;
            }

            String movieInfo;
            InputStream is = null;
            try {
                String urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=" + sortParam + "&api_key=" + getString(R.string.moviedb_api_key);               // FIXME: Create utility class for moviedb urls
                URL popularMoviesURL = new URL(urlString);

                HttpURLConnection conn = (HttpURLConnection) popularMoviesURL.openConnection();
                conn.setReadTimeout(10000);                     // 10 sec
                conn.setConnectTimeout(15000);                 // 15 sec
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();


                int response = conn.getResponseCode();
                Log.d(LOG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                movieInfo = readIt(is);

            } finally {
                if (is != null) {
                    is.close();
                }
            }

            return movieInfo;
        }


        @Override
        protected void onPostExecute(String s) {
            Log.i(LOG_TAG, "*** Result: " + s);

            List movieInfoList = null;
            try {
                movieInfoList = readMovieInfoJsonStream(new ByteArrayInputStream(s.getBytes("UTF-8")));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.d(LOG_TAG, "" + movieInfoList);

            movieInfoAdapter = new MovieInfoAdapter(MainActivity.this, SortOptionsEnum.Popularity);
            movieGridView.setAdapter(movieInfoAdapter);
        }

        private String readIt(InputStream urlInputStream) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(urlInputStream));
            StringBuilder contentString = new StringBuilder();

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                contentString.append(inputLine);
            }

            return contentString.toString();
        }


        private List<MovieInfo> readMovieInfoJsonStream(InputStream in) throws IOException, ParseException {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

            try {
                return processPopularMovieResponse(reader);
            } finally {
                reader.close();
            }
        }

        private List<MovieInfo> processPopularMovieResponse(JsonReader reader) throws IOException, ParseException {
            List<MovieInfo> movieInfoList = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results")) {
                    // TODO: Process movie info list
                    movieInfoList = processPopularMovieList(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

            if (movieInfoList == null) {
                movieInfoList = new ArrayList<>();
            }

            return movieInfoList;
        }

        private List<MovieInfo> processPopularMovieList(JsonReader reader) throws IOException, ParseException {
            List<MovieInfo> movieInfoList = new ArrayList<>();

            // Clean up saved movies ready for a new list
            resetMovies();

            reader.beginArray();
            while (reader.hasNext()) {

                String title = null;
                String synopsis = null;
                double rating = 0.0;
                double popularity = 0.0;
                String posterPath = null;
                Date releaseDate = null;
                int movieDBId = 0;

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    // Skip all null values
                    if (reader.peek() == JsonToken.NULL) {
                        reader.skipValue();
                        continue;
                    }

                    if (name.equals("original_title")) {
                        title = reader.nextString();
                    } else if (name.equals("overview")) {
                        synopsis = reader.nextString();
                    } else if (name.equals("vote_average")) {
                        rating = reader.nextDouble();
                    } else if (name.equals("poster_path")) {
                        posterPath = reader.nextString();
                    } else if (name.equals("release_date")) {
                        try {
                            releaseDate = this.movieInfoReleaseDateFormat.parse(reader.nextString());
                        } catch (ParseException pe) {
                            releaseDate = null;
                        }
                    } else if (name.equals("popularity")) {
                        popularity = reader.nextDouble();
                    } else if (name.equals("id")) {
                        movieDBId = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

                // We've retrieved all the properties we need from the json. Now lets save it as an object
                MovieInfo currMovieInfo = updateMovie(title, synopsis, posterPath, (float) rating, (float) popularity, releaseDate, movieDBId);
                currMovieInfo.setCurrent(true);
                currMovieInfo.save();
                movieInfoList.add(currMovieInfo);
            }
            reader.endArray();

            SharedPreferences.Editor editor = MainActivity.this.preferences.edit();
            editor.putLong(LAST_SYNCED_PREF, System.currentTimeMillis());
            editor.apply();

            return movieInfoList;
        }

        private MovieInfo updateMovie(String title, String synopsis, String posterPath, float rating, float popularity, Date releaseDate, int movieDBId) {
            MovieInfo movieInfo = MovieInfo.findMovieInfo(movieDBId);
            if (movieInfo == null) {
                movieInfo = new MovieInfo(title, synopsis, posterPath, (float) rating, (float) popularity, releaseDate, movieDBId);
            } else {
                movieInfo.updateMovieInfo(title, synopsis, posterPath, (float) rating, (float) popularity, releaseDate);
            }

            return movieInfo;
        }

        private void resetMovies() {
            // Clear out all the old popular movies
            Iterator<MovieInfo> movieInfoIterator = MovieInfo.findAll(MovieInfo.class);

            while(movieInfoIterator.hasNext()) {
                MovieInfo currentMovieInfo = movieInfoIterator.next();
                if (!currentMovieInfo.isFavorite()) {
                    currentMovieInfo.delete();
                } else {
                    currentMovieInfo.setCurrent(false);
                    currentMovieInfo.save();
                }
            }
        }
    }
}
