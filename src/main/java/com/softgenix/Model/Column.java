package com.softgenix.Model;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private int id;
    private String nombre;
    private int orden;
    private int tableroId;
    private List<Card> tarjetas;

    public Column() {
        tarjetas = new ArrayList<>();
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

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public int getTableroId() {
        return tableroId;
    }

    public void setTableroId(int tableroId) {
        this.tableroId = tableroId;
    }

    public List<Card> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<Card> tarjetas) {
        this.tarjetas = tarjetas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}