package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    /**
     * creating an object so we can use it to call the
     * methods of the userContoller.class and verify them or test them
     */
    private UserController userController;
    //also need the user repository
    private UserRepository userRepository = mock(UserRepository.class); //Creating a mock object
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        //the three autowired fields that userController needs
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    //where happy path means positive use case or the very minimum positive use case that we need to run
    //Sanitity test
    @Test
    public void create_user_happy_path() throws Exception {
        //testing to create a user
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed"); //example of stubbing
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        //need a response: so calling the controller for the method
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        //can now run assertions on response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody(); // now have the User
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("Test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }
}