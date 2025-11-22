package com.warehouse.item.service;

import com.warehouse.item.model.ItemRequest;
import com.warehouse.item.model.general.GeneralResponse;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

public interface ItemService {
    Pair<HttpStatus, GeneralResponse> itemList();
    Pair<HttpStatus, GeneralResponse> itemDetail(Long id);
    Pair<HttpStatus, GeneralResponse> createItem(ItemRequest itemRequest);
    Pair<HttpStatus, GeneralResponse> updateItem(Long id, ItemRequest itemRequest);
    Pair<HttpStatus, GeneralResponse> deleteItem(Long id);
}
