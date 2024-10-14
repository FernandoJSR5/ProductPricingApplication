package com.commerce.driving.controllers.adapters;

import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.api.PricesApi;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * The type Price controller adapter.
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class PriceControllerAdapter implements PricesApi {

    private final PriceServicePort priceServicePort;
    private final PriceResponseMapper priceResponseMapper;

    @Override
    public CompletableFuture<ResponseEntity<PriceResponse>> getApplicablePrice(@RequestParam("applicationDate") OffsetDateTime applicationDate,
                                                                               @RequestParam("productId") Long productId,
                                                                               @RequestParam("brandId") Long brandId) {
        return priceServicePort.getApplicablePrice(productId, brandId, applicationDate.toLocalDateTime())
                .thenApply(price -> ResponseEntity.ok(priceResponseMapper.mapListPriceToPriceResponse(price)));
    }
}
