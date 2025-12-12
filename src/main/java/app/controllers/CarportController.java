package app.controllers;

import app.entities.Carport;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CarportController
{

    public static void addRoutes(Javalin app)
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        app.get("/", ctx -> showCarports(ctx, connectionPool));
        app.get("/product/{id}", ctx -> displayProductPage(ctx, connectionPool));
        app.get("/product/pdf/{EAN}", ctx -> displayPdfPage(ctx));
        app.get("/cart", ctx -> ctx.render("cart.html"));
        app.get("/pay_page", ctx -> ctx.render("pay_page.html"));
        app.post("/payment_complete", ctx -> paymentComplete(ctx, connectionPool));
        app.get("/payment_complete", ctx -> ctx.render("payment_complete.html"));


    }

    private static void paymentComplete(Context ctx, ConnectionPool connectionPool) throws DatabaseException, SQLException {
       String idString = ctx.formParam("carportID");

       if(idString == null || idString.isEmpty()){
        ctx.status(400).result("Carport ID Missing");
        return;
       }

       int carportID = Integer.parseInt(idString);

       User user = ctx.sessionAttribute("currentUser");
       if(user == null) {
           ctx.status(401).result("User not logged in");
           return;
       }

       CarportRequestMapper.createCarportRequest(user.getUserId(), carportID, 0, connectionPool);
       OrderMapper.saveOrder(user.getUserId(),LocalDate.now(),connectionPool);
       ctx.redirect("/payment_complete");
    }

    private static void displayPdfPage(Context ctx)
    {
        String ean = ctx.pathParam("EAN");
        String resourcePath = "/public/carportPdf/" + ean + ".pdf";
        java.io.InputStream pdfStream = CarportController.class.getResourceAsStream(resourcePath);

            if(pdfStream == null)
            {
                ctx.status(404).result("PDF not found for EAN" + ean);
                return;
            }
            ctx.contentType("application/pdf");
            ctx.result(pdfStream);
    }

    private static void displayProductPage(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
    Carport actualCarport = CarportMapper.getCarportByID(Integer.parseInt(ctx.pathParam("id")),connectionPool);
    List<Carport> standardCarports = CarportMapper.getAllStandardCarportForSlider(connectionPool);

        ctx.render("product.html", Map.of
                (
                "carport", actualCarport,
                "standard_carports", standardCarports
        ));
    }

    public static void showCarports(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        try {
            List<Carport> standardCarports = CarportMapper.getAllStandardCarportForSlider(connectionPool);
            ctx.render("index.html", Map.of("standard_carports", standardCarports));

        }
        catch (DatabaseException e)
        {
            System.out.println("showCarport signature: Could not get all standard carports" + e.getMessage());
            ctx.redirect("/");
        }
    }


    }

