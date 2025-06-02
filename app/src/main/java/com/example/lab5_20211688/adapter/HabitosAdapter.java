package com.example.lab5_20211688.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20211688.R;
import com.example.lab5_20211688.model.Habito;
import com.google.gson.Gson;

import java.util.List;

public class HabitosAdapter extends RecyclerView.Adapter<HabitosAdapter.HabitoViewHolder> {

    private List<Habito> listaHabitos;

    public HabitosAdapter(List<Habito> listaHabitos) {
        this.listaHabitos = listaHabitos;
    }

    @NonNull
    @Override
    public HabitoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitoViewHolder holder, int position) {
        Habito habito = listaHabitos.get(position);
        holder.tvNombre.setText(habito.getNombre());
        holder.tvCategoria.setText("Categoría: " + habito.getCategoria());
        holder.tvFrecuencia.setText("Cada " + habito.getFrecuenciaHoras() + " horas");
        holder.tvFechaInicio.setText("Inicio: " + habito.getFechaHoraInicio());
    }

    @Override
    public int getItemCount() {
        return listaHabitos.size();
    }

    public class HabitoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvFrecuencia, tvFechaInicio;

        public HabitoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuencia);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicio);

            itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Eliminar hábito")
                        .setMessage("¿Seguro que deseas eliminar este hábito?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listaHabitos.remove(position);
                                notifyItemRemoved(position);
                                guardarListaEnPreferences(itemView.getContext());
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            });
        }

        private void guardarListaEnPreferences(Context context) {
            SharedPreferences prefs = context.getSharedPreferences("habitos_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String json = new Gson().toJson(listaHabitos);
            editor.putString("lista_habitos", json);
            editor.apply();
        }
    }

}
