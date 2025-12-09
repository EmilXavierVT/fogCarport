package app.entities;

public class StandardCarport extends Carport
{
String pdfFile;


    public StandardCarport(int standardId, String name, float price, int type, String description, Specification specification, String pdf_file)
    {
        super(standardId,name,price,type,description,specification);
        this.pdfFile = pdf_file;
    }

    public StandardCarport(int carportID, String name, float price, int type, String productionDescription, String pdfFile) {
        super(carportID, name, price, type, productionDescription);
        this.pdfFile = pdfFile;
    }

    public String getPdf(){
        return this.pdfFile;
    }
}
