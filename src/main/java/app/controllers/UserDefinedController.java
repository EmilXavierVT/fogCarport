package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.CarportRequestMapper;
import app.persistence.ConnectionPool;
import app.services.Calculator;
import app.services.SpecificationWizard;
import app.util.GmailSender;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UserDefinedController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
       app.get("/userdefined",ctx -> ctx.render("userdefined.html"));
       app.get("/flat",ctx->ctx.render("flat.html"));
       app.post("/flat",ctx-> sendRequest(ctx));
       app.get("/angle", ctx -> ctx.render("angle"));
       app.post("/angle", ctx -> sendAngleRequest(ctx));

    }

    private static void sendAngleRequest(Context ctx) throws DatabaseException, MessagingException {
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


        Calculator calc = new Calculator(SpecificationWizard.makeAngleSpecification(width,length,roof,shedWidth,shedLength,angle));
//        en user baseret på mail hvis ikke ny user
//        sales rep Id
//        en carport med styliste









        if(ctx.sessionAttribute("currentUser") != null)
        {
            User user = ctx.sessionAttribute("currentUser");
            CarportRequestMapper.createCarportRequest(user,);
        }

        System.out.println(calc.setItemList());
        GmailSender gms = new GmailSender();
        gms.sendPlainTextEmail(email,
                "Tak for din forespørgsel!",
                "Kære " + name + " Det glæder os at du skal ha en ny carport! " +
                        "Vi kontroller mål og dimensioner og vender tilbage hurtigst muligt " +
                        "mvh. Fog");

        ctx.sessionAttribute("request_sent",true);
        ctx.render("/index",Map.of("request_sent",true));
        ctx.redirect("/");
    }

    private static void sendRequest(Context ctx) throws DatabaseException, MessagingException {
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



        Calculator calc = new Calculator(SpecificationWizard.makeASpecification(width,length,roof,shedWidth,shedLength));

        System.out.println(calc.setItemList());
        GmailSender gms = new GmailSender();
        gms.sendPlainTextEmail(email,
                "Tak for din forespørgsel!",
                " kære " + name + " Det glæder os at du skal ha en ny carport! " +
                        "Vi kontroller mål og dimensioner og vender tilbage hurtigst muligt " +
                        "mvh. Fog");
        ctx.sessionAttribute("request_sent",true);
        ctx.redirect("/");
    }


}
