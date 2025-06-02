package com.example.lab5_20211688;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab5_20211688.databinding.ActivityConfigBinding;

public class ConfigActivity extends AppCompatActivity {

    private ActivityConfigBinding binding;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_NAME = "nombre_usuario";
    private static final String KEY_MSG = "mensaje_motivacional";
    private static final String KEY_FREQ = "frecuencia_motivacional_horas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        binding.etNombre.setText(preferences.getString(KEY_NAME, ""));
        binding.etMensaje.setText(preferences.getString(KEY_MSG, ""));


        int frecuenciaGuardada = preferences.getInt(KEY_FREQ, -1);
        if (frecuenciaGuardada != -1) {
            binding.etFrecuenciaMotivacional.setText(String.valueOf(frecuenciaGuardada));
        } else {
            binding.etFrecuenciaMotivacional.setHint("Frecuencia en horas");
        }


        binding.btnGuardarMotivacional.setOnClickListener(v -> {
            String nuevoNombre = binding.etNombre.getText().toString().trim();
            String nuevoMensaje = binding.etMensaje.getText().toString().trim();
            String frecuenciaStr = binding.etFrecuenciaMotivacional.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevoMensaje.isEmpty() || frecuenciaStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int frecuenciaHoras;
            try {
                frecuenciaHoras = Integer.parseInt(frecuenciaStr);
                if (frecuenciaHoras <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Frecuencia inválida", Toast.LENGTH_SHORT).show();
                return;
            }

            preferences.edit()
                    .putString(KEY_NAME, nuevoNombre)
                    .putString(KEY_MSG, nuevoMensaje)
                    .putInt(KEY_FREQ, frecuenciaHoras)
                    .apply();

            programarNotificacionMotivacional(nuevoMensaje, frecuenciaHoras);

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void programarNotificacionMotivacional(String mensaje, int frecuenciaHoras) {
        Intent intent = new Intent(this, Motivacional.class);
        intent.putExtra("mensaje", mensaje);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                9999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long primerInicio = System.currentTimeMillis() + 5000; // 5 segs para pruebas

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    primerInicio,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    primerInicio,
                    pendingIntent
            );
        }
    }

}
