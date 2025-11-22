package com.warehouse.order.service;

import com.warehouse.order.model.ItemRequest;
import com.warehouse.order.model.general.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "item-service")
public interface ItemClientService {

    @GetMapping("/item-service/item/detail")
    GeneralResponse getItemById(@RequestParam("id") Long id);

    @PostMapping("/item/update")
    GeneralResponse updateItem(@RequestParam("id") Long id, @RequestBody ItemRequest itemRequest);
}

