package com.commerce.driven.repositories.mappers;

import com.commerce.domain.api.PriceApi;
import com.commerce.domain.model.Brand;
import com.commerce.driven.repositories.models.PriceMO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The type Price api mapper.
 */
@Component
@AllArgsConstructor
public class PriceApiMapper {
    /**
     * Map price mo to price api price api.
     *
     * @param priceMO the price mo
     * @return the price api
     */
    public PriceApi mapPriceMOToPriceApi(PriceMO priceMO) {
        return PriceApi.builder()
                .productId(priceMO.getProductId())
                .brand(Brand.builder()
                        .brandId(priceMO.getBrand().getId())
                        .brandName(priceMO.getBrand().getBrandName())
                        .build())
                .priceList(priceMO.getPriceList())
                .startDate(priceMO.getStartDate())
                .endDate(priceMO.getEndDate())
                .price(priceMO.getPrice())
                .currency(priceMO.getCurrency())
                .build();
    }
}
