package com.example.kavinraju.popularmoviesstage1;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kavinraju.popularmoviesstage1.Adapter.CastListAdapter;
import com.example.kavinraju.popularmoviesstage1.Data_Model.CastDetails;
import com.example.kavinraju.popularmoviesstage1.Data_Model.MovieDetail2;
import com.example.kavinraju.popularmoviesstage1.Data_Model.MovieDetail;
import com.example.kavinraju.popularmoviesstage1.HelperClass.HelperMethods;
import com.example.kavinraju.popularmoviesstage1.Utils.JsonUtils;
import com.example.kavinraju.popularmoviesstage1.Utils.NetworkUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
Rubric
<UI contains a screen for displaying the details for a selected movie.>
 */
public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    //Constants
    private static int MOVIE_DETAIL_QUERY_LOADER_ID = 100;
    private static String MOVIE_DETAIL_QUERY_EXTRA = "movie_detail_query";
    private static int MOVIE_LANGUAGE_QUERY_LOADER_ID = 101;
    private static String MOVIE_LANGUAGE_QUERY_EXTRA = "movie_language_query";
    private static int CAST_DETAILS_QUERY_LOADER_ID = 102;
    private static String CAST_DETAILS_QUERY_EXTRA = "cast_details_query";

    //Keys used for savedInstancestate
    private static String MOVIE_DETAIL_KEY ="movie_detail";
    private static String MOVIE_DETAIL_2_KEY ="movie_detail_2";
    private static String LANGUAGE__KEY ="language";
    private static String CAST_LIST_KEY ="cast_list";



    // POJO Class
    MovieDetail movieDetail;
    MovieDetail2 movieDetail2;
    private ArrayList<CastDetails> castDetails;

    //CastListAdapter
    private CastListAdapter castListAdapter;


    private int movieID;
    private String movieLanguageCode;
    private String language = "";

    // UI Components
    @BindView(R.id.textView_movie_title)
    TextView textView_movieTitle;
    @BindView(R.id.textView_movieLanguage)
    TextView textView_movie_language;
    @BindView(R.id.textView_genres)
    TextView textView_generes;
    @BindView(R.id.textView_releaseDate)
    TextView textView_releaseDate;
    @BindView(R.id.textView_tagline)
    TextView textView_tagline;
    @BindView(R.id.textView_description)
    TextView textView_description;
    @BindView(R.id.textView_rating)
    TextView textView_rating;
    @BindView(R.id.imageView_backDrop)
    ImageView imageView_backdrop;
    @BindView(R.id.imageView_poster)
    ImageView imageView_poster;
    @BindView(R.id.imageButton_back)
    ImageButton imageButton_back;
    @BindView(R.id.progressBar_movieDetails)
    ProgressBar progressBar;
    @BindView(R.id.recyclerView_casts)
    RecyclerView recyclerView_casts;
    /*@BindView(R.id.shimmer_details_layout)
    ShimmerFrameLayout shimmerFrameLayout;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Getting the MovieDetail object from intent
        Bundle bundle = getIntent().getExtras();
        movieDetail = getIntent().getParcelableExtra("movie");

        ButterKnife.bind(this);

        //It works version greater that LOLLIPOP
        assert bundle != null;
        String imageTransitionName = bundle.getString(HomeActivity.SHARED_ELEMENT_TRANSITION_EXTRA);
        imageView_poster.setTransitionName(imageTransitionName);

        if (savedInstanceState != null){

            HelperMethods.showProgressBar(progressBar,false);

            loadImage(true);
            loadImage(false);

            movieDetail2 = savedInstanceState.getParcelable(MOVIE_DETAIL_2_KEY);
            castDetails = (ArrayList<CastDetails>) savedInstanceState.getSerializable(CAST_LIST_KEY);
            language = savedInstanceState.getString(LANGUAGE__KEY);

            textView_movieTitle.setText(movieDetail.getOriginalTitle()); // Title is set here
            String date = HelperMethods.getdetiledDate(movieDetail.getReleaseDate());
            textView_releaseDate.setText(date);
            textView_rating.setText(String.valueOf(movieDetail.getVoteAverage()));
            textView_description.setText(movieDetail.getOverView());

            textView_movie_language.setText(language);

            //Check if the MovieDetails2 and ArrayList<CastDetails> is null or not
            if (movieDetail2 != null && castDetails != null) {
                setUIComponentsUsingMovieDetails2(movieDetail2);
                setCastRecyclerView();

            }else {
                setUIComponents();
            }

        }else {
            setUIComponents();
        }

    }// onCreate()

    @Override
    protected void onResume() {
        super.onResume();
        //shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //shimmerFrameLayout.stopShimmerAnimation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_DETAIL_KEY,  movieDetail);
        outState.putParcelable(MOVIE_DETAIL_2_KEY, movieDetail2);
        outState.putSerializable(CAST_LIST_KEY, castDetails);
        outState.putString(LANGUAGE__KEY,language);
    }


    @OnClick(R.id.imageButton_back)
    public void onClick(){
        supportFinishAfterTransition();
    }

    // Loader CallBack methods
    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable final Bundle args) {

        if (id == MOVIE_DETAIL_QUERY_LOADER_ID){

            return new AsyncTaskLoader<MovieDetail2>(this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                }

                @Nullable
                @Override
                public MovieDetail2 loadInBackground() {
                    return null;

                }
            };

        }
        else if (id == MOVIE_LANGUAGE_QUERY_LOADER_ID){

            return new AsyncTaskLoader<String>(this) {
                @Nullable
                @Override
                public String loadInBackground() {
                    return null;
                }
            };

        }else if ( id == CAST_DETAILS_QUERY_LOADER_ID){
            return new AsyncTaskLoader<ArrayList<CastDetails>>(this) {
                @Nullable
                @Override
                public ArrayList<CastDetails> loadInBackground() {
                    return null;
                }
            };
        }
        return new AsyncTaskLoader(this) {
            @Nullable
            @Override
            public Object loadInBackground() {
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }


    /*
             **********************LoaderCallback METHODS**************************
     */

    // MovieDetail2 LoaderCallback method
    private LoaderManager.LoaderCallbacks<MovieDetail2> movieDetailLoaderCallback() {

        return new LoaderManager.LoaderCallbacks<MovieDetail2>() {

            @SuppressLint("StaticFieldLeak")
            @NonNull
            @Override
            public Loader<MovieDetail2> onCreateLoader(int id, @Nullable final Bundle args) {

                return new AsyncTaskLoader<MovieDetail2>(getApplicationContext()) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        //Show ProgoressBar
                        //HelperMethods.showProgressBar(progressBar,true);
                        Log.d("Progress" , "1");
                    }

                    @Nullable
                    @Override
                    public MovieDetail2 loadInBackground() {

                        assert args != null;
                        int movieID = args.getInt(MOVIE_DETAIL_QUERY_EXTRA, -1);

                        if (movieID == -1) {
                            return null;
                        }

                        try {
                            URL url = NetworkUtils.buildURL_for_MovieDetail(movieID);
                            String jsonString = NetworkUtils.getResponceFromURl(url);
                            Log.d("URL: ", url.toString());
                            return JsonUtils.getMovieDetail2(jsonString);

                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }

                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<MovieDetail2> loader, MovieDetail2 data) {

                /*
                    Rubric
                    <Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.>
                 */
                textView_movieTitle.setText(movieDetail.getOriginalTitle()); // Title is set here
                String date = HelperMethods.getdetiledDate(movieDetail.getReleaseDate());
                textView_releaseDate.setText(date);
                textView_rating.setText(String.valueOf(movieDetail.getVoteAverage()));
                textView_description.setText(movieDetail.getOverView());

                if (data != null) {
                    movieDetail2 = data;
                    textView_generes.setText("");

                    setUIComponentsUsingMovieDetails2(data);
                    HelperMethods.showProgressBar(progressBar,false);
                    //shimmerFrameLayout.setVisibility(View.GONE);
                    //shimmerFrameLayout.stopShimmerAnimation();
                }else {
                    //Network error code
                    Toast.makeText(MovieDetailsActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    HelperMethods.showProgressBar(progressBar,false);

                }

            }

            @Override
            public void onLoaderReset(@NonNull Loader<MovieDetail2> loader) {

            }

        };

    }

    // Movie Language LoaderCallback method
    private LoaderManager.LoaderCallbacks<String> languageLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<String>() {
            @SuppressLint("StaticFieldLeak")
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {

                return new AsyncTaskLoader<String>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        //Show ProgoressBar
                        //HelperMethods.showProgressBar(progressBar,true);
                        Log.d("Progress" , "2");
                    }

                    @Nullable
                    @Override
                    public String loadInBackground() {

                        assert args != null;
                        String lang_code = args.getString(MOVIE_LANGUAGE_QUERY_EXTRA);

                        if (lang_code == null) {
                            return null;
                        }

                        try {
                            URL url = NetworkUtils.buildURL_for_movieLanguages();
                            String jsonString = NetworkUtils.getResponceFromURl(url);
                            Log.d("URL: ", url.toString());

                            // Get the list of languages (ISO 639-1 tags) used throughout TMDb, as a HashMap<String,String>
                            HashMap<String,String> languages = JsonUtils.getLanguages(jsonString);

                            assert languages != null;
                            String language = languages.get(lang_code);  // get the desired language
                            Log.d("MAP(lang): ",languages.get(lang_code));

                            return language;

                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }


                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {

                if (data != null) {
                    // Display the movie language
                    textView_movie_language.setText(data);
                    language = data;
                    HelperMethods.showProgressBar(progressBar,false);
                }else {
                    // Netowork error code comes here
                    Toast.makeText(MovieDetailsActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {

            }
        };
    }

    //CastDetails LoaderCallback method
    private LoaderManager.LoaderCallbacks<ArrayList<CastDetails>> castDetailsLoaderCallbacks(){

        return new LoaderManager.LoaderCallbacks<ArrayList<CastDetails>>() {
            @SuppressLint("StaticFieldLeak")
            @NonNull
            @Override
            public Loader<ArrayList<CastDetails>> onCreateLoader(int id, @Nullable final Bundle args) {
                return new AsyncTaskLoader<ArrayList<CastDetails>>(getApplicationContext()) {

                    @Nullable
                    @Override
                    public ArrayList<CastDetails> loadInBackground() {

                        assert args != null;
                        int movieId = args.getInt(CAST_DETAILS_QUERY_EXTRA);

                        if (movieId < 0) {
                            return null;
                        }

                        try {
                            URL url = NetworkUtils.buildURL_for_castDetails(movieId);
                            String jsonString = NetworkUtils.getResponceFromURl(url);
                            Log.d("URL: ", url.toString());

                            return JsonUtils.getCastDetails(jsonString);

                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<ArrayList<CastDetails>> loader, ArrayList<CastDetails> data) {

                if (data != null){

                    castDetails = data;
                    setCastRecyclerView();
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<ArrayList<CastDetails>> loader) {

            }
        };
    }


    /*
            **********************HELPER METHODS**************************
     */

    //Helper Method to set UI Components
    private void setUIComponents() {

        HelperMethods.showProgressBar(progressBar,true);
        loadImage(true);
        loadImage(false);

        movieID = movieDetail.getId();
        movieLanguageCode = movieDetail.getOriginal_language();

        // Loading Loaders to get movieDetail and Movie Language and Cast List
        runCastDetails_LoaderManager(movieID);
        runMovieDetail2_LoaderManager(movieID);
        runLanguageQuery_LoaderManager(movieLanguageCode);


    }

    //Helper Method to load Images
    @SuppressLint("StaticFieldLeak")
    private void loadImage(boolean backDrop) {

        if (backDrop) {

            String path = movieDetail.getBackDrop_path();
            String url = NetworkUtils.buildURL_for_Image(path, true).toString();

            int width = getDisplaySize();
            int height = (int) getResources().getDimension(R.dimen.movie_backDrop_height);

            Picasso.with(getApplicationContext())
                    .load(url)
                    .error(getResources().getDrawable(R.drawable.error_bk))
                    .into(imageView_backdrop);


        } else {

            String path = movieDetail.getPoster_path();
            String url = NetworkUtils.buildURL_for_Image(path, false).toString();

            int width = (int) getResources().getDimension(R.dimen.movie_poster_width);
            int height = (int) getResources().getDimension(R.dimen.movie_poster_height);
            Picasso.with(this)
                    .load(url)
                    .resize(width, height)
                    .error(getResources().getDrawable(R.drawable.error_bk))
                    .into(imageView_poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportStartPostponedEnterTransition();
                        }
                    });
        }
    }

    // Helper Method to start Loader for MovieDetails2
    private void runMovieDetail2_LoaderManager(int movieID) {

        // Parsing data into bundle
        Bundle movieDetailBundle = new Bundle();
        movieDetailBundle.putInt(MOVIE_DETAIL_QUERY_EXTRA, movieID);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> movieLoader = loaderManager.getLoader(MOVIE_DETAIL_QUERY_LOADER_ID);

        // Check if loader is available already
        if (movieLoader == null) {
            /*loaderManager.initLoader(MOVIE_DETAIL_QUERY_LOADER_ID, movieDetailBundle, this).forceLoad();*/
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_QUERY_LOADER_ID,movieDetailBundle,movieDetailLoaderCallback()).forceLoad();
        } else {
            /*loaderManager.restartLoader(MOVIE_DETAIL_QUERY_LOADER_ID, movieDetailBundle, this).forceLoad();*/
            getSupportLoaderManager().restartLoader(MOVIE_DETAIL_QUERY_LOADER_ID,movieDetailBundle,movieDetailLoaderCallback()).forceLoad();
        }

    }

    // Helper Method to start Loader for Movie Language
    private void runLanguageQuery_LoaderManager(String movieLanguageCode) {

        // Parsing data into bundle
        Bundle movieLangBundle = new Bundle();
        movieLangBundle.putString(MOVIE_LANGUAGE_QUERY_EXTRA , movieLanguageCode);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLanguageLoader = loaderManager.getLoader(MOVIE_LANGUAGE_QUERY_LOADER_ID);

        // Check if loader is available already
        if (movieLanguageLoader == null) {
            getSupportLoaderManager().initLoader(MOVIE_LANGUAGE_QUERY_LOADER_ID, movieLangBundle, languageLoaderCallback()).forceLoad();
        } else {
            getSupportLoaderManager().initLoader(MOVIE_LANGUAGE_QUERY_LOADER_ID, movieLangBundle, languageLoaderCallback()).forceLoad();
        }

    }

    // Helper Method to start Loader for Cast List
    private void runCastDetails_LoaderManager(int movieID){

        // Parsing data into bundle
        Bundle castDetailsBundle = new Bundle();
        castDetailsBundle.putInt(CAST_DETAILS_QUERY_EXTRA, movieID);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Integer> castDetailsLoader = loaderManager.getLoader(CAST_DETAILS_QUERY_LOADER_ID);

        // Check if loader is available already
        if (castDetailsLoader == null) {
            getSupportLoaderManager().initLoader(CAST_DETAILS_QUERY_LOADER_ID,
                    castDetailsBundle,
                    castDetailsLoaderCallbacks()).forceLoad();
        } else {
            getSupportLoaderManager().initLoader(CAST_DETAILS_QUERY_LOADER_ID,
                    castDetailsBundle,
                    castDetailsLoaderCallbacks()).forceLoad();
        }
    }

    // Helper Method to get the Display width
    public int getDisplaySize(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return pxToDp(displayMetrics.widthPixels);
    }

    // Helper Metho to get the px to dp
    public int pxToDp(int px){
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    //Helper Method to set RecyclerView for Cast List
    private void setCastRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),0,false);

        recyclerView_casts.setLayoutManager(linearLayoutManager);
        recyclerView_casts.setHasFixedSize(true);
        castListAdapter = new CastListAdapter(castDetails,castDetails.size());
        recyclerView_casts.setAdapter(castListAdapter);
    }

    //Helper Method to set UI Components using MovieDetails2 object
    private void setUIComponentsUsingMovieDetails2(MovieDetail2 data) {

        if (null == data){
            return;
        }
        if (data.getGenres() != null) {
            //Displaying genres from MovieDetail2
            for (int i = 0; i < data.getGenres().size(); i++) {
                textView_generes.append(data.getGenres().get(i) + " ");
            }

        } else {
            textView_generes.setText(R.string.no_data_available);
        }

        if (data.getTagline() != null && !data.getTagline().equals("")) {
            //Displaying Tagline from MovieDetail2
            textView_tagline.setText(data.getTagline());
        } else {
            textView_tagline.setText(R.string.no_tagline_available);
        }
    }
}