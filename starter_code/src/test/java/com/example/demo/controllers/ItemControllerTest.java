package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class); //Creating a mock object

    @Before
    public void setUp() {
        //the three autowired fields that userController needs
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        //create items to test
        //insert into item (name, price, description) values ('Round Widget', 2.99, 'A widget that is round');
        //insert into item (name, price, description) values ('Square Widget', 1.99, 'A widget that is square');
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

        //stubbing
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item, item1));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findByName("Round Widget")).thenReturn(Collections.singletonList(item));
        when(itemRepository.findByName("Square Widget")).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void getItemsTest_HappyPath() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        Assert.assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void getItemByIdTest_HappyPath(){
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getItemByIdTest_DoesNotExist(){
        ResponseEntity<Item> response = itemController.getItemById(3L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameTest_HappyPath(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertEquals(1, items.size());
        assertEquals("Round Widget", "Round Widget");

        ResponseEntity<List<Item>> response1 = itemController.getItemsByName("Square Widget");
        assertEquals(200, response1.getStatusCodeValue());
        assertEquals("Square Widget", "Square Widget");
    }

    @Test
    public void getItemsByNameTest_DoesNotExist(){
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Oval Widget");
        assertEquals(404, response.getStatusCodeValue());
    }
}
