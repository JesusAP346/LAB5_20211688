package com.example.lab5_20211688.model;

import java.io.Serializable;

public class Habito implements Serializable {
    private String nombre;
    private String categoria;
    private int frecuenciaHoras;
    private String fechaHoraInicio; // en milisegundos

    public Habito(String nombre, String categoria, int frecuenciaHoras, String fechaHoraInicio) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.frecuenciaHoras = frecuenciaHoras;
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public int getFrecuenciaHoras() { return frecuenciaHoras; }
    public String getFechaHoraInicio() { return fechaHoraInicio; }
}
