package com.example.weatherapp.networking;

import com.example.weatherapp.model.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPICalls {
    @GET("data/2.5/weather")
    Call<Response> getWeatherByCityName(
            @Query("q") String cityName,
            @Query("APPID") String apiKey
    );
}
