package com.warehouse.order.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "`order`")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long itemId;
    private String customerName;
    private Long price;
    private Integer quantity;
    private Date createDate;
    private Date updateDate;

    @PrePersist
    public void prePersist() {
        createDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = new Date();
    }
}

