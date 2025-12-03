package app.entities;


public class StandardCarport extends Carport
{
int standardId;
String name;
float price;
int type;
String description;
int specification;
String pdfFile;


    public StandardCarport(int standardId, String name, float price, int type, String description, int specification, String pdf_file)
    {
        super(standardId,name,price,type,description,specification);
        this.pdfFile = pdf_file;
    }

}
