package com.warehouse.order.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.order.entity.OrderEntity;
import com.warehouse.order.enums.ResponseType;
import com.warehouse.order.model.ItemRequest;
import com.warehouse.order.model.ItemResponse;
import com.warehouse.order.model.OrderRequest;
import com.warehouse.order.model.OrderResponse;
import com.warehouse.order.model.general.GeneralResponse;
import com.warehouse.order.repository.OrderRepository;
import com.warehouse.order.service.OrderService;
import com.warehouse.order.service.ItemIntegrationService;
import com.warehouse.order.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.warehouse.order.constant.GeneralConstant.SUCCESS;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final ItemIntegrationService itemIntegrationService;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderServiceImpl(ItemIntegrationService itemIntegrationService, OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.itemIntegrationService = itemIntegrationService;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    private ItemResponse mapToItemResponse(GeneralResponse itemResponse) {
        return objectMapper.convertValue(itemResponse.getData(), ItemResponse.class);
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> orderList() {
        log.info("start get all order list");
        try {
            List<OrderEntity> orderEntityList = orderRepository.findAll();
            if (orderEntityList.isEmpty()) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            List<OrderResponse> orderResponses = new ArrayList<>();
            for (OrderEntity orderEntity : orderEntityList) {
                GeneralResponse itemCheck = itemIntegrationService.getItem(orderEntity.getItemId());
                if (!itemCheck.getStatus().equals(SUCCESS)) {
                    log.info("item is not found for itemId {}", orderEntity.getItemId());
                    continue;
                }

                OrderResponse response = new OrderResponse();
                response.setId(orderEntity.getId());
                response.setItemId(orderEntity.getItemId());
                response.setCustomerName(orderEntity.getCustomerName());
                response.setPrice(orderEntity.getPrice());
                response.setQuantity(orderEntity.getQuantity());
                response.setCreateDate(orderEntity.getCreateDate());
                response.setUpdateDate(orderEntity.getUpdateDate());
                orderResponses.add(response);
            }

            if (orderResponses.isEmpty()) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_FOUND, orderResponses);

        } catch (Exception e) {
            log.error("error get all orders", e);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> orderDetail(Long id) {
        log.info("start get order with id {}", id);
        try {
            OrderEntity order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            GeneralResponse itemCheck = itemIntegrationService.getItem(order.getItemId());
            if (!itemCheck.getStatus().equals(SUCCESS)) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_ITEM_NOT_FOUND, null);
            }

            OrderResponse response = new OrderResponse();
            response.setId(order.getId());
            response.setItemId(order.getItemId());
            response.setCustomerName(order.getCustomerName());
            response.setPrice(order.getPrice());
            response.setQuantity(order.getQuantity());
            response.setCreateDate(order.getCreateDate());
            response.setUpdateDate(order.getUpdateDate());

            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_FOUND, response);

        } catch (Exception e) {
            log.error("error get order with id {}", id, e);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> createOrder(OrderRequest request) {
        log.info("start create order with request {}", request);
        try {
            GeneralResponse itemCheck = itemIntegrationService.getItem(request.getItemId());
            if (!itemCheck.getStatus().equals(SUCCESS)) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_ITEM_NOT_FOUND, null);
            }

            ItemResponse itemData = mapToItemResponse(itemCheck);

            if (!itemData.getActive()) {
                return ResponseUtil.generateErrorResponse(ResponseType.INACTIVE_STOCK, null);
            }

            if (request.getQuantity() > itemData.getQuantity()) {
                return ResponseUtil.generateErrorResponse(ResponseType.INSUFFICIENT_STOCK, null);
            }

            int remainingStock = itemData.getQuantity() - request.getQuantity();

            ItemRequest updateItemRequest = new ItemRequest();
            updateItemRequest.setName(itemData.getName());
            updateItemRequest.setDescription(itemData.getDescription());
            updateItemRequest.setPricePerItem(itemData.getPricePerItem());
            updateItemRequest.setCategory(itemData.getCategory());
            updateItemRequest.setQuantity(remainingStock);
            updateItemRequest.setActive(remainingStock > 0);

            GeneralResponse updateResult = itemIntegrationService.updateItem(itemData.getId(), updateItemRequest);
            if (!updateResult.getStatus().equals(SUCCESS)) {
                GeneralResponse recheck = itemIntegrationService.getItem(request.getItemId());
                if (recheck.getStatus().equals(SUCCESS)) {
                    ItemResponse recheckData = mapToItemResponse(recheck);
                    if (request.getQuantity() > recheckData.getQuantity()) {
                        return ResponseUtil.generateErrorResponse(ResponseType.INSUFFICIENT_STOCK, null);
                    }
                }

                return ResponseUtil.generateErrorResponse(ResponseType.STOCK_CONFLICT, null);
            }

            Long totalPrice = itemData.getPricePerItem() * request.getQuantity();
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setItemId(request.getItemId());
            orderEntity.setCustomerName(request.getCustomerName());
            orderEntity.setQuantity(request.getQuantity());
            orderEntity.setPrice(totalPrice);
            orderRepository.save(orderEntity);

            OrderResponse response = new OrderResponse();
            response.setId(orderEntity.getId());
            response.setItemId(orderEntity.getItemId());
            response.setCustomerName(orderEntity.getCustomerName());
            response.setQuantity(orderEntity.getQuantity());
            response.setPrice(orderEntity.getPrice());

            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_ADD, response);

        } catch (Exception e) {
            log.error("Failed to create order", e);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> updateOrder(Long orderId, OrderRequest request) {
        log.info("start update order with id {} and request {}", orderId, request);
        try {
            OrderEntity existingOrder = orderRepository.findById(orderId).orElse(null);
            if (existingOrder == null) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            GeneralResponse itemCheck = itemIntegrationService.getItem(request.getItemId());
            if (!itemCheck.getStatus().equals(SUCCESS)) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_ITEM_NOT_FOUND, null);
            }

            ItemResponse itemData = mapToItemResponse(itemCheck);
            if (!itemData.getActive()) {
                return ResponseUtil.generateErrorResponse(ResponseType.INACTIVE_STOCK, null);
            }

            int oldQuantity = existingOrder.getQuantity();
            int newQuantity = request.getQuantity();
            int diffQuantity = newQuantity - oldQuantity;

            int updatedStock = itemData.getQuantity() - diffQuantity;
            if (updatedStock < 0) {
                return ResponseUtil.generateErrorResponse(ResponseType.INSUFFICIENT_STOCK, null);
            }

            ItemRequest updateItemRequest = new ItemRequest();
            updateItemRequest.setName(itemData.getName());
            updateItemRequest.setDescription(itemData.getDescription());
            updateItemRequest.setPricePerItem(itemData.getPricePerItem());
            updateItemRequest.setCategory(itemData.getCategory());
            updateItemRequest.setQuantity(updatedStock);
            updateItemRequest.setActive(updatedStock > 0);

            GeneralResponse updateResult = itemIntegrationService.updateItem(itemData.getId(), updateItemRequest);
            if (!updateResult.getStatus().equals(SUCCESS)) {
                GeneralResponse recheck = itemIntegrationService.getItem(request.getItemId());
                if (recheck.getStatus().equals(SUCCESS)) {
                    ItemResponse recheckData = mapToItemResponse(recheck);
                    int finalDiffQuantity = newQuantity - oldQuantity;
                    int finalUpdatedStock = recheckData.getQuantity() - finalDiffQuantity;
                    if (finalUpdatedStock < 0) {
                        return ResponseUtil.generateErrorResponse(ResponseType.INSUFFICIENT_STOCK, null);
                    }
                }
                return ResponseUtil.generateErrorResponse(ResponseType.STOCK_CONFLICT, null);
            }

            Long totalPrice = itemData.getPricePerItem() * newQuantity;
            existingOrder.setCustomerName(request.getCustomerName());
            existingOrder.setQuantity(newQuantity);
            existingOrder.setPrice(totalPrice);
            orderRepository.save(existingOrder);

            OrderResponse response = new OrderResponse();
            response.setId(existingOrder.getId());
            response.setItemId(existingOrder.getItemId());
            response.setCustomerName(existingOrder.getCustomerName());
            response.setQuantity(existingOrder.getQuantity());
            response.setPrice(existingOrder.getPrice());

            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_UPDATED, response);

        } catch (Exception e) {
            log.error("Failed to update order", e);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Pair<HttpStatus, GeneralResponse> deleteOrder(Long id) {
        log.info("start delete order with id {}", id);
        try {
            OrderEntity orderEntity = orderRepository.findById(id).orElse(null);
            if (orderEntity == null) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_NOT_FOUND, null);
            }

            GeneralResponse itemCheck = itemIntegrationService.getItem(orderEntity.getItemId());
            if (!itemCheck.getStatus().equals(SUCCESS)) {
                return ResponseUtil.generateErrorResponse(ResponseType.DATA_ITEM_NOT_FOUND, null);
            }

            ItemResponse itemData = mapToItemResponse(itemCheck);

            int updatedStock = itemData.getQuantity() + orderEntity.getQuantity();

            ItemRequest updateItemRequest = new ItemRequest();
            updateItemRequest.setName(itemData.getName());
            updateItemRequest.setDescription(itemData.getDescription());
            updateItemRequest.setPricePerItem(itemData.getPricePerItem());
            updateItemRequest.setCategory(itemData.getCategory());
            updateItemRequest.setQuantity(updatedStock);
            updateItemRequest.setActive(true);

            GeneralResponse updateResult = itemIntegrationService.updateItem(itemData.getId(), updateItemRequest);
            if (!updateResult.getStatus().equals(SUCCESS)) {
                return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
            }

            orderRepository.deleteById(id);

            return ResponseUtil.generateSuccessResponse(ResponseType.SUCCESS_DATA_DELETED, null);

        } catch (Exception ex) {
            log.error("Failed to delete order", ex);
            return ResponseUtil.generateErrorResponse(ResponseType.INTERNAL_SERVER_ERROR, null);
        }
    }
}