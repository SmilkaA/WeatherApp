package com.example.weatherapp.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.example.weatherapp.adapter.MainAdapter;
import com.example.weatherapp.model.weather.MainModel;
import com.example.weatherapp.model.weather.WeatherResponse;
import com.example.weatherapp.model.weather.Weather;
import com.example.weatherapp.model.weather.Wind;
import com.example.weatherapp.networking.GpsTracker;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView temperature, weather_main, date, humidity, tempFeelsLike, windSpeed;
    private ImageView weatherPicture, imageView;
    private EditText input;
    private Button mapButton;
    private RecyclerView recyclerView;
    private final String PREFERENCES_PATH = "com.example.weatherapp.preferences";
    private final String PREFERENCES_KEY = "cityName";
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        createNotificationChanel();
        initComponents();
        initRecyclerView();

        imageView.setOnClickListener(view -> {
            hideVirtualKeyboard();
            getCurrentWeather(String.valueOf(input.getText()));
            getForecast(String.valueOf(input.getText()));
        });

        mapButton.setOnClickListener(view -> startActivity(new Intent(this, MapsActivity.class)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFERENCES_KEY, input.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_PATH, Context.MODE_PRIVATE);
        String cityName = sharedPreferences.getString(PREFERENCES_KEY, "");
        input.setText(cityName);
        getCurrentWeather(input.getText().toString());
        getForecast(input.getText().toString());
    }

    private void checkPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        gson = new Gson();
        temperature = findViewById(R.id.temperature);
        weather_main = findViewById(R.id.weather_main);
        date = findViewById(R.id.date);
        humidity = findViewById(R.id.humidity);
        tempFeelsLike = findViewById(R.id.Temp_feels_like);
        windSpeed = findViewById(R.id.wind_speed);
        weatherPicture = findViewById(R.id.weather_image);
        input = findViewById(R.id.search_text);
        imageView = findViewById(R.id.image_search);
        mapButton = findViewById(R.id.map_button);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.weather_forecast_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(12);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Weather channel", "Weather", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void getCurrentWeather(String cityName) {
        if (!String.valueOf(input.getText()).matches("")) {
            getCurrentWeatherByCityName(cityName);
        } else {
            getCurrentWeatherByLatAndLon(getLatAndLonFromGPS());
        }
    }

    private void getCurrentWeatherByCityName(String cityName) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.CURRENT_WEATHER + WeatherAPI.CITY_NAME + cityName + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getCurrentWeatherOnResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), getString(R.string.city_name_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getCurrentWeatherByLatAndLon(LatLng latLon) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.CURRENT_WEATHER + WeatherAPI.LAT + latLon.latitude + WeatherAPI.LON + latLon.longitude + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getCurrentWeatherOnResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), getString(R.string.city_name_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getForecast(String cityName) {
        if (!String.valueOf(input.getText()).matches("")) {
            getForecastByCityName(cityName);
        } else {
            getForecastByLatAndLon(getLatAndLonFromGPS());
        }
    }

    private void getForecastByCityName(String cityName) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.FORECAST_WEATHER + WeatherAPI.CITY_NAME + cityName + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getForecastOnResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), getString(R.string.city_name_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getForecastByLatAndLon(LatLng latLon) {
        AndroidNetworking.get(WeatherAPI.BASE_URL + WeatherAPI.FORECAST_WEATHER + WeatherAPI.LAT + latLon.latitude + WeatherAPI.LON + latLon.longitude + WeatherAPI.UNITS_APPID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getForecastOnResponse(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), getString(R.string.city_name_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private LatLng getLatAndLonFromGPS() {
        GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = 0, longitude = 0;
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }
        return new LatLng(latitude, longitude);
    }

    @SuppressLint("SetTextI18n")
    public void getCurrentWeatherOnResponse(JSONObject response) {
        WeatherResponse weatherResponse = gson.fromJson(response.toString(), WeatherResponse.class);
        Weather weather = weatherResponse.getWeather().get(0);
        MainModel mainModel = weatherResponse.getMain();
        Wind wind = weatherResponse.getWind();

        temperature.setText(mainModel.getTemp() + getString(R.string.celsium));
        weather_main.setText(weatherResponse.getName() + " : " + weather.getMain());
        date.setText(getToday());
        humidity.setText(getString(R.string.humidity) + mainModel.getHumidity());
        tempFeelsLike.setText(getString(R.string.temp_feel) + new DecimalFormat("##.##").format(mainModel.getFeelsLike()));
        windSpeed.setText(getString(R.string.wind_speed) + wind.getSpeed().toString());

        Glide.with(getApplicationContext())
                .load(WeatherAPI.IMAGE_URL + weather.getIcon() + WeatherAPI.IMAGE_CODE)
                .into(weatherPicture);

        createNotification(weather, mainModel);
    }

    private void getForecastOnResponse(JSONObject response) {
        List<WeatherResponse> weatherResponse = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                weatherResponse.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), WeatherResponse.class));
            }
            MainAdapter adapter = new MainAdapter(weatherResponse, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
        }
    }

    private String getToday() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(date);
        return formattedDate;
    }

    private void hideVirtualKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }


    private void createNotification(Weather weather, MainModel mainModel) {
        NotificationCompat.Builder notificationComBuilder = new NotificationCompat.Builder(this, "Weather channel");
        notificationComBuilder.setContentTitle(getString(R.string.notification_title, weather.getDescription()));
        notificationComBuilder.setContentText(getString(R.string.notification_text, mainModel.getTemp().toString(), mainModel.getFeelsLike().toString()));
        notificationComBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifyPendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }
        notificationComBuilder.setContentIntent(notifyPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1, notificationComBuilder.build());
    }
}