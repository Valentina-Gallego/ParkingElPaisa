package com.parking.servlet;

import com.google.gson.JsonObject;
import com.parking.dao.CuposDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/cupos")
public class CuposServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        JsonObject json = new JsonObject();

        try {

            int[] cupos =
                    new CuposDAO().getCupos();

            json.addProperty("ok", true);

            json.addProperty(
                    "total",
                    cupos[0]
            );

            json.addProperty(
                    "ocupados",
                    cupos[1]
            );

            json.addProperty(
                    "disponibles",
                    cupos[2]
            );

        } catch (Exception e) {

            json.addProperty("ok", false);

            json.addProperty(
                    "mensaje",
                    e.getMessage()
            );
        }

        res.getWriter().print(json);
    }
}