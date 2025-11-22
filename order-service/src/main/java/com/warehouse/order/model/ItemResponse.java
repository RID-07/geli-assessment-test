package com.warehouse.order.model;

import lombok.Data;

import java.util.Date;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private Integer quantity;
    private String description;
    private Integer category;
    private Long pricePerItem;
    private Boolean active;
    private Date createDate;
    private Date updateDate;
}
