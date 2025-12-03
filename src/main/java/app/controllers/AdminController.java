package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;

public class AdminController {
    public static void addRoutes(Javalin app) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        app.get("/admin/alert", ctx -> ctx.render("admin/alert.html"));
        
    }
}
