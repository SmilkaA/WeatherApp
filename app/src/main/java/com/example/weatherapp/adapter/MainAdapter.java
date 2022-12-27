package com.example.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.model.MainModel;
import com.example.weatherapp.R;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    List<MainModel> items;
    Context context;

    public MainAdapter(List<MainModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_details, parent, false);
        MainAdapter.MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MyViewHolder holder, int position) {

        holder.humidity.setText(items.get(position).getHumidity());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        ImageView imageDetails;
        TextView weatherState;
        TextView windSpeed;
        TextView humidity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time_forecast);
            imageDetails = itemView.findViewById(R.id.image_forecast);
            weatherState = itemView.findViewById(R.id.weather_main_forecast);
            windSpeed = itemView.findViewById(R.id.wind_speed_forecast);
            humidity = itemView.findViewById(R.id.humidity_forecast);
        }
    }
}
