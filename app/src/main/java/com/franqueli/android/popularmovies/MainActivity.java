package com.franqueli.android.popularmovies;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mDetailPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetailPresent = findViewById(R.id.main_detail_container) != null;

        if (mDetailPresent) {
            // Tablet layout

            // Create a new Fragment to be placed in the activity layout
            MovieGridFragment firstFragment = new MovieGridFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Create a new Fragment to be placed in the activity layout
            MovieDetailFragment secondFragment = MovieDetailFragment.newInstance(14);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());


            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.main_master_container, firstFragment).add(R.id.main_detail_container, secondFragment).commit();


        } else {

            // Check that the activity is using the layout version with
            // the fragment_container FrameLayout
            if (findViewById(R.id.main_fragment_container) != null) {

                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                if (savedInstanceState != null) {
                    return;
                }

                // Create a new Fragment to be placed in the activity layout
                MovieGridFragment firstFragment = new MovieGridFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                firstFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, firstFragment).commit();
//                    add(R.id.main_fragment_container, firstFragment).commit();
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
