package com.example.weatherapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.model.Response;
import com.example.weatherapp.networking.WeatherAPI;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    List<Response> weatherResponse;
    Context context;

    public MainAdapter(List<Response> items, Context context) {
        this.weatherResponse = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_details, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MyViewHolder holder, int position) {
        Response currentWeather = weatherResponse.get(position);
        holder.time.setText(currentWeather.getDt());
        Glide.with(context)
                .load(WeatherAPI.IMAGE_URL + currentWeather.getWeather().get(0).getIcon() + WeatherAPI.IMAGE_CODE)
                .into(holder.imageDetails);
        holder.weatherState.setText(currentWeather.getMain().getTemp().toString() + context.getString(R.string.celsium));
        holder.windSpeed.setText(context.getString(R.string.wind_speed) + currentWeather.getWind().getSpeed().toString());
        holder.humidity.setText(context.getString(R.string.humidity) + currentWeather.getMain().getHumidity().toString());
    }

    @Override
    public int getItemCount() {
        return weatherResponse.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        ImageView imageDetails;
        TextView weatherState;
        TextView windSpeed;
        TextView humidity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time_forecast);
            imageDetails = itemView.findViewById(R.id.image_forecast);
            weatherState = itemView.findViewById(R.id.temperature_forecast);
            windSpeed = itemView.findViewById(R.id.wind_speed_forecast);
            humidity = itemView.findViewById(R.id.humidity_forecast);
        }
    }
}
