package com.parking.servlet;

import com.google.gson.JsonObject;
import com.parking.dao.TicketDAO;
import com.parking.model.Ticket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/ticket/salida")
public class TicketSalidaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        JsonObject json = new JsonObject();

        HttpSession session =
                req.getSession(false);

        if (session == null ||
                session.getAttribute("roleId") == null) {

            res.setStatus(401);

            json.addProperty("ok", false);

            json.addProperty(
                    "mensaje",
                    "No autorizado"
            );

            res.getWriter().print(json);

            return;
        }

        try {

            String placa =
                    req.getParameter("placa");

            TicketDAO dao =
                    new TicketDAO();

            Ticket t =
                    dao.buscarActivo(placa);

            if (t == null) {

                json.addProperty("ok", false);

                json.addProperty(
                        "mensaje",
                        "No se encontró vehículo activo"
                );

            } else {

                double total =
                        dao.registrarSalida(
                                t.getId()
                        );

                json.addProperty("ok", true);

                json.addProperty(
                        "placa",
                        placa.toUpperCase()
                );

                json.addProperty(
                        "total",
                        total
                );

                json.addProperty(
                        "mensaje",
                        "Salida registrada. Total: $"
                                + String.format("%,.0f", total)
                );
            }

        } catch (Exception e) {

            res.setStatus(500);

            json.addProperty("ok", false);

            json.addProperty(
                    "mensaje",
                    "Error: " + e.getMessage()
            );
        }

        res.getWriter().print(json);
    }
}