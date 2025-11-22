package com.warehouse.order.service;

import com.warehouse.order.enums.ResponseType;
import com.warehouse.order.model.ItemRequest;
import com.warehouse.order.model.general.GeneralResponse;
import com.warehouse.order.util.ResponseUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ItemIntegrationService {

    private final ItemClientService itemClientService;

    @Autowired
    public ItemIntegrationService(ItemClientService itemClientService) {
        this.itemClientService = itemClientService;
    }

    @CircuitBreaker(name = "itemService", fallbackMethod = "getItemFallback")
    @Retry(name = "itemService")
    @RateLimiter(name = "itemService", fallbackMethod = "getItemFallback")
    public GeneralResponse getItem(Long id) {
        log.info("Calling item service to get item with id: {}", id);
        return itemClientService.getItemById(id);
    }

    public GeneralResponse getItemFallback(Long id, Exception ex) {
        log.error("Fallback method called for getItem with id: {}, error: {}", id, ex.getMessage());
        return ResponseUtil.generateErrorResponse(ResponseType.SERVICE_UNAVAILABLE, null).getSecond();
    }

    @CircuitBreaker(name = "itemService", fallbackMethod = "updateItemFallback")
    @Retry(name = "itemService")
    @RateLimiter(name = "itemService", fallbackMethod = "updateItemFallback")
    public GeneralResponse updateItem(Long id, ItemRequest request) {
        log.info("Calling item service to update item with id: {}", id);
        return itemClientService.updateItem(id, request);
    }

    public GeneralResponse updateItemFallback(Exception ex) {
        log.error("Fallback method called for updateItem, error: {}", ex.getMessage());
        return ResponseUtil.generateErrorResponse(ResponseType.SERVICE_UNAVAILABLE, null).getSecond();
    }
}
