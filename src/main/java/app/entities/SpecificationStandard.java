package app.entities;

public class SpecificationStandard extends Specification{

    public SpecificationStandard(int specificationId, int EAN, String model, String roomFor, boolean shed, int post, int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront, int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength, int parkingWidth, int shedDepth, int shedWidth) {
        super(specificationId, EAN, model, roomFor, shed, post, beam, rafter, roof, fasciaBoard, length, width, heightFront, heightRear, roofLength, roofWidth, exteriorWidthAtPost, parkingLength, parkingWidth, shedDepth, shedWidth);
    }

}
