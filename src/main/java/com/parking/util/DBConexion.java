package com.parking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/parking_management_db?useSSL=false&serverTimezone=America/Bogota";

    private static final String USUARIO = "root";//aca el usuario

    private static final String PASSWORD = "aqui_tu_contraseña_de_mysql";//aca la contraseña

    public static Connection obtener() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            throw new SQLException("Driver MySQL no encontrado");
        }

        return DriverManager.getConnection(
                URL,
                USUARIO,
                PASSWORD
        );
    }
}
