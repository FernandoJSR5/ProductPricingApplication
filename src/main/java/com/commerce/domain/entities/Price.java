package com.commerce.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Price.
 */
@Data
@Builder
public class Price {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long priceList;
    private Long brandId;
    private Long productId;
    private Double price;
    private String currency;
    private Integer priority;

}
