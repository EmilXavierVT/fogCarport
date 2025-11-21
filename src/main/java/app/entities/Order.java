package app.entities;

import java.time.LocalDate;


public class Order {
int id;
int userId;
LocalDate date;


    public Order(int id, int userId, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.date = date;

    }
}
