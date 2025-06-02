package com.example.lab5_20211688;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab5_20211688.adapter.HabitosAdapter;
import com.example.lab5_20211688.databinding.ActivityHabitosBinding;
import com.example.lab5_20211688.model.Habito;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HabitosActivity extends AppCompatActivity {

    private ActivityHabitosBinding binding;
    private List<Habito> listaHabitos;
    private HabitosAdapter adapter;
    private SharedPreferences preferences;

    private static final String PREF_HABITOS = "habitos_prefs";
    private static final String KEY_LISTA = "lista_habitos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_HABITOS, Context.MODE_PRIVATE);
        listaHabitos = cargarHabitos();

        adapter = new HabitosAdapter(listaHabitos);
        binding.rvHabitos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHabitos.setAdapter(adapter);

        binding.fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, HabitoNuevoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listaHabitos.clear();
        listaHabitos.addAll(cargarHabitos());

        if (listaHabitos.isEmpty()) {
            binding.tvMensaje.setVisibility(View.VISIBLE);
        } else {
            binding.tvMensaje.setVisibility(View.GONE);
        }


        adapter.notifyDataSetChanged();
    }

    private List<Habito> cargarHabitos() {
        String json = preferences.getString(KEY_LISTA, null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Habito>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
