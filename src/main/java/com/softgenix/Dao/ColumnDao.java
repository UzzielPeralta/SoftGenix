package com.softgenix.Dao;

import com.softgenix.App.Config.Database;
import com.softgenix.Model.Column;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnDao {

    public static List<Column> obtenerColumnasPorTablero(int tableroId) {
        List<Column> columnas = new ArrayList<>();
        String sql = "SELECT ID, NOMBRE, TABLERO_ID, ORDEN FROM COLUMNAS WHERE TABLERO_ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableroId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Column columna = new Column();
                    // Usa los nombres exactos de las columnas como están en la BD
                    columna.setId(rs.getInt("ID"));
                    columna.setNombre(rs.getString("NOMBRE"));
                    columna.setTableroId(rs.getInt("TABLERO_ID"));
                    columna.setOrden(rs.getInt("ORDEN"));
                    columnas.add(columna);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columnas;
    }
}