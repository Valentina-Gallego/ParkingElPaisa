package com.parking.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.parking.dao.TicketDAO;
import com.parking.model.Ticket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

import java.time.LocalDateTime;

import java.util.List;

@WebServlet("/api/historial")
public class HistorialServlet extends HttpServlet {

    private static final Gson gson =
            new GsonBuilder()

                    .registerTypeAdapter(
                            LocalDateTime.class,

                            (com.google.gson.JsonSerializer<LocalDateTime>)
                                    (src, t, ctx) ->
                                            new com.google.gson.JsonPrimitive(
                                                    src.toString()
                                            )
                    )

                    .create();

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        HttpSession session =
                req.getSession(false);

        if (session == null) {

            res.setStatus(401);

            res.getWriter().print(
                    "{\"ok\":false,\"mensaje\":\"No autorizado\"}"
            );

            return;
        }

        try {

            TicketDAO dao =
                    new TicketDAO();

            String placa =
                    req.getParameter("placa");

            if (placa != null &&
                    !placa.isEmpty()) {

                List<Ticket> lista =
                        dao.historialPorPlaca(
                                placa
                        );

                res.getWriter().print(
                        gson.toJson(lista)
                );

            } else {

                List<Ticket> lista =
                        dao.ticketsActivos();

                res.getWriter().print(
                        gson.toJson(lista)
                );
            }

        } catch (Exception e) {

            res.setStatus(500);

            res.getWriter().print(
                    "{\"ok\":false,\"mensaje\":\""
                            + e.getMessage()
                            + "\"}"
            );
        }
    }
}