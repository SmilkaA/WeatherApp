package com.example.weatherapp.model.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityData {
    @SerializedName("city")
    @Expose
    public String city;

    @SerializedName("country")
    @Expose
    public String country;

    @SerializedName("populationCounts")
    @Expose
    public List<CityPopulationCount> populationCounts;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<CityPopulationCount> getPopulationCounts() {
        return populationCounts;
    }

    public void setPopulationCounts(List<CityPopulationCount> populationCounts) {
        this.populationCounts = populationCounts;
    }
}
