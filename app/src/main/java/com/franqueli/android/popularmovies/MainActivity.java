package com.franqueli.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    protected GridView movieGridView;
    protected MovieInfoAdapter movieInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieInfoAdapter= new MovieInfoAdapter(this);
        movieGridView = (GridView) findViewById(R.id.gridview);
        movieGridView.setStretchMode(GridView.NO_STRETCH);
        movieGridView.setAdapter(movieInfoAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<MovieInfo> allMovies = MovieInfo.listAll(MovieInfo.class);

                MovieInfo selectedMovie = MovieInfo.findById(MovieInfo.class, id);

                if (selectedMovie != null) {
                    Toast.makeText(MainActivity.this, "Clicked item: " + selectedMovie.getTitle(), LENGTH_SHORT).show();
                }
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            DownloadMovieInfoTask movieInfoTask = new DownloadMovieInfoTask();
            movieInfoTask.execute(getString(R.string.moviedb_api_key));
        } else {
            // TODO-fm: Instead of displaying a toast message. Display a message in the main view. Along with a retry.
            Toast.makeText(MainActivity.this, "No network available", LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Created by Franqueli Mendez on 9/10/15.
     * <p>
     * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
     */
    class DownloadMovieInfoTask extends AsyncTask<String, Void, String> {
        private String LOG_TAG = DownloadMovieInfoTask.class.getSimpleName();
        private SimpleDateFormat movieInfoReleaseDateFormat;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            movieInfoReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = downloadMovieInfo(params[0]);
            } catch (IOException e) {
                Log.d(LOG_TAG, "DownloadMovieInfo: " + e.getMessage());
            }

            return result;
        }

        private String downloadMovieInfo(String apiKey) throws IOException {
            String movieInfo;
            InputStream is = null;
            try {
                String urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + apiKey;               // FIXME: Create utility class for moviedb urls
                URL popularMoviesURL = new URL(urlString);

                HttpURLConnection conn = (HttpURLConnection) popularMoviesURL.openConnection();
                conn.setReadTimeout(10000);                     // 10 sec
                conn.setConnectTimeout(15000 );                 // 15 sec
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

            // FIXME : Need to update UI now that the data has changed
            movieInfoAdapter.notifyDataSetChanged();
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


        private List<MovieInfo> readMovieInfoJsonStream (InputStream in) throws IOException, ParseException {
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

            // Clear out all the old popular movies
            MovieInfo.deleteAll(MovieInfo.class);

            reader.beginArray();
            while (reader.hasNext()) {

                String title = null;
                String synonsis = null;
                double rating = 0.0;
                double popularity = 0.0;
                String posterPath = null;
                Date releaseDate = null;

                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("original_title")) {
                        title = reader.nextString();
                    } else if (name.equals("overview")) {
                        synonsis = reader.nextString();
                    } else if (name.equals("vote_average")) {
                        rating = reader.nextDouble();
                    } else if (name.equals("poster_path")) {
                        posterPath = reader.nextString();
                    } else if (name.equals("release_date")) {
                        releaseDate = this.movieInfoReleaseDateFormat.parse(reader.nextString());
                    } else if (name.equals("popularity")) {
                        popularity = reader.nextDouble();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

                // We've retrieved all the properties we need from the json. Now lets save it as an object
                MovieInfo currMovieInfo = new MovieInfo(title, synonsis, posterPath, (float) rating, (float)popularity, releaseDate);
                currMovieInfo.save();
                movieInfoList.add(currMovieInfo);
            }
            reader.endArray();

            return movieInfoList;
        }
    }
}
