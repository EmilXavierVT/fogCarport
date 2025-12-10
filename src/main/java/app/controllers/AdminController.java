package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AdminController {
    public static void addRoutes(Javalin app)
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        app.get("/admin/alert", ctx -> showAdminDashboard(ctx, connectionPool));
        app.post("/update_price", ctx -> updatePrice(ctx, connectionPool));
    }

    private static void updatePrice(Context ctx, ConnectionPool connectionPool)
    {
        int productId = Integer.parseInt(ctx.formParam("product_id"));
        float newPrice = Float.parseFloat(ctx.formParam("new_price"));
        ProductMapper.updateProductPrice(productId, newPrice, connectionPool);
        ctx.sessionAttribute("price_update_message", "Produkt pris opdateret !");
        ctx.redirect("/admin/alert");

    }


    private static void showAdminDashboard(Context ctx, ConnectionPool connectionPool)
    {try
        {
           List<User> users = UserMapper.getAllUsers(connectionPool);
//           List<CarportRequest> orders = CarportRequestMapper.getAllCarportRequests(connectionPool);
            List<Carport> standardCarports = CarportMapper.getAllStandardCarportForSlider(connectionPool);
            List<Product> products = ProductMapper.getAllProducts(connectionPool);

            ctx.render("admin/alert.html", Map.of("all_users", users,
//                    "all_carport_requests", orders,
                    "all_standard_carports", standardCarports,
                    "all_products", products));

        } catch (DatabaseException e)
        {
            System.out.println("showAdminDashboard signature: Could not show admin dashboard" + e.getMessage());
            ctx.redirect("/");
        }

    }


    private static void showAllUsers(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<User> users = UserMapper.getAllUsers(connectionPool);

            ctx.render("admin/alert.html", Map.of("all_users", users));

        } catch (DatabaseException e)
        {
            System.out.println("showAllUsers signature: Could not show all users" + e.getMessage());
            ctx.redirect("/");
        }
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
