package ex.example.daweatherapp.services.openweather;

import lombok.Data;

@Data
public class Weather {
    private int id;
    private String main;
    private String description;
}