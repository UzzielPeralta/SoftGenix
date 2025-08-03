package com.softgenix.App.Config;

import java.sql.*;
import java.util.Properties;

public class Database {

    public static final String URL = "jdbc:oracle:thin:@zendo_low";
    public static final String USER = "ADMIN";
    public static final String PASSWORD = "Zendo123****";
    public static final String WALLET_PATH = "C:\\Users\\Usuario\\OneDrive\\Documentos\\Wallet_Zendo";

    public static Connection getConnection() throws SQLException {
        try {
            // Consider using ConnectionPool instead for better performance
            System.setProperty("oracle.net.tns_admin", WALLET_PATH);
            System.setProperty("oracle.net.ssl_server_dn_match", "true");

            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("oracle.jdbc.readTimeout", "15000");
            props.setProperty("oracle.jdbc.connectTimeout", "15000");

            return DriverManager.getConnection(URL, props);
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos:");
            e.printStackTrace();
            throw e;
        }
    }

    public static boolean validateUser(String email, String password) {
        String sql = "SELECT 1 FROM USUARIOS WHERE LOWER(EMAIL) = LOWER(?) AND CONTRASENA = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim());
            pstmt.setString(2, password.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Devuelve true si se encontró el usuario
            }

        } catch (SQLException e) {
            System.err.println("Error al validar el usuario:");
            e.printStackTrace();
            return false;
        }
    }
}