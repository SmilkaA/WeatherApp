package com.example.weatherapp.model.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryPopulationCount {
    @SerializedName("year")
    @Expose
    public int year;

    @SerializedName("value")
    @Expose
    public String value;

    public int getYear() {
        return year;
    }

    public String getValue() {
        return value;
    }
}
