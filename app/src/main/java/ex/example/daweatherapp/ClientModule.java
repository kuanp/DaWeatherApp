package ex.example.daweatherapp;

import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
import ex.example.daweatherapp.services.unsplash.UnsplashService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ClientModule {
    private InputStream citiesInputStream;

    private class UnsplashAuthInterceptor implements Interceptor {
        private String accessToken;

        public UnsplashAuthInterceptor(String accessToken) {
            this.accessToken = accessToken;
        }

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Request modifiedRequest = request.newBuilder()
                    .addHeader("Authorization", "Client-ID " + accessToken)
                    .build();
            return chain.proceed(modifiedRequest);
        }
    }

    public ClientModule(InputStream citiesInputStream) {
        this.citiesInputStream = citiesInputStream;
    }

    @Provides
    @Inject
    public UnsplashService provideUnsplashService(@Named("UnsplashHttpClient") OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(UnsplashService.class);
    }

    @Provides
    @Inject
    @Named("UnsplashHttpClient")
    public OkHttpClient providesUnsplashHttpClient(@Named("unsplashAccessToken") String accessToken) {
        return new OkHttpClient.Builder()
                .addInterceptor(new UnsplashAuthInterceptor(accessToken))
                .build();
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
    @Named("unsplashAccessToken")
    public String provideUnsplashAccessToken() {
        return BuildConfig.UNSPLASH_KEY;
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
