package com.example.weatherapp.ui;

import androidx.fragment.app.FragmentActivity;

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
import com.example.weatherapp.model.Response;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView textView;
    private ImageView weatherPicture;
    private final Integer MAP_ZOOM = 8;
    private Gson gson;

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
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMaxZoomPreference(MAP_ZOOM);
        googleMap.setMinZoomPreference(MAP_ZOOM);
        googleMap.setOnMapClickListener(point -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(point));
            getCurrentWeather(point.latitude, point.longitude);
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
                        Response weatherResponse = gson.fromJson(response.toString(), Response.class);

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
}
