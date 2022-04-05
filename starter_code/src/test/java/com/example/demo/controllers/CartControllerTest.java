package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);

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

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(userRepository.findByUsername("testingUser")).thenReturn(null);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(java.util.Optional.of(item1));
    }

    @Test
    public void ModifyCartRequestTest(){
    }

    @Test
    public void removeFromCartTest_HappyPath() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(2L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, request.getQuantity());

        request.setUsername("test");
        request.setItemId(2L);
        request.setQuantity(1);

        response = cartController.removeFromCart(request);
        assertEquals(200, response.getStatusCodeValue());
        Cart cartItems = response.getBody();
        assertEquals(BigDecimal.valueOf(1.99), cartItems.getTotal());
        System.out.println(cartItems.getItems());

    }

    //remove from cart - null user
    //id not found
    @Test
    public void removeFromCartTest_InvalidUser(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(2L);
        request.setQuantity(2);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        request.setUsername("testingUser");
        request.setItemId(2L);
        request.setQuantity(1);

        response = cartController.removeFromCart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_InvalidItemId(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(3l);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        request.setUsername("test");
        request.setItemId(3L);
        request.setQuantity(1);

        response = cartController.removeFromCart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartTest_HappyPath() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cartItems = response.getBody();
        assertEquals(BigDecimal.valueOf(2.99), cartItems.getTotal());

        request.setItemId(2L);
        request.setQuantity(1);
        response = cartController.addToCart(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(BigDecimal.valueOf(4.98), cartItems.getTotal());

    }

    @Test
    public void addToCartTest_InvalidId(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(3L);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartTest_WrongUser(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testingUser");
        request.setItemId(1L);
        request.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertEquals(404, response.getStatusCodeValue());
    }
}
