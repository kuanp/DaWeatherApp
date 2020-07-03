package ex.example.daweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import ex.example.daweatherapp.services.openweather.KnownCities;
import ex.example.daweatherapp.services.openweather.OpenWeatherServiceWrapper;
import ex.example.daweatherapp.services.openweather.WeatherResponse;
import ex.example.daweatherapp.services.unsplash.UnsplashServiceWrapper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView resultTextView;
    private AutoCompleteTextView cityInputTextView;
    private ConstraintLayout activityLayout;

    @Inject
    OpenWeatherServiceWrapper openWeatherServiceWrapper;

    @Inject
    UnsplashServiceWrapper unsplashServiceWrapper;

    @Inject
    KnownCities knownCities;

    private class GetWeatherAsyncTask extends AsyncTask<String, Void, WeatherResponse> {
        @Override
        protected WeatherResponse doInBackground(String... cities) {
            return openWeatherServiceWrapper.getWeather(cities[0]);
        }

        @Override
        protected void onPostExecute(WeatherResponse weatherResponse) {
            super.onPostExecute(weatherResponse);

            String cityName = weatherResponse.getName();
            String baseWeather = weatherResponse.getWeather().get(0).getMain();
            String weatherDesc = weatherResponse.getWeather().get(0).getMain();
            String weatherText = String.format("Weather in %s is mostly %s (%s)",
                    cityName,
                    baseWeather,
                    weatherDesc);
            resultTextView.setText(weatherText);
            resultTextView.setVisibility(View.VISIBLE);
            cityInputTextView.setText("");

            new GetPhotoAsyncTask().execute(cityName + " " + weatherDesc);
        }
    }

    private class GetPhotoAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... weathers) {
            return unsplashServiceWrapper.getImageForWeather(weathers[0]);
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            // TODO: implement animation effects
            activityLayout.setBackground(new BitmapDrawable(getResources(), image));
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

        activityLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
    }

    public void onSearchButtonClicked(View view) {
        String citySelected = cityInputTextView.getText().toString();
        if (knownCities.contains(citySelected)) {
            hideKeyboard(this);
            new GetWeatherAsyncTask().execute(cityInputTextView.getText().toString());
        } else {
            resultTextView.setText(citySelected + " is not a valid city.");
            resultTextView.setVisibility(View.VISIBLE);
        }
    }

    // utility method
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}