package com.example.weatherapp.model.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryData {
    @SerializedName("country")
    @Expose
    public String country;

    @SerializedName("code")
    @Expose
    public String code;

    @SerializedName("iso3")
    @Expose
    public String iso3;

    @SerializedName("populationCounts")
    @Expose
    public List<CountryPopulationCount> populationCounts;

    public String getCountry() {
        return country;
    }

    public String getCode() {
        return code;
    }

    public String getIso3() {
        return iso3;
    }

    public List<CountryPopulationCount> getPopulationCounts() {
        return populationCounts;
    }
}
