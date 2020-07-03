package ex.example.daweatherapp.services.openweather;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Response;

public class OpenWeatherServiceWrapper {
    private OpenWeatherService openWeatherService;
    private String openWeatherAppId;

    private static final String TAG = "WeatherWrapper";

    @Inject
    public OpenWeatherServiceWrapper(OpenWeatherService openWeatherService, @Named("openWeatherAppId") String openWeatherAppId) {
        this.openWeatherService = openWeatherService;
        this.openWeatherAppId = openWeatherAppId;
    }

    public WeatherResponse getWeather(String city) {
        try {
            return openWeatherService.getWeather(city, openWeatherAppId)
                    .execute()
                    .body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get weathers", e);
            return null;
        }
    }
}
