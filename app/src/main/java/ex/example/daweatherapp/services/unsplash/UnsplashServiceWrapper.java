package ex.example.daweatherapp.services.unsplash;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import retrofit2.http.Url;

public class UnsplashServiceWrapper {
    private UnsplashService unsplashService;
    private static final String TAG = "UnsplashServiceWrapper";

    @Inject
    public UnsplashServiceWrapper(UnsplashService unsplashService) {
        this.unsplashService = unsplashService;
    }

    public Bitmap getImageForWeather(String weather) {
        URL url;
        try {
            url = new URL(getUrlForWeather(weather));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Failed to parse URL,", e);
            return null;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(TAG, "Failed to download photo", e);
            return null;
        }
    }

    private String getUrlForWeather(String weather) {
        try {
            SearchResponse response = unsplashService.getWeather(weather).execute().body();
            if (response != null && response.getResults() != null && response.getResults().size() > 0) {
                return response.getResults().get(0).getUrls().getRegular();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to search for photos", e);
        }
        return null;
    }
}
