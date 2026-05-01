package com.parking.servlet;

import com.google.gson.JsonObject;
import com.parking.dao.TicketDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/ticket/entrada")
public class TicketEntradaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        JsonObject json = new JsonObject();

        // Verificar sesión
        HttpSession session = req.getSession(false);

        if (session == null) {
            json.addProperty("ok", false);
            json.addProperty("mensaje", "Sesión expirada, vuelve a iniciar sesión");
            res.getWriter().print(json);
            return;
        }

        // Verificar que userId existe en sesión
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            json.addProperty("ok", false);
            json.addProperty("mensaje", "No hay userId en sesión, vuelve a iniciar sesión");
            res.getWriter().print(json);
            return;
        }

        try {

            String placa = req.getParameter("placa");

            int vehicleTypeId = Integer.parseInt(
                    req.getParameter("vehicleTypeId")
            );

            int userId = (Integer) userIdObj;

            TicketDAO dao = new TicketDAO();

            long ticketId = dao.registrarEntrada(
                    placa,
                    vehicleTypeId,
                    userId
            );

            json.addProperty("ok", true);
            json.addProperty("ticketId", ticketId);
            json.addProperty("mensaje",
                    "Entrada registrada para " + placa.toUpperCase()
            );

        } catch (IllegalStateException e) {
        // Error de validación — mostrar mensaje limpio
        json.addProperty("ok", false);
        json.addProperty("mensaje", e.getMessage());

    } catch (Exception e) {
        // Error técnico — mostrar stack trace
        res.setStatus(500);
        json.addProperty("ok", false);
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        json.addProperty("mensaje", "Error: " + sw.toString());
    }

        res.getWriter().print(json);
    }
}