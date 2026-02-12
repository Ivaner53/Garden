package com.example.mygardenn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    private ArrayList<Plant> plantList;
    private OnPlantClickListener listener;

    public interface OnPlantClickListener {
        void onPlantClick(Plant plant);
        void onWaterPlant(Plant plant);
    }

    public PlantAdapter(ArrayList<Plant> plantList, OnPlantClickListener listener) {
        this.plantList = plantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plant_item, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);

        holder.tvPlantName.setText(plant.getName());
        holder.tvPlantingDate.setText("Посажено: " + plant.getPlantingDate());
        holder.tvSunlight.setText("Освещение: " + plant.getSunlight());

        // Рассчитываем следующий полив
        Date nextWatering = calculateNextWatering(plant);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        holder.tvNextWatering.setText("След. полив: " + sdf.format(nextWatering));

        // Обработчик клика на карточке
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlantClick(plant);
            }
        });

        // Обработчик клика на кнопке полива
        holder.btnWater.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWaterPlant(plant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    private Date calculateNextWatering(Plant plant) {
        Date lastWatered = plant.getLastWatered();
        if (lastWatered == null) {
            lastWatered = new Date();
        }

        long nextWateringTime = lastWatered.getTime() +
                (long) plant.getWateringFrequency() * 24 * 60 * 60 * 1000;
        return new Date(nextWateringTime);
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlantName, tvPlantingDate, tvNextWatering, tvSunlight;
        ImageView btnWater;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlantName = itemView.findViewById(R.id.tv_plant_name);
            tvPlantingDate = itemView.findViewById(R.id.tv_planting_date);
            tvNextWatering = itemView.findViewById(R.id.tv_next_watering);
            tvSunlight = itemView.findViewById(R.id.tv_sunlight);
            btnWater = itemView.findViewById(R.id.btn_water);
        }
    }
}