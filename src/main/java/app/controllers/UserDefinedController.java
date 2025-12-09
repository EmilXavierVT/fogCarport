package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.services.Calculator;
import app.services.SpecificationWizard;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class UserDefinedController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
       app.get("userdefined",ctx -> ctx.render("userdefined.html"));
       app.get("flat",ctx->ctx.render("flat.html"));
       app.post("flat",ctx-> sendRequest(ctx,connectionPool));
       app.get("/angle", ctx -> ctx.render("angle"));
       app.post("/angle", ctx -> sendAngleRequest(ctx, connectionPool));

    }

    private static void sendAngleRequest( Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        String roofType = ctx.formParam("roof");
        int angle = Integer.parseInt(ctx.formParam("angle"));
        int shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        int shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        String remarks = ctx.formParam("remarks");
        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip_code"));
        String city = ctx.formParam("city");
        String phoneNumber = ctx.formParam("phone_number");
        String email = ctx.formParam("email");

        boolean roof = !roofType.equals("Ingen tag");



        Calculator calculator = new Calculator(SpecificationWizard.makeAngleSpecification(width,length,roof,shedWidth,shedLength,angle));

        System.out.println(calculator.setItemList());
    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        String roofType = ctx.formParam("roof");
        int shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        int shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        String remarks = ctx.formParam("remarks");
        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip_code"));
        String city = ctx.formParam("city");
        String phoneNumber = ctx.formParam("phone_number");
        String email = ctx.formParam("email");

        boolean roof = !roofType.equals("Ingen tag");



        Calculator calculator = new Calculator(SpecificationWizard.makeASpecification(width,length,roof,shedWidth,shedLength));

        System.out.println(calculator.setItemList());
    }

}
