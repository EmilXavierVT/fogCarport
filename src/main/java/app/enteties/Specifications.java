package app.enteties;

public class Specifications {
    int specificationId;
    int EAN;
    String model;
    String roomFor;
    boolean shed;
    int post;
    int beam;
    int rafter;
    int roof;
    int fasciaBoard;
    int length;
    int width;
    int heightFront;
    int heightRear;
    int roofLength;
    int roofWidth;
    int exteriorWidthAtPost;
    int parkingLength;
    int parkingWidth;
    int shedDepth;
    int shedWidth;

    public Specifications(int specificationId,int EAN, String model, String roomFor,boolean shed, int post,
                          int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront,
                          int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength,
                          int parkingWidth, int shedDepth, int shedWidth) {
        this.specificationId = specificationId;
        this.EAN = EAN;
        this.model = model;
        this.roomFor = roomFor;
        this.shed = shed;
        this.post = post;
        this.beam = beam;
        this.rafter = rafter;
        this.roof = roof;
        this.fasciaBoard = fasciaBoard;
        this.length = length;
        this.width = width;
        this.heightFront = heightFront;
        this.heightRear = heightRear;
        this.roofLength = roofLength;
        this.roofWidth = roofWidth;
        this.exteriorWidthAtPost = exteriorWidthAtPost;
        this.parkingLength = parkingLength;
        this.parkingWidth = parkingWidth;
        this.shedDepth = shedDepth;
        this.shedWidth = shedWidth;
    }
}
