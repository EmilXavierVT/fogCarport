package app;

import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.CarportController;
import app.controllers.OrderController;
import app.controllers.UserController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class App
{
    public static void initiate()
    {
        Javalin app = Javalin.create(config ->
        {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.staticFiles.add("/templates");
        }).start(7071);
        // Routing
        // add controllers here
        UserController.addRoutes(app);
        CarportController.addRoutes(app);
        AdminController.addRoutes(app);

        //slet senere
        app.get("/showOrder", ctx -> OrderController.showOrder(ctx));

    }

}
