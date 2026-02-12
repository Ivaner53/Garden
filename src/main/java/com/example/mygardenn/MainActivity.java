package com.example.mygardenn;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Списки для хранения данных (вместо базы данных)
    private ArrayList<Plant> plantList = new ArrayList<>();
    private PlantAdapter plantAdapter;
    private RecyclerView plantsRecyclerView;

        // ... другие переменные


        // Добавьте эту строку:
        private LinearLayout calendarLayout;
        // ... остальной код

    // Элементы для добавления растения
    private ScrollView addPlantLayout; // ИЗМЕНИТЕ ТИП НА ScrollView
    private EditText etPlantName, etWateringFrequency;
    private Button btnPlantingDate, btnSavePlant;
    private Spinner spinnerSunlight;
    private FrameLayout fragmentContainer;
    private TextView tvSelectedDate;
    private Calendar plantingCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация всех элементов
        initViews();

        // Загрузка тестовых данных
        loadSamplePlants();

        // Настройка RecyclerView
        setupRecyclerView();

        // Настройка Spinner для освещения
        setupSpinner();

        // Настройка обработчиков событий
        setupEventListeners();
    }

    private void initViews() {
        plantsRecyclerView = findViewById(R.id.plants_recycler_view);

        // Элементы для добавления растения (изначально скрыты)
        // Было:
// LinearLayout addPlantLayout = findViewById(R.id.add_plant_layout);

// Стало:
        // Это нужно добавить, если её нет:
        calendarLayout = findViewById(R.id.calendar_layout);
        addPlantLayout = findViewById(R.id.add_plant_layout);
        etPlantName = findViewById(R.id.et_plant_name);
        etWateringFrequency = findViewById(R.id.et_watering_frequency);
        btnPlantingDate = findViewById(R.id.btn_planting_date);
        btnSavePlant = findViewById(R.id.btn_save_plant);
        spinnerSunlight = findViewById(R.id.spinner_sunlight);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        fragmentContainer = findViewById(R.id.fragment_container);
        // Кнопки навигации
        Button btnShowAddPlant = findViewById(R.id.btn_show_add_plant);
        Button btnShowCalendar = findViewById(R.id.btn_show_calendar);
        Button btnShowPlants = findViewById(R.id.btn_show_plants);

        // Обработчики кнопок навигации
        btnShowAddPlant.setOnClickListener(v -> showAddPlantScreen());
        btnShowCalendar.setOnClickListener(v -> showCalendarScreen());
        btnShowPlants.setOnClickListener(v -> showPlantsList());
    }

    private void loadSamplePlants() {
        // Добавляем несколько тестовых растений
        plantList.add(new Plant("Томат Черри", "12.05.2024", 2, "Полное солнце"));
        plantList.add(new Plant("Огурцы", "01.06.2024", 1, "Полное солнце"));
        plantList.add(new Plant("Фикус", "15.03.2024", 7, "Частичная тень"));
        plantList.add(new Plant("Розы", "20.04.2024", 3, "Полное солнце"));
    }

    private void setupRecyclerView() {
        plantAdapter = new PlantAdapter(plantList, new PlantAdapter.OnPlantClickListener() {
            @Override
            public void onPlantClick(Plant plant) {
                // Показываем детали растения
                showPlantDetails(plant);
            }

            @Override
            public void onWaterPlant(Plant plant) {
                // Отмечаем полив
                markPlantAsWatered(plant);
            }
        });

        plantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantsRecyclerView.setAdapter(plantAdapter);
    }

    private void setupSpinner() {
        // Настройка выпадающего списка для освещения
        String[] sunlightOptions = {"Полное солнце", "Частичная тень", "Полная тень"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sunlightOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSunlight.setAdapter(adapter);
    }

    private void setupEventListeners() {
        // Кнопка выбора даты посадки
        btnPlantingDate.setOnClickListener(v -> showDatePickerDialog());

        // Кнопка сохранения растения
        btnSavePlant.setOnClickListener(v -> saveNewPlant());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        plantingCalendar.set(year, month, day);
                        String dateStr = String.format(Locale.getDefault(), "%02d.%02d.%d", day, month + 1, year);
                        tvSelectedDate.setText("Дата посадки: " + dateStr);
                        tvSelectedDate.setVisibility(View.VISIBLE);
                    }
                },
                plantingCalendar.get(Calendar.YEAR),
                plantingCalendar.get(Calendar.MONTH),
                plantingCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveNewPlant() {
        String name = etPlantName.getText().toString().trim();
        String wateringStr = etWateringFrequency.getText().toString().trim();

        // Проверка заполнения полей
        if (name.isEmpty()) {
            etPlantName.setError("Введите название растения");
            return;
        }
        if (wateringStr.isEmpty()) {
            etWateringFrequency.setError("Введите частоту полива");
            return;
        }

        try {
            int wateringFrequency = Integer.parseInt(wateringStr);
            String sunlight = spinnerSunlight.getSelectedItem().toString();
            String plantingDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());

            // Создаем новое растение
            Plant newPlant = new Plant(name, plantingDate, wateringFrequency, sunlight);
            plantList.add(0, newPlant); // Добавляем в начало списка
            plantAdapter.notifyItemInserted(0);

            // Показываем сообщение об успехе
            Toast.makeText(this, "Растение добавлено!", Toast.LENGTH_SHORT).show();

            // Очищаем поля и возвращаемся к списку
            clearAddPlantForm();
            showPlantsList();

        } catch (NumberFormatException e) {
            etWateringFrequency.setError("Введите число");
        }
    }

    private void clearAddPlantForm() {
        etPlantName.setText("");
        etWateringFrequency.setText("");
        tvSelectedDate.setVisibility(View.GONE);
        spinnerSunlight.setSelection(0);
    }

    private void showPlantDetails(Plant plant) {
        // Скрываем основные элементы

            if (plant == null) {
                Toast.makeText(this, "Растение не выбрано", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("MainActivity", "Открываем детали растения: " + plant.getName());
            // ... остальной код открытия фрагмента

        if (addPlantLayout != null) addPlantLayout.setVisibility(View.GONE);
        if (plantsRecyclerView != null) plantsRecyclerView.setVisibility(View.GONE);
        if (calendarLayout != null) calendarLayout.setVisibility(View.GONE);

        // Показываем контейнер для фрагмента
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);

            // Создаём и отображаем фрагмент
            PlantDetailFragment fragment = PlantDetailFragment.newInstance(plant);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("plant_details") // Укажем имя для стека
                    .commit();
        } else {
            // Если контейнер не найден, покажем Toast (как запасной вариант)
            Toast.makeText(this, "Ошибка отображения деталей", Toast.LENGTH_SHORT).show();
        }
    }

    private void markPlantAsWatered(Plant plant) {
        plant.setLastWatered(new Date());
        plantAdapter.notifyDataSetChanged();
        Toast.makeText(this, plant.getName() + " полито!", Toast.LENGTH_SHORT).show();
    }

    // Методы для навигации между экранами
    void showPlantsList() {
        // Проверка на null для безопасности
        if (addPlantLayout != null) addPlantLayout.setVisibility(View.GONE);
        if (plantsRecyclerView != null) plantsRecyclerView.setVisibility(View.VISIBLE);
        if (calendarLayout != null) calendarLayout.setVisibility(View.GONE);
        if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
    }

    private void showAddPlantScreen() {
        addPlantLayout.setVisibility(View.VISIBLE);
        plantsRecyclerView.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.GONE);
    }

    private void showCalendarScreen() {
        addPlantLayout.setVisibility(View.GONE);
        plantsRecyclerView.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.VISIBLE);
        updateCalendarTasks();
    }

    private void updateCalendarTasks() {
        TextView tvTasks = findViewById(R.id.tv_calendar_tasks);
        StringBuilder tasks = new StringBuilder("Задачи на сегодня:\n\n");

        for (Plant plant : plantList) {
            tasks.append("• Полить: ").append(plant.getName()).append("\n");
        }

        if (plantList.isEmpty()) {
            tasks.append("Нет задач на сегодня");
        }

        tvTasks.setText(tasks.toString());
    }
}

// Класс модели Plant
class Plant {
    private String name;
    private String plantingDate;
    private int wateringFrequency; // дни между поливами
    private String sunlight;
    private Date lastWatered;

    public Plant(String name, String plantingDate, int wateringFrequency, String sunlight) {
        this.name = name;
        this.plantingDate = plantingDate;
        this.wateringFrequency = wateringFrequency;
        this.sunlight = sunlight;
        this.lastWatered = new Date(); // сегодня
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPlantingDate() { return plantingDate; }
    public void setPlantingDate(String plantingDate) { this.plantingDate = plantingDate; }

    public int getWateringFrequency() { return wateringFrequency; }
    public void setWateringFrequency(int wateringFrequency) { this.wateringFrequency = wateringFrequency; }

    public String getSunlight() { return sunlight; }
    public void setSunlight(String sunlight) { this.sunlight = sunlight; }

    public Date getLastWatered() { return lastWatered; }
    public void setLastWatered(Date lastWatered) { this.lastWatered = lastWatered; }
}