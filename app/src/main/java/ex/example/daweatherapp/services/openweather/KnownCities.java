package ex.example.daweatherapp.services.openweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Holds known cities; wrapper over Set<City> that ignores case
 */
public class KnownCities extends HashSet<String> {
    private List<String> originalStrings;

    // store in lower case
    public KnownCities(@NonNull Collection<? extends String> c) {
        super(c.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()));
        originalStrings = (List<String>) c;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        String lower = ((String) o).toLowerCase();
        return super.contains(lower);
    }

    public List<String> getAsList() {
        return originalStrings;
    }
}
