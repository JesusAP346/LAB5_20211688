package com.example.lab5_20211688;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Motivacional extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String mensaje = intent.getStringExtra("mensaje");
        int frecuencia = prefs.getInt("frecuencia_motivacional", 6); // por defecto

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "motivacional")
                .setSmallIcon(R.drawable.ic_motivacion)
                .setContentTitle("Mensaje Motivacional")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());

        // Reprogramar próxima
        Intent newIntent = new Intent(context, Motivacional.class);
        newIntent.putExtra("mensaje", mensaje);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, 1000, newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long nextTime = System.currentTimeMillis() + frecuencia * 60 * 60 * 1000L;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, nextTime, pi);
        }
    }
}
