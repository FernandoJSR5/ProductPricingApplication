package com.commerce.driving.controllers.adapters;

import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.api.PricesApi;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;

/**
 * The type Price controller adapter.
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class PriceControllerAdapter implements PricesApi {

    private final PriceServicePort priceServicePort;
    private final PriceResponseMapper priceResponseMapper;

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(@RequestParam("applicationDate") OffsetDateTime applicationDate,
                                                            @RequestParam("productId") Long productId,
                                                            @RequestParam("brandId") Long brandId) {

        var response = priceServicePort.getApplicablePrice(productId, brandId, applicationDate.toLocalDateTime());

        return ResponseEntity.ok(priceResponseMapper.mapListPriceToPriceResponse(response));

    }
}
