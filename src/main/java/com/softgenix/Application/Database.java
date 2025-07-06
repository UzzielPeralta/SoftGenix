package com.softgenix.Application;

import java.sql.*;
import java.util.Properties;

public class Database {
    // Actualiza la URL para usar el mismo nombre del Wallet
    private static final String URL = "jdbc:oracle:thin:@zendo_high";
    private static final String USER = "ADMIN";
    private static final String PASSWORD = "Zendo123****";
    private static final String WALLET_PATH = "C:/Users/erick/Downloads/Wallet_Zendo";


    public static Connection getConnection() throws SQLException {
        try {
            System.setProperty("oracle.net.tns_admin", WALLET_PATH);
            System.setProperty("oracle.net.ssl_server_dn_match", "true");

            // Añadir más diagnóstico
            System.out.println("Intentando conectar con usuario: " + USER);
            System.out.println("Propiedades del sistema: oracle.net.tns_admin=" + System.getProperty("oracle.net.tns_admin"));

            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("oracle.jdbc.readTimeout", "15000");
            props.setProperty("oracle.jdbc.connectTimeout", "15000");

            return DriverManager.getConnection(URL, props);
        } catch (SQLException e) {
            System.err.println("Error detallado: " + e.toString());
            if (e.getNextException() != null) {
                System.err.println("Causa secundaria: " + e.getNextException());
            }
            throw e;
        }
    }


    // ... los métodos addUser y validateUser están correctos y no necesitan cambios ...
    public static boolean addUser(String nombreUsuario, String email, String password, String rol) {
        String sql = "INSERT INTO USUARIOS(NOMBRE_USUARIO, EMAIL, CONTRASENA, ROL) VALUES(?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, rol);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateUser(String email, String password) {
        String sql = "SELECT 1 FROM USUARIOS WHERE EMAIL = ? AND CONTRASENA = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar el usuario: " + e.getMessage());
            return false;
        }
    }
}
