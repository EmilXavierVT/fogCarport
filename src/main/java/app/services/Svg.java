package app.services;

public class Svg {
    private static final String SVG_TEMPLATE = "<svg version=\"1.1\"\n" +
            "     x=\"%d\" y=\"%d\"\n "+
            "     viewBox=\"%s\" width=\"%s\" \n" +
            "     height=\"%s\" preserveAspectRatio=\"xMinYMin\">";

    private static final String SVG_RECT_TEMPLATE = "    <rect x=\"%d\" y=\"%d\" height=\"%f\" width=\"%f\" style=\"%s\"/>\n";


    private static final String SVG_ARROW_DEFS = "<defs>\n" +
            "        <marker id=\"beginArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"0\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill: #000000;\" />\n" +
            "        </marker>\n" +
            "        <marker id=\"endArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"12\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,0 L12,6 L0,12 L0,0 \" style=\"fill: #000000;\" />\n" +
            "        </marker>\n" +
            "    </defs>";

    private static final String SVG_LINE_TEMPLATE = " <line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"%s\" />\n";

    private static final String SVG_ARROW_LINE_TEMPLATE = "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:#000000;\n" +
            "        marker-start: url(#beginArrow);\n" +
            "        marker-end: url(#endArrow);\" />";

    private static final String SVG_TEXT_TEMPLATE = "    <text style=\"text-anchor: middle\" transform=\"translate(%d,%d) rotate(%d)\">%s</text>\n";
    private StringBuilder svg = new StringBuilder();


    public Svg (int x, int y, String viewBox, String width, String height)
    {
     svg.append(String.format(SVG_TEMPLATE, x, y, viewBox, width, height));
     svg.append(SVG_ARROW_DEFS);
    }

    public void addRectangle(int x, int y, double width, double length, String style)
    {
    svg.append(String.format(SVG_RECT_TEMPLATE, x, y, width, length, style));

    }
    public void addLine(int x1, int y1, int x2, int y2, String style)
    {
    svg.append(String.format(SVG_LINE_TEMPLATE, x1, y1, x2, y2, style));
    }

    public void addArrow(int x1, int y1, int x2, int y2)
    {
    svg.append(String.format(SVG_ARROW_LINE_TEMPLATE, x1, y1, x2, y2));
    }

    public void addText(int x, int y, int rotation, String text)
    {
    svg.append(String.format(SVG_TEXT_TEMPLATE, x, y, rotation, text));
    }

    public void addSvg(Svg innerSvg)
    {
    svg.append(innerSvg.toString());
    }

    public String toString(){
        return svg.append("</svg>").toString();
    }

    public void beginGroup(String attributes) {
        this.svg.append("<g " + attributes + ">");
    }

    public void endGroup() {
        this.svg.append("</g>");
    }
}
