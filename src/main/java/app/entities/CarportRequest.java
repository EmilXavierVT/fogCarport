package app.entities;

import lombok.Data;
@Data

public class CarportRequest
{
    private int carportRequestID;
    private User user;
    private Carport carport;
    private User salesRep;
    private int status;

    public CarportRequest(int carportRequestID, User userByID, Carport carportByID, User salesRepByID)
    {
        this.carportRequestID = carportRequestID;
        this.user = userByID;
        this.carport = carportByID;
        this.salesRep = salesRepByID;
    }
    public CarportRequest(int carportRequestID, User userByID, Carport carportByID, User salesRepByID, int status)
    {
        this.carportRequestID = carportRequestID;
        this.user = userByID;
        this.carport = carportByID;
        this.salesRep = salesRepByID;
        this.status = status;
    }
}
