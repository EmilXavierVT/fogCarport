package app.controllers;

import app.persistence.ConnectionPool;
import app.services.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class UserDefinedController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
       app.get("userdefined",ctx -> ctx.render("userdefined.html"));
       app.get("flat",ctx->ctx.render("flat.html"));
//       app.post("flat",ctx-> sendRequest(ctx,connectionPool));

    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool)
    {
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));
        String roofType = ctx.formParam("roof");
        int shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        int shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        String remarks = ctx.formParam("remarks");
        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip_code"));
        String city = ctx.formParam("city");
        String phoneNumberr = ctx.formParam("phone_number");
        String email = ctx.formParam("email");

        Calculator calculator

    }

}
