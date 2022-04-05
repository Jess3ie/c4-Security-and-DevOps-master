package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository );

        //Create order to test - need user & item
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("A widget that is round");

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Square Widget");
        BigDecimal price1 = BigDecimal.valueOf(1.99);
        item1.setPrice(price1);
        item.setDescription("A widget that is square");

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item1);
        cart.setItems(items);
        BigDecimal sum;
        sum = price.add(price1);
        cart.setTotal(sum);
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(userRepository.findByUsername("testingUsername")).thenReturn(null);
    }

    @Test
    public void submitAnOrderTest_HappyPath(){
        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(2, userOrder.getItems().size());
    }

    @Test
    public void submitAnOrderTest_UserNotFound(){
        ResponseEntity<UserOrder> response = orderController.submit("testingUsername");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserTest_HappyPath(){
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("test");
        assertEquals(200, userOrders.getStatusCodeValue());
        List<UserOrder> orderList = userOrders.getBody();
        assertNotNull(orderList);
    }

    @Test
    public void getOrdersForUserTest_DoesNotExist(){
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("testingUsername");
        assertEquals(404, userOrders.getStatusCodeValue());
    }
}
