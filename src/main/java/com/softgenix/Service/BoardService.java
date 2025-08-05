package com.softgenix.Service;

import com.softgenix.App.Utils.Auth;
import com.softgenix.Dao.BoardAssignmentDAO;
import com.softgenix.Dao.BoardDAO;
import com.softgenix.Dao.UserDAO;
import com.softgenix.Model.Board;
import com.softgenix.Model.Column;
import com.softgenix.Model.User;

import java.util.ArrayList;
import java.util.List;

public class BoardService {



    /**
     * Crea un nuevo tablero
     /**
     * Crea un nuevo tablero
     * @param nombre Nombre del tablero
     * @param descripcion Descripción del tablero
     * @return true si se creó correctamente, false en caso contrario
     */
    public static boolean crearTablero(String nombre, String descripcion, int propietarioId) {
        try {
            // Si descripción es null o vacía, usar valor por defecto
            if (descripcion == null || descripcion.trim().isEmpty()) {
                descripcion = "Sin descripción";
            }

            int tableroId = BoardDAO.crearTableroConId(nombre, descripcion, propietarioId);

            if (tableroId > 0) {
                // Crear columnas predeterminadas
                boolean columna1 = crearColumna(tableroId, "En Proceso");
                boolean columna2 = crearColumna(tableroId, "Completadas");

                if (columna1 && columna2) {
                    return true;
                } else {
                    System.err.println("Error al crear las columnas predeterminadas");
                    return false;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error en BoardService.crearTablero: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Crea una nueva columna en un tablero
     * @param tableroId ID del tablero
     * @param nombre Nombre de la columna
     * @return true si se creó correctamente, false en caso contrario
     */
    public static boolean crearColumna(int tableroId, String nombre) {
        if (!Auth.isAdmin()) {
            return false;
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        // Verificar que el tablero exista y pertenezca al usuario actual
        Board tablero = BoardDAO.obtenerTableroPorId(tableroId);
        if (tablero == null || tablero.getPropietarioId() != Auth.getUsuarioActual().getId()) {
            return false;
        }

        // Obtener el orden máximo actual para agregar al final
        int ordenMaximo = BoardDAO.obtenerOrdenMaximoColumnas(tableroId);

        Column columna = new Column();
        columna.setNombre(nombre);
        columna.setTableroId(tableroId);
        columna.setOrden(ordenMaximo + 1);

        return BoardDAO.crearColumna(columna);
    }



    /**
     * Obtiene todos los tableros del usuario actual
     * @return Lista de tableros
     */
    public static List<Board> obtenerTablerosUsuario() {
        if (!Auth.isLoggedIn()) {
            return new ArrayList<>();
        }

        int usuarioId = Auth.getUsuarioActual().getId();
        // Cambiar obtenerTablerosPorUsuario por obtenerTablerosUsuario
        return BoardDAO.obtenerTablerosUsuario();
    }

    /**
     * Obtiene las columnas de un tablero como strings formateados
     * @param tableroId ID del tablero
     * @return Lista de strings con formato "ID - Nombre"
     */
    public static List<String> obtenerColumnasComoStrings(int tableroId) {
        if (!Auth.isLoggedIn()) {
            return new ArrayList<>();
        }

        List<Column> columnas = BoardDAO.obtenerColumnasPorTablero(tableroId);
        List<String> columnasStr = new ArrayList<>();

        for (Column columna : columnas) {
            columnasStr.add(columna.getId() + " - " + columna.getNombre());
        }

        return columnasStr;
    }

    /**
     * Elimina un tablero
     * @param tableroId ID del tablero a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public static boolean eliminarTablero(int tableroId) {
        if (!Auth.isAdmin()) {
            return false;
        }

        // Verificar que el tablero exista y pertenezca al usuario actual
        Board tablero = BoardDAO.obtenerTableroPorId(tableroId);
        if (tablero == null || tablero.getPropietarioId() != Auth.getUsuarioActual().getId()) {
            return false;
        }

        return BoardDAO.eliminarTablero(tableroId);
    }
    /**
     * Obtiene los tableros asignados a un usuario específico
     * @param userId ID del usuario
     * @return Lista de tableros asignados al usuario
     */
    public static List<Board> obtenerTablerosAsignadosAUsuario(int userId) {
        if (userId <= 0) {
            return new ArrayList<>();
        }

        // Usar BoardAssignmentDAO para obtener los tableros asignados
        return BoardAssignmentDAO.obtenerTablerosAsignadosAUsuario(userId);
    }
    /**
     * Asigna un tablero a un usuario
     * @param tableroId ID del tablero
     * @param usuarioId ID del usuario
     * @return true si se asignó correctamente, false en caso contrario
     */
    public static boolean asignarTableroAUsuario(int tableroId, int usuarioId) {
        if (!Auth.isAdmin()) {
            return false;
        }

        // Verificar que el tablero exista
        Board tablero = BoardDAO.obtenerTableroPorId(tableroId);
        if (tablero == null) {
            return false;
        }

        // Verificar que el usuario exista y tenga rol USER
        User usuario = UserDAO.obtenerUsuarioPorId(usuarioId);
        if (usuario == null || !"USER".equals(usuario.getRol())) {
            return false;
        }

        return BoardAssignmentDAO.asignarTableroAUsuario(tableroId, usuarioId);
    }

    /**
     * Desasigna un tablero de un usuario
     * @param tableroId ID del tablero
     * @param usuarioId ID del usuario
     * @return true si se desasignó correctamente, false en caso contrario
     */
    public static boolean desasignarTableroDeUsuario(int tableroId, int usuarioId) {
        if (!Auth.isAdmin()) {
            return false;
        }

        return BoardAssignmentDAO.desasignarTableroDeUsuario(tableroId, usuarioId);
    }

    /**
     * Obtiene los usuarios asignados a un tablero
     * @param tableroId ID del tablero
     * @return Lista de usuarios con el tablero asignado
     */
    public static List<User> obtenerUsuariosAsignadosATablero(int tableroId) {
        if (!Auth.isAdmin()) {
            return new ArrayList<>();
        }

        return BoardAssignmentDAO.obtenerUsuariosAsignadosATablero(tableroId);
    }

}