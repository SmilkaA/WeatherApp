package com.example.weatherapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityMapsBinding;
import com.example.weatherapp.model.geo.CityData;
import com.example.weatherapp.model.geo.CityPopulationCount;
import com.example.weatherapp.model.geo.CityResponse;
import com.example.weatherapp.model.geo.CountryData;
import com.example.weatherapp.model.geo.CountryPopulationCount;
import com.example.weatherapp.model.geo.CoutriesResponse;
import com.example.weatherapp.model.weather.WeatherResponse;
import com.example.weatherapp.networking.GeoAPI;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private TextView textView;
    private ImageView weatherPicture;
    private Gson gson;
    private final Integer MAP_ZOOM = 7;
    private static List<CountryData> countries;
    private static List<String> cityNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponents();
    }

    private void initComponents() {
        textView = findViewById(R.id.weather_details_in_fragment);
        weatherPicture = findViewById(R.id.image_weather_details_in_fragment);

        gson = new Gson();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap1) {
        googleMap = googleMap1;
        getCountriesData();
        getCitiesData();

        googleMap.setOnMapClickListener(point -> {
            googleMap.clear();
            getCurrentWeather(point.latitude, point.longitude);
            googleMap.addMarker(new MarkerOptions().position(point));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, MAP_ZOOM));

            for (String cityName : cityNames) {
                getWeatherForCities(cityName);
            }
        });
    }


    private void getCurrentWeather(double lat, double lon) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.CURRENT_WEATHER + WeatherAPI.LAT + lat + WeatherAPI.LON + lon + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        WeatherResponse weatherResponse = gson.fromJson(response.toString(), WeatherResponse.class);

                        textView.setText(getString(R.string.weather_on_maps
                                , weatherResponse.getWeather().get(0).getDescription()
                                , weatherResponse.getMain().getTemp().toString()
                                , weatherResponse.getName()
                                , weatherResponse.getMain().getFeelsLike().toString()));

                        Glide.with(getApplicationContext())
                                .load(WeatherAPI.IMAGE_URL + weatherResponse.getWeather().get(0).getIcon()
                                        + WeatherAPI.IMAGE_CODE)
                                .into(weatherPicture);
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void getWeatherForCities(String cityName) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.CURRENT_WEATHER + WeatherAPI.CITY_NAME + cityName + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        WeatherResponse weatherResponse = gson.fromJson(response.toString(), WeatherResponse.class);
                        Glide.with(getApplicationContext()).asBitmap()
                                .load(WeatherAPI.IMAGE_URL + weatherResponse.getWeather().get(0).getIcon()
                                        + WeatherAPI.IMAGE_CODE)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(weatherResponse.getCoord().getLat(), weatherResponse.getCoord().getLon()))
                                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                                .title(weatherResponse.getMain().getTemp() + getString(R.string.celsium)));                                    }
                                });
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void getCitiesData() {
        AndroidNetworking.get(GeoAPI.BASE_URL + GeoAPI.CITIES)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CityResponse cityResponse = gson.fromJson(response.toString(), CityResponse.class);
                        List<CityData> cityData = cityResponse.getData();
                        cityNames = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            countries.forEach(countryData -> names.add(countryData.getCountry()));
                        }
                        for (CityData city : cityData) {
                            if (!city.getCity().equals("null")) {
                                List<CityPopulationCount> populationCounts = city.getPopulationCounts();
                                try {
                                    double cityPopulation = Double.parseDouble(populationCounts.get(populationCounts.size() - 1).getValue());

                                    int countryIndex = names.indexOf(city.getCountry());
                                    if (countryIndex != -1) {
                                        List<CountryPopulationCount> countryPopulationCount = countries.get(countryIndex).getPopulationCounts();
                                        double countryPopulationPercentage = 0.01 * Double.parseDouble(countryPopulationCount.get(countryPopulationCount.size() - 1).getValue());

                                        if (cityPopulation > countryPopulationPercentage) {
                                            cityNames.add(city.getCity());
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void getCountriesData() {
        AndroidNetworking.get(GeoAPI.BASE_URL)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CoutriesResponse countryResponse = gson.fromJson(response.toString(), CoutriesResponse.class);
                        countries = countryResponse.getData();
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }
}
