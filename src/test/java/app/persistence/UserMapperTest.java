package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class UserMapperTest {


    @Test
    void getUserByID() throws DatabaseException
    {
        User real = UserMapper.getUserByID(1);
        User expected = new User(1,"Frederik","Edvardsen",2450,"vej",39,"1tv","fred@dk.dk", "1234",1);

        assertEquals(real.getFirstName(),expected.getFirstName());
        assertEquals(real.getLastName(),expected.getLastName());
        assertEquals(real.getZipCode(),expected.getZipCode());
        assertEquals(real.getStreetName(),expected.getStreetName());
        assertEquals(real.getHouseNumber(),expected.getHouseNumber());
        assertEquals(real.getFloor(),expected.getFloor());
        assertEquals(real.getEmail(),expected.getEmail());
        assertEquals(real.getPassword(),expected.getPassword());
        assertEquals(real.getRole(),expected.getRole());

    }
  
}