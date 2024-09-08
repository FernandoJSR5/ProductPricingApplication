package com.commerce.driven.repositories.mappers;

import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.models.PriceMO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The type Price mapper.
 */
@Component
@AllArgsConstructor
public class PriceMapper {
    /**
     * Map price mo to price.
     *
     * @param priceMO the price mo
     * @return the price
     */
    public Price mapPriceMOToPrice(PriceMO priceMO) {
        return Price.builder()
                .productId(priceMO.getProductId())
                .priceList(priceMO.getPriceList())
                .startDate(priceMO.getStartDate())
                .endDate(priceMO.getEndDate())
                .price(priceMO.getPrice())
                .priority(priceMO.getPriority())
                .currency(priceMO.getCurrency())
                .brandId(priceMO.getBrand().getId())
                .build();
    }
}
