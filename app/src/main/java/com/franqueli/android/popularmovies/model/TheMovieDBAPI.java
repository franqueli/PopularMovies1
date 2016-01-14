package com.franqueli.android.popularmovies.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Franqueli Mendez on 1/11/16.
 * <p/>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class TheMovieDBAPI {
    private static final String LOG_TAG = TheMovieDBAPI.class.getSimpleName();

    private String apiKey;
    private static final String movieDBBaseURL = "http://api.themoviedb.org/3%1$s?api_key=%2$s";
    private static final String reviewURLSegment = "/movie/%1$s/reviews";
    private static final String videoURLSegment = "/movie/%1$s/videos";
    private static final String movieDetailURLSegment = "/movie/%1$s/reviews";

    public TheMovieDBAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReviewURL(String movieID) {
        String reviewPath = String.format(reviewURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }

    public String getVideosURL(String movieID) {
        String reviewPath = String.format(videoURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }

    public String getDetailsURL(String movieID) {
        String reviewPath = String.format(movieDetailURLSegment, movieID);
        return String.format(movieDBBaseURL, reviewPath, apiKey);
    }


    // TODO : Add the http connection in this class. All you need to do is call this from the doInBackground method

    // TODO : Return an array of objects containing the information for a review
    public String requestReviewJSON(String movieID) {
        return requestPayloadString(getReviewURL(movieID));
    }

    public String requestVideosJSON(String movieID) {
        return requestPayloadString(getVideosURL(movieID));
    }

    public String requestDetails(String movieID) {
        return requestPayloadString(getDetailsURL(movieID));
    }

    private String requestPayloadString(String urlStr) {
        String result;
        try {
            result =  downloadPayload(urlStr);
        } catch (IOException e) {
            result = "";

            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return result;
    }


    private String downloadPayload(String urlStr) throws IOException {
        String responseString;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
            responseString = readIt(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }

        return responseString;
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


    /*
            String responseString;
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

            // Clear out all the old popular movies
            MovieInfo.deleteAll(MovieInfo.class);

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
                MovieInfo currMovieInfo = new MovieInfo(title, synopsis, posterPath, (float) rating, (float) popularity, releaseDate, movieDBId);
                currMovieInfo.save();
                movieInfoList.add(currMovieInfo);
            }
            reader.endArray();

            SharedPreferences.Editor editor = MainActivity.this.preferences.edit();
            editor.putLong(LAST_SYNCED_PREF, System.currentTimeMillis());
            editor.apply();

            return movieInfoList;
        }
    }

     */


}
