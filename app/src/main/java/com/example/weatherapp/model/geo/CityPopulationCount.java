package com.example.weatherapp.model.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityPopulationCount {
    @SerializedName("year")
    @Expose
    public String year;

    @SerializedName("value")
    @Expose
    public String value;


    @SerializedName("sex")
    @Expose
    public String sex;


    @SerializedName("reliabilty")
    @Expose
    public String reliabilty;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getReliabilty() {
        return reliabilty;
    }

    public void setReliabilty(String reliabilty) {
        this.reliabilty = reliabilty;
    }
}
