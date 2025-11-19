package app.enteties;

import java.time.LocalDate;


public class Order {
int id;
int userId;
LocalDate date;
int discountId;

    public Order(int id, int userId, LocalDate date, int discountId) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.discountId = discountId;
    }
}
