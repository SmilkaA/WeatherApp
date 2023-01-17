package com.example.weatherapp.ui;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityMapsBinding;
import com.example.weatherapp.model.geo.CityData;
import com.example.weatherapp.model.geo.CityResponse;
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

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private TextView textView;
    private static ImageView weatherPicture;
    private static Gson gson;
    private final Integer MAP_ZOOM = 7;
    private static double countryLimit;
    private BitmapDrawable drawable;
    private Bitmap bitmap;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.5085, -0.1257), MAP_ZOOM));

        googleMap.setOnMapClickListener(point -> {
            getCurrentWeather(point.latitude, point.longitude);
            googleMap.clear();
            getCitiesMarkers();
            googleMap.addMarker(new MarkerOptions().position(point));
        });
    }

    private void getCurrentWeather(double lat, double lon) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.CURRENT_WEATHER + WeatherAPI.LAT + lat + WeatherAPI.LON + lon + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        gson = new Gson();
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
                        //setWeatherPicture(weatherPicture); - works here and
                        //weatherPicture.getDrawable() dont returns null later
                        //but pistures is the same
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void showCurrentWeather(String cityName) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.CURRENT_WEATHER + WeatherAPI.CITY_NAME + cityName + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        gson = new Gson();
                        WeatherResponse weatherResponse = gson.fromJson(response.toString(), WeatherResponse.class);

                        //dont work weatherPicture.getDrawable() is null somehow
//                        Glide.with(getApplicationContext())
//                                .load(WeatherAPI.IMAGE_URL + weatherResponse.getWeather().get(0).getIcon()
//                                        + WeatherAPI.IMAGE_CODE)
//                                .into(weatherPicture);
//                        setWeatherPicture(weatherPicture);
//
//                        drawable = (BitmapDrawable) weatherPicture.getDrawable();
//                        bitmap = drawable.getBitmap();
//
//                        googleMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(weatherResponse.getCoord().getLat(), weatherResponse.getCoord().getLon()))
//                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void getCitiesMarkers() {
        AndroidNetworking.get(GeoAPI.BASE_URL + GeoAPI.CITIES)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CityResponse cityResponse = gson.fromJson(response.toString(), CityResponse.class);
                        // because api doesn`t allow more calls per minute
                        // but in case of removing limit use commented part below
                        for (int i = 0; i < 10 /*cityResponse.getData().size()*/; i++) {
                            CityData cityData = cityResponse.getData().get(i);
                            try {
                                double population = Double.parseDouble(cityData.getPopulationCounts().get(0).getValue());
                                if (population > getPopulationLimit(cityData.getCountry())) {
                                    showCurrentWeather(cityData.getCity());
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private static double getPopulationLimit(String countryName) {
        AndroidNetworking.post(GeoAPI.BASE_URL)
                .addBodyParameter("country", countryName).setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CoutriesResponse countryResponse = gson.fromJson(response.toString(), CoutriesResponse.class);
                        List<CountryPopulationCount> countryPopulationCount = countryResponse.getData().getPopulationCounts();
                        try {
                            countryLimit = 0.1 * Double.parseDouble(countryPopulationCount.get(countryPopulationCount.size() - 1).getValue());
                            setCountryLimit(countryLimit);
                        } catch (Exception e) {
                            countryLimit = 1;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
        return countryLimit;
    }

    public static void setCountryLimit(double countryLimit) {
        MapsActivity.countryLimit = countryLimit;
    }

    public void setWeatherPicture(ImageView weatherPicture) {
        MapsActivity.weatherPicture = weatherPicture;
    }
}
