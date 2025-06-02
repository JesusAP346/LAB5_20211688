package com.example.lab5_20211688;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Recordatorio extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String nombre = intent.getStringExtra("nombre");
        String categoria = intent.getStringExtra("categoria");
        int frecuencia = intent.getIntExtra("frecuencia", 24); // fallback

        String canalId = categoria.toLowerCase();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_recordaar)
                .setContentTitle("Recordatorio: " + nombre)
                .setContentText("¡Hora de realizar tu hábito de " + categoria + "!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());

        //  Reprogramar siguiente
        Intent newIntent = new Intent(context, Recordatorio.class);
        newIntent.putExtra("nombre", nombre);
        newIntent.putExtra("categoria", categoria);
        newIntent.putExtra("frecuencia", frecuencia);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                nombre.hashCode(),
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long intervalo = frecuencia * 60 * 60 * 1000L;
        long siguiente = System.currentTimeMillis() + intervalo;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, siguiente, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, siguiente, pendingIntent);
        }
    }
}

