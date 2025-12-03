package app.controllers;

import app.entities.Carport;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class AdminController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        app.get("/admin/alert", ctx -> ctx.render("admin/alert.html"));

    }

    public static void showAllCarports (Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        try
        {
            List<Carport> standardCarports = CarportMapper.getAllStandardCarport(connectionPool);

            ctx.render("index.html", Map.of("standard_carports", standardCarports));

        } catch (DatabaseException e)
        {
            System.out.println("showCarport signature: Could not get all standard carports" + e.getMessage());
            ctx.redirect("/");
        }
    }
}
