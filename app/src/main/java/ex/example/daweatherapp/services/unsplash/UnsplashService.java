package ex.example.daweatherapp.services.unsplash;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashService {
    @GET("/search/photos?orientation=portrait")
    Call<SearchResponse> getWeather(@Query("query") String searchQuery);
}
