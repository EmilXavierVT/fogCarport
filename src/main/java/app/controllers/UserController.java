package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.*;
import app.services.Calculator;
import app.services.Svg;
import app.util.GmailSender;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.mail.MessagingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UserController
{
    public static void addRoutes(Javalin app)
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        app.get("/logout", ctx -> logout(ctx));
        app.post("/login", ctx -> login(ctx,connectionPool));
        app.get("/register_password", ctx -> ctx.render("register_password.html"));
        app.post("/register_password", ctx -> createUser(ctx, connectionPool));
        app.get("/create_user", ctx -> ctx.render("create_user.html"));
        app.get("/view_order",ctx-> showOrderPage(ctx, connectionPool));
        app.get("/accept_offer/{id}", ctx -> showAcceptPage(ctx,connectionPool));
        app.post("/accept_offer",ctx-> acceptOffer(ctx, connectionPool));
        app.post("/decline_offer",ctx-> declineOffer(ctx, connectionPool));

        app.post("/create_user", ctx -> registerInfo(ctx, connectionPool));
    }

    private static void showAcceptPage(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        int id = Integer.parseInt(ctx.pathParam("id"));

        try
        {
            CarportRequest cr = CarportRequestMapper.getCarportByRequestID(id, connectionPool);
            ctx.sessionAttribute("carport_request", cr);
            ctx.render("/accept_offer.html", Map.of("carport_request", cr));
        }
        catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static void declineOffer(Context ctx, ConnectionPool connectionPool) throws SQLException, DatabaseException, MessagingException
    {
        CarportRequest rq = CarportRequestMapper.getCarportByRequestID(Integer.parseInt(ctx.formParam("carport_request_id")),connectionPool);
        User salesRep = rq.getSalesRep();
        User user = rq.getUser();
        GmailSender mailSender = new GmailSender();
        mailSender.sendPlainTextEmail(salesRep.getEmail(),"Kunden har ikke accepteret tilbuddet", "kunden " +  user.getFirstName() + " " + user.getLastName()
                + " har afslået jeres carport tilbud" + "\n" + "CarportRequest nr: " + rq.getCarportRequestID() + ". " +
                "\n" + " kunden kan kontaktes på telefon: " + user.getPhoneNumber() + " eller på pr. mail: " + user.getEmail() );
        ctx.render("/");
    }

    private static void acceptOffer(Context ctx, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        CarportRequest cr = CarportRequestMapper.getCarportByRequestID(Integer.parseInt(ctx.formParam("carport_request_id")),connectionPool);
        CarportRequestMapper.updateStatus(cr.getCarportRequestID(), 2,connectionPool);
        Order order = OrderMapper.saveOrderAndReturn(cr.getUser().getUserId(), LocalDate.now(),connectionPool);

        for(ProductInOrder productInOrder : new Calculator(cr.getCarport().getSpecification()).setItemList())
        {
            ProductInOrderMapper.createProductInOrder(order.getId(), productInOrder.getProduct(), productInOrder.getAmount(), connectionPool);
        }
        ctx.redirect("/view_order");
    }

    public static void showOrderPage(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
        CarportRequest cr  = ctx.sessionAttribute("carport_request");
        Specification sp = cr.getCarport().getSpecification();
        Calculator calc = new Calculator(sp);
        List<ProductInOrder> itemList = calc.setItemList();
        ctx.sessionAttribute("item_list", itemList);
        Svg svg = UserDefinedController.showDrawing(sp.getWidth(), sp.getLength(), sp.getShedWidth(), sp.getShedDepth(), sp);
        ctx.attribute("svg", svg.toString());
        ctx.render("/view_order", Map.of("carport_request", cr, "item_list",itemList));
    }
    catch (DatabaseException e)
    {
        throw new RuntimeException(e);
        }
    }

    public static boolean login(Context ctx,ConnectionPool connectionPool)
    {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try
        {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.attribute("currentUser", user);

            if(UserMapper.checkIfAdmin(user,connectionPool) == 1)
            {
                ctx.sessionAttribute("admin", true);
                ctx.attribute("message", "Du er nu logget ind som admin.");

                ctx.redirect("/admin/alert");
                ctx.render("/admin/alert.html", Map.of("message", "Du er nu logget ind som admin."));
                return true;
            }
            else
            {
                ctx.sessionAttribute("admin", false);
                ctx.attribute("loginMessage", "Du er nu logget ind");

                ctx.redirect("/");
                ctx.render("/index.html", Map.of("loginMessage", "Du er nu logget ind"));
                return true;
            }
        }
        catch (DatabaseException e)
        {
            ctx.sessionAttribute("error_login", "login fejlede!");
            System.out.println("login logs errors");
            ctx.redirect("/");
            ctx.render("/index.html",Map.of("error_login", "login fejlede!"));
            return false;
        }
    }

    private static void logout(Context ctx)
    {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private static void createUser(Context ctx, ConnectionPool connectionPool)
    {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirm_password");

        if (password.equals(confirmPassword))
        {
            try
            {
                User user = UserMapper.createUser(email, password, connectionPool);
                ctx.sessionAttribute("currentUser", user);
                ctx.attribute("message", "Du er hermed oprettet med email: " + email + ". Nu skal du logge på.");
                ctx.render("create_user.html", Map.of("currentUser", user));
            }
            catch (DatabaseException e)
            {
                ctx.sessionAttribute("register_password_error", "Den e-mail findes allerede. Prøv igen, eller log ind");
                ctx.render("register_password.html");
            }
        }
        else
        {
            ctx.sessionAttribute("register_password_error", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("register_password.html");
        }
    }

    public static void registerInfo(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        try
        {
            String firstName = ctx.formParam("first_name");
            String lastName = ctx.formParam("last_name");
            int phoneNumber = Integer.parseInt(ctx.formParam("phone_number"));
            String streetName = ctx.formParam("street_name");
            String floor = ctx.formParam("floor");
            int zipCode = Integer.parseInt(ctx.formParam("post_code"));
            int streetNumber = Integer.parseInt(ctx.formParam("street_number"));
            User user = ctx.sessionAttribute("currentUser");
            int userId = user.getUserId();
            user = UserMapper.updateUser(userId, firstName, lastName, zipCode, streetName, streetNumber, floor, phoneNumber, connectionPool);

            ctx.sessionAttribute("currentUser", user);
            ctx.sessionAttribute("message", "Du har opdateret din profil !");

            ctx.redirect("/");
            ctx.render("index.html");

        }
        catch (NumberFormatException | DatabaseException e)
        {
            ctx.sessionAttribute("register_info_error", "Der skete en fejl under opdatering af din profil, prøv igen !");
            ctx.render("create_user.html");
        }
    }
}