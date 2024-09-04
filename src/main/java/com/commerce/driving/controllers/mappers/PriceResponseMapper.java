package com.commerce.driving.controllers.mappers;

import com.commerce.domain.api.PriceApi;
import com.commerce.domain.model.Brand;
import com.commerce.domain.model.Price;
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
     * @param priceApi the price api
     * @return the price response
     */
    public PriceResponse mapListPriceApiToPriceResponse(PriceApi priceApi) {
        var price = Price.builder()
                .productId(priceApi.getProductId())
                .priceList(priceApi.getPriceList())
                .startDate(OffsetDateTime.of(priceApi.getStartDate(), ZoneOffset.UTC))
                .endDate(OffsetDateTime.of(priceApi.getEndDate(), ZoneOffset.UTC))
                .price(priceApi.getPrice())
                .currency(priceApi.getCurrency())
                .brandId(priceApi.getBrand().getBrandId())
                .build();

        return PriceResponse.builder()
                .brand(Brand.builder()
                        .brandId(priceApi.getBrand().getBrandId())
                        .brandName(priceApi.getBrand().getBrandName())
                        .build())
                .price(price)
                .build();

    }
}
