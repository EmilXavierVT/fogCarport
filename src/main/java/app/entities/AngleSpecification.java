package app.entities;

public class AngleSpecification extends Specification {
   private int angle;


    public AngleSpecification(long EAN, String model, int roomFor, boolean shed, Product post, Product beam, Product rafter, Product roof, Product fasciaBoard, int length, int width, int heightFront, int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength, int parkingWidth, int shedDepth, int shedWidth, int angle) {
        super(EAN, model, roomFor, shed, post, beam, rafter, roof, fasciaBoard, length, width, heightFront, heightRear, roofLength, roofWidth, exteriorWidthAtPost, parkingLength, parkingWidth, shedDepth, shedWidth);
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }
}
