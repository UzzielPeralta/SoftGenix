package com.softgenix.Service;

import com.softgenix.App.Utils.Auth;
import com.softgenix.Dao.UserDAO;
import com.softgenix.Model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    /**
     * Crea un nuevo usuario en el sistema
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param nombre Nombre completo del usuario
     * @param rol Rol del usuario (ADMIN o USER)
     * @return
     */
    public static boolean crearUsuario(String email, String password, String nombre, String rol) {
        // Validar permisos según el rol
        if (Auth.isSuperAdmin()) {
            // SUPERADMIN puede crear cualquier tipo de usuario
        } else if (Auth.isAdmin() && "USER".equals(rol)) {
            // ADMIN solo puede crear usuarios con rol USER
        } else {
            return false; // Otros roles no pueden crear usuarios
        }


        // Validaciones básicas
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                nombre == null || nombre.trim().isEmpty() ||
                rol == null || (!rol.equals("ADMIN") && !rol.equals("USER"))) {
            return false;
        }

        // Verificar que el email no exista
        if (UserDAO.existeEmail(email)) {
            return false;
        }

        // Continuar con la creación...
        User nuevoUsuario = new User();
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasena(password);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setRol(rol);

        return UserDAO.crearUsuario(nuevoUsuario);
    }
    /**
     * Obtiene todos los usuarios del sistema
     * @return Lista de usuarios
     */
    public static List<User> obtenerTodosUsuarios() {
        // Solo SUPERADMIN puede ver todos los usuarios
        if (!Auth.isSuperAdmin()) {
            return new ArrayList<>();
        }

        return UserDAO.obtenerTodosUsuarios();
    }

    /**
     * Obtiene usuarios filtrados por rol
     * @param rol Rol a filtrar (ADMIN, USER, SUPERADMIN)
     * @return Lista de usuarios con el rol especificado
     */
    public static List<User> obtenerUsuariosPorRol(String rol) {
        // Solo administradores pueden consultar usuarios por rol

        if (rol == null || rol.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return UserDAO.obtenerUsuariosPorRol(rol);
    }

    /**
     * Elimina un usuario del sistema
     * @param userId ID del usuario a eliminar
     * @return
     */
    public static boolean eliminarUsuario(int userId) {
        // Solo SUPERADMIN puede eliminar usuarios
        if (!Auth.isSuperAdmin()) {
            return false;
        }

        // No permitir eliminar al usuario actual
        if (Auth.getUsuarioActual().getId() == userId) {
            return false;
        }

        return UserDAO.eliminarUsuario(userId);
    }

    /**
     * Busca un usuario por su ID
     * @param userId ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public static User obtenerUsuarioPorId(int userId) {
        // Solo administradores pueden buscar usuarios
        if (!Auth.isAdmin()) {
            return null;
        }

        return UserDAO.obtenerUsuarioPorId(userId);
    }
}