package com.example.mygardenn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class PlantDetailFragment extends Fragment {

    // Ключи для передачи данных
    private static final String ARG_PLANT_NAME = "plant_name";
    private static final String ARG_PLANT_DATE = "plant_date";
    private static final String ARG_PLANT_WATER = "plant_water";
    private static final String ARG_PLANT_SUN = "plant_sun";

    private String plantName, plantDate, plantWater, plantSun;

    // Метод для создания фрагмента с данными
    public static PlantDetailFragment newInstance(Plant plant) {
        PlantDetailFragment fragment = new PlantDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLANT_NAME, plant.getName());
        args.putString(ARG_PLANT_DATE, plant.getPlantingDate());
        args.putString(ARG_PLANT_WATER, String.valueOf(plant.getWateringFrequency()));
        args.putString(ARG_PLANT_SUN, plant.getSunlight());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Только получаем аргументы, НЕ работаем с View здесь
        if (getArguments() != null) {
            plantName = getArguments().getString(ARG_PLANT_NAME);
            plantDate = getArguments().getString(ARG_PLANT_DATE);
            plantWater = getArguments().getString(ARG_PLANT_WATER);
            plantSun = getArguments().getString(ARG_PLANT_SUN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Надуваем макет фрагмента
        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);

        // Находим и заполняем текстовые поля
        TextView tvName = view.findViewById(R.id.tv_plant_name);
        TextView tvDate = view.findViewById(R.id.tv_planting_date);
        TextView tvWater = view.findViewById(R.id.tv_next_watering);
        TextView tvSun = view.findViewById(R.id.tv_sunlight);

        tvName.setText(plantName);
        tvDate.setText("Посажено: " + plantDate);
        tvWater.setText("Полив каждые: " + plantWater + " дней");
        tvSun.setText("Освещение: " + plantSun);

        // ТОЛЬКО ЗДЕСЬ работаем с кнопкой (после inflate)
        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Закрываем фрагмент
                requireActivity().getSupportFragmentManager().popBackStack();
                // Показываем список растений через метод MainActivity
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showPlantsList();
                }
            }
        });

        return view;
    }
}