package com.softgenix.Model;

import java.time.LocalDateTime;

public class Card {
    private int id;
    private String titulo;
    private String descripcion;
    private int columnaId;
    private LocalDateTime fechaCreacion;
    private int creadoPorUsuarioId;

    public Card() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getColumnaId() {
        return columnaId;
    }

    public void setColumnaId(int columnaId) {
        this.columnaId = columnaId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getCreadoPorUsuarioId() {
        return creadoPorUsuarioId;
    }

    public void setCreadoPorUsuarioId(int creadoPorUsuarioId) {
        this.creadoPorUsuarioId = creadoPorUsuarioId;
    }
}