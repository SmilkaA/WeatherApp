package com.example.weatherapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.example.weatherapp.databinding.ActivityMapsBinding;
import com.example.weatherapp.model.Response;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(point -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(point));
            getCurrentWeather(point.latitude, point.longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 8));
        });
    }

    private void getCurrentWeather(double lat, double lon) {
        AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.CurrentWeather + "lat=" + lat + "&lon=" + lon + WeatherAPI.UnitsAppid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Response weatherResponse;

                        weatherResponse = gson.fromJson(response.toString(), Response.class);

                        String description = "It`s " + weatherResponse.getWeather().get(0).getDescription()
                                + " and " + weatherResponse.getMain().getTemp()
                                + "°C in " + weatherResponse.getName()
                                + ", but feels like " + weatherResponse.getMain().getFeelsLike();

                        TextView textView = findViewById(R.id.weather_details_in_fragment);
                        textView.setText(description);

                        ImageView weatherPicture = findViewById(R.id.image_weather_details_in_fragment);
                        Glide.with(getApplicationContext())
                                .load(WeatherAPI.IMAGEURL + weatherResponse.getWeather().get(0).getIcon()
                                        + WeatherAPI.ImageCode)
                                .into(weatherPicture);
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }
}
