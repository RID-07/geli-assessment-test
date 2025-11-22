package com.warehouse.order.model;

import lombok.Data;

@Data
public class OrderRequest {
    private String customerName;
    private Long itemId;
    private Integer quantity;
}
