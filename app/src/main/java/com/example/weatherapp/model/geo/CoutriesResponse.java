package com.example.weatherapp.model.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoutriesResponse {
    @SerializedName("error")
    @Expose
    public boolean error;

    @SerializedName("msg")
    @Expose
    public String msg;

    @SerializedName("data")
    @Expose
    public CountryData data;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CountryData getData() {
        return data;
    }

    public void setData(CountryData data) {
        this.data = data;
    }
}
