package app.controllers;

import app.entities.Carport;
import app.entities.CarportRequest;
import app.entities.Order;
import app.entities.Product;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AdminController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        app.get("/admin/alert", ctx -> {showAllCarports(ctx, connectionPool);
            showAllMaterials(ctx, connectionPool);
        showAllOrders(ctx, connectionPool);});


    }

    private static void showAllOrders(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<CarportRequest> orders = CarportRequestMapper.getAllCarportRequests(connectionPool);

            ctx.render("admin/alert.html", Map.of("all_carport_requests", orders));

        } catch (DatabaseException e)
        {
            System.out.println("showAllMaterials signature: Could not get all materials" + e.getMessage());
            ctx.redirect("/");
        }
    }



    public static void showAllCarports (Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        try
        {
            List<Carport> standardCarports = CarportMapper.getAllStandardCarport(connectionPool);

            ctx.render("admin/alert.html", Map.of("all_standard_carports", standardCarports));

        } catch (DatabaseException e)
        {
            System.out.println("showAllCarport signature: Could not get all carports" + e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void showAllMaterials (Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        try
        {
            List<Product> products = ProductMapper.getAllProducts(connectionPool);

            ctx.render("admin/alert.html", Map.of("all_materials", products));

        } catch (DatabaseException e)
        {
            System.out.println("showAllMaterials signature: Could not get all materials" + e.getMessage());
            ctx.redirect("/");
        }
    }
}
