package com.warehouse.order.service;

import com.warehouse.order.model.OrderRequest;
import com.warehouse.order.model.general.GeneralResponse;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

public interface OrderService {

    Pair<HttpStatus, GeneralResponse> orderList();
    Pair<HttpStatus, GeneralResponse> orderDetail(Long id);
    Pair<HttpStatus, GeneralResponse> createOrder(OrderRequest request);
    Pair<HttpStatus, GeneralResponse> updateOrder(Long id, OrderRequest request);
    Pair<HttpStatus, GeneralResponse> deleteOrder(Long id);
}

