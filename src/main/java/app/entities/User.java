package app.entities;

import lombok.Data;

@Data
public class User
{
int userId;
String firstName;
String lastName;
int zipCode;
String streetName;
int houseNumber;
String floor;
String email;
String password;
int role;

    public User(int userId, String firstName, String lastName, int zip_code, String streetName, int houseNumber, String floor, String email, String password,int role)
    {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zip_code;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.floor = floor;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
