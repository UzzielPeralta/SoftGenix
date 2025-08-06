package com.softgenix.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Card {
    private int id;
    private String titulo;
    private String descripcion;
    private int columnaId;
    private LocalDateTime fechaCreacion;
    private int creadoPorUsuarioId;

    // Constantes para prioridades
    public static final String PRIORIDAD_ALTA = "Alta";
    public static final String PRIORIDAD_MEDIA = "Media";
    public static final String PRIORIDAD_BAJA = "Baja";

    public Card() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Card(String titulo, String descripcion, int columnaId, int creadoPorUsuarioId) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.columnaId = columnaId;
        this.creadoPorUsuarioId = creadoPorUsuarioId;
    }

    public Card(int id, String titulo, String descripcion, int columnaId, int creadoPorUsuarioId) {
        this(titulo, descripcion, columnaId, creadoPorUsuarioId);
        this.id = id;
    }

    // Getters y Setters
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
        this.titulo = titulo != null ? titulo.trim() : null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : "";
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
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
    }

    public int getCreadoPorUsuarioId() {
        return creadoPorUsuarioId;
    }

    public void setCreadoPorUsuarioId(int creadoPorUsuarioId) {
        this.creadoPorUsuarioId = creadoPorUsuarioId;
    }

    // Métodos de utilidad
    public boolean esValida() {
        return titulo != null && !titulo.trim().isEmpty() &&
                columnaId > 0 && creadoPorUsuarioId > 0;
    }

    public String getTituloCorto() {
        if (titulo == null || titulo.isEmpty()) {
            return "Sin título";
        }
        return titulo.length() > 30 ?
                titulo.substring(0, 27) + "..." :
                titulo;
    }

    public String getDescripcionCorta() {
        if (descripcion == null || descripcion.isEmpty()) {
            return "Sin descripción";
        }
        return descripcion.length() > 100 ?
                descripcion.substring(0, 97) + "..." :
                descripcion;
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) {
            return "Fecha no disponible";
        }
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaCreacionCorta() {
        if (fechaCreacion == null) {
            return "N/A";
        }
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public long getDiasDesdeCreacion() {
        if (fechaCreacion == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(fechaCreacion.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    public boolean esReciente() {
        return getDiasDesdeCreacion() <= 1;
    }

    public boolean esAntigua() {
        return getDiasDesdeCreacion() > 7;
    }

    // Para mostrar en listas y tablas
    public String getResumen() {
        return String.format("%s - %s", getTituloCorto(), getFechaCreacionCorta());
    }

    public String getDisplayText() {
        return String.format("[%d] %s", id, titulo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return titulo != null ? titulo : "Tarjeta sin título";
    }


}