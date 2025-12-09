package app;

import app.entities.Carport;
import app.exceptions.DatabaseException;
import app.persistence.CarportMapper;
import app.persistence.ConnectionPool;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws DatabaseException
    {
        App.initiate();
    }

}
