package ex.example.daweatherapp;

import android.app.Application;

import java.io.InputStream;

public class MyWeatherApplication extends Application {
    public WeatherApplicationComponent weatherApplicationComponent;
    public WeatherApplicationComponent initWeatherApplicationComponent(InputStream inputStream) {
        weatherApplicationComponent = DaggerWeatherApplicationComponent.builder()
                .clientModule(new ClientModule(inputStream))
                .build();
        return weatherApplicationComponent;
    }
}
