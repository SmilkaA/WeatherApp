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
import com.example.weatherapp.model.MainModel;
import com.example.weatherapp.model.Response;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.Wind;
import com.example.weatherapp.networking.GpsTracker;
import com.example.weatherapp.networking.WeatherAPI;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText input;
    private RecyclerView recyclerView;
    private final String PREFERENCES_PATH = "com.example.weatherapp.preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        createNotificationChanel();
        initRecyclerView();

        input = findViewById(R.id.search_text);
        ImageView imageView = findViewById(R.id.image_search);
        imageView.setOnClickListener(view -> {
            hideVirtualKeyboard();
            getCurrentWeather(String.valueOf(input.getText()));
            getForecast(String.valueOf(input.getText()));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cityName", input.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_PATH, Context.MODE_PRIVATE);
        String cityName = sharedPreferences.getString("cityName", "");
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
            AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.CurrentWeather + "q=" + cityName + WeatherAPI.UnitsAppid)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
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
            GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
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

    private void getForecast(String cityName) {
        if (!String.valueOf(input.getText()).matches("")) {
            AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.ListWeather + "q=" + cityName + WeatherAPI.UnitsAppid)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            getForecastOnResponse(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), "Incorrect city name!", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
            double latitude = 0, longitude = 0;
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            } else {
                gpsTracker.showSettingsAlert();
            }
            AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.ListWeather + "lat=" + latitude + "&lon=" + longitude + WeatherAPI.UnitsAppid)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            getForecastOnResponse(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), "Incorrect city name!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    public void getCurrentWeatherOnResponse(JSONObject response) {
        Gson gson = new Gson();
        Weather weather;
        MainModel mainModel;
        Wind wind;

        try {
            weather = gson.fromJson(response.getJSONArray("weather").getJSONObject(0).toString(), Weather.class);
            mainModel = gson.fromJson(response.getJSONObject("main").toString(), MainModel.class);
            wind = gson.fromJson(response.getJSONObject("wind").toString(), Wind.class);

            TextView temperature = findViewById(R.id.temperature);
            temperature.setText(mainModel.getTemp() + "°C");

            TextView weather_main = findViewById(R.id.weather_main);
            weather_main.setText(response.get("name") + " : " + weather.getMain());

            TextView date = findViewById(R.id.date);
            date.setText(getToday());

            TextView humidity = findViewById(R.id.humidity);
            humidity.setText("Humidity: " + mainModel.getHumidity());

            TextView tempFeelsLike = findViewById(R.id.Temp_feels_like);
            tempFeelsLike.setText("Temperature feelings: " + new DecimalFormat("##.##").format(mainModel.getFeelsLike()));

            TextView windSpeed = findViewById(R.id.wind_speed);
            windSpeed.setText("Wind speed: " + wind.getSpeed().toString());

            ImageView weatherPicture = findViewById(R.id.weather_image);
            Glide.with(getApplicationContext())
                    .load(WeatherAPI.IMAGEURL + weather.getIcon() + WeatherAPI.ImageCode)
                    .into(weatherPicture);

            createNotification(weather, mainModel);
        } catch (JSONException ignored) {
        }
    }

    private void getForecastOnResponse(JSONObject response) {
        Gson gson = new Gson();
        List<Response> weatherResponse = new ArrayList<>();

        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                weatherResponse.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), Response.class));
            }
            MainAdapter adapter = new MainAdapter(weatherResponse, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error loading hourly forecast", Toast.LENGTH_SHORT).show();
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
        notificationComBuilder.setContentTitle("It`s " + weather.getDescription() + " in city");
        notificationComBuilder.setContentText("Temperature: " + mainModel.getTemp()
                + ", but it feels like: " + mainModel.getFeelsLike());
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