package com.parking.servlet;

import com.google.gson.JsonObject;
import com.parking.dao.UsuarioDAO;
import com.parking.model.Usuario;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        String username = req.getParameter("username");

        String password = req.getParameter("password");

        UsuarioDAO dao = new UsuarioDAO();

        Usuario u = dao.login(username, password);

        JsonObject json = new JsonObject();

        if (u != null) {

            HttpSession session = req.getSession();

            session.setAttribute("userId", u.getId());

            session.setAttribute("roleId", u.getRoleId());

            session.setAttribute("fullName", u.getFullName());

            json.addProperty("ok", true);

            json.addProperty("nombre", u.getFullName());

            json.addProperty("roleId", u.getRoleId());

        } else {

            res.setStatus(401);

            json.addProperty("ok", false);

            json.addProperty(
                    "mensaje",
                    "Usuario o contraseña incorrectos"
            );
        }

        res.getWriter().print(json);
    }
}