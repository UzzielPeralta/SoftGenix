package com.softgenix.Service;

import com.softgenix.App.Utils.Auth;
import com.softgenix.Dao.CardDAO;
import com.softgenix.Model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardService {

    /**
     * Crea una nueva tarjeta
     * @param titulo Título de la tarjeta
     * @param descripcion Descripción de la tarjeta
     * @param columnaId ID de la columna a la que pertenece
     * @return true si se creó correctamente, false en caso contrario
     */
    public static boolean crearTarjeta(String titulo, String descripcion, int columnaId) {
        if (!Auth.isLoggedIn()) {
            return false;
        }

        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }

        Card tarjeta = new Card();
        tarjeta.setTitulo(titulo);
        tarjeta.setDescripcion(descripcion);
        tarjeta.setColumnaId(columnaId);
        tarjeta.setCreadoPorUsuarioId(Auth.getUsuarioActual().getId());

        return CardDAO.crearTarjeta(tarjeta);
    }

    /**
     * Obtiene las tarjetas asignadas a una columna específica
     * @param columnaId ID de la columna
     * @return Lista de tarjetas en la columna
     */
    public static List<Card> obtenerTarjetasPorColumna(int columnaId) {
        if (!Auth.isLoggedIn()) {
            return new ArrayList<>();
        }

        return CardDAO.obtenerTarjetasPorColumna(columnaId);
    }

    /**
     * Elimina una tarjeta
     * @param tarjetaId ID de la tarjeta a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public static boolean eliminarTarjeta(int tarjetaId) {
        if (!Auth.isLoggedIn()) {
            return false;
        }

        return CardDAO.eliminarTarjeta(tarjetaId);
    }

    /**
     * Mueve una tarjeta a otra columna
     * @param tarjetaId ID de la tarjeta
     * @param nuevaColumnaId ID de la nueva columna
     * @return true si se movió correctamente, false en caso contrario
     */
    public static boolean moverTarjeta(int tarjetaId, int nuevaColumnaId) {
        if (!Auth.isLoggedIn()) {
            return false;
        }

        return CardDAO.actualizarColumnaTarjeta(tarjetaId, nuevaColumnaId);
    }
}

