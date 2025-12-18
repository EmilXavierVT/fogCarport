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
import java.util.List;
import java.util.Map;

public class AdminController
{
    public static void addRoutes(Javalin app)
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        app.get("/admin/alert", ctx -> showAdminDashboard(ctx, connectionPool));
        app.post("/update_price", ctx -> updatePrice(ctx, connectionPool));
        app.get("/admin/construction/{id}", ctx -> showConstructionPage(ctx, connectionPool));
        app.post("/admin/construction", ctx -> sendEmail(ctx));
        app.post("/send_acceptance_offer", ctx -> sendAcceptanceOffer(ctx));
        app.post("/delete_offer", ctx -> deleteOffer(ctx, connectionPool));
        app.post("/admin/update_request", ctx -> updateRequest(ctx, connectionPool));
        app.post("/update_offer_price/{id}", ctx -> updateOfferPrice(ctx));
        app.post("/admin/update_request_alert", ctx -> updateRequestAlert(ctx, connectionPool));

    }

    private static void deleteOffer(Context ctx, ConnectionPool connectionPool) throws SQLException, DatabaseException
    {
        CarportRequest rq = ctx.sessionAttribute("carport_request");
        CarportMapper.changeTypeToDeletedByID(rq.getCarport().getCarportID(), connectionPool);
        ctx.redirect("/admin/alert");
    }

    private static void sendAcceptanceOffer(Context ctx) throws MessagingException
    {
        GmailSender gmailSender = new GmailSender();
        CarportRequest rq = ctx.sessionAttribute("carport_request");
        User user = rq.getUser();
        int id = rq.getCarportRequestID();
        String to = user.getEmail();
        String subject = "Tillykke du skal ha en carport fra FOG";
        String body = "Vi har vurderet at din carport kan bygges! " + "Her  er et link til at bekræfte tilbuddet: \n" +
                "http://carport.project-ice.dk/accept_offer/" + id + "\n" +
                "DET ER ESSENTIELT AT VÆRE LOGGET IND FØR DU TRYKKER PÅ LINKET!";
        gmailSender.sendPlainTextEmail(to, subject, body);
        ctx.sessionAttribute("email_sent_message", "Email sendt til " + user.getFirstName() + " " + user.getLastName() + ". Med e-mail: " + to);
        ctx.redirect("/admin/alert");
    }

    private static void updateOfferPrice(Context ctx)
    {
        double markupPercentage = ctx.formParam("markup_percentage") == null ? 1.39 : Double.parseDouble(ctx.formParam("markup_percentage"));
        CarportRequest req = ctx.sessionAttribute("carport_request");
        ctx.sessionAttribute("markup_percentage", 1 + (markupPercentage / 100));
        ctx.redirect("/admin/construction/" + req.getCarportRequestID());
    }

    private static void updateRequest(Context ctx, ConnectionPool connectionPool) throws SQLException, DatabaseException
    {
        CarportRequest req = ctx.sessionAttribute("carport_request");
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        boolean shed = Integer.parseInt(ctx.formParam("shed")) == 0 ? false : true;
        int shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        int shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        int roof = Integer.parseInt(ctx.formParam("roof"));
        SpecificationMapper.updateSpecification(req.getCarportRequestID(), width, length, shed, shedWidth, shedLength, roof, connectionPool);
        ctx.redirect("/admin/construction/" + req.getCarportRequestID());
    }

    private static void updateRequestAlert(Context ctx, ConnectionPool connectionPool) throws SQLException, DatabaseException
    {
        String requestIDString = ctx.formParam("carport_request_id");
        int requestID = Integer.parseInt(requestIDString);
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        boolean shed = Boolean.parseBoolean(ctx.formParam("shed"));
        int shedWidth = Integer.parseInt(ctx.formParam("shed_width"));
        int shedLength = Integer.parseInt(ctx.formParam("shed_length"));
        int roof = CarportRequestMapper.getCarportByRequestID(requestID,connectionPool).getCarport().getSpecification().getRoof().getProductID();
        SpecificationMapper.updateSpecification(requestID, width, length, shed, shedWidth, shedLength, roof, connectionPool);
        ctx.redirect("/admin/alert/");
    }

    private static void sendEmail(Context ctx) throws MessagingException
    {
        GmailSender mailSender = new GmailSender();
        CarportRequest req = ctx.sessionAttribute("carport_request");
        String to = req.getUser().getEmail();
        String subject = ctx.formParam("email_subject");
        String body = ctx.formParam("email_body");
        mailSender.sendPlainTextEmail(to, subject, body);
        ctx.sessionAttribute("email_sent_message", "Email sendt til " + to);
        ctx.redirect("/admin/alert");
    }

    private static void showConstructionPage(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CarportRequestMapper.updateStatus(id, 1, connectionPool);
            CarportRequest cr = CarportRequestMapper.getCarportByRequestID(id, connectionPool);
            ctx.sessionAttribute("carport_request", cr);

            if (cr != null)
            {
                Specification sp = cr.getCarport().getSpecification();
                Calculator calc = new Calculator(sp);
                List<ProductInOrder> itemList = calc.setItemList();
                ctx.sessionAttribute("item_list", itemList);
                DecimalFormat df = new DecimalFormat("#.00");
                df.setRoundingMode(RoundingMode.HALF_UP);

                double markupPercentage = ctx.sessionAttribute("markup_percentage") == null ? 1.39 : ctx.sessionAttribute("markup_percentage");
                double salesCost = 5;
                double costPrice = salesCost * calc.getCostPrice();
                double actualOffer = (costPrice * markupPercentage) * 1.25;

                Svg svg = UserDefinedController.showDrawing(sp.getWidth(), sp.getLength(), sp.getShedWidth(), sp.getShedDepth(), sp);
                ctx.attribute("svg", svg.toString());
                ctx.render("admin/construction.html", Map.of("carport_request", cr, "item_list", itemList, "cost_price", costPrice, "actual_offer", actualOffer));
            }
            else
            {
                throw new RuntimeException("Svg Could not be found");
            }
        }
        catch (DatabaseException | SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void updatePrice(Context ctx, ConnectionPool connectionPool)
    {
        int productId = Integer.parseInt(ctx.formParam("product_id"));
        float newPrice = Float.parseFloat(ctx.formParam("new_price"));
        ProductMapper.updateProductPrice(productId, newPrice, connectionPool);
        ctx.sessionAttribute("price_update_message", "Produkt pris opdateret !");
        ctx.redirect("/admin/alert");
    }

    private static void showAdminDashboard(Context ctx, ConnectionPool connectionPool)
    {
        try
        {
            List<User> users = UserMapper.getAllUsers(connectionPool);
            List<CarportRequest> requests = CarportRequestMapper.getAllCarportRequests(connectionPool);
            List<Carport> standardCarports = CarportMapper.getAllStandardCarportForSlider(connectionPool);
            List<Product> products = ProductMapper.getAllProducts(connectionPool);
            requests = requests.stream().filter(cr -> cr.getStatus() == 0 || cr.getStatus() == 1).toList();
            requests = requests.stream().filter((cr -> cr.getCarport().getType() == 70)).toList();

            ctx.render("admin/alert.html", Map.of("all_users", users,
                    "all_carport_requests", requests,
                    "all_standard_carports", standardCarports,
                    "all_products", products));

        }
        catch (DatabaseException e)
        {
            System.out.println("showAdminDashboard signature: Could not show admin dashboard" + e.getMessage());
            ctx.redirect("/");
        }
    }
}

