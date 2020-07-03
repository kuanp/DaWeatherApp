package ex.example.daweatherapp;

import dagger.Component;
import dagger.Module;

@Component(modules = ClientModule.class)
public interface WeatherApplicationComponent {
    void inject(MainActivity mainActivity);
}
