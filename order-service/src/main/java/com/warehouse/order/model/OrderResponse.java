package com.warehouse.order.model;

import lombok.Data;

import java.util.Date;

@Data
public class OrderResponse {
    private Long id;
    private Long itemId;
    private String customerName;
    private Long price;
    private Integer quantity;
    private Date createDate;
    private Date updateDate;
}
