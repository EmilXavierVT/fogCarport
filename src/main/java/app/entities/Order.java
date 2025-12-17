package app.entities;

import java.time.LocalDate;
import lombok.Data;
@Data

public class Order
{
int id;
User userId;
LocalDate date;

    public Order(int id, User userId, LocalDate date)
    {
        this.id = id;
        this.userId = userId;
        this.date = date;
    }
}
