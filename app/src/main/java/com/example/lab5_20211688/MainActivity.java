package com.example.lab5_20211688;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lab5_20211688.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_NAME = "nombre_usuario";
    private static final String KEY_MSG = "mensaje_motivacional";
    private static final String IMAGE_FILENAME = "imagen_perfil.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannels(); // canales de notificación
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        cargarSaludoYMensaje();
        cargarImagen();

        binding.ivFoto.setOnClickListener(v -> seleccionarImagen());
        binding.btnConfig.setOnClickListener(v -> startActivity(new Intent(this, ConfigActivity.class)));
        binding.verHabitos.setOnClickListener(v -> startActivity(new Intent(this, HabitosActivity.class)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                Toast.makeText(this, "Debes permitir alarmas exactas para recibir recordatorios a tiempo", Toast.LENGTH_LONG).show();
                startActivity(intent);
                return; // espera a que acepte
            }
        }

    }

    private void cargarSaludoYMensaje() {
        String nombre = preferences.getString(KEY_NAME, "Jesus");
        String mensaje = preferences.getString(KEY_MSG, "Ingresa un mensaje motivacional xd");

        binding.saludo.setText("¡Hola, " + nombre + "!");
        binding.mensajeMotivacional.setText(mensaje);
    }

    private void cargarImagen() {
        File file = new File(getFilesDir(), IMAGE_FILENAME);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            binding.ivFoto.setImageBitmap(bitmap);
        }
    }

    private void seleccionarImagen() {
        abrirGaleria.launch("image/*");
    }

    ActivityResultLauncher<String> abrirGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    guardarImagenEnStorage(uri);
                }
            }
    );

    private void guardarImagenEnStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), IMAGE_FILENAME);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            inputStream.close();
            outputStream.close();

            cargarImagen();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSaludoYMensaje();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificación concedido ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificación denegado ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria.launch("image/*");
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            createChannel(manager, "ejercicio", "Canal Ejercicio", NotificationManager.IMPORTANCE_HIGH);
            createChannel(manager, "alimentación", "Canal Alimentación", NotificationManager.IMPORTANCE_DEFAULT);
            createChannel(manager, "sueño", "Canal Sueño", NotificationManager.IMPORTANCE_LOW);
            createChannel(manager, "lectura", "Canal Lectura", NotificationManager.IMPORTANCE_DEFAULT);
            createChannel(manager, "motivacional", "Canal Motivacional", NotificationManager.IMPORTANCE_DEFAULT);


            askNotificationPermission();
        }
    }

    private void createChannel(NotificationManager manager, String id, String name, int importance) {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription("Notificaciones para " + name);
        channel.enableLights(true);
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);
    }


    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2001);
            }
        }
    }



}
