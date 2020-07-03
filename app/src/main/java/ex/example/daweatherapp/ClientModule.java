package ex.example.daweatherapp;

import android.content.res.Resources;
import android.util.Log;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ex.example.daweatherapp.services.openweather.City;
import ex.example.daweatherapp.services.openweather.CityJsonParser;
import ex.example.daweatherapp.services.openweather.OpenWeatherService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ClientModule {
    private InputStream citiesInputStream;

    public ClientModule(InputStream citiesInputStream) {
        this.citiesInputStream = citiesInputStream;
    }

    @Provides
    @Named("knownCities")
    public List<String> provideKnownCities() {
        // super heavy. Probably should do this on a background thread...
        List<City> cities = CityJsonParser.parseCitiesFromJSONFile(citiesInputStream);
        Log.i("TT", "Num:" + cities.size());
        return cities.parallelStream()
                .map(city -> city.getName())
                .collect(Collectors.toList());
    }

    @Provides
    @Named("openWeatherAppId")
    public String provideOpenWeatherAppId() {
        return BuildConfig.WEATHER_MAP_KEY;
    }

    @Provides
    public OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Inject
    public OpenWeatherService providesOpenWeatherService(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(OpenWeatherService.class);
    }
}
