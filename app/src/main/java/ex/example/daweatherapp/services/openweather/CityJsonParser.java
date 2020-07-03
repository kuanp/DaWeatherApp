package ex.example.daweatherapp.services.openweather;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CityJsonParser {
    public static List<City> parseCitiesFromJSONFile(InputStream jsonInput) {
        InputStreamReader resourceReader = new InputStreamReader(jsonInput);
        Gson gson = new Gson();

        return gson.fromJson(resourceReader, new TypeToken<List<City>>(){}.getType());
    }
}
