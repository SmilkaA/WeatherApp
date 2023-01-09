package com.example.weatherapp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.weatherapp.model.MainModel;
import com.example.weatherapp.model.Response;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.Wind;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.weatherapp.databinding.ActivityMapsBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(point -> {
            googleMap.clear();
            Log.d("Map", "Map clicked" + point);
            googleMap.addMarker(new MarkerOptions().position(point)
                    .title(getCurrentWeather(point.latitude, point.longitude)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 7));
        });
    }

    private String getCurrentWeather(double lat, double lon) {
        String[] weatherMessage = new String[1];
        AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.CurrentWeather + "lat=" + lat + "&lon=" + lon + WeatherAPI.UnitsAppid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Response weatherResponse;

                        weatherResponse = gson.fromJson(response.toString(), Response.class);
                        weatherMessage[0] = "It`s " + weatherResponse.getWeather().get(0).getDescription() + " and " + weatherResponse.getMain().getTemp() + "°C in " + weatherResponse.getName();

                        Toast.makeText(getApplicationContext(), weatherMessage[0], Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });

        return weatherMessage[0];
    }
}
