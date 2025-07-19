package com.softgenix.Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int id;
    private String nombre;
    private String descripcion; // Nuevo campo
    private int propietarioId;
    private List<Column> columnas;

    public Board() {
        columnas = new ArrayList<>();
    }

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
        this.nombre = nombre;
    }

    // Getters y setters para descripcion
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(int propietarioId) {
        this.propietarioId = propietarioId;
    }

    public List<Column> getColumnas() {
        return columnas;
    }

    public void setColumnas(List<Column> columnas) {
        this.columnas = columnas;
    }

    @Override
    public String toString() {
        return nombre; // Para mostrar en ComboBox
    }
}