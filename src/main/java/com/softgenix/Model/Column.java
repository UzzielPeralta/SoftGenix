package com.softgenix.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Column {
    private int id;
    private String nombre;
    private int orden;
    private int tableroId;
    private List<Card> tarjetas;

    // Constantes para tipos de columnas
    public static final String TIPO_PENDIENTE = "Pendiente";
    public static final String TIPO_EN_PROCESO = "En Proceso";
    public static final String TIPO_COMPLETADO = "Completado";

    public Column() {
        tarjetas = new ArrayList<>();
    }

    public Column(String nombre, int orden, int tableroId) {
        this();
        this.nombre = nombre;
        this.orden = orden;
        this.tableroId = tableroId;
    }

    public Column(int id, String nombre, int orden, int tableroId) {
        this(nombre, orden, tableroId);
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
        this.tarjetas = tarjetas != null ? tarjetas : new ArrayList<>();
    }

    // Métodos de utilidad para manejo de tarjetas
    public void agregarTarjeta(Card tarjeta) {
        if (tarjeta != null) {
            tarjetas.add(tarjeta);
            tarjeta.setColumnaId(this.id);
        }
    }

    public boolean removerTarjeta(Card tarjeta) {
        return tarjetas.remove(tarjeta);
    }

    public boolean removerTarjetaPorId(int tarjetaId) {
        return tarjetas.removeIf(tarjeta -> tarjeta.getId() == tarjetaId);
    }

    public int getCantidadTarjetas() {
        return tarjetas.size();
    }

    public boolean estaVacia() {
        return tarjetas.isEmpty();
    }

    public Card buscarTarjetaPorId(int tarjetaId) {
        return tarjetas.stream()
                .filter(tarjeta -> tarjeta.getId() == tarjetaId)
                .findFirst()
                .orElse(null);
    }

    // Validación
    public boolean esValida() {
        return nombre != null && !nombre.trim().isEmpty() &&
                orden >= 0 && tableroId > 0;
    }

    // Métodos para identificar tipo de columna
    public boolean esPendiente() {
        String nombreLower = nombre.toLowerCase();
        return nombreLower.contains("pendiente") ||
                nombreLower.contains("todo") ||
                nombreLower.contains("backlog");
    }

    public boolean esEnProceso() {
        String nombreLower = nombre.toLowerCase();
        return nombreLower.contains("proceso") ||
                nombreLower.contains("progreso") ||
                nombreLower.contains("doing");
    }

    public boolean esCompletado() {
        String nombreLower = nombre.toLowerCase();
        return nombreLower.contains("completad") ||
                nombreLower.contains("terminad") ||
                nombreLower.contains("done") ||
                nombreLower.contains("finaliz");
    }

    public String getEstadisticas() {
        return String.format("%s (%d tarjetas)", nombre, getCantidadTarjetas());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Column column = (Column) obj;
        return id == column.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}