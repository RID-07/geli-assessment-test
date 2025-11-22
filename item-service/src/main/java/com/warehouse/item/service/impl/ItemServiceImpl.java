package com.warehouse.item.service.impl;

import com.warehouse.item.entity.ItemEntity;
import com.warehouse.item.enums.ResponseType;
import com.warehouse.item.model.ItemRequest;
import com.warehouse.item.model.ItemResponse;
import com.warehouse.item.model.general.GeneralResponse;
import com.warehouse.item.repository.ItemRepository;
import com.warehouse.item.service.ItemService;
import com.warehouse.item.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    @Override
    public Pair<HttpStatus, GeneralResponse> itemList() {
        log.info("start get all items");

        try {
            List<ItemResponse> itemResponses = new ArrayList<>();

            List<ItemEntity> itemEntities = itemRepository.findAll();
            if (itemEntities.isEmpty()) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            for (ItemEntity itemEntity : itemEntities) {
                ItemResponse itemResponse = new ItemResponse();
                itemResponse.setId(itemEntity.getId());
                itemResponse.setName(itemEntity.getName());
                itemResponse.setQuantity(itemEntity.getQuantity());
                itemResponse.setDescription(itemEntity.getDescription());
                itemResponse.setCategory(itemEntity.getCategory());
                itemResponse.setPricePerItem(itemEntity.getPricePerItem());
                itemResponse.setActive(itemEntity.getActive());
                itemResponse.setCreateDate(itemEntity.getCreateDate());
                itemResponse.setUpdateDate(itemEntity.getUpdateDate());

                itemResponses.add(itemResponse);
            }

            log.info("end get all items with size items {} ", itemResponses.size());
            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_FOUND, itemResponses);
        } catch (Exception e) {
            log.error("error get all items with message {}", e.getMessage());
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> itemDetail(Long id) {
        log.info("start get items with id {} ", id);

        try {
            ItemEntity itemEntity = itemRepository.findById(id).orElse(null);

            if (itemEntity == null) {
                log.info("item with id {} not found", id);
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            ItemResponse itemResponse = new ItemResponse();
            itemResponse.setId(itemEntity.getId());
            itemResponse.setName(itemEntity.getName());
            itemResponse.setQuantity(itemEntity.getQuantity());
            itemResponse.setCategory(itemEntity.getCategory());
            itemResponse.setPricePerItem(itemEntity.getPricePerItem());
            itemResponse.setDescription(itemEntity.getDescription());
            itemResponse.setActive(itemEntity.getActive());
            itemResponse.setCreateDate(itemEntity.getCreateDate());
            itemResponse.setUpdateDate(itemEntity.getUpdateDate());

            log.info("end get items with id {}", id);
            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_FOUND, itemResponse);

        } catch (Exception e) {
            log.error("error get items with id {}", id, e);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> createItem(ItemRequest itemRequest) {
        log.info("start add items with request {} ", itemRequest);

        try {
            ItemEntity existingItem = itemRepository.findByName(itemRequest.getName());
            if (!ObjectUtils.isEmpty(existingItem)) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_EXIST, existingItem);
            }

            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setName(itemRequest.getName());
            itemEntity.setQuantity(itemRequest.getQuantity());
            itemEntity.setDescription(itemRequest.getDescription());
            itemEntity.setCategory(itemRequest.getCategory());
            itemEntity.setPricePerItem(itemRequest.getPricePerItem());
            itemEntity.setActive(itemRequest.getActive());
            itemRepository.save(itemEntity);

            ItemResponse itemResponse = new ItemResponse();
            itemResponse.setId(itemEntity.getId());
            itemResponse.setName(itemEntity.getName());
            itemResponse.setQuantity(itemEntity.getQuantity());
            itemResponse.setDescription(itemEntity.getDescription());
            itemResponse.setCategory(itemEntity.getCategory());
            itemResponse.setPricePerItem(itemEntity.getPricePerItem());
            itemResponse.setActive(itemEntity.getActive());
            itemResponse.setCreateDate(itemEntity.getCreateDate());

            log.info("end add items with request {} ", itemRequest);
            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_ADD, itemEntity);
        } catch (Exception e) {
            log.error("error add items with request {}", itemRequest);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> updateItem(Long id, ItemRequest itemRequest) {
        log.info("start update items with id {} and request {} ", id, itemRequest);

        try {
            ItemEntity itemWithSameName = itemRepository.findByName(itemRequest.getName());
            if (itemWithSameName != null && !itemWithSameName.getId().equals(id)) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_EXIST, null);
            }

            ItemEntity itemEntity = itemRepository.findById(id).orElse(null);
            if (itemEntity == null) {
                log.info("item with id {} not found", id);
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            itemEntity.setName(itemRequest.getName());
            itemEntity.setQuantity(itemRequest.getQuantity());
            itemEntity.setDescription(itemRequest.getDescription());
            itemEntity.setCategory(itemRequest.getCategory());
            itemEntity.setPricePerItem(itemRequest.getPricePerItem());
            itemEntity.setActive(itemRequest.getActive());

            itemRepository.save(itemEntity);

            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_UPDATED, itemEntity);

        } catch (Exception e) {
            log.error("error update items with id {}", id, e);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> deleteItem(Long id) {
        log.info("start delete items with id {} ", id);

        try {
            ItemEntity itemEntity = itemRepository.findById(id).orElseThrow();
            if (!ObjectUtils.isEmpty(itemEntity)) {
                itemRepository.deleteById(id);

                log.info("end delete items with id {} ", id);
                return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_DELETED, null);
            } else {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }
        } catch (Exception e) {
            log.error("error delete items with id {}", id);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }
}
