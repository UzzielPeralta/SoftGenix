package com.softgenix.Model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Board {
    private int id;
    private String nombre;
    private int propietarioId;
    private String descripcion;
    private LocalDateTime fechaCreacion;

    // Constructores
    public Board() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Board(String nombre, int propietarioId, String descripcion) {
        this();
        this.nombre = nombre;
        this.propietarioId = propietarioId;
        this.descripcion = descripcion;
    }

    public Board(int id, String nombre, int propietarioId, String descripcion) {
        this(nombre, propietarioId, descripcion);
        this.id = id;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : null;
    }

    public int getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(int propietarioId) {
        this.propietarioId = propietarioId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : "";
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Métodos de utilidad
    public boolean esValido() {
        return nombre != null && !nombre.trim().isEmpty() &&
                propietarioId > 0;
    }

    public String getDescripcionCorta() {
        if (descripcion == null || descripcion.isEmpty()) {
            return "Sin descripción";
        }
        return descripcion.length() > 50 ?
                descripcion.substring(0, 47) + "..." :
                descripcion;
    }

    public String getIdentificador() {
        return id + " - " + nombre;
    }

    // Para ComboBox con información adicional
    public String getDisplayText() {
        return String.format("%s (%s)", nombre, getDescripcionCorta());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Board board = (Board) obj;
        return id == board.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre; // Para mostrar en ComboBox
    }
}