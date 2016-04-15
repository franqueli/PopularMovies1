package com.franqueli.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.MovieInfo;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {
    enum MovieDetailEnum {
        Detail, Videos, Reviews;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "movieIdParam";
    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private TheMovieDBAPI movieDBAPI;

    private View rootView;
    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView synopsisTextView;
    private TextView runtimeTextView;
    private Button favoriteButtonView;

    private RecyclerView recyclerView;
    private RecyclerView reviewsRecyclerView;

    // TODO: Rename and change types of parameters
    private long movieIdParam;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieIdParam Parameter 1.
     * @return A new instance of fragment MovieDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetailFragment newInstance(long movieIdParam) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, movieIdParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            movieIdParam = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        posterImageView = (ImageView) rootView.findViewById(R.id.movieDetailImageView);
        titleTextView = (TextView) rootView.findViewById(R.id.movieHeaderTitleTextView);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.movieDetailReleaseDateTextView);
        ratingTextView = (TextView) rootView.findViewById(R.id.movieDetailRatingTextView);
        synopsisTextView = (TextView) rootView.findViewById(R.id.movieDetailSynopsisTextView);
        runtimeTextView = (TextView) rootView.findViewById(R.id.movieDetailRuntimeTextView);
        favoriteButtonView = (Button) rootView.findViewById(R.id.movieDetailFavoriteButton);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movieDetailTrailerRecyclerView);
        reviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.movieDetailReviewsRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        synopsisTextView.setMovementMethod(new ScrollingMovementMethod());

        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        updateView();

        downloadUpdates();
//        Log.d(LOG_TAG, "*** onResume ***");
    }

    public void downloadUpdates() {
        movieDBAPI = new TheMovieDBAPI(getString(R.string.moviedb_api_key));
        DownloadMovieDetailsTask movieInfoTask = new DownloadMovieDetailsTask();
        movieInfoTask.execute(MovieDetailEnum.Detail);
    }


    public void updateView() {
        if (getArguments() != null) {
            movieIdParam = getArguments().getLong(ARG_PARAM1);
        }

//        Log.d(LOG_TAG, "*** updateView ***");

        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

        if (movieInfo == null || getActivity() == null) {
            rootView.setVisibility(View.GONE);
            return;
        }

        rootView.setVisibility(View.VISIBLE);

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

        int movieRuntime = movieInfo.getRuntime();
        if (movieRuntime > 0) {
            runtimeTextView.setVisibility(View.VISIBLE);
            runtimeTextView.setText(String.format(getString(R.string.runtime_text), movieRuntime));
        } else {
            runtimeTextView.setVisibility(View.GONE);
        }

        ratingTextView.setText(String.format(getString(R.string.rating_text), ratingFormat.format(movieInfo.getRating())));
        synopsisTextView.setText(movieInfo.getSynopsis());

        favoriteButtonView.setText(movieInfo.isFavorite() ? "Unfavorite" : "Mark as favorite");

        String posterURL = movieInfo.getPosterURL();
        if (posterURL == null) {
            posterImageView.setImageDrawable(getResources().getDrawable(R.drawable.movie_place_holder));
        } else {
            Picasso.with(getActivity()).load(posterURL).into(posterImageView);
        }

//        Log.d(LOG_TAG, "*** Trailers: " + Arrays.toString(movieInfo.getMovieTrailers().toArray()));
//        Log.d(LOG_TAG, "*** Reviews: " + Arrays.toString(movieInfo.getMovieReviews().toArray()));
        // TODO-fm: should we be resetting this each time
        recyclerView.setAdapter(new TrailerListAdapter(movieInfo));
        reviewsRecyclerView.setAdapter(new MovieReviewAdapter(movieInfo));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(MOVIE_ID_PARAM, movieIdParam);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            movieIdParam = savedInstanceState.getLong(MOVIE_ID_PARAM, -1);
        }
    }


    public void toggleFavorite(View view) {
        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);
        movieInfo.setFavorite(!movieInfo.isFavorite());

        movieInfo.save();

        favoriteButtonView.setText(movieInfo.isFavorite() ? "Unfavorite" : "Mark as favorite");
    }

    public void setMovieIdParam(long movieIdParam) {
        this.movieIdParam = movieIdParam;
    }

    class DownloadMovieDetailsTask extends AsyncTask<MovieDetailEnum, Void, Void> {

        private MovieDetailEnum currentRequestType;

        @Override
        protected Void doInBackground(MovieDetailEnum... params) {
//            Log.d(LOG_TAG, "***** DoInBackground *****");
            MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

            if (movieInfo == null) {
                return null;
            }
//            Log.d(LOG_TAG, "***** MovieInfoNotNull *****");
            try {
                movieInfo.updateWithJSON(movieDBAPI.requestAllDetails(movieInfo.getMovieDBId() + ""));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            Log.d(LOG_TAG, "***** onPostExecute *****");

            updateView();
        }
    }
}
