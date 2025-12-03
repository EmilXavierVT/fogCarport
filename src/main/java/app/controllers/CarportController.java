package app.controllers;

import app.entities.Carport;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;

import io.javalin.http.Context;


import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarportController {

    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        app.get("/", ctx -> showCarports(ctx, connectionPool));
        app.get("/product/{id}", ctx -> displayProductPage(ctx,connectionPool));
    }

    private static void displayProductPage(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
    Carport actualCarport = CarportMapper.getCarportByID(Integer.parseInt(ctx.pathParam("id")),connectionPool);
    List<Carport> standardCarports = CarportMapper.getAllStandardCarport(connectionPool);

        ctx.render("product.html", Map.of(
                "carport", actualCarport,
                "standard_carports", standardCarports
        ));


    }


    public static void showCarports(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        try {
            List<Carport> standardCarports = CarportMapper.getAllStandardCarport(connectionPool);

            ctx.render("index.html", Map.of("standard_carports", standardCarports));

        } catch (DatabaseException e) {
            System.out.println("showCarport signature: Could not get all standard carports" + e.getMessage());
            ctx.redirect("/");
        }
    }
    }

