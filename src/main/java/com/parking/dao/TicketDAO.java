package com.parking.dao;

import com.parking.model.Ticket;
import com.parking.util.DBConexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public long registrarEntrada(String placa,
                                 int vehicleTypeId,
                                 int userId) throws SQLException {

        // Verificar si la placa ya tiene un ticket activo
        String sqlVerificar =
                "SELECT COUNT(*) FROM parking_tickets " +
                        "WHERE license_plate=? AND status='ACTIVE'";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlVerificar)) {

            ps.setString(1, placa.toUpperCase());

            ResultSet rs = ps.executeQuery();

            rs.next();

            if (rs.getInt(1) > 0) {
                throw new IllegalStateException(
                        "El vehículo " + placa.toUpperCase() + " ya está en el parqueadero"
                );
            }
        }

        // Insertar el ticket
        String sql =
                "INSERT INTO parking_tickets " +
                        "(license_plate, vehicle_type_id, user_id, entry_time, status) " +
                        "VALUES (?,?,?,NOW(),'ACTIVE')";

        long ticketId;

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setString(1, placa.toUpperCase());
            ps.setInt(2, vehicleTypeId);
            ps.setInt(3, userId);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            ticketId = rs.next() ? rs.getLong(1) : -1;
        }

        // Ocupar un cupo disponible
        String sqlOcupar =
                "UPDATE parking_spots SET status='OCCUPIED' " +
                        "WHERE status='AVAILABLE' LIMIT 1";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlOcupar)) {
            ps.executeUpdate();
        }

        return ticketId;
    }

    public Ticket buscarActivo(String placa) throws SQLException {

        String sql =
                "SELECT * FROM parking_tickets " +
                        "WHERE license_plate=? " +
                        "AND status='ACTIVE' " +
                        "ORDER BY entry_time DESC LIMIT 1";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa.toUpperCase());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Ticket t = new Ticket();

                t.setId(rs.getLong("id"));
                t.setLicensePlate(rs.getString("license_plate"));
                t.setVehicleTypeId(rs.getInt("vehicle_type_id"));
                t.setUserId(rs.getInt("user_id"));
                t.setEntryTime(
                        rs.getTimestamp("entry_time")
                                .toLocalDateTime()
                );
                t.setStatus(rs.getString("status"));

                return t;
            }
        }

        return null;
    }

    public double registrarSalida(long ticketId) throws SQLException {

        String sqlTarifa =
                "SELECT t.hourly_rate " +
                        "FROM parking_tickets pt " +
                        "JOIN tariffs t " +
                        "ON pt.vehicle_type_id = t.vehicle_type_id " +
                        "WHERE pt.id=? AND t.is_active=1";

        double tarifaHora = 3000;

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlTarifa)) {

            ps.setLong(1, ticketId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tarifaHora = rs.getDouble("hourly_rate");
            }
        }

        String sqlHoras =
                "SELECT TIMESTAMPDIFF(MINUTE, entry_time, NOW()) AS minutos " +
                        "FROM parking_tickets WHERE id=?";

        double total;

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlHoras)) {

            ps.setLong(1, ticketId);

            ResultSet rs = ps.executeQuery();

            rs.next();

            double minutos = rs.getDouble("minutos");

            double horas = Math.ceil(minutos / 60.0);

            if (horas < 1) {
                horas = 1;
            }

            total = horas * tarifaHora;
        }

        String sqlCerrar =
                "UPDATE parking_tickets " +
                        "SET exit_time=NOW(), " +
                        "total_amount=?, " +
                        "status='COMPLETED' " +
                        "WHERE id=?";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlCerrar)) {

            ps.setDouble(1, total);
            ps.setLong(2, ticketId);

            ps.executeUpdate();
        }

        // Liberar un cupo
        String sqlLiberar =
                "UPDATE parking_spots SET status='AVAILABLE' " +
                        "WHERE status='OCCUPIED' LIMIT 1";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sqlLiberar)) {
            ps.executeUpdate();
        }

        return total;
    }

    public List<Ticket> historialPorPlaca(String placa)
            throws SQLException {

        List<Ticket> lista = new ArrayList<>();

        String sql =
                "SELECT * FROM parking_tickets " +
                        "WHERE license_plate=? " +
                        "ORDER BY entry_time DESC LIMIT 20";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa.toUpperCase());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ticket t = new Ticket();

                t.setId(rs.getLong("id"));
                t.setLicensePlate(rs.getString("license_plate"));

                t.setEntryTime(
                        rs.getTimestamp("entry_time")
                                .toLocalDateTime()
                );

                if (rs.getTimestamp("exit_time") != null) {
                    t.setExitTime(
                            rs.getTimestamp("exit_time")
                                    .toLocalDateTime()
                    );
                }

                t.setTotalAmount(rs.getDouble("total_amount"));
                t.setStatus(rs.getString("status"));

                lista.add(t);
            }
        }

        return lista;
    }

    public List<Ticket> ticketsActivos() throws SQLException {

        List<Ticket> lista = new ArrayList<>();

        String sql =
                "SELECT * FROM parking_tickets " +
                        "WHERE status='ACTIVE' " +
                        "ORDER BY entry_time ASC";

        try (Connection con = DBConexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ticket t = new Ticket();

                t.setId(rs.getLong("id"));
                t.setLicensePlate(rs.getString("license_plate"));
                t.setVehicleTypeId(rs.getInt("vehicle_type_id"));

                t.setEntryTime(
                        rs.getTimestamp("entry_time")
                                .toLocalDateTime()
                );

                t.setStatus("ACTIVE");

                lista.add(t);
            }
        }

        return lista;
    }
}
