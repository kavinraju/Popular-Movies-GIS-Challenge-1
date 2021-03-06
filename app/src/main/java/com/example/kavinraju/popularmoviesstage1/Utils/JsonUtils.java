package com.example.kavinraju.popularmoviesstage1.Utils;

import android.util.Log;

import com.example.kavinraju.popularmoviesstage1.Data_Model.CastDetails;
import com.example.kavinraju.popularmoviesstage1.Data_Model.MovieDetail;
import com.example.kavinraju.popularmoviesstage1.Data_Model.MovieDetail2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

 // This class converts Json String into Objects
public class JsonUtils {
    

    public static ArrayList<MovieDetail> getMovieArrayList(String json){

        ArrayList<MovieDetail> movieDetails = new ArrayList<>();


        try {

            JSONObject jsonObject_main = new JSONObject(json);
            JSONArray jsonArray_results = jsonObject_main.getJSONArray("results");

            for (int i = 0; i < jsonArray_results.length() - 1; i++){

                JSONObject jsonObject_movie = jsonArray_results.getJSONObject(i);

                MovieDetail movieDetail = new MovieDetail();
                movieDetail.setId(jsonObject_movie.getInt("id"));
                movieDetail.setVoteAverage(jsonObject_movie.getDouble("vote_average"));
                movieDetail.setOriginalTitle(jsonObject_movie.getString("original_title"));
                movieDetail.setOriginal_language(jsonObject_movie.getString("original_language"));
                movieDetail.setReleaseDate(jsonObject_movie.getString("release_date"));
                movieDetail.setOverView(jsonObject_movie.getString("overview"));
                movieDetail.setPoster_path(jsonObject_movie.getString("poster_path"));
                movieDetail.setBackDrop_path(jsonObject_movie.getString("backdrop_path"));

                movieDetail.setPage(jsonObject_main.getInt("page"));
                movieDetail.setTotalPages(jsonObject_main.getInt("total_pages"));

                movieDetails.add(movieDetail);

            }


            return movieDetails;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static MovieDetail2 getMovieDetail2(String json){

        Log.d("MovieDetail2 JSON: ", json);

        MovieDetail2 MovieDetail2 = new MovieDetail2();
        ArrayList<String> genres = new ArrayList<>();

        try{

            JSONObject jsonObject_main = new JSONObject(json);
            JSONArray jsonArray_genres = jsonObject_main.getJSONArray("genres");
            for (int i = 0;  i< jsonArray_genres.length(); i++){
                JSONObject jsonObject_name = jsonArray_genres.getJSONObject(i);
                genres.add(jsonObject_name.getString("name"));
                Log.d("MovieDetail2 \"name:\"" , jsonObject_name.getString("name") );
            }
            MovieDetail2.setGenres(genres);
            MovieDetail2.setOriginal_language(jsonObject_main.getString("original_language"));
            MovieDetail2.setTagline(jsonObject_main.getString("tagline"));

            return MovieDetail2;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static HashMap<String , String> getLanguages(String json){

        HashMap<String,String> languages = new HashMap<>();

        try{
            JSONArray jsonArray_main = new JSONArray(json);
            for(int i = 0; i<jsonArray_main.length(); i++){
                JSONObject jsonObject_lang = jsonArray_main.getJSONObject(i);
                languages.put(jsonObject_lang.getString("iso_639_1"),jsonObject_lang.getString("english_name"));
            }
            return languages;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<CastDetails> getCastDetails(String json){

        ArrayList<CastDetails> castDetails = new ArrayList<>();
        try{
            JSONObject jsonObject_main = new JSONObject(json);
            JSONArray jsonArray_cast = jsonObject_main.getJSONArray("cast");
            for (int i = 0; i< jsonArray_cast.length(); i++){
                JSONObject jsonObject_cast = jsonArray_cast.getJSONObject(i);

                CastDetails castDetail = new CastDetails();

                castDetail.setCast_id(jsonObject_cast.getInt("cast_id"));
                castDetail.setCharacter(jsonObject_cast.getString("character"));
                castDetail.setCredit_id(jsonObject_cast.getString("credit_id"));
                castDetail.setGender(jsonObject_cast.getInt("gender"));
                castDetail.setId(jsonObject_cast.getInt("id"));
                castDetail.setName(jsonObject_cast.getString("name"));
                castDetail.setOrder(jsonObject_cast.getInt("order"));
                castDetail.setProfile_path(jsonObject_cast.getString("profile_path"));

                castDetails.add(castDetail);
            }

            return castDetails;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
