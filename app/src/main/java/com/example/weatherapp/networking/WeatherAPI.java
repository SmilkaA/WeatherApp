package com.example.weatherapp.networking;

public class WeatherAPI {
    public static final String BASEURL = "http://api.openweathermap.org/data/2.5/";
    public static final String CurrentWeather = "weather?";
    public static final String ListWeather = "forecast?";
    public static final String Daily = "forecast/daily?";
    public static final String UnitsAppid = "&units=metric&appid=4a4697bee1747a834c2d866b2179dc6f";
    public static final String UnitsAppidDaily = "&units=metric&cnt=15&appid{YOUR APP ID}=4a4697bee1747a834c2d866b2179dc6f";
}
