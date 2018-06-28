package com.example.kavinraju.popularmoviesstage1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kavinraju.popularmoviesstage1.Fragments.PopularMoviesFragment;
import com.example.kavinraju.popularmoviesstage1.Fragments.TopRatedMoviesFragement;
import com.example.kavinraju.popularmoviesstage1.HelperClass.BottomNavigationBehavior;
import com.example.kavinraju.popularmoviesstage1.HelperClass.HelperMethods;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{


    //Constants
    private static String TOP_RATED = "top_rated";
    private static String POPULAR = "popular";
    private static String currentSelectedbottomNavigation = POPULAR;

    //Key used for SharedElementTransition
    public static String SHARED_ELEMENT_TRANSITION_EXTRA = "sharedElementTransition";

    //Keys used for savedInstancestate
    private static String POPULAR_MOVIE_LIST_FRAGMENT_KEY ="popular_movies_fragment";
    private static String TOP_RATED_MOVIE_LIST_FRAGMENT_KEY ="top_rated_movies_fragment";

    // UI components
    Toolbar mToolbar;
    BottomNavigationView mBottomNavigationView;


    //Fragments
    private FragmentManager fragmentManager;
    private PopularMoviesFragment popularMoviesFragment;
    private TopRatedMoviesFragement topRatedMoviesFragement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initializing UI Components
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");

        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        if (savedInstanceState != null){


            popularMoviesFragment = (PopularMoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState, POPULAR_MOVIE_LIST_FRAGMENT_KEY);
            topRatedMoviesFragement = (TopRatedMoviesFragement) getSupportFragmentManager().getFragment(savedInstanceState, TOP_RATED_MOVIE_LIST_FRAGMENT_KEY);

            /*
            This 'if' condition is to avoid NullPointerException on Fragment if we rotate the device
            without inflating the other fragment. i.e: If we rotate the device as soon as we open the
            App.
            */
            if (topRatedMoviesFragement == null){
                topRatedMoviesFragement = new TopRatedMoviesFragement();
                topRatedMoviesFragement.setCurrentSelectedbottomNavigation(TOP_RATED);
            }

            if (popularMoviesFragment == null){
                popularMoviesFragment = new PopularMoviesFragment();
                popularMoviesFragment.setCurrentSelectedbottomNavigation(POPULAR);
            }
        }else{

            popularMoviesFragment = new PopularMoviesFragment();
            popularMoviesFragment.setCurrentSelectedbottomNavigation(POPULAR);

            topRatedMoviesFragement = new TopRatedMoviesFragement();
            topRatedMoviesFragement.setCurrentSelectedbottomNavigation(TOP_RATED);
        }


        fragmentManager = getSupportFragmentManager();

        if (currentSelectedbottomNavigation.equals(POPULAR)) {

            mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_popular));
            fragmentManager.beginTransaction()
                    .add(R.id.container_for_recycler_view, topRatedMoviesFragement, TOP_RATED)
                    // Its been added to avoid Fragment is not currently in the FragmentManager
                    .replace(R.id.container_for_recycler_view, popularMoviesFragment, POPULAR)
                    .addToBackStack(null)
                    .commit();
        }else if (currentSelectedbottomNavigation.equals(TOP_RATED)){

            mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_top_rated));
            fragmentManager.beginTransaction()
                    .replace(R.id.container_for_recycler_view, topRatedMoviesFragement, TOP_RATED)
                    .addToBackStack(null)
                    .commit();
        }

    } //end of onCreate

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getSupportFragmentManager().findFragmentByTag(POPULAR).isAdded()){

            getSupportFragmentManager().putFragment(outState, POPULAR_MOVIE_LIST_FRAGMENT_KEY, popularMoviesFragment);
        }

        if (getSupportFragmentManager().findFragmentByTag(TOP_RATED).isAdded()){

            getSupportFragmentManager().putFragment(outState, TOP_RATED_MOVIE_LIST_FRAGMENT_KEY, topRatedMoviesFragement);
        }

    }// end of onSaveInstanceState


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.bottom_nav_popular:
                mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_popular));
                currentSelectedbottomNavigation = POPULAR;

                /*
                    Rubric
                    <When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.>
                 */

                if(fragmentManager.findFragmentByTag(POPULAR) instanceof PopularMoviesFragment){
                    popularMoviesFragment = (PopularMoviesFragment) fragmentManager.findFragmentByTag(POPULAR);
                }

                fragmentManager.beginTransaction()
                            .replace(R.id.container_for_recycler_view, popularMoviesFragment, POPULAR)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                return true;

            case R.id.bottom_nav_top_rated:
                mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_top_rated));
                currentSelectedbottomNavigation = TOP_RATED;

                if(fragmentManager.findFragmentByTag(TOP_RATED) instanceof TopRatedMoviesFragement){
                    topRatedMoviesFragement = (TopRatedMoviesFragement) fragmentManager.findFragmentByTag(TOP_RATED);
                }
                fragmentManager.beginTransaction()
                            .replace(R.id.container_for_recycler_view, topRatedMoviesFragement , TOP_RATED)
                            .addToBackStack(null)
                            .commit();

                return true;
            case R.id.bottom_nav_upcoming:
                Toast.makeText(this, "Feature yet to implement", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bottom_nav_favorite:
                Toast.makeText(this, "Feature yet to implement", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bottom_nav_settings:
                Toast.makeText(this, "Feature yet to implement", Toast.LENGTH_SHORT).show();
                return true;
        }

        return false;
    }

}
