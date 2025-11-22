package com.warehouse.order.controller;

import com.warehouse.order.model.OrderRequest;
import com.warehouse.order.model.general.GeneralResponse;
import com.warehouse.order.service.OrderService;
import com.warehouse.order.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public ResponseEntity<Object> listOrders() {
        Pair<HttpStatus, GeneralResponse> response = orderService.orderList();
        return ResponseUtil.toResponseEntity(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<Object> detailOrder(@RequestParam Long id) {
        Pair<HttpStatus, GeneralResponse> response = orderService.orderDetail(id);
        return ResponseUtil.toResponseEntity(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addOrder(@RequestBody OrderRequest orderRequest) {
        Pair<HttpStatus, GeneralResponse> response = orderService.createOrder(orderRequest);
        return ResponseUtil.toResponseEntity(response);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateOrder(@RequestParam Long id, @RequestBody OrderRequest orderRequest) {
        Pair<HttpStatus, GeneralResponse> response = orderService.updateOrder(id, orderRequest);
        return ResponseUtil.toResponseEntity(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteOrder(@RequestParam Long id) {
        Pair<HttpStatus, GeneralResponse> response = orderService.deleteOrder(id);
        return ResponseUtil.toResponseEntity(response);
    }
}

