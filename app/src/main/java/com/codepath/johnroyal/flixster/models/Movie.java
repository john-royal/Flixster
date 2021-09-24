package com.codepath.johnroyal.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Movie implements Serializable {
    private static final String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=%s";
    private static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=%s";

    Integer id;
    String title;
    String overview;
    float rating;
    String posterPath;

    public Movie(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = (float) jsonObject.getDouble("vote_average");
        posterPath = jsonObject.getString("poster_path");
    }

    public static List<Movie> fromJSONArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            JSONObject jsonObject = movieJsonArray.getJSONObject(i);
            Movie movie = new Movie(jsonObject);
            movies.add(movie);
        }
        return movies;
    }

    public static String getNowPlayingURL() {
        return String.format(NOW_PLAYING_URL, API_KEY);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterURL() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public float getRating() {
        return rating;
    }

    public String getVideosURL() {
        return String.format(VIDEOS_URL, id, API_KEY);
    }

    public boolean isPopular() {
        return rating > 5;
    }
}
