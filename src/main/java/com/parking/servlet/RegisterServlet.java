package com.parking.servlet;

import com.google.gson.JsonObject;
import com.parking.dao.UsuarioDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        JsonObject json = new JsonObject();

        try {
            String nombre = req.getParameter("nombre");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            int roleId = Integer.parseInt(req.getParameter("roleId"));

            UsuarioDAO dao = new UsuarioDAO();
            boolean ok = dao.registrar(nombre, username, password, roleId);

            json.addProperty("ok", ok);
            json.addProperty("mensaje", ok ? "Registro exitoso" : "El usuario ya existe");

        } catch (Exception e) {
            res.setStatus(500);
            json.addProperty("ok", false);

            // Muestra TODO el stack trace como mensaje
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            json.addProperty("mensaje", sw.toString());
        } finally {

        }

res.getWriter().print(json);
    }
}