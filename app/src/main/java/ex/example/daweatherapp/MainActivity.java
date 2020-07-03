package ex.example.daweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import ex.example.daweatherapp.services.openweather.City;
import ex.example.daweatherapp.services.openweather.OpenWeatherService;
import ex.example.daweatherapp.services.openweather.OpenWeatherServiceWrapper;
import ex.example.daweatherapp.services.openweather.Weather;
import ex.example.daweatherapp.services.openweather.WeatherResponse;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView resultTextView;
    private AutoCompleteTextView cityInputTextView;

    @Inject
    OpenWeatherServiceWrapper openWeatherServiceWrapper;

    @Inject
    @Named("knownCities")
    List<String> knownCities;
    Set<String> knownCitiesSet;

    private class GetWeatherAsyncTask extends AsyncTask<String, Void, WeatherResponse> {
        @Override
        protected WeatherResponse doInBackground(String... cities) {
            return openWeatherServiceWrapper.getWeather(cities[0]);
        }

        @Override
        protected void onPostExecute(WeatherResponse weatherResponse) {
            super.onPostExecute(weatherResponse);
            String weatherText = String.format("Weather in %s is mostly %s (%s)",
                    weatherResponse.getName(),
                    weatherResponse.getWeather().get(0).getMain(),
                    weatherResponse.getWeather().get(0).getDescription());
            resultTextView.setText(weatherText);
            resultTextView.setVisibility(View.VISIBLE);
            cityInputTextView.clearComposingText();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyWeatherApplication) getApplicationContext())
                .initWeatherApplicationComponent(getResources().openRawResource(R.raw.smallercities))
                .inject(this);

        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setVisibility(View.INVISIBLE);

        cityInputTextView = findViewById(R.id.cityInputTextView);
        knownCitiesSet = new HashSet<>(knownCities);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public void onSearchButtonClicked(View view) {
        String citySelected = cityInputTextView.getText().toString();
        if (knownCitiesSet.contains(citySelected)) {
            new GetWeatherAsyncTask().execute(cityInputTextView.getText().toString());
        } else {
            resultTextView.setText(citySelected + " is not a valid city.");
        }
    }
}