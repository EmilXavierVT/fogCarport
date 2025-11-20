package app.entities;

public class StandardCarport {
int standardId;
String name;
int price;
int type;
String description;
int specification;
String pdfFile;
    public StandardCarport(int standardId, String name, int price, int type, String description, int specification, String pdf_file) {
        this.standardId = standardId;
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
        this.specification = specification;
        this.pdfFile = pdf_file;
    }
}
