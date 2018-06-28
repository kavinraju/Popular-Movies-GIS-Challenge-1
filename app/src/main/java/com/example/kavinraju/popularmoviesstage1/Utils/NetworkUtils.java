package com.example.kavinraju.popularmoviesstage1.Utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NetworkUtils {


    private static String BASE_URL = "https://api.themoviedb.org/3";  // This is the base URL
    private static String MOVIE = "movie";   // This is used as path for movie
    private static String API_KEY = "api_key";  // This is used as parameter key
    private static String LANGUAGE = "language";    // This is used as parameter key
    private static String LANGUAGES = "languages";    // This is used as parameter key
    private static String CONFIGURATION = "configuration";  // This is used as parameter key
    private static String PAGE = "page";    // This is used as parameter key
    private static String CREDITS = "credits";    // This is used as parameter key
    private static String api_key = "YOU_API_KEY"; // This is used as parameter value of api_key
    private static String language_english = "en-US";   // This is used as parameter value of language
    private static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p";
    private static String W_185 = "w185";
    private static String W_500 = "w780";




    /*
        Rubric
        <In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu>
     */
    // Helper Method to build URL for List of Movies using Movie Rating Type ( Sort Order ) and Page Num.
    public static URL buildURL_for_MoivesList(String movie_rating_type, int page){

        /*
        My intension is to generate URL of these types -
                https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1
                https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US&page=1
         */
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(MOVIE)
                    .appendPath(movie_rating_type)
                    .appendQueryParameter(API_KEY , api_key)
                    .appendQueryParameter(LANGUAGE , language_english)
                    .appendQueryParameter(PAGE , String.valueOf(page))
                    .build();
        Log.d("NetworkUtils: Uri: " , uri.toString());
        URL url = null;

        try{
            url = new URL(uri.toString());
            Log.d("NetworkUtils: URL: " , url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    // Helper Method to build URL for Image
    public static URL buildURL_for_Image(String path, boolean backdrop){

        /*
        We do this because by default path be like “ /nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg ”.
        Hence if we build URL with this we will have a extra space character %20 in the URL which leads to error.
        */

        path = path.replace("/","");

        Uri uri;

        if (backdrop){

            uri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                    .appendPath(W_500) // getting image of size w500
                    .appendPath(path)
                    .build();

        }else {
            uri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                    .appendPath(W_185)  // getting image of size w185
                    .appendPath(path)
                    .build();
        }

        URL url = null;
        try{
            url = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    // Helper Method to build URL for Movie Detail using passed Movie ID
    public static URL buildURL_for_MovieDetail(int id){

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(MOVIE)
                        .appendPath(String.valueOf(id))
                        .appendQueryParameter(API_KEY , api_key)
                        .appendQueryParameter(LANGUAGE , language_english)
                        .build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // Helper Method to build URL for lists of Language
    public static URL buildURL_for_movieLanguages(){

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(CONFIGURATION)
                .appendPath(LANGUAGES)
                .appendQueryParameter(API_KEY , api_key)
                .build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // Helper Method to build URL for lists of Cast Details
    public static URL buildURL_for_castDetails(int movieID){


        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(MOVIE)
                .appendPath(String.valueOf(movieID))
                .appendPath(CREDITS)
                .appendQueryParameter(API_KEY , api_key)
                .build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // Helper Method to get responce from the passed URL
    public static String getResponceFromURl(URL url) throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                            .url(url)
                            .build();

        Response response = okHttpClient.newCall(request).execute();

        assert response.body() != null;
        return response.body().string();
    }

}