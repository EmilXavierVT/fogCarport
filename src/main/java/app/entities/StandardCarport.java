package app.entities;

public class StandardCarport extends Carport
{
String pdfFile;


    public StandardCarport(int standardId, String name, float price, int type, String description, Specification specification, String pdf_file)
    {
        super(standardId,name,price,type,description,specification);
        this.pdfFile = pdf_file;
    }
    public String getPdf(){
        return this.pdfFile;
    }
}
