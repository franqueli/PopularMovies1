package com.franqueli.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "movieIdParam";

    // TODO: Rename and change types of parameters
    private long movieIdParam;

    private OnFragmentInteractionListener mListener;

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
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        posterImageView = (ImageView) rootView.findViewById(R.id.movieDetailImageView);
        titleTextView = (TextView) rootView.findViewById(R.id.movieHeaderTitleTextView);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.movieDetailReleaseDateTextView);
        ratingTextView = (TextView) rootView.findViewById(R.id.movieDetailRatingTextView);
        synopsisTextView = (TextView) rootView.findViewById(R.id.movieDetailSynopsisTextView);
        runtimeTextView = (TextView) rootView.findViewById(R.id.movieDetailRuntimeTextView);
        favoriteButtonView = (Button) rootView.findViewById(R.id.movieDetailFavoriteButton);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movieDetailTrailerRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        synopsisTextView.setMovementMethod(new ScrollingMovementMethod());

        // FIXME: Use param for movie id
//        Intent intent = getIntent();
//        movieIdParam = intent.getLongExtra(MOVIE_ID_PARAM, -1);

        Log.d(LOG_TAG, "*** MovieParam: " + movieIdParam);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

// ---------------------------------

    enum MovieDetailEnum {
        Detail, Videos, Reviews;
    }


    public static final String MOVIE_ID_PARAM = "com.franqueli.android.popularmovies.MOVIE_ID";

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private TheMovieDBAPI movieDBAPI;

    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView synopsisTextView;
    private TextView runtimeTextView;
    private Button favoriteButtonView;

    private RecyclerView recyclerView;

//    private long movieIdParam;

    @Override
    public void onResume() {
        super.onResume();

        movieDBAPI = new TheMovieDBAPI(getString(R.string.moviedb_api_key));

        updateView();

        DownloadMovieDetailsTask movieInfoTask = new DownloadMovieDetailsTask();
        movieInfoTask.execute(MovieDetailEnum.Detail);

    }

    private void updateView() {
        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

        if (movieInfo == null) {
            return;
        }

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


        // TODO-fm: should we be resetting this each time
        recyclerView.setAdapter(new TrailerListAdapter(movieInfo));
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


    public void toggleFavorite (View view) {
        MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);
        movieInfo.setFavorite(!movieInfo.isFavorite());

        movieInfo.save();

        favoriteButtonView.setText(movieInfo.isFavorite() ? "Unfavorite" : "Mark as favorite");
    }


    class DownloadMovieDetailsTask extends AsyncTask<MovieDetailEnum, Void, Void> {

        private MovieDetailEnum currentRequestType;

        @Override
        protected Void doInBackground(MovieDetailEnum... params) {

            MovieInfo movieInfo = SugarRecord.findById(MovieInfo.class, movieIdParam);

            if (movieInfo == null) {
                return null;
            }

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

            updateView();
        }
    }


// ---------------------------------
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
