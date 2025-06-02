package com.example.lab5_20211688;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab5_20211688.databinding.ActivityHabitoNuevoBinding;
import com.example.lab5_20211688.model.Habito;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HabitoNuevoActivity extends AppCompatActivity {

    private ActivityHabitoNuevoBinding binding;
    private Calendar calendar;
    private SharedPreferences preferences;
    private static final String PREF_HABITOS = "habitos_prefs";
    private static final String KEY_LISTA = "lista_habitos";
    private SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitoNuevoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();
        preferences = getSharedPreferences(PREF_HABITOS, Context.MODE_PRIVATE);

        String[] categorias = {"Ejercicio", "Alimentación", "Sueño", "Lectura"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategoria.setAdapter(adapter);

        binding.btnFecha.setOnClickListener(v -> {
            DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                actualizarFechaHoraLabel();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        binding.btnHora.setOnClickListener(v -> {
            TimePickerDialog tp = new TimePickerDialog(this, (view, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                actualizarFechaHoraLabel();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            tp.show();
        });

        binding.btnGuardar.setOnClickListener(v -> guardarHabito());
    }

    private void actualizarFechaHoraLabel() {
        String fechaHora = formatoFechaHora.format(calendar.getTime());
        binding.tvFechaHora.setText("Inicio: " + fechaHora);
    }

    private void guardarHabito() {
        String nombre = binding.etNombre.getText().toString().trim();
        String categoria = binding.spCategoria.getSelectedItem().toString();
        String frecuenciaStr = binding.etFrecuencia.getText().toString().trim();
        String fechaHoraTexto = binding.tvFechaHora.getText().toString().replace("Inicio: ", "");

        if (nombre.isEmpty() || frecuenciaStr.isEmpty() || fechaHoraTexto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int frecuencia;
        try {
            frecuencia = Integer.parseInt(frecuenciaStr);
            if (frecuencia <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Frecuencia inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Habito nuevo = new Habito(nombre, categoria, frecuencia, fechaHoraTexto);

        List<Habito> lista = cargarHabitos();
        lista.add(nuevo);

        String json = new Gson().toJson(lista);
        preferences.edit().putString(KEY_LISTA, json).apply();

        programarRecordatorio(nuevo);
        Toast.makeText(this, "Hábito guardado con recordatorio", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void programarRecordatorio(Habito habito) {
        Intent intent = new Intent(this, Recordatorio.class);
        intent.putExtra("nombre", habito.getNombre());
        intent.putExtra("categoria", habito.getCategoria());
        intent.putExtra("frecuencia", habito.getFrecuenciaHoras()); // Para reprogramar

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                habito.getNombre().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            Date fechaInicio = formatoFechaHora.parse(habito.getFechaHoraInicio());
            long primerInicio = fechaInicio.getTime();

            // Si ya pasó la hora, programa dentro de 10 segundos (solo para pruebas)
            long ahora = System.currentTimeMillis();
            if (primerInicio < ahora) {
                primerInicio = ahora + 10_000;
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, primerInicio, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, primerInicio, pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private List<Habito> cargarHabitos() {
        String json = preferences.getString(KEY_LISTA, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Habito>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
