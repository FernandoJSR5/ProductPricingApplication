package com.commerce.domain.api;

import com.commerce.domain.model.Brand;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Price api.
 */
@Data
@Builder
public class PriceApi {

    private Brand brand;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long priceList;
    private Long productId;
    private Double price;
    private String currency;

    /**
     * Empty price api.
     *
     * @return the price api
     */
    public static PriceApi empty() {
        return PriceApi.builder()
                .priceList(0L)
                .startDate(LocalDateTime.MIN)
                .endDate(LocalDateTime.MAX)
                .price(0.0)
                .currency("")
                .brand(Brand.builder().build())
                .build();
    }

}
