package com.commerce.driving.controllers.mappers;

import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * The type Price response mapper.
 */
@Component
public class PriceResponseMapper {

    /**
     * Map list price api to price response price response.
     *
     * @param price the price
     * @return the price response
     */
    public PriceResponse mapListPriceToPriceResponse(Price price) {
        return PriceResponse.builder()
                .price(com.commerce.domain.model.Price.builder()
                        .productId(price.getProductId())
                        .priceList(price.getPriceList())
                        .startDate(OffsetDateTime.of(price.getStartDate(), ZoneOffset.UTC))
                        .endDate(OffsetDateTime.of(price.getEndDate(), ZoneOffset.UTC))
                        .price(price.getPrice())
                        .currency(price.getCurrency())
                        .brandId(price.getBrandId())
                        .build())
                .build();

    }
}
