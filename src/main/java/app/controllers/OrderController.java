package app.controllers;

import app.services.Svg;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public class OrderController {


    public static void showOrder(@NotNull Context ctx) {
        Locale.setDefault(Locale.US);

        int length = 550;
        int width = 600;

        Svg scale = new Svg(0,0,"0 0 855 690","100%","auto");
        scale.addLine(40,10,40,350,"stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");
        scale.addLine(75,380,500,380,"stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");

        scale.addText(30,175,-90, String.valueOf(length) + " cm");
        scale.addText(275,395,0, String.valueOf(width) + " cm");


        Svg carportSvg = new Svg(75,10,"0 0 780 600","50%","auto");
        //ramme
        carportSvg.addRectangle(0,0,length,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //remme
        carportSvg.addRectangle(0,30,4,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        carportSvg.addRectangle(0,length-30,4,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //stolper
        for (int i=100; i< width ; i +=310){
            carportSvg.addRectangle(i,26,10,10,"stroke-width:1px; stroke:#000000; fill:#ffffff");
            carportSvg.addRectangle(i,length-34,10,10,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        }

        //dash lines
        carportSvg.addLine(55,35,width-35,length-30,"stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(55,length-30,width-35,35,"stroke:#000000; stroke-dasharray: 5 5;");

        scale.addSvg(carportSvg);

        ctx.attribute("svg",scale.toString());
        ctx.render("showOrder.html");
    }
}
