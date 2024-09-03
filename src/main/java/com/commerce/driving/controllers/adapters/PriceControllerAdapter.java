package com.commerce.driving.controllers.adapters;

import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.api.PricesApi;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;

@RestController
@AllArgsConstructor
public class PriceControllerAdapter implements PricesApi {

    private final PriceServicePort priceServicePort;
    private final PriceResponseMapper priceResponseMapper;

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(@RequestParam("applicationDate") OffsetDateTime applicationDate,
                                                            @RequestParam("productId") Integer productId,
                                                            @RequestParam("brandId") Integer brandId) {

        var response = priceServicePort.getApplicablePrice(productId, brandId, applicationDate);

        return ResponseEntity.ok(priceResponseMapper.mapListPriceApiToPriceResponse(response));

    }
}
