package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class UserController {

    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        app.get("logout", ctx -> logout(ctx) );
        app.post("/login", ctx -> login(ctx,connectionPool));
    }


    public static void login(Context ctx,ConnectionPool connectionPool)
    {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try
        {
            User user = UserMapper.login(email, password,connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.attribute("currentUser", user);

            if(UserMapper.checkIfAdmin(user,connectionPool) == 1)
            {
                ctx.sessionAttribute("admin", true);
                ctx.attribute("message", "Du er nu logget ind som admin.");

                ctx.redirect("/adminIndex");
                ctx.render("adminPages/adminIndex.html", Map.of("message", "Du er nu logget ind som admin."));
            }
            else
            {
                ctx.sessionAttribute("admin", false);
                ctx.sessionAttribute("loginMessage", "Du er nu logget ind");
                ctx.redirect("/profile-page");
                ctx.render("/profile-page", Map.of("loginMessage", "Du er nu logget ind"));
            }
        }
        catch (DatabaseException e)
        {
            ctx.sessionAttribute("errorLogin", "login fejlede!");
            System.out.println("login logs errors");
            ctx.redirect("/login");
            ctx.render("/login",Map.of("errorLogin", "login fejlede!"));
        }
    }

    private static void logout(Context ctx)
    {
        ctx.req().getSession().invalidate();

        ctx.redirect("/");
    }

}
