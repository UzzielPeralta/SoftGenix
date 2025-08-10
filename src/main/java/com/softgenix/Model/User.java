package com.softgenix.Model;

import java.util.Objects;

public class User {
    private int id;
    private String email;
    private String contrasena;
    private String nombre;
    private String rol;

    // Constantes para roles
    public static final String ROL_SUPER_ADMIN = "super_admin";
    public static final String ROL_ADMIN = "admin";
    public static final String ROL_USUARIO = "usuario";

    // Constructor vacío
    public User() {
    }

    // Constructor completo
    public User(int id, String email, String contrasena, String nombre, String rol) {
        this.id = id;
        this.email = email;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Constructor sin ID (para crear usuarios nuevos)
    public User(String email, String contrasena, String nombre, String rol) {
        this.email = email;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : null;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // Métodos de utilidad
    public boolean esSuperAdmin() {
        return ROL_SUPER_ADMIN.equals(rol);
    }

    public boolean esAdmin() {
        return ROL_ADMIN.equals(rol);
    }

    public boolean esUsuario() {
        return ROL_USUARIO.equals(rol);
    }

    public boolean tienePrivilegiosAdmin() {
        return esSuperAdmin() || esAdmin();
    }

    // Validación de datos
    public boolean esValido() {
        return email != null && !email.trim().isEmpty() &&
                contrasena != null && !contrasena.trim().isEmpty() &&
                nombre != null && !nombre.trim().isEmpty() &&
                rol != null && !rol.trim().isEmpty();
    }

    public String getNombreCompleto() {
        return nombre + " (" + email + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
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