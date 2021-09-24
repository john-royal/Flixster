package com.codepath.johnroyal.flixster;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.johnroyal.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    private static final String TAG = "DetailActivity";
    private static final String YOUTUBE_API_KEY = "AIzaSyBdEqMyUJQ9Zh1pTv3nEn4G6Au75j0DcO8";

    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView playerView;

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvOverview = findViewById(R.id.tvDetailOverview);
        ratingBar = findViewById(R.id.ratingBar);
        playerView = findViewById(R.id.player);

        movie = (Movie) getIntent().getSerializableExtra("movie");

        configureSubviews();

        Log.d(TAG, "Showing detail for movie “" + movie.getTitle() + "” with rating " + movie.getRating());
    }

    private void configureSubviews() {
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating(movie.getRating());

        // Use the Movie Database API to find the YouTube video key representing the movie’s trailer.
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(movie.getVideosURL(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        Log.w(TAG, "Found no results for video search");
                        return;
                    }
                    String videoKey = results.getJSONObject(0).getString("key");
                    Log.d(TAG, "Found YouTube video with key: " + videoKey);

                    // Initialize the YouTubePlayerView with the video key we found.
                    initializePlayerViewWithKey(videoKey);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON: ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Movie API request failed");
            }
        });
    }

    private void initializePlayerViewWithKey(String videoKey) {
        DetailActivity self = this;
        playerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "Initialized YouTubePlayerView");
                if (movie.isPopular()) {
                    Log.d(TAG, "Movie is popular, playing trailer immediately");
                    youTubePlayer.loadVideo(videoKey);
                } else {
                    Log.d(TAG, "Movie is not popular, user must press play to view trailer");
                    youTubePlayer.cueVideo(videoKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                /**
                 * Lots of headaches caused here!
                 * See https://developers.google.com/youtube/android/player/reference/com/google/android/youtube/player/YouTubeInitializationResult#Summary for a full list of possible errors.
                 */
                Log.e(TAG, "Failed to initialize YouTubePlayerView: " + youTubeInitializationResult.toString());
                Log.i(TAG, youTubeInitializationResult.isUserRecoverableError() ? "This error is user recoverable." : "This error is NOT user recoverable.");
                Dialog dialog = youTubeInitializationResult.getErrorDialog(self, 0);
                dialog.show();
            }
        });
    }
}