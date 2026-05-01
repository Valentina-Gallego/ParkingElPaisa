package com.parking.dao;

import com.parking.model.Usuario;
import com.parking.util.DBConexion;

import java.security.MessageDigest;
import java.sql.*;

public class UsuarioDAO {

    public static String hashPassword(String password) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] bytes = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {

            throw new RuntimeException("Error al hashear contraseña");
        }
    }

    public boolean registrar(String nombre,
                             String username,
                             String password,
                             int roleId) {

        String sql =
                "INSERT INTO users(role_id, full_name, username, password_hash) VALUES(?,?,?,?)";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roleId);
            ps.setString(2, nombre);
            ps.setString(3, username);
            ps.setString(4, hashPassword(password));

            ps.executeUpdate();

            return true;

        } catch (SQLIntegrityConstraintViolationException e) {

            return false;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }

    public Usuario login(String username, String password) {

        String sql =
                "SELECT id, role_id, full_name, username " +
                        "FROM users " +
                        "WHERE username=? AND password_hash=?";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashPassword(password));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Usuario(
                        rs.getInt("id"),
                        rs.getInt("role_id"),
                        rs.getString("full_name"),
                        rs.getString("username")
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }
}