package com.warehouse.item.controller;

import com.warehouse.item.model.ItemRequest;
import com.warehouse.item.model.general.GeneralResponse;
import com.warehouse.item.service.ItemService;
import com.warehouse.item.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/list")
    public ResponseEntity<Object> listItems() {
        Pair<HttpStatus, GeneralResponse> response = itemService.itemList();
        return ResponseUtil.toResponseEntity(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<Object> detailItem(@RequestParam Long id) {
        Pair<HttpStatus, GeneralResponse> response = itemService.itemDetail(id);
        return ResponseUtil.toResponseEntity(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addItem(@RequestBody ItemRequest itemRequest) {
        Pair<HttpStatus, GeneralResponse> response = itemService.createItem(itemRequest);
        return ResponseUtil.toResponseEntity(response);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateItem(@RequestParam Long id, @RequestBody ItemRequest itemRequest) {
        Pair<HttpStatus, GeneralResponse> response = itemService.updateItem(id, itemRequest);
        return ResponseUtil.toResponseEntity(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteItem(@RequestParam Long id) {
        Pair<HttpStatus, GeneralResponse> response = itemService.deleteItem(id);
        return ResponseUtil.toResponseEntity(response);
    }
}
