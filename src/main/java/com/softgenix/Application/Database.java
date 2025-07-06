package com.softgenix.Application;

import java.sql.*;
import java.util.Properties;

public class Database {

    // --- CAMBIO AQUÍ ---
    // Asegúrate de que este alias coincida EXACTAMENTE con una entrada en tu tnsnames.ora
    //
    private static final String DB_ALIAS = "zendo_medium"; // O _high, _low, etc.

    private static final String USERNAME = "ADMIN";
    private static final String PASSWORD = "Zendo1234***";
    private static final String WALLET_PATH = "C:\\Users\\erick\\Downloads\\Wallet_Zendo";

    public static Connection getConnection() throws SQLException {
        // El resto del código no necesita cambios
        String url = "jdbc:oracle:thin:@" + DB_ALIAS;
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);
        props.setProperty("oracle.net.tns_admin", WALLET_PATH);
        props.setProperty("oracle.net.ssl_server_dn_match", "true");
        return DriverManager.getConnection(url, props);
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