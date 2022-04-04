package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
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

        //Create new user
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepository.findByUsername("wrongUser")).thenReturn(null);
    }

    //where happy path means positive use case or the very minimum positive use case that we need to run
    //Sanitity test
    @Test
    public void createUserHappyPath() throws Exception {
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

    @Test
    public void findByIdTest_HappyPath() {
        CreateUserRequest testUser = new CreateUserRequest();
        testUser.setUsername("test");
        testUser.setPassword("testPassword");
        testUser.setConfirmPassword("testPassword");
        final ResponseEntity<User> response = userController.createUser(testUser);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final ResponseEntity<User> responseId = userController.findById(0L);
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());;
        assertEquals("test", user.getUsername());

    }

    @Test
    public void findByIdTest_DoesNotExist(){
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        //The 404 represent resource not exist
        assertEquals(404, response.getStatusCodeValue());
    }


    @Test
    public void signUpVerificationTest() {
        // testing password requirement: if password can be created with less than 8
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("s123");
        userRequest.setConfirmPassword("s123");
        ResponseEntity<User> userResponseEntity = userController.createUser(userRequest);
        //400 the resource exist but input is wrong
        assertEquals(400, userResponseEntity.getStatusCodeValue());

        //test if user with unmatched password can be created
        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testing");
        userResponseEntity = userController.createUser(userRequest);
        assertEquals(400, userResponseEntity.getStatusCodeValue());
    }

    @Test
    public void findByUserNameTest_HappyPath(){
        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("test", user.getUsername());
    }

    @Test
    public void findByUserNameTest_DoesNotExist(){
        final ResponseEntity<User> response = userController.findByUserName("wrongUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }
}