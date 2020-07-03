package ex.example.daweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import javax.inject.Inject;

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
            // TODO: implement animation effects around background images
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
        cityInputTextView.setAdapter(
                new ArrayAdapter<String>(this, R.layout.autocomplete_textview, knownCities.getAsList()));
        cityInputTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (v.equals(cityInputTextView) &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
        activityLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
    }

    private void startSearch() {
        String citySelected = cityInputTextView.getText().toString();
        if (citySelected.isEmpty()) {
            showInvalidInputToast("Please enter a city.");
            return;
        }
        if (knownCities.contains(citySelected)) {
            hideKeyboard(this);
            new GetWeatherAsyncTask().execute(cityInputTextView.getText().toString());
        } else {
            Log.e(TAG, "Tried to search invalid city" + citySelected);
            showInvalidInputToast(citySelected + " is not a valid city.");
        }
    }

    private void showInvalidInputToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(),
                msg,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 1000);
        toast.show();
    }

    public void onSearchButtonClicked(View view) {
        startSearch();
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