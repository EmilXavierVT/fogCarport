package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

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
        app.post("/create_user", ctx -> registerInfo(ctx, connectionPool));
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
                ctx.sessionAttribute("loginMessage", "Du er nu logget ind");
                ctx.redirect("/");
                ctx.render("/index.html", Map.of("loginMessage", "Du er nu logget ind"));
                return true;
            }
        }
        catch (DatabaseException e)
        {
            ctx.sessionAttribute("errorLogin", "login fejlede!");
            System.out.println("login logs errors");
            ctx.redirect("/");
            ctx.render("/index.html",Map.of("errorLogin", "login fejlede!"));
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
                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind");
                ctx.render("register_password.html");
            }
        }
        else
        {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("register_password.html");
        }
    }

    public static void registerInfo(Context ctx, ConnectionPool connectionPool) throws DatabaseException
    {
        String firstName = ctx.formParam("first_name");
        String lastName = ctx.formParam("last_name");
        String streetName = ctx.formParam("street_name");
        String floor = ctx.formParam("floor");
        int zipCode = Integer.parseInt(ctx.formParam("post_code"));
        int streetNumber = Integer.parseInt(ctx.formParam("street_number"));
        User user = ctx.sessionAttribute("currentUser");
        int userId = user.getUserId();
        user = UserMapper.updateUser(userId,firstName,lastName,zipCode,streetName,streetNumber,floor,connectionPool);
        ctx.sessionAttribute("currentUser",user);
        ctx.sessionAttribute("message","Du har opdateret din profil!");
        ctx.render("index.html");
    }
}