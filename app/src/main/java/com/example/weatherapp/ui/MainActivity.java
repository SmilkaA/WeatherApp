package com.example.weatherapp.ui;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.model.Coord;
import com.example.weatherapp.model.MainModel;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.Wind;
import com.example.weatherapp.networking.GpsTracker;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView temperature, weather_main, date, windSpeed, humidity, tempFeelsLike;
    private EditText input;
    private ImageView weatherPicture;
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        input = findViewById(R.id.search_text);
        ImageView imageView = findViewById(R.id.image_search);
        imageView.setOnClickListener(view -> {
            hideVirtualKeyboard();
            getCurrentWeather(String.valueOf(input.getText()));
        });
    }

    private void getCurrentWeather(String cityName) {
        if (!String.valueOf(input.getText()).matches("")) {
            AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.CurrentWeather + "q=" + cityName + WeatherAPI.UnitsAppid)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(JSONObject response) {
                            getCurrentWeatherOnResponse(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), "Incorrect city name!", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            gpsTracker = new GpsTracker(MainActivity.this);
            double latitude = 0, longitude = 0;
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            } else {
                gpsTracker.showSettingsAlert();
            }
            AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.CurrentWeather + "lat=" + latitude + "&lon=" + longitude + WeatherAPI.UnitsAppid)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(JSONObject response) {
                            getCurrentWeatherOnResponse(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), "Incorrect city name!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void getForecast() {
    }

    public void getCurrentWeatherOnResponse(JSONObject response) {
        Gson gson = new Gson();
        Coord coord = null;
        Weather weather;
        MainModel mainModel;
        Wind wind;

        try {
            coord = gson.fromJson(response.getJSONObject("coord").toString(), Coord.class);
            weather = gson.fromJson(response.getJSONArray("weather").getJSONObject(0).toString(), Weather.class);
            mainModel = gson.fromJson(response.getJSONObject("main").toString(), MainModel.class);
            wind = gson.fromJson(response.getJSONObject("wind").toString(), Wind.class);

            temperature = findViewById(R.id.temperature);
            temperature.setText(mainModel.getTemp() + "°C");

            weather_main = findViewById(R.id.weather_main);
            weather_main.setText(weather.getMain());

            date = findViewById(R.id.date);
            date.setText(getToday());

            humidity = findViewById(R.id.humidity);
            humidity.setText("Humidity: " + mainModel.getHumidity());

            tempFeelsLike = findViewById(R.id.Temp_feels_like);
            tempFeelsLike.setText("Temperature feelings: " + new DecimalFormat("##.##").format(mainModel.getFeelsLike()));

            windSpeed = findViewById(R.id.wind_speed);
            windSpeed.setText("Wind speed: " + wind.getSpeed().toString());

            weatherPicture = findViewById(R.id.weather_image);
            Glide.with(getApplicationContext())
                    .load(WeatherAPI.IMAGEURL + weather.getIcon() + WeatherAPI.ImageCode)
                    .into(weatherPicture);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Here is your weather!", Toast.LENGTH_SHORT).show();
    }

    private String getToday() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") String formatedDate = new SimpleDateFormat("dd MMM yyyy").format(date);
        return formatedDate;
    }

    private void hideVirtualKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }
}