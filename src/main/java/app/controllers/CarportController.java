package app.controllers;

import app.entities.Carport;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;
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
        app.get("/payment_complete", ctx -> ctx.render("payment_complete.html"));

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

