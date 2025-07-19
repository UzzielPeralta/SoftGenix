package com.softgenix.App.Utils;

import com.softgenix.Model.User;

public class Auth {
    private static User usuarioActual;

    public static User getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(User usuario) {
        usuarioActual = usuario;
    }

    public static boolean isLoggedIn() {
        return usuarioActual != null;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static boolean isAdmin() {
        return isLoggedIn() &&
                (usuarioActual.getRol().equals("ADMIN") ||
                        usuarioActual.getRol().equals("SUPERADMIN"));
    }

    public static boolean isSuperAdmin() {
        return isLoggedIn() && usuarioActual.getRol().equals("SUPERADMIN");
    }
}