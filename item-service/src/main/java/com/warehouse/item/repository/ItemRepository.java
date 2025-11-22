package com.warehouse.item.repository;

import com.warehouse.item.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    ItemEntity findByName(String name);
}
