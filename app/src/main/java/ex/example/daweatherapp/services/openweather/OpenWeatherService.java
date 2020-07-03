package ex.example.daweatherapp.services.openweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherService {
    @GET("/data/2.5/weather")
    Call<WeatherResponse> getWeather(@Query("q") String city, @Query("appid") String appId);
}
