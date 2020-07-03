package ex.example.daweatherapp.services.unsplash;

import lombok.Data;

@Data
public class SearchEntry {
    private String id;
    private String description;
    private UrlData urls;
    private String color;
}
