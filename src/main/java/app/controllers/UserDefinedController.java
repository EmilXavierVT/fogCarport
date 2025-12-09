package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.CarportRequestMapper;
import app.persistence.ConnectionPool;
import app.services.Calculator;
import app.services.SpecificationWizard;
import app.services.Svg;
import app.util.GmailSender;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UserDefinedController {
    public static int width;
    public static int length;
    public static int shedWidth;
    public static int shedLength
            ;
    private static Calculator calc;

    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
       app.get("/userdefined",ctx -> ctx.render("userdefined.html"));
       app.get("/flat",ctx->ctx.render("flat.html"));
       app.post("/flat",ctx-> sendRequest(ctx));
       app.get("/angle", ctx -> ctx.render("angle"));
       app.post("/angle", ctx -> sendAngleRequest(ctx));
       app.get("/show_drawing", ctx -> showDrawing(ctx));
    }

    private static void sendAngleRequest(Context ctx) throws DatabaseException, MessagingException {
        width = Integer.parseInt(ctx.formParam("width"));
        length = Integer.parseInt(ctx.formParam("length"));
        String roofType = ctx.formParam("roof");
        int angle = Integer.parseInt(ctx.formParam("angle"));
        shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        String remarks = ctx.formParam("remarks");
        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip_code"));
        String city = ctx.formParam("city");
        String phoneNumber = ctx.formParam("phone_number");
        String email = ctx.formParam("email");

        boolean roof = !roofType.equals("Ingen tag");

        calc = new Calculator(SpecificationWizard.makeAngleSpecification(width,length,roof,shedWidth,shedLength,angle));

        System.out.println(calc.setItemList());



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
        width = Integer.parseInt(ctx.formParam("width"));
        length = Integer.parseInt(ctx.formParam("length"));
        String roofType = ctx.formParam("roof");
        shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        String remarks = ctx.formParam("remarks");
        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip_code"));
        String city = ctx.formParam("city");
        String phoneNumber = ctx.formParam("phone_number");
        String email = ctx.formParam("email");

        boolean roof = !roofType.equals("Ingen tag");



        calc = new Calculator(SpecificationWizard.makeASpecification(width,length,roof,shedWidth,shedLength));

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

    public static void showDrawing(Context ctx) {
        Locale.setDefault(Locale.US);

        //creating scale around carport
        Svg scale = new Svg(0,0,"0 0 855 690","100%","auto");
        scale.addLine(40,10,40,350,"stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");
        scale.addLine(75,380,500,380,"stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");

        scale.addText(30,175,-90, String.valueOf(width) + " cm");
        scale.addText(275,395,0, String.valueOf(length) + " cm");


        //carport
        Svg carportSvg = new Svg(75,10,"0 0 780 600","50%","auto");
        //ramme
        carportSvg.addRectangle(0,0,width,length,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //remme
        carportSvg.addRectangle(0,30,4,length,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        carportSvg.addRectangle(0,width-30,4,length,"stroke-width:1px; stroke:#000000; fill:#ffffff");


        if(shedLength > 0 && shedWidth > 0 )
        {
            carportSvg.addRectangle(5,32,shedWidth,shedLength,"stroke-width:2px; stroke:#000000; fill:#ffffff");

            carportSvg.addRectangle(5,shedWidth+22,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
            carportSvg.addRectangle(shedLength-5,shedWidth+22,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
        }

        //spær
        int startRafter = 0;
        int endRafter = length - 5;
        int raftersBettwen = calc.getAmountOfRafters()-2;
        int spaceBettwenRafter = endRafter/(raftersBettwen+1);

        carportSvg.addRectangle(startRafter,0,width,5,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        carportSvg.addRectangle(endRafter,0,width,5,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        while (raftersBettwen>0)
        {
            startRafter += spaceBettwenRafter;
            carportSvg.addRectangle(startRafter,0,width,5,"stroke-width:1px; stroke:#000000; fill:#ffffff");
            raftersBettwen --;
        }


        //stolper
        carportSvg.addRectangle(100,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
        carportSvg.addRectangle(100,width-33,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");

        carportSvg.addRectangle(length-30,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
        carportSvg.addRectangle(length-30,width-33,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");

        int spaceBettwenStartAndEnd = length-130;
        int startPole = 100;
        int postsPerSide = (calc.getAmountOfPosts()-4)/2;
        int spaceBettwenEachpost = spaceBettwenStartAndEnd/(postsPerSide + 1);

        while(postsPerSide > 0)
        {
            startPole +=spaceBettwenEachpost;
            carportSvg.addRectangle(startPole,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
            carportSvg.addRectangle(startPole,width-33,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
            postsPerSide--;
        }



        //dash lines
        carportSvg.addLine(55,35,length-35,width-30,"stroke:#000000; stroke-dasharray: 5 5;");
            carportSvg.addLine(55,width-30,length-35,35,"stroke:#000000; stroke-dasharray: 5 5;");

        //adding carport to scale
        scale.addSvg(carportSvg);

        ctx.attribute("svg",scale.toString());
        ctx.render("/show_drawing.html");

    }
}
