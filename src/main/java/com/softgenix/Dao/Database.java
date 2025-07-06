package com.softgenix.Dao;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static boolean addUser(String firstName, String lastName, String email, String password) {
        String sql = "INSERT INTO USERS(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_KEY) VALUES(?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            return false;
        }
    }
    public static boolean validateUser(String email, String password) {
        String sql = "SELECT USER_KEY FROM USERS WHERE USER_EMAIL = ? AND USER_KEY = ?";

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