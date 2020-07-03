package ex.example.daweatherapp.services.openweather;

import java.util.List;

import lombok.Data;

@Data
public class WeatherResponse {
    private List<Weather> weather;
    private String name;
}
