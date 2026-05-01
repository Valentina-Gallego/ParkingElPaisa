package com.parking.servlet;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/api/logout")
public class LogoutServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json;charset=UTF-8");

        req.getSession().invalidate();

        JsonObject json = new JsonObject();

        json.addProperty("ok", true);

        res.getWriter().print(json);
    }
}
