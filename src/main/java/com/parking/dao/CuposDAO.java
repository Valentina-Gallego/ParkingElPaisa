package com.parking.dao;

import com.parking.util.DBConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CuposDAO {

    public int[] getCupos() throws SQLException {

        String sql =
                "SELECT " +
                        "(SELECT COUNT(*) FROM parking_spots) AS total, " +
                        "(SELECT COUNT(*) FROM parking_spots WHERE status='OCCUPIED') AS ocupados";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            rs.next();

            int total = rs.getInt("total");

            int ocupados = rs.getInt("ocupados");

            return new int[]{
                    total,
                    ocupados,
                    total - ocupados
            };
        }
    }
}
