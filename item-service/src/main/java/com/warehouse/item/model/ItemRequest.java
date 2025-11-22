package com.warehouse.item.model;

import lombok.Data;

@Data
public class ItemRequest {
    private String name;
    private Integer quantity;
    private String description;
    private Integer category;
    private Long pricePerItem;
    private Boolean active;
}
