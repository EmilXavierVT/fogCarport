package app.controllers;

import app.entities.Carport;
import app.entities.Specification;
import app.entities.User;
import app.entities.UserDefinedCarport;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.CarportRequestMapper;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import app.services.Calculator;
import app.services.SpecificationWizard;
import app.services.Svg;
import app.util.GmailSender;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UserDefinedController {
    public static int width;
    public static int length;
    public static int shedWidth;
    public static int shedLength;
    private static Calculator calc;

    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
       app.get("/userdefined",ctx -> ctx.render("userdefined.html"));
       app.get("/flat",ctx->ctx.render("flat.html"));
       app.post("/flat",ctx-> sendRequest(ctx, connectionPool));
       app.get("/angle", ctx -> ctx.render("angle"));
       app.post("/angle", ctx -> sendAngleRequest(ctx, connectionPool));
//       app.get("/show_drawing", ctx -> showDrawing(ctx));
    }

    private static void sendAngleRequest(Context ctx,ConnectionPool connectionPool) throws DatabaseException, MessagingException {
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



        int carportID = CarportMapper.SaveAndGetCarportInDB(name,calc.getCostPrice(),70,"custom",calc.getSpecification().getSpecificationId(),connectionPool);
        User salesRep = UserMapper.getUserByID(29,connectionPool);
        if(ctx.sessionAttribute("currentUser") != null)
        {
            User user = ctx.sessionAttribute("currentUser");
            CarportRequestMapper.createCarportRequest(user.getUserId(),carportID,salesRep.getUserId(),connectionPool);
        }
        else
        {
            User user= UserMapper.getUserByEmail(email,connectionPool);
            if(user !=null) {
                CarportRequestMapper.createCarportRequest(user.getUserId(), carportID, salesRep.getUserId(), connectionPool);
            }
            else {
                User newUser = UserMapper.createUser(name," ",zipCode,address,0," ",email," ",connectionPool);
                CarportRequestMapper.createCarportRequest(newUser.getUserId(),carportID,salesRep.getUserId(),connectionPool);
            }
        }

        System.out.println(calc.setItemList());
        GmailSender gms = new GmailSender();
        gms.sendPlainTextEmail(email,
                "Tak for din forespørgsel!",
                "Kære " + name + " Det glæder os at du skal ha en ny carport! " +
                        "Vi kontroller mål og dimensioner og vender tilbage hurtigst muligt " +
                        "mvh. Fog");
        gms.sendPlainTextEmail(salesRep.getEmail(), "Ny forespørgsel er landet i din indbakke", "Hej " + salesRep.getFirstName() + ", " +
                " Der er kommet en ny forespørgsel fra " + name + " de kan kontaktes på " + email + " eller " + phoneNumber + ".");

        ctx.sessionAttribute("request_sent",true);
        ctx.render("/index",Map.of("request_sent",true));
        ctx.redirect("/");
    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException, MessagingException, SQLException {
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

        int carportID = CarportMapper.SaveAndGetCarportInDB(name,  calc.getCostPrice(),70,"custom",calc.getSpecification().getSpecificationId(),connectionPool);
        User salesRep = UserMapper.getUserByID(29,connectionPool);
        if(ctx.sessionAttribute("currentUser") != null)
        {
            User user = ctx.sessionAttribute("currentUser");
            CarportRequestMapper.createCarportRequest(user.getUserId(),carportID,salesRep.getUserId(),connectionPool);
        }
        else
        {
            User user= UserMapper.getUserByEmail(email,connectionPool);
            if(user !=null) {
                CarportRequestMapper.createCarportRequest(user.getUserId(), carportID, salesRep.getUserId(), connectionPool);
            }
            else {
                User newUser = UserMapper.createUser(name," ",zipCode,address,0," ",email," ",connectionPool);
                CarportRequestMapper.createCarportRequest(newUser.getUserId(),carportID,salesRep.getUserId(),connectionPool);
            }
            }

        GmailSender gms = new GmailSender();
        gms.sendPlainTextEmail(email,
                "Tak for din forespørgsel!",
                " kære " + name + " Det glæder os at du skal ha en ny carport! " +
                        "Vi kontroller mål og dimensioner og vender tilbage hurtigst muligt " +
                        "mvh. Fog");
        gms.sendPlainTextEmail(salesRep.getEmail(), "Ny forespørgsel er landet i din indbakke", "Hej " + salesRep.getFirstName() + ", " +
                " Der er kommet en ny forespørgsel fra " + name + " de kan kontaktes på " + email + " eller " + phoneNumber + ".");
        ctx.sessionAttribute("request_sent",true);
        ctx.redirect("/");
    }

    public static Svg showDrawing( int width, int length, int shedWidth, int shedLength, Specification specification) {

        Locale.setDefault(Locale.US);
        Calculator calc = new Calculator(specification);

        //creating scale around carport
        Svg scale = new Svg(0,0,"0 0 855 690","100%","auto");


        scale.addLine(50,10,50,width/2+20,"stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");
        scale.addLine(80,width/2 + 45,length/2+100,width/2 +45,"stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");

        scale.addText(30,width/3-10,-90, String.valueOf(width) + " cm");
        scale.addText(length/3 +55,width/2 + 60,0, String.valueOf(length) + " cm");


        //carport
        Svg carportSvg = new Svg(75,10,"0 0 780 600","50%","auto");


        //ramme
        carportSvg.addRectangle(0,0,width,length,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //remme
        carportSvg.addRectangle(0,30,4,length,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        carportSvg.addRectangle(0,width-30,4,length,"stroke-width:1px; stroke:#000000; fill:#ffffff");



        if(shedLength == 0 && shedWidth == 0 ) {
            //stolper
            carportSvg.addRectangle(30, 27, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
            carportSvg.addRectangle(30, width - 33, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");

            carportSvg.addRectangle(length - 100, 27, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
            carportSvg.addRectangle(length - 100, width - 33, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");

            int spaceBettwenStartAndEnd = length - 130;
            int startPole = 30;
            int postsPerSide = (calc.getAmountOfPosts() - 4) / 2;
            int spaceBettwenEachpost = spaceBettwenStartAndEnd / (postsPerSide + 1);

            while (postsPerSide > 0) {
                startPole += spaceBettwenEachpost;
                carportSvg.addRectangle(startPole, 27, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                carportSvg.addRectangle(startPole, width - 33, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                postsPerSide--;
            }
        }

        if(shedLength > 0 && shedWidth > 0 )
        {
            //skur
            carportSvg.addRectangle(30,27,shedWidth,shedLength,"stroke-width:2px; stroke:#000000; fill:#ffffff");

            //øverst venstre
            carportSvg.addRectangle(30,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");

            //æverst højre
            carportSvg.addRectangle(shedLength+20,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");

            //nederst venstre
            carportSvg.addRectangle(30,shedWidth+17,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");

            //nederst højre
            carportSvg.addRectangle(shedLength+20,shedWidth+17,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");

            //carport nederst venstre
            carportSvg.addRectangle(30, width - 33, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");

            //øverste højre stolpe
            carportSvg.addRectangle(length - 100, 27, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");

            //nederste stolpte højer
            carportSvg.addRectangle(length - 100, width - 33, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");

            int spaceBettwenBottomStartAndEnd = length - 130;
            int startBottomPole = 30;
            int postsPerSide = (calc.getAmountOfPosts() - 4) / 2;
            int bottomPostAmount = (calc.getAmountOfPosts() - 4) / 2;
            int spaceBettwenEachBottompost = spaceBettwenBottomStartAndEnd / (postsPerSide + 1);

            // nederste stolper midden
            while (bottomPostAmount > 0) {
                startBottomPole += spaceBettwenEachBottompost;
                carportSvg.addRectangle(startBottomPole, width - 33, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                bottomPostAmount--;
            }

            //en stolpe brede
            if(shedWidth >= 310 && shedWidth <= 620)
            {
                carportSvg.addRectangle(30,(shedWidth/2)+22,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
                carportSvg.addRectangle(shedLength+20,(shedWidth/2)+22,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
            }

            // 1 stolpe længde
            if(shedLength >= 310 && shedLength <= 620) {
                carportSvg.addRectangle((shedLength / 2) + 30, 27, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                carportSvg.addRectangle((shedLength / 2) + 30, shedWidth + 17, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                postsPerSide--;
            }

            //2 stoler længde
            if (shedLength >= 620) {
                    int spaceBettwenStartAndEnd = shedLength / 3;
                    int startPole = 30;
                    int postToPlace = 2;

                    while (postToPlace > 0) {
                        startPole += spaceBettwenStartAndEnd;
                        carportSvg.addRectangle(startPole, 27, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                        carportSvg.addRectangle(startPole, shedWidth + 17, 10, 10, "stroke-width:2px; stroke:#000000; fill:#ffffff");
                        postToPlace--;
                        postsPerSide--;
                    }
                }
            if(length-100-shedLength > 130){
                carportSvg.addRectangle(shedLength+130+30,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
            }
            if(length-100-shedLength > 460){
                int firstPost = shedLength+130+30;
                int space = length-100-firstPost;
                int secondPost = space/2;
                carportSvg.addRectangle(firstPost,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
                carportSvg.addRectangle(firstPost+secondPost,27,10,10,"stroke-width:2px; stroke:#000000; fill:#ffffff");
            }
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
        //stolpe mellem skur og sidste stolpte øverst højre

        //dash lines
        carportSvg.addLine(55,35,length-35,width-30,"stroke:#000000; stroke-dasharray: 5 5;");
            carportSvg.addLine(55,width-30,length-35,35,"stroke:#000000; stroke-dasharray: 5 5;");

        //adding carport to scale
        scale.addSvg(carportSvg);

        return scale;


    }


}
