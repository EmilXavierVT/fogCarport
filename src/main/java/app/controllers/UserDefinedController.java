package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;

public class UserDefinedController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
       app.get("userdefined",ctx -> ctx.render("userdefined.html"));

    }

}
