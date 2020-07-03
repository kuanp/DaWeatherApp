package ex.example.daweatherapp.services.unsplash;

import java.util.List;

import lombok.Data;

@Data
public class SearchResponse {
    private List<SearchEntry> results;
    private int total;
}
