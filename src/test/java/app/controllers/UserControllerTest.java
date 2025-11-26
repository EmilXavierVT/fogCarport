package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;





import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = "jdbc:postgresql://128.199.42.25:5432/%s?currentSchema=test_schema";
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER,PASSWORD,URL,DB);


    @Test
    void login() throws DatabaseException {
        UserMapper.createUser("emil","thorsen",2200,"farumgade",1,"2th","ex@tv.dk","1234",connectionPool);
        // 1. Mock Context
        Context ctx = mock(Context.class);

        // 2. Setup what you want the formParam() call to return
        when(ctx.formParam("email")).thenReturn("ex@tv.dk");
        when(ctx.formParam("password")).thenReturn("1234");

       UserController.login(ctx,connectionPool);

       assertNotNull(ctx.sessionAttribute("currentUser"));
    }
}